package org.boot.reservationproject.domain.review.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.customer.entity.Customer;
import org.boot.reservationproject.domain.customer.repository.CustomerRepository;
import org.boot.reservationproject.domain.facility.dto.response.FacilitiesPageResponse.PageMetadata;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.Photo;
import org.boot.reservationproject.domain.facility.entity.Room;
import org.boot.reservationproject.domain.facility.repository.FacilityRepository;
import org.boot.reservationproject.domain.facility.repository.PhotoRepository;
import org.boot.reservationproject.domain.facility.repository.RoomRepository;
import org.boot.reservationproject.domain.review.dto.request.WriteReviewRequest;
import org.boot.reservationproject.domain.review.dto.response.ReviewDetail;
import org.boot.reservationproject.domain.review.dto.response.ReviewRatingResponse;
import org.boot.reservationproject.domain.review.dto.response.ReviewsPageResponse;
import org.boot.reservationproject.domain.review.entity.Review;
import org.boot.reservationproject.domain.review.repository.ReviewRepository;
import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.boot.reservationproject.domain.search.document.RoomDocument;
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
public class ReviewService {
  private final ReviewRepository reviewRepository;
  private final FacilityRepository facilityRepository;
  private final RoomRepository roomRepository;
  private final CustomerRepository customerRepository;
  private final S3Service s3Service;
  private final PhotoRepository photoRepository;
  private final ElasticsearchClient elasticsearchClient;
  @Transactional
  public void writeReviews(
      Long facilityIdx,
      Long roomIdx,
      String customerEmail,
      WriteReviewRequest request,
      List<MultipartFile> reviewPhotos) throws IOException {

    Facility facility = getFacility(facilityIdx);
    Room room = getRoom(roomIdx);
    Customer customer = getCustomer(customerEmail);

    Review review = Review.builder()
        .customer(customer)
        .facility(facility)
        .room(room)
        .content(request.reviewContent())
        .rating(request.rating())
        .build();
    review = reviewRepository.save(review);

    // 사진 저장
    saveReviewPhotos(reviewPhotos, facility, room, review);

    // 시설 > 평점 갯수 및 평균 평점 업데이트
    updateFacilityRating(facility,request.rating());

    // 엘라스틱 서치 업데이트
    facility = getFacility(facilityIdx);
    updateElasticSearch(facility);
  }

  @Transactional
  public void updateElasticSearch(Facility facility) throws IOException {
    // 이미 존재하는 Document Get
    GetResponse<FacilityDocument> getResponse = elasticsearchClient.get(g -> g
        .index("facilities")
        .id(facility.getId().toString()), FacilityDocument.class);

    if (getResponse.found()) {
      FacilityDocument facilityDocument = getResponse.source();

      assert facilityDocument != null;
      updateFacilityDocument(facility, facilityDocument);

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

  private void updateFacilityDocument(Facility facility, FacilityDocument facilityDocument) {
    facilityDocument.setFacilityName(facility.getFacilityName());
    facilityDocument.setCategory(facility.getCategory());
    facilityDocument.setRegion(facility.getRegion());
    facilityDocument.setLocation(facility.getLocation());
    facilityDocument.setAverageRating(facility.getAverageRating());
    facilityDocument.setNumberOfReviews(facility.getNumberOfReviews());
    facilityDocument.setPreviewFacilityPhotoUrl(facility.getPreviewFacilityPhotoUrl());
    facilityDocument.setPreviewFacilityPhotoName(facility.getPreviewFacilityPhotoName());

    List<RoomDocument> updatedRoomDocuments = facility.getRooms().stream().map(room -> {
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
        throw new RuntimeException("Elastic Search와 제대로 동기화되지 않음");
      }
    }).collect(Collectors.toList());

    facilityDocument.setRooms(updatedRoomDocuments);
  }

  private Facility getFacility(Long facilityIdx) {
    return facilityRepository.findById(facilityIdx)
        .orElseThrow(() -> new BaseException(ErrorCode.FACILITY_NOT_FOUND));
  }

  private Room getRoom(Long roomIdx) {
    return roomRepository.findById(roomIdx)
        .orElseThrow(() -> new BaseException(ErrorCode.ROOM_NOT_FOUND));
  }

  private Customer getCustomer(String customerEmail) {
    return customerRepository.findByEmail(customerEmail)
        .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
  }

  private void saveReviewPhotos(List<MultipartFile> reviewPhotos, Facility facility, Room room, Review review) {
    List<Photo> reviewPhotoEntities = reviewPhotos.stream()
        .map(photo -> {
          try {
            String photoUrl = s3Service.uploadFileAndGetUrl(photo,
                "facilities/" + facility.getCategory().name().toLowerCase() +
                    "/" + facility.getFacilityName() +
                    "/reviews" +
                    "/review" + review.getId());
            return Photo.builder()
                .facility(facility)
                .room(room)
                .review(review)
                .photoUrl(photoUrl)
                .photoName(photo.getOriginalFilename())
                .build();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }).toList();

    photoRepository.saveAll(reviewPhotoEntities);
  }
  private void updateFacilityRating(Facility facility, BigDecimal newRating) {
    int currentNumberOfReviews = facility.getNumberOfReviews();
    BigDecimal currentAverageRating = facility.getAverageRating();

    BigDecimal updatedAverageRating = currentAverageRating
        .multiply(BigDecimal.valueOf(currentNumberOfReviews))
        .add(newRating)
        .divide(BigDecimal.valueOf(currentNumberOfReviews + 1), 1, RoundingMode.HALF_UP); // 소수점 이하 한 자리, 반올림

    facilityRepository.updateRating(facility.getId(),currentNumberOfReviews + 1,updatedAverageRating);
  }

  public ReviewRatingResponse getReviewRating(Long facilityIdx) {
    Facility facility = getFacility(facilityIdx);
    return ReviewRatingResponse.builder()
        .rating(facility.getAverageRating())
        .numberOfReviews(facility.getNumberOfReviews())
        .build();
  }

  public ReviewsPageResponse getReviewList(Long facilityIdx, Pageable pageable) {
    Page<Review> reviews = reviewRepository.findByFacilityId(facilityIdx, pageable);
    List<ReviewDetail> reviewDetails = reviews.getContent().stream()
        .map(this::convertToReviewDetail)
        .collect(Collectors.toList());

    PageMetadata pageMetadata = new PageMetadata(
        reviews.getNumber(),
        reviews.getSize(),
        reviews.getTotalElements(),
        reviews.getTotalPages(),
        reviews.isLast(),
        reviews.isFirst(),
        reviews.getNumberOfElements(),
        reviews.isEmpty()
    );

    return new ReviewsPageResponse(reviewDetails, pageMetadata);
  }

  private ReviewDetail convertToReviewDetail(Review review) {
    return new ReviewDetail(
        review.getId(),
        review.getCustomer().getNickname(),
        review.getRating(),
        calculateTimeAgo(review.getCreatedAt()),
        review.getRoom().getRoomName(),
        review.getContent()
    );
  }

  private String calculateTimeAgo(LocalDateTime createdAt) {
    // 작성 시간과 현재 시간의 차이를 계산하여 "1분 전", "1주 전" 등으로 변환하는 로직
    Duration duration = Duration.between(createdAt, LocalDateTime.now());
    if (duration.toMinutes() < 1) {
      return "방금 전";
    } else if (duration.toMinutes() < 60) {
      return duration.toMinutes() + "분 전";
    } else if (duration.toHours() < 24) {
      return duration.toHours() + "시간 전";
    } else if (duration.toDays() < 7) {
      return duration.toDays() + "일 전";
    } else if (duration.toDays() < 30) {
      return duration.toDays() / 7 + "주 전";
    } else {
      return duration.toDays() / 30 + "개월 전";
    }
  }
}
