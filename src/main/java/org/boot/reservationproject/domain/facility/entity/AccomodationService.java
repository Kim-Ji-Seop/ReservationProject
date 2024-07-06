package org.boot.reservationproject.domain.facility.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.global.BaseEntity;
@Entity
@Table(name = "accomodation_map_service")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccomodationService extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "accomodation_id")
  private AccomodationEntity accomodation;

  @ManyToOne(optional = false)
  @JoinColumn(name = "service_id")
  private ServiceEntity service;
}
