package org.boot.reservationproject.domain.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.domain.customer.entity.Customer;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.Room;
import org.boot.reservationproject.global.BaseEntity.Status;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "reservation")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "facility_id")
  private Facility facility;

  @ManyToOne(optional = false)
  @JoinColumn(name = "room_id")
  private Room room;

  @ManyToOne(optional = false)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @Column(name = "checkin_date", nullable = false)
  private LocalDate checkinDate;

  @Column(name = "checkout_date", nullable = false)
  private LocalDate checkoutDate;

  @Column(name = "customer_name", nullable = false, length = 10)
  private String customerName;

  @Column(name = "phone_number", nullable = false, length = 11)
  private String phoneNumber;

  @Column(name = "price", nullable = false)
  private int price;

  @Column(name = "merchant_uid",nullable = false, length = 100)
  private String merchantUid;

  @CreatedDate
  @Column(updatable = false)
  protected LocalDateTime createdAt;

  @LastModifiedDate
  protected LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;
}
