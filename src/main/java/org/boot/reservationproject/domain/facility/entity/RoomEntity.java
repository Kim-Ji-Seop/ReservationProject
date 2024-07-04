package org.boot.reservationproject.domain.facility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.global.BaseEntity;

@Entity
@Table(name = "room")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "room_number",nullable = false,length = 40)
  private String roomNumber; // 객실 번호

  @Column(name = "min_people",nullable = false)
  private Integer minPeople;

  @Column(name = "max_people",nullable = false)
  private Integer maxPeople;

  @Column(name = "check_in_time",nullable = false)
  private LocalDateTime checkInTime;

  @Column(name = "check_out_time",nullable = false)
  private LocalDateTime checkOutTime;

  @Column(name = "price",nullable = false)
  private Integer price;

}
