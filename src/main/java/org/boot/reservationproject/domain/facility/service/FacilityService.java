package org.boot.reservationproject.domain.facility.service;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.facility.dto.request.RegisterFacilityRequest;
import org.boot.reservationproject.domain.facility.dto.request.RoomDetail;
import org.boot.reservationproject.domain.facility.dto.response.FacilitiesInformationPreviewResponse;
import org.boot.reservationproject.domain.facility.dto.response.FacilityInformationDetailResponse;
import org.boot.reservationproject.domain.facility.dto.response.RegisterFacilityResponse;
import org.boot.reservationproject.domain.facility.dto.response.RegisteredRoom;
import org.boot.reservationproject.domain.facility.dto.response.RoomPreviews;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.FacilitySubsidiary;
import org.boot.reservationproject.domain.facility.entity.Photo;
import org.boot.reservationproject.domain.facility.entity.Room;
import org.boot.reservationproject.domain.facility.entity.Subsidiary;
import org.boot.reservationproject.domain.facility.repository.FacilityRepository;
import org.boot.reservationproject.domain.facility.repository.FacilitySubsidiaryRepository;
import org.boot.reservationproject.domain.facility.repository.PhotoRepository;
import org.boot.reservationproject.domain.facility.repository.RoomRepository;
import org.boot.reservationproject.domain.facility.repository.SubsidiaryRepository;
import org.boot.reservationproject.domain.seller.entity.Seller;
import org.boot.reservationproject.domain.seller.repository.SellerRepository;
import org.boot.reservationproject.global.Category;
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
import org.boot.reservationproject.global.s3.S3Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacilityService {
  private final SellerRepository sellerRepository;
  private final FacilityRepository facilityRepository;
  private final RoomRepository roomRepository;
  private final PhotoRepository photoRepository;
  private final SubsidiaryRepository subsidiaryRepository;
  private final FacilitySubsidiaryRepository facilitySubsidiaryRepository;
  private final S3Service s3Service;

  @Transactional
  public RegisterFacilityResponse registerFacility(
      RegisterFacilityRequest request,
      List<MultipartFile> facilityPhotos,
      String sellerEmail) throws IOException {

    // 1. 판매자 정보 가져오기
    Seller seller = sellerRepository.findByCpEmail(sellerEmail)
        .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

    // 2. 썸네일 사진 가져오기 (첫번째사진)
    MultipartFile thumbNailPhoto = facilityPhotos.get(0);
    String thumbNailPhotoUrl =
        s3Service.uploadFileAndGetUrl(
            thumbNailPhoto,
            "facilities/" + request.category().name().toLowerCase() + "/" + request.name() + "/fac-photo/thumb-nail");

    // 3. Facility(시설) 엔티티 생성 및 저장
    Facility facility = Facility.builder()
        .seller(seller)
        .facilityName(request.name())
        .category(request.category())
        .region(request.region())
        .location(request.location())
        .regCancelRefund(request.regCancelRefund())
        .averageRating(BigDecimal.valueOf(0.0))
        .numberOfReviews(0)
        .previewFacilityPhotoUrl(thumbNailPhotoUrl)
        .previewFacilityPhotoName(thumbNailPhoto.getOriginalFilename())
        .build();
    Facility finalFacility = facilityRepository.save(facility);

    // 4. Photo 엔티티 생성 및 시설 사진 저장
    List<Photo> facilityPhotoEntities = facilityPhotos.stream()
        .map(facilityPhoto -> {
          try {
            String photoUrl = s3Service.uploadFileAndGetUrl(
                facilityPhoto,
                "facilities/" + request.category().name().toLowerCase() + "/" + request.name() + "/fac-photo");
            return Photo.builder()
                .facility(finalFacility)
                .photoUrl(photoUrl)
                .photoName(facilityPhoto.getOriginalFilename())
                .build();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toList());
    photoRepository.saveAll(facilityPhotoEntities);

    // 5. Room(객실) 엔티티 생성 및 저장
    List<Room> rooms = request.rooms().stream()
        .map(roomDetail -> Room.builder()
            .facility(finalFacility)
            .roomName(roomDetail.roomName())
            .minPeople(roomDetail.minPeople())
            .maxPeople(roomDetail.maxPeople())
            .checkInTime(roomDetail.checkInTime())
            .checkOutTime(roomDetail.checkOutTime())
            .price(roomDetail.price())
            .build())
        .collect(Collectors.toList());
    rooms = roomRepository.saveAll(rooms);

    List<RegisteredRoom> registeredRooms = rooms.stream()
        .map(room -> new RegisteredRoom(room.getId(), room.getRoomName()))
        .collect(Collectors.toList());

    // 6. Facility - Service 매핑 엔티티 생성 및 저장
    List<FacilitySubsidiary> facilitySubsidiaries = request.subsidiaries().stream()
        .map(subsidiary -> {
          Subsidiary subsidiaryOne = subsidiaryRepository.findBySubsidiaryInformation(subsidiary)
              .orElseThrow(() -> new BaseException(ErrorCode.BAD_REQUEST));
          return FacilitySubsidiary.builder()
              .facility(finalFacility)
              .subsidiary(subsidiaryOne)
              .build();
        })
        .collect(Collectors.toList());
    facilitySubsidiaryRepository.saveAll(facilitySubsidiaries);


    return RegisterFacilityResponse.builder()
        .facilityIdx(facility.getId())
        .registeredRooms(registeredRooms)
        .build();
  }

  public void registerRoomPhotos(
      Long facilityIdx, Long roomIdx,
      List<MultipartFile> roomPhotos) throws IOException {

    Facility facility = facilityRepository.findById(facilityIdx)
        .orElseThrow(() -> new BaseException(ErrorCode.FACILITY_NOT_FOUND));

    Room room = roomRepository.findById(roomIdx)
        .orElseThrow(() -> new BaseException(ErrorCode.ROOM_NOT_FOUND));

    // 썸네일 사진 가져오기 (첫번째사진)
    MultipartFile thumbNailPhoto = roomPhotos.get(0);
    String thumbNailPhotoUrl = s3Service.uploadFileAndGetUrl(
        thumbNailPhoto,
        "facilities/" + facility.getCategory().toString().toLowerCase()
            + "/" + facility.getFacilityName() + "/room" + roomIdx + "/thumb-nail"
    );

    // 썸네일 사진 Room 엔티티에 Update Query
    roomRepository.updatePreviewPhoto(
        roomIdx,
        thumbNailPhotoUrl,
        thumbNailPhoto.getOriginalFilename());

    List<Photo> facilityPhotoEntities = roomPhotos.stream()
        .map(roomPhoto -> {
          try {
            String roomPhotoUrl = s3Service.uploadFileAndGetUrl(
                roomPhoto,
                "facilities/" + facility.getCategory().toString().toLowerCase()
                    + "/" + facility.getFacilityName() + "/room" + roomIdx
            );
            return Photo.builder()
                .facility(facility)
                .room(room)
                .photoUrl(roomPhotoUrl)
                .photoName(roomPhoto.getOriginalFilename())
                .build();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toList());

    photoRepository.saveAll(facilityPhotoEntities);
  }

  @Transactional(readOnly = true)
  public Page<FacilitiesInformationPreviewResponse>
    getFacilitiesPreview(Category category, Pageable pageable) {

    Page<Facility> facilities = getFacilityList(category,pageable);

    return facilities.map(this::convertToDto);
  }
  private Page<Facility> getFacilityList(Category category, Pageable pageable){
    if (category == Category.TOTAL) {
      return facilityRepository.findAll(pageable);
    } else {
      return facilityRepository.findByCategory(category,pageable);
    }
  }
  private FacilitiesInformationPreviewResponse convertToDto(Facility facility) {
    String previewPhoto = facility.getPreviewFacilityPhotoUrl();

    int minPrice = facility.getRooms().stream()
        .min(Comparator.comparingInt(Room::getPrice))
        .map(Room::getPrice)
        .orElse(0);
    // 시설 > 객실 중 가장 싼 값의 가격을 preview로 배치시킴.
    // customer가 시설 예약을 할 때, 선택된 날짜 범위내에 가능한 시설 > 객실들 중에서, 가장 저렴한 값을 내보내야 함
    // 하지만 날짜 범위에 예약 가능한 객실이 없다면 해당 가격란은 "다른 날짜를 알아보세요" 라고 써지게 되어야 함

    return new FacilitiesInformationPreviewResponse(
        facility.getId(),
        facility.getCategory(),
        facility.getFacilityName(),
        facility.getRegion(),
        facility.getAverageRating(),
        facility.getNumberOfReviews(),
        minPrice,
        previewPhoto
    );
  }

  public FacilityInformationDetailResponse getFacilityDetail(Long facilityIdx) {
    Facility facility = facilityRepository.findFacilityWithRooms(facilityIdx)
        .orElseThrow(() -> new BaseException(ErrorCode.FACILITY_NOT_FOUND));

    List<RoomPreviews> roomPreviewsList = facility.getRooms().stream()
            .map(room -> new RoomPreviews(
        room.getId(),
        room.getMinPeople(),
        room.getMaxPeople(),
        room.getCheckInTime(),
        room.getCheckOutTime(),
        room.getPrice(),
        room.getPreviewRoomPhotoUrl()
    )).collect(Collectors.toList());

    return FacilityInformationDetailResponse.builder()
        .facilityName(facility.getFacilityName())
        .category(facility.getCategory())
        .region(facility.getRegion())
        .location(facility.getLocation())
        .regCancelRefund(facility.getRegCancelRefund())
        .averageRating(facility.getAverageRating())
        .numberOfReviews(facility.getNumberOfReviews())
        .roomPreviewsList(roomPreviewsList)
        .build();
  }
}
