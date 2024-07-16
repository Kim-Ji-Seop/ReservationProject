package org.boot.reservationproject.domain.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.global.BaseEntity.Status;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDocument {
  private Long roomIdx;
  private String roomName;
  private String checkInTime;
  private String checkOutTime;
  private int minPeople;
  private int maxPeople;
  private int price;
  private Status status;
}
