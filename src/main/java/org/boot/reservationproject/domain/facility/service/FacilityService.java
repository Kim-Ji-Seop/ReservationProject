package org.boot.reservationproject.domain.facility.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.facility.dto.request.RegisterFacilityRequest;
import org.boot.reservationproject.domain.facility.dto.request.RoomDetail;
import org.boot.reservationproject.domain.facility.dto.request.UpdateFacilityRequest;
import org.boot.reservationproject.domain.facility.dto.request.UpdateRoomDetail;
import org.boot.reservationproject.domain.facility.dto.response.FacilitiesInformationPreviewResponse;
import org.boot.reservationproject.domain.facility.dto.response.FacilitiesPageResponse;
import org.boot.reservationproject.domain.facility.dto.response.FacilitiesPageResponse.PageMetadata;
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
import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.boot.reservationproject.domain.search.document.RoomDocument;
import org.boot.reservationproject.domain.search.service.FacilitySearchService;
import org.boot.reservationproject.domain.seller.entity.Seller;
import org.boot.reservationproject.domain.seller.repository.SellerRepository;
import org.boot.reservationproject.global.BaseEntity.Status;
import org.boot.reservationproject.global.Category;
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
import org.boot.reservationproject.global.s3.S3Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
  private final FacilitySearchService facilitySearchService;
  private final ElasticsearchClient elasticsearchClient;

  @Transactional
  @CacheEvict(value = "facility", allEntries = true)
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

    // 7. 엘라스틱 서치에 저장
    facilitySearchService.saveToElasticsearch(finalFacility);

    return RegisterFacilityResponse.builder()
        .facilityIdx(facility.getId())
        .registeredRooms(registeredRooms)
        .build();
  }

  @CacheEvict(value = "facility", allEntries = true)
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
  @Cacheable(value = "facility", key = "#category.name() + '-' + #checkInDate.toString() + '-' + #checkOutDate.toString() + '-' + #personal + '-' + #pageable.pageNumber")
  public FacilitiesPageResponse getFacilitiesPreview( Category category,
                                                      LocalDate checkInDate,
                                                      LocalDate checkOutDate,
                                                      int personal,
                                                      Pageable pageable) {

    Page<Facility> facilities = getFacilityList(category,pageable);

    List<FacilitiesInformationPreviewResponse> content = facilities.getContent()
        .stream()
        .map(facility -> convertToDtoWithFilter(facility, checkInDate, checkOutDate, personal))
        .collect(Collectors.toList());

    PageMetadata metadata = new PageMetadata(
        facilities.getNumber(),
        facilities.getSize(),
        facilities.getTotalElements(),
        facilities.getTotalPages(),
        facilities.isLast(),
        facilities.isFirst(),
        facilities.getNumberOfElements(),
        facilities.isEmpty()
    );

    return new FacilitiesPageResponse(content, metadata);
  }
  private FacilitiesInformationPreviewResponse convertToDtoWithFilter(Facility facility, LocalDate checkInDate, LocalDate checkOutDate, int personal) {
    List<Room> availableRooms = facility.getRooms().stream()
        .filter(room -> room.getMinPeople() <= personal && room.getMaxPeople() >= personal)
        .filter(room -> room.getReservations().stream().noneMatch(reservation ->
            (checkInDate.isBefore(reservation.getCheckoutDate()) && checkOutDate.isAfter(reservation.getCheckinDate())) &&
                (reservation.getStatus() == Status.PAYMENT_FINISH || reservation.getStatus() == Status.PAYMENT_WAIT)
        ))
        .toList();
    for(Room r : availableRooms){
      log.info("available room idx : {}",r.getId());
      log.info("available room name : {}",r.getRoomName());
      log.info("available room price : {}",r.getPrice());
      log.info("available room reservation : {}",r.getReservations());
    }

    int minPrice = availableRooms.stream()
        .min(Comparator.comparingInt(Room::getPrice))
        .map(Room::getPrice)
        .orElse(0);

    return FacilitiesInformationPreviewResponse.builder()
        .facilityIdx(facility.getId())
        .category(facility.getCategory())
        .name(facility.getFacilityName())
        .region(facility.getRegion())
        .averageRating(facility.getAverageRating())
        .numberOfReviews(facility.getNumberOfReviews())
        .price(minPrice)
        .previewPhoto(facility.getPreviewFacilityPhotoUrl())
        .build();
  }

  private Page<Facility> getFacilityList(Category category, Pageable pageable){
    if (category == Category.TOTAL) {
      return facilityRepository.findAll(pageable);
    } else {
      return facilityRepository.findByCategory(category,pageable);
    }
  }


  @Transactional(readOnly = true)
  @Cacheable(value = "facilityDetails", key = "#facilityIdx  + '-' + #checkInDate.toString() + '-' + #checkOutDate.toString() + '-' + #personal")
  public FacilityInformationDetailResponse getFacilityDetail(
      Long facilityIdx, LocalDate checkInDate, LocalDate checkOutDate, int personal) {

    Facility facility = facilityRepository.findFacilityWithRooms(facilityIdx)
        .orElseThrow(() -> new BaseException(ErrorCode.FACILITY_NOT_FOUND));

    List<RoomPreviews> roomPreviewsList = facility.getRooms().stream()
        .filter(room -> (room.getMinPeople() <= personal) && (room.getMaxPeople() >= personal))
        .filter(room -> room.getReservations().stream().noneMatch(reservation ->
            (checkInDate.isBefore(reservation.getCheckoutDate()) && checkOutDate.isAfter(reservation.getCheckinDate())) &&
                (reservation.getStatus() == Status.PAYMENT_FINISH || reservation.getStatus() == Status.PAYMENT_WAIT)
        ))
        .map(room -> new RoomPreviews(
          room.getId(),
          room.getRoomName(),
          room.getMinPeople(),
          room.getMaxPeople(),
          room.getCheckInTime(),
          room.getCheckOutTime(),
          room.getPrice(),
          room.getPreviewRoomPhotoUrl()
        ))
        .collect(Collectors.toList());

    List<String> subsidiaryDetails = facilitySubsidiaryRepository.findSubsidiariesByFacilityIdx(facilityIdx)
        .orElseThrow(() -> new BaseException(ErrorCode.FACILITY_NOT_FOUND));

    return FacilityInformationDetailResponse.builder()
        .facilityName(facility.getFacilityName())
        .category(facility.getCategory())
        .region(facility.getRegion())
        .location(facility.getLocation())
        .regCancelRefund(facility.getRegCancelRefund())
        .averageRating(facility.getAverageRating())
        .numberOfReviews(facility.getNumberOfReviews())
        .roomPreviewsList(roomPreviewsList)
        .subsidiaryDetails(subsidiaryDetails)
        .build();
  }

  @Transactional
  @CacheEvict(value = "facility", allEntries = true)
  public void updateFacility(
      Long facilityIdx,
      UpdateFacilityRequest request,
      List<MultipartFile> facilityPhotos,
      String sellerEmail) throws IOException {

    // 1. 판매자 정보 가져오기
    Seller seller = sellerRepository.findByCpEmail(sellerEmail)
        .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

    // 2. Facility(시설) 정보 가져오기
    Facility facility = facilityRepository.findById(facilityIdx)
        .orElseThrow(() -> new BaseException(ErrorCode.FACILITY_NOT_FOUND));

    // 3. 기존 썸네일 사진 삭제
    if (facility.getPreviewFacilityPhotoUrl() != null) {
      log.info("썸네일 사진 url : {}", facility.getPreviewFacilityPhotoUrl());
      s3Service.deleteFile(facility.getPreviewFacilityPhotoUrl());
    }

    // 4. 썸네일 사진 가져와서 업데이트 (첫번째사진) > S3 업데이트
    MultipartFile thumbNailPhoto = facilityPhotos.get(0);
    String thumbNailPhotoUrl = s3Service.uploadFileAndGetUrl(
        thumbNailPhoto,
        "facilities/" + request.category().name().toLowerCase() +
            "/" + request.name() + "/fac-photo/thumb-nail");

    // 5. Facility(시설) 정보 업데이트
    facilityRepository.updateFacility(facilityIdx,
                                      request.name(),
                                      request.category(),
                                      request.region(),
                                      request.location(),
                                      request.regCancelRefund(),
                                      thumbNailPhotoUrl,
                                      thumbNailPhoto.getOriginalFilename());

    // 업데이트된 Facility 정보를 다시 가져오기
    Facility updatedFacility = facilityRepository.findById(facilityIdx)
        .orElseThrow(() -> new BaseException(ErrorCode.FACILITY_NOT_FOUND));

    // 6. 기존 사진 정보 업데이트 및 S3 사진 삭제
    List<Photo> existingPhotos = photoRepository.findByFacility(
        facilityRepository.findById(facilityIdx)
            .orElseThrow(() -> new BaseException(ErrorCode.FACILITY_NOT_FOUND)));

    for (Photo photo : existingPhotos) {
      s3Service.deleteFile(photo.getPhotoUrl());
      photoRepository.delete(photo);
    }

    // 5. 새로운 사진 정보 저장
    List<Photo> facilityPhotoEntities = facilityPhotos.stream()
        .map(facilityPhoto -> {
      try {
        String photoUrl = s3Service.uploadFileAndGetUrl(facilityPhoto,
            "facilities/" + request.category().name().toLowerCase() +
                "/" + request.name() + "/fac-photo");
        return Photo.builder()
            .facility(updatedFacility)
            .photoUrl(photoUrl)
            .photoName(facilityPhoto.getOriginalFilename())
            .build();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toList());
    photoRepository.saveAll(facilityPhotoEntities);

    // 6. Room(객실) 정보 업데이트
    List<Room> existingRooms = roomRepository.findByFacility(facility);

    for (UpdateRoomDetail roomDetail : request.rooms()) {
      Optional<Room> existingRoomOpt = existingRooms.stream()
          .filter(r -> r.getId().equals(roomDetail.id()))
          .findFirst();

      if (existingRoomOpt.isPresent()) {
        Room existingRoom = existingRoomOpt.get();
        roomRepository
            .updateRoom(existingRoom.getId(),
                        roomDetail.roomName(),
                        roomDetail.minPeople(),
                        roomDetail.maxPeople(),
                        roomDetail.checkInTime(),
                        roomDetail.checkOutTime(),
                        roomDetail.price());
      } else {
        Room newRoom = Room.builder()
            .facility(updatedFacility)
            .roomName(roomDetail.roomName())
            .minPeople(roomDetail.minPeople())
            .maxPeople(roomDetail.maxPeople())
            .checkInTime(roomDetail.checkInTime())
            .checkOutTime(roomDetail.checkOutTime())
            .price(roomDetail.price())
            .build();
        roomRepository.save(newRoom);
      }
    }

    // 7. 엘라스틱 서치에 정보 업데이트
    updateElasticsearchIndex(updatedFacility);
  }

  @Transactional
  public void updateElasticsearchIndex(Facility facility) throws IOException {
    // 이미 존재하는 Document Get
    GetResponse<FacilityDocument> getResponse = elasticsearchClient.get(g -> g
        .index("facilities")
        .id(facility.getId().toString()), FacilityDocument.class);

    if (getResponse.found()) {
      FacilityDocument facilityDocument = getResponse.source();

      assert facilityDocument != null;
      facilityDocument.setFacilityName(facility.getFacilityName());
      facilityDocument.setCategory(facility.getCategory());
      facilityDocument.setRegion(facility.getRegion());
      facilityDocument.setLocation(facility.getLocation());
      facilityDocument.setAverageRating(facility.getAverageRating());
      facilityDocument.setNumberOfReviews(facility.getNumberOfReviews());
      facilityDocument.setPreviewFacilityPhotoUrl(facility.getPreviewFacilityPhotoUrl());
      facilityDocument.setPreviewFacilityPhotoName(facility.getPreviewFacilityPhotoName());

      List<RoomDocument> updatedRoomDocuments = facility.getRooms().stream().map(room -> {
        // 존재하는 Room 찾아서 업데이트
        Optional<RoomDocument> existingRoomDocOpt = facilityDocument.getRooms().stream()
            .filter(rd -> rd.getRoomIdx().equals(room.getId()))
            .findFirst();

        if (existingRoomDocOpt.isPresent()) {
          RoomDocument existingRoomDoc = existingRoomDocOpt.get();
          existingRoomDoc.setRoomName(room.getRoomName());
          existingRoomDoc.setMinPeople(room.getMinPeople());
          existingRoomDoc.setMaxPeople(room.getMaxPeople());
          existingRoomDoc.setCheckInTime(room.getCheckInTime());
          existingRoomDoc.setCheckOutTime(room.getCheckOutTime());
          existingRoomDoc.setPrice(room.getPrice());
          existingRoomDoc.setStatus(room.getStatus());
          return existingRoomDoc;
        } else {
          return RoomDocument.builder()
              .roomIdx(room.getId())
              .roomName(room.getRoomName())
              .checkInTime(room.getCheckInTime())
              .checkOutTime(room.getCheckOutTime())
              .minPeople(room.getMinPeople())
              .maxPeople(room.getMaxPeople())
              .price(room.getPrice())
              .status(room.getStatus())
              .checkList(new ArrayList<>()) // 초기에는 빈 리스트로 설정
              .build();
        }
      }).collect(Collectors.toList());

      facilityDocument.setRooms(updatedRoomDocuments);

      try {
        // Doc 업데이트
        UpdateResponse<FacilityDocument> updateResponse = elasticsearchClient.update(u -> u
                .index("facilities")
                .id(facility.getId().toString())
                .doc(facilityDocument),
            FacilityDocument.class);

        log.info("Update response: {}", updateResponse);
      } catch (Exception e) {
        log.error("Failed to update", e);
        throw new RuntimeException("Failed to update", e);
      }
    } else {
      log.warn("ES에서 해당 Document를 찾을 수 없습니다 id: {}", facility.getId());
    }
  }
}
