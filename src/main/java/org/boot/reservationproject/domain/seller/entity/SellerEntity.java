package org.boot.reservationproject.domain.seller.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.domain.facility.entity.AccomodationEntity;
import org.boot.reservationproject.global.BaseEntity;
import org.boot.reservationproject.global.CustomUserDetails;
import org.boot.reservationproject.global.Role;

@Entity
@Table(name = "seller")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SellerEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "cp_email",nullable = false, length = 300)
  private String cpEmail; // 회사 이메일

  @Column(name = "cp_password",nullable = false, length = 60)
  private String cpPassword; // 패스워드

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

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 45)
  private Role role;

  public CustomUserDetails toUserDetails() {
    return new CustomUserDetails(cpEmail, cpPassword, role);
  }
}
