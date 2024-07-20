package org.boot.reservationproject.domain.review;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.review.dto.request.WriteReviewRequest;
import org.boot.reservationproject.domain.review.dto.response.ReviewRatingResponse;
import org.boot.reservationproject.domain.review.dto.response.ReviewsPageResponse;
import org.boot.reservationproject.domain.review.service.ReviewService;
import org.boot.reservationproject.global.error.BaseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
  private final ReviewService reviewService;
  // 시설 리뷰쓰기
  @PostMapping(value = "/{facilityIdx}/{roomIdx}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void writeReviews(
      @PathVariable Long facilityIdx, @PathVariable Long roomIdx,
      @RequestPart("reviewContent") WriteReviewRequest writeReviewRequest,
      @RequestPart("reviewPhotos") List<MultipartFile> reviewPhotos) throws IOException {
    String customerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    reviewService.writeReviews(facilityIdx,roomIdx,customerEmail,writeReviewRequest,reviewPhotos);
  }
  // 시설 리뷰 평점 조회
  @GetMapping("/{facilityIdx}/ratings")
  public ResponseEntity<BaseResponse<ReviewRatingResponse>> getReviewRating(
      @PathVariable("facilityIdx") Long facilityIdx
  ){
    ReviewRatingResponse response = reviewService.getReviewRating(facilityIdx);
    return ResponseEntity.ok(new BaseResponse<>(response));
  }

  // 시설 리뷰 조회하기
  @GetMapping("/{facilityIdx}/reviews")
  public ResponseEntity<BaseResponse<ReviewsPageResponse>> getReviewList(
      @PathVariable("facilityIdx") Long facilityIdx,
      Pageable pageable
  ){
    ReviewsPageResponse response = reviewService.getReviewList(facilityIdx, pageable);
    return ResponseEntity.ok(new BaseResponse<>(response));
  }

}
