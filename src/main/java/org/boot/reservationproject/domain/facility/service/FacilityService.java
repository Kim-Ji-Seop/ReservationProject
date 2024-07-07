package org.boot.reservationproject.domain.facility.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.facility.dto.request.RegisterFacilityRequest;
import org.boot.reservationproject.domain.facility.dto.request.RoomDetail;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.Photo;
import org.boot.reservationproject.domain.facility.entity.Room;
import org.boot.reservationproject.domain.facility.repository.FacilityRepository;
import org.boot.reservationproject.domain.facility.repository.PhotoRepository;
import org.boot.reservationproject.domain.facility.repository.RoomRepository;
import org.boot.reservationproject.domain.seller.entity.Seller;
import org.boot.reservationproject.domain.seller.repository.SellerRepository;
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

  @Transactional
  public void registerFacility(
      RegisterFacilityRequest request,
      List<MultipartFile> facilityPhotos,
      String sellerEmail) throws IOException {

    // 1. 판매자 정보 가져오기
    Seller seller = sellerRepository.findByCpEmail(sellerEmail)
        .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

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
      log.info("facilityPhoto 길이 : {}",facilityPhoto.getBytes().length);
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
    roomRepository.saveAll(rooms);

    // 6. Facility - Service 매핑 엔티티 생성 및 저장
  }
}
