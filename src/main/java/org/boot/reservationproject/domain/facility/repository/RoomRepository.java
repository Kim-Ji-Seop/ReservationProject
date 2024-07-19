package org.boot.reservationproject.domain.facility.repository;

import java.util.List;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.Room;
import org.boot.reservationproject.global.BaseEntity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
  @Modifying
  @Transactional
  @Query("UPDATE Room r "
      + "SET r.previewRoomPhotoUrl = :previewRoomPhotoUrl, "
          + "r.previewRoomPhotoName = :previewRoomPhotoName "
      + "WHERE r.id = :roomId")
  void updatePreviewPhoto(
      @Param("roomId") Long roomId,
      @Param("previewRoomPhotoUrl") String previewRoomPhotoUrl,
      @Param("previewRoomPhotoName") String previewPhotoName);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query("UPDATE Room r "
      + "SET r.roomName = :roomName, "
          + "r.minPeople = :minPeople, "
          + "r.maxPeople = :maxPeople, "
          + "r.checkInTime = :checkInTime, "
          + "r.checkOutTime = :checkOutTime, "
          + "r.price = :price "
      + "WHERE r.id = :roomId")
  void updateRoom(@Param("roomId") Long roomId,
      @Param("roomName") String roomName,
      @Param("minPeople") int minPeople,
      @Param("maxPeople") int maxPeople,
      @Param("checkInTime") String checkInTime,
      @Param("checkOutTime") String checkOutTime,
      @Param("price") int price);

  List<Room> findByFacility(Facility facility);

  @Modifying
  @Transactional
  @Query("UPDATE Room r SET r.status = :status WHERE r.id = :roomId")
  void updateRoomStatus(@Param("roomId") Long roomId, @Param("status") Status status);
}
