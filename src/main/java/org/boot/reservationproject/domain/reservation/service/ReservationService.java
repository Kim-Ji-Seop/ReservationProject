package org.boot.reservationproject.domain.reservation.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.customer.entity.Customer;
import org.boot.reservationproject.domain.customer.repository.CustomerRepository;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.Room;
import org.boot.reservationproject.domain.facility.repository.FacilityRepository;
import org.boot.reservationproject.domain.facility.repository.RoomRepository;
import org.boot.reservationproject.domain.reservation.dto.CreateReservationRequest;
import org.boot.reservationproject.domain.reservation.dto.PaymentRequest;
import org.boot.reservationproject.domain.reservation.entity.PaymentEntity;
import org.boot.reservationproject.domain.reservation.entity.Reservation;
import org.boot.reservationproject.domain.reservation.repository.PaymentRepository;
import org.boot.reservationproject.domain.reservation.repository.ReservationRepository;
import org.boot.reservationproject.domain.search.document.CheckList;
import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.boot.reservationproject.global.BaseEntity;
import org.boot.reservationproject.global.BaseEntity.Status;
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final FacilityRepository facilityRepository;
  private final RoomRepository roomRepository;
  private final CustomerRepository customerRepository;
  private final PaymentRepository paymentRepository;
  private final ElasticsearchClient elasticsearchClient;
  private IamportClient iamportClient;
  @Value("${iamport.api-key}")
  private String apiKey;

  @Value("${iamport.secret-key}")
  private String secretKey;
  @PostConstruct
  public void init() {
    this.iamportClient = new IamportClient(apiKey, secretKey);
  }
  @Transactional
  public void reserveFacility(CreateReservationRequest request, String userEmail)
      throws IOException {
    Facility facility = facilityRepository.findById(request.facilityId())
        .orElseThrow(() -> new BaseException(ErrorCode.FACILITY_NOT_FOUND));

    Room room = roomRepository.findById(request.roomId())
        .orElseThrow(() -> new BaseException(ErrorCode.ROOM_NOT_FOUND));

    Customer customer = customerRepository.findByEmail(userEmail)
        .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

    if (!isRoomAvailable(request.roomId(), request.checkinDate(), request.checkoutDate())) {
      throw new BaseException(ErrorCode.RESERVATION_FAILED);
    }

    Reservation reservation = Reservation.builder()
        .facility(facility)
        .room(room)
        .customer(customer)
        .checkinDate(request.checkinDate())
        .checkoutDate(request.checkoutDate())
        .customerName(customer.getName()==null ? customer.getNickname() : customer.getName())
        .phoneNumber(customer.getPhoneNumber())
        .price(room.getPrice())
        .merchantUid(generateMerchantUid())
        .status(BaseEntity.Status.PAYMENT_WAIT)
        .build();

    reservationRepository.save(reservation);

    // 엘라스틱 서치에 동기화 checkList의 상태값 : PATMENT_WAIT
    updateElasticsearchIndex(reservation);
  }

  @Transactional
  public boolean isRoomAvailable(Long roomId, LocalDate checkinDate, LocalDate checkoutDate) {
    List<Reservation> conflictingReservations = reservationRepository
        .findByRoomIdAndCheckoutDateGreaterThanEqualAndCheckinDateLessThanEqual(roomId, checkinDate, checkoutDate);

    return conflictingReservations.isEmpty();
  }

  // 주문번호 생성 메서드
  private String generateMerchantUid() {
    // 현재 날짜와 시간을 포함한 고유한 문자열 생성
    String uniqueString = UUID.randomUUID().toString().replace("-", "").substring(0, 8); // 8자리만 사용
    LocalDateTime today = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd"); // 날짜 포맷을 yyyyMMdd로 변경
    String formattedDay = today.format(formatter);

    // 무작위 문자열과 현재 날짜/시간을 조합하여 주문번호 생성
    return formattedDay + '-' + uniqueString;
  }

  @Transactional
  public void completePayment(PaymentRequest request) throws IamportResponseException, IOException {

    IamportResponse<Payment> paymentResponse = iamportClient.paymentByImpUid(request.impUid());
    if (paymentResponse.getResponse().getStatus().equals("paid")) {

      // RDB 예약 상태 업데이트
      reservationRepository
          .updateReservationDetails(
              request.merchantUid(),
              Status.PAYMENT_FINISH,
              request.customerName(),
              request.customerPhoneNumber());

      // 예약 상태 업데이트 된 reservation 엔티티 객체 가져옴.
      Reservation reservation = reservationRepository.findByMerchantUid(request.merchantUid())
          .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND_BY_MID));

      // 결제 정보 저장
      PaymentEntity payment = PaymentEntity.builder()
          .reservation(reservation)
          .paidMoney(paymentResponse.getResponse().getAmount().intValue())
          .build();

      paymentRepository.save(payment);

      // 엘라스틱 서치에 등록된 checkList의 상태값 업데이트 PATMENT_WAIT > PATMENT_FINISH
      updateElasticsearchIndex(reservation);
    }
  }
  private void updateElasticsearchIndex(Reservation reservation) throws IOException {
    Facility facility = reservation.getFacility();
    Long facilityId = facility.getId();
    Long roomId = reservation.getRoom().getId();

    // 이미 존재하는 Document Get
    GetResponse<FacilityDocument> getResponse = elasticsearchClient.get(g -> g
        .index("facilities")
        .id(facilityId.toString()), FacilityDocument.class);

    if (getResponse.found()) {
      FacilityDocument facilityDocument = getResponse.source();

      assert facilityDocument != null;
      facilityDocument.getRooms().forEach(room -> {
        if (room.getRoomIdx().equals(roomId)) {
          List<CheckList> checkLists = room.getCheckList();
          boolean updated = false;

          // 결제 대기 중인 상태의 Doc's checkList 있는지 순회
          for (CheckList checkList : checkLists) {
            if (checkList.getCheckInDate().equals(reservation.getCheckinDate()) &&
                checkList.getCheckOutDate().equals(reservation.getCheckoutDate()) &&
                checkList.getIsPaid() == Status.PAYMENT_WAIT) {
              checkList.setIsPaid(Status.PAYMENT_FINISH); // 결제 완료 처리
              updated = true; // 수정상태 true
              break;
            }
          }

          log.info("updated? : {}", updated);

          // 수정이 안된 상태라면? > 결제 대기 상태 : 누군가가 이제 예약 선점을 한 객실
          if (!updated) {
            CheckList newCheckList = CheckList.builder()
                .checkInDate(reservation.getCheckinDate())
                .checkOutDate(reservation.getCheckoutDate())
                .isPaid(reservation.getStatus())
                .build();
            room.getCheckList().add(newCheckList);
          }

          log.info("CheckIndate : {}", reservation.getCheckinDate());
          log.info("CheckOutdate : {}", reservation.getCheckoutDate());
        }
      });

      // Doc 업데이트
      elasticsearchClient.update(u -> u
              .index("facilities")
              .id(facilityId.toString())
              .doc(facilityDocument),
          FacilityDocument.class);
    }
  }
}
