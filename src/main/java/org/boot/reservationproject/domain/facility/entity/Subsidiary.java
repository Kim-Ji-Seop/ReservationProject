package org.boot.reservationproject.domain.facility.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.global.BaseEntity;

@Entity
@Table(name = "subsidiary") // 서비스 및 부대시설
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Subsidiary extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "service_information",nullable = false, length = 50)
  private String subsidiaryInformation; // 서비스 및 부대시설 정보

}
