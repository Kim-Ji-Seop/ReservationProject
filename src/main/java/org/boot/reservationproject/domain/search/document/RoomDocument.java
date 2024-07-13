package org.boot.reservationproject.domain.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDocument {
  @Field(type = FieldType.Long)
  private Long id;

  @Field(type = FieldType.Text)
  private String roomName;

  @Field(type = FieldType.Integer)
  private int minPeople;

  @Field(type = FieldType.Integer)
  private int maxPeople;

  @Field(type = FieldType.Text)
  private String checkInTime;

  @Field(type = FieldType.Text)
  private String checkOutTime;

  @Field(type = FieldType.Integer)
  private int price;

  @Field(type = FieldType.Text)
  private String previewRoomPhotoUrl;

  @Field(type = FieldType.Text)
  private String previewRoomPhotoName;
}
