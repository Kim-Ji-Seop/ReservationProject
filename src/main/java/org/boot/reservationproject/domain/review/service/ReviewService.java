package org.boot.reservationproject.domain.review.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.customer.entity.Customer;
import org.boot.reservationproject.domain.customer.repository.CustomerRepository;
import org.boot.reservationproject.domain.facility.dto.request.RegisterFacilityRequest;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.Photo;
import org.boot.reservationproject.domain.facility.entity.Room;
import org.boot.reservationproject.domain.facility.repository.FacilityRepository;
import org.boot.reservationproject.domain.facility.repository.PhotoRepository;
import org.boot.reservationproject.domain.facility.repository.RoomRepository;
import org.boot.reservationproject.domain.review.dto.WriteReviewRequest;
import org.boot.reservationproject.domain.review.entity.Review;
import org.boot.reservationproject.domain.review.repository.ReviewRepository;
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
import org.boot.reservationproject.global.s3.S3Service;
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
  @Transactional
  public void writeReviews(
      Long facilityIdx,
      Long roomIdx,
      String customerEmail,
      WriteReviewRequest request,
      List<MultipartFile> reviewPhotos) {

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
}
