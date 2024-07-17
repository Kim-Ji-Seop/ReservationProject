package org.boot.reservationproject.domain.reservation.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.boot.reservationproject.domain.reservation.entity.Reservation;
import org.boot.reservationproject.global.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
  // 유저A의 체크인Date >= 유저B가 예약해둔 체크아웃Date
  // 유저A의 체크아웃Date <= 유저B가 예약해둔 체크인Date
  // 그 외 모든 예약은 불가
  List<Reservation> findByRoomIdAndCheckoutDateGreaterThanEqualAndCheckinDateLessThanEqual
      (Long roomId, LocalDate checkinDate, LocalDate checkoutDate);

  @Modifying
  @Query("UPDATE Reservation r SET r.status = :status, r.customerName = :customerName, r.phoneNumber = :customerPhoneNumber WHERE r.merchantUid = :merchantUid")
  void updateReservationDetails(@Param("merchantUid") String merchantUid, @Param("status") BaseEntity.Status status, @Param("customerName") String customerName, @Param("customerPhoneNumber") String customerPhoneNumber);


  Optional<Reservation> findByMerchantUid(String merchantUid);

  List<Reservation> findByRoomId(Long roomId);
}
