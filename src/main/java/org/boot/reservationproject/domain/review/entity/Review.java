package org.boot.reservationproject.domain.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.domain.customer.entity.Customer;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.Room;
import org.boot.reservationproject.global.BaseEntity;

@Entity
@Table(name = "review")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne
  @JoinColumn(name = "facility_id")
  private Facility facility;

  @ManyToOne
  @JoinColumn(name = "room_id")
  private Room room;

  @Column(name = "content", nullable = false, length = 1000)
  private String content;

  @Column(name = "rating", nullable = false, precision = 3, scale = 1)
  private BigDecimal rating;
}
