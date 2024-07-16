package org.boot.reservationproject.domain.search.document;

import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.boot.reservationproject.global.Category;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "facilities")
@Setting(settingPath = "settings.json")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FacilityDocument {
  @Id
  private Long id;

  private String facilityName;

  private Category category;

  private String region;

  private String location;

  private String regCancelRefund;

  private BigDecimal averageRating;

  private int numberOfReviews;

  private String previewFacilityPhotoUrl;

  private String previewFacilityPhotoName;

  private String facilityName_ngram;

  private String region_ngram;

  private String location_ngram;
}