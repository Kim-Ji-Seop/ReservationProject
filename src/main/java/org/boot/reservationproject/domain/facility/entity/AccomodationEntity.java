package org.boot.reservationproject.domain.facility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.global.BaseEntity;
import org.boot.reservationproject.global.Category;

@Entity
@Table(name = "accomodation")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccomodationEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "accomodation_name", nullable = false, length = 20)
  private String accomodationName;

  @Column(name = "phone_number",nullable = false,length = 11)
  private String phoneNumber;

  @Column(name = "category",nullable = false)
  private Category category;

  @Column(name = "region", nullable = false, length = 10)
  private String region; // 지역(경북/강원..)

  @Column(name = "location", nullable = false, length = 100)
  private String location; // 위치(상세주소)

  @Column(name = "reg_cancel_refund",nullable = false,length = 1500)
  private String regCancelRefund;
}
