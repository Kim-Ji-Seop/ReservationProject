package org.boot.reservationproject.domain.seller.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.boot.reservationproject.global.BaseEntity;

@Entity
@Table(name = "seller")
public class SellerEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "ep_email",nullable = false, length = 300)
  private String epEmail; // 대표 이메일

  @Column(name = "ep_password",nullable = false, length = 60)
  private String epPassword; // 패스워드

  @Column(name = "ep_phone_number",nullable = false, length = 11)
  private String epPhoneNumber; // 대표자 전화번호

  @Column(name = "ep_name",nullable = false, length = 10)
  private String epName; // 대표자 이름

  @Column(name = "ep_code",nullable = false, length = 20)
  private String epCode; // 사업자 번호

  @Column(name = "cp_name",nullable = false, length = 50)
  private String cpName; // 법인명

  @Column(name = "cp_location",nullable = false, length = 100)
  private String cpLocation; // 법인 주소

  @Column(name = "cp_account",nullable = false, length = 20)
  private String cpAccount; // 법인 계좌

}
