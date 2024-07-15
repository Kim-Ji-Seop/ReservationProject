package org.boot.reservationproject.domain.search.document;

import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.global.Category;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
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

  @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "standard_analyzer")
  private String facilityName;

  @Field(type = FieldType.Keyword)
  private Category category;

  @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "standard_analyzer")
  private String region;

  @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "standard_analyzer")
  private String location;

  @Field(type = FieldType.Text)
  private String regCancelRefund;

  @Field(type = FieldType.Double)
  private BigDecimal averageRating;

  @Field(type = FieldType.Integer)
  private int numberOfReviews;

  @Field(type = FieldType.Text)
  private String previewFacilityPhotoUrl;

  @Field(type = FieldType.Text)
  private String previewFacilityPhotoName;

  @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "standard_analyzer")
  private String facilityName_ngram;

  @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "standard_analyzer")
  private String region_ngram;

  @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "standard_analyzer")
  private String location_ngram;
}