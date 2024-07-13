package org.boot.reservationproject.domain.search.document;

import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.boot.reservationproject.global.Category;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "facilities")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FacilityDocument {
  @Id
  private Long id;

  @Field(type = FieldType.Text)
  private String facilityName;

  @Field(type = FieldType.Keyword)
  private Category category;

  @Field(type = FieldType.Keyword)
  private String region;

  @Field(type = FieldType.Text)
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

  @Field(type = FieldType.Nested, includeInParent = true)
  private List<RoomDocument> rooms;
}
