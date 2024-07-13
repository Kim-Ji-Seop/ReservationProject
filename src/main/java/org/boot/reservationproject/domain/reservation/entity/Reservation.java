package org.boot.reservationproject.domain.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.domain.customer.entity.Customer;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.Room;

@Entity
@Table(name = "reservation")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
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

  @Column(name = "paid_money", nullable = false)
  private int paidMoney;

}
