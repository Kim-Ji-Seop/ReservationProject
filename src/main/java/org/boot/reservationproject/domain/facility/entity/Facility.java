package org.boot.reservationproject.domain.facility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.domain.seller.entity.Seller;
import org.boot.reservationproject.global.BaseEntity;
import org.boot.reservationproject.global.Category;

@Entity
@Table(name = "facility")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Facility extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "seller_id")
  private Seller seller; // 판매자

  @Column(name = "facility_name", nullable = false, length = 20)
  private String facilityName; // 시설 이름

  @Column(name = "category",nullable = false)
  private Category category; // 시설 카테고리

  @Column(name = "region", nullable = false, length = 10)
  private String region; // 지역(경북/강원..)

  @Column(name = "location", nullable = false, length = 100)
  private String location; // 위치(상세주소)

  @Column(name = "reg_cancel_refund",nullable = false,length = 1500)
  private String regCancelRefund; // 취소 및 환불 규정
}
