package org.boot.reservationproject.domain.review;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.customer.entity.Customer;
import org.boot.reservationproject.domain.review.dto.WriteReviewRequest;
import org.boot.reservationproject.domain.review.service.ReviewService;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
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
      @RequestPart("reviewPhotos") List<MultipartFile> reviewPhotos) {
    String customerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    reviewService.writeReviews(facilityIdx,roomIdx,customerEmail,writeReviewRequest,reviewPhotos);
  }
  // 시설 리뷰 조회하기

}
