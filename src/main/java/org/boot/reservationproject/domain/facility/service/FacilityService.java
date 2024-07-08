package org.boot.reservationproject.domain.facility.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.facility.dto.request.RegisterFacilityRequest;
import org.boot.reservationproject.domain.facility.dto.request.RoomDetail;
import org.boot.reservationproject.domain.facility.dto.response.RegisterFacilityResponse;
import org.boot.reservationproject.domain.facility.dto.response.RegisteredRoom;
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
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
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

  @Transactional
  public RegisterFacilityResponse registerFacility(
      RegisterFacilityRequest request,
      List<MultipartFile> facilityPhotos,
      String sellerEmail) throws IOException {

    // 1. 판매자 정보 가져오기
    Seller seller = sellerRepository.findByCpEmail(sellerEmail)
        .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

    // 2. Facility(시설) 엔티티 생성 및 저장
    Facility facility = Facility.builder()
        .seller(seller)
        .facilityName(request.name())
        .category(request.category())
        .region(request.region())
        .location(request.location())
        .regCancelRefund(request.regCancelRefund())

        .build();
    facility = facilityRepository.save(facility);

    // 3. Photo 엔티티 생성 및 시설 사진 저장
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

    // 4. Room(객실) 엔티티 생성 및 저장
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
}
