package org.boot.reservationproject.domain.facility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Room extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "accomodation_id")
  private Accomodation accomodation;

  @Column(name = "room_number",nullable = false,length = 40)
  private String roomNumber; // 객실 번호

  @Column(name = "min_people",nullable = false)
  private int minPeople; // 최소 인원

  @Column(name = "max_people",nullable = false)
  private int maxPeople; // 최대 인원

  @Column(name = "check_in_time",nullable = false)
  private LocalDateTime checkInTime; // 체크인 가능 시간

  @Column(name = "check_out_time",nullable = false)
  private LocalDateTime checkOutTime; // 체크아웃 시간

  @Column(name = "price",nullable = false)
  private int price; // 가격

}
