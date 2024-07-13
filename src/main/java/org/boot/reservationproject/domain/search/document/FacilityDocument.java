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
  private String region;
  public static FacilityDocument from(Facility facility){
    return FacilityDocument.builder()
        .id(facility.getId())
        .facilityName(facility.getFacilityName())
        .region(facility.getRegion())
        .build();
  }
}
