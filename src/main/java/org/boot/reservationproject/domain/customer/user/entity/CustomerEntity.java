package org.boot.reservationproject.domain.customer.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.global.BaseEntity;
import org.boot.reservationproject.global.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "customer")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomerEntity extends BaseEntity implements UserDetails {
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
  @Column(name = "role", nullable = false, length = 10)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", nullable = false)
  private Gender gender;

  @Column(name = "name", length = 10)
  private String name;

  @Column(name = "nickname", nullable = false, length = 20, unique = true)
  private String nickname;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+role.getKey());
    System.out.println(authority.getAuthority());
    return Collections.singletonList(authority);
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  public enum Gender {
    MALE, FEMALE
  }
}
