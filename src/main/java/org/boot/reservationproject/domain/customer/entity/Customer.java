package org.boot.reservationproject.domain.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.global.BaseEntity;
import org.boot.reservationproject.global.userDetails.CustomUserDetails;
import org.boot.reservationproject.global.Gender;
import org.boot.reservationproject.global.Role;

@Entity
@Table(name = "customer")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Customer extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "email", nullable = false, length = 300)
  private String email;

  @Column(name = "password", nullable = false, length = 60)
  private String password;

  @Column(name = "phone_number", nullable = false, length = 11)
  private String phoneNumber;

  @Column(name = "birthday", nullable = false, length = 8)
  private String birthday;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 15)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", nullable = false, length = 8)
  private Gender gender;

  @Column(name = "name", length = 10)
  private String name;

  @Column(name = "nickname", nullable = false, length = 20, unique = true)
  private String nickname;

  public CustomUserDetails toUserDetails() {
    return new CustomUserDetails(email, password, role);
  }

}
