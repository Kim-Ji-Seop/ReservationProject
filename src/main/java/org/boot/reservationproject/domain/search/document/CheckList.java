package org.boot.reservationproject.domain.search.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.global.BaseEntity.Status;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckList {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate checkInDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate checkOutDate;
  private Status isPaid;

  public void setIsPaid(Status isPaid) {
    this.isPaid = isPaid;
  }
}
