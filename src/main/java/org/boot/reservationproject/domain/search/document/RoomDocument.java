package org.boot.reservationproject.domain.search.document;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.boot.reservationproject.global.BaseEntity.Status;

@Builder
@Getter
@Setter
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
  private List<CheckList> checkList;
}
