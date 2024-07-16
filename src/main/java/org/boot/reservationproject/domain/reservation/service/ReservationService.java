package org.boot.reservationproject.domain.reservation.service;

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
  public void reserveFacility(CreateReservationRequest request, String userEmail) {
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
      Reservation reservation = reservationRepository.findByMerchantUid(request.merchantUid())
          .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND_BY_MID));
      // 예약 상태 업데이트
      reservationRepository.updateReservationDetails(request.merchantUid(), Status.PATMENT_FINISH, request.customerName(), request.customerPhoneNumber());

      // 결제 정보 저장
      PaymentEntity payment = PaymentEntity.builder()
          .reservation(reservation)
          .paidMoney(paymentResponse.getResponse().getAmount().intValue())
          .build();
      paymentRepository.save(payment);
    }
  }
}
