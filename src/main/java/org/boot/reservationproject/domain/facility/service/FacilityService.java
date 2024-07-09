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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
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
    log.info("썸네일 사진 : {}",thumbNailPhoto.getOriginalFilename());
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
        .previewFacilityPhotoData(thumbNailPhoto.getBytes())
        .previewFacilityPhotoName(thumbNailPhoto.getOriginalFilename())
        .build();
    facility = facilityRepository.save(facility);

    // 4. Photo 엔티티 생성 및 시설 사진 저장
    List<Photo> facilityPhotoEntities = new ArrayList<>();
    for(MultipartFile facilityPhoto : facilityPhotos){
      Photo photo = Photo.builder()
          .facility(facility)
          .photoData(facilityPhoto.getBytes())
          .photoName(facilityPhoto.getOriginalFilename())
          .build();
      facilityPhotoEntities.add(photo);
    }
    photoRepository.saveAll(facilityPhotoEntities);

    // 5. Room(객실) 엔티티 생성 및 저장
    List<Room> rooms = new ArrayList<>();
    List<RegisteredRoom> registeredRooms = new ArrayList<>(); // dto에 넣을 List 객체
    for(RoomDetail roomDetail : request.rooms()){
      Room room = Room.builder()
          .facility(facility)
          .roomName(roomDetail.roomName())
          .minPeople(roomDetail.minPeople())
          .maxPeople(roomDetail.maxPeople())
          .checkInTime(roomDetail.checkInTime())
          .checkOutTime(roomDetail.checkOutTime())
          .price(roomDetail.price())
          .build();
      rooms.add(room);
    }
    rooms = roomRepository.saveAll(rooms);

    for(Room room : rooms){ // 응답값
      registeredRooms.add(new RegisteredRoom(room.getId(),room.getRoomName()));
    }

    // 6. Facility - Service 매핑 엔티티 생성 및 저장
    List<FacilitySubsidiary> facilitySubsidiaries = new ArrayList<>();
    for(String subsidiary : request.subsidiaries()){
       Subsidiary subsidiaryOne = subsidiaryRepository.findBySubsidiaryInformation(subsidiary)
           .orElseThrow(() -> new BaseException(ErrorCode.BAD_REQUEST));
       FacilitySubsidiary facilitySubsidiary = FacilitySubsidiary.builder()
           .facility(facility)
           .subsidiary(subsidiaryOne)
           .build();
      facilitySubsidiaries.add(facilitySubsidiary);
    }
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

    // 썸네일 사진 Room 엔티티에 Update Query
    roomRepository.updatePreviewPhoto(
        roomIdx,
        thumbNailPhoto.getBytes(),
        thumbNailPhoto.getOriginalFilename());

    List<Photo> facilityPhotoEntities = new ArrayList<>();
    for(MultipartFile roomPhoto : roomPhotos){
      Photo photo = Photo.builder()
          .facility(facility)
          .room(room)
          .photoData(roomPhoto.getBytes())
          .photoName(roomPhoto.getOriginalFilename())
          .build();
      facilityPhotoEntities.add(photo);
    }
    photoRepository.saveAll(facilityPhotoEntities);
  }

  @Transactional(readOnly = true)
  public List<FacilitiesInformationPreviewResponse> getFacilitiesPreview(String category) {

    List<Facility> facilities;
    if ("total".equalsIgnoreCase(category)) {
      facilities = facilityRepository.findAll();
    } else {
      Category categoryEnum = Category.valueOf(category.toUpperCase());
      facilities = facilityRepository.findByCategory(categoryEnum);
    }

    return facilities.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }
  private FacilitiesInformationPreviewResponse convertToDto(Facility facility) {
    String previewPhotoBase64 = facility.getPreviewFacilityPhotoData() != null
        ? Base64.getEncoder().encodeToString(facility.getPreviewFacilityPhotoData()) : null;

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
        previewPhotoBase64
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
        Base64.getEncoder().encodeToString(room.getPreviewRoomPhotoData())
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
