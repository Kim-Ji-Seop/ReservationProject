package org.boot.reservationproject.domain.facility.repository;

import org.boot.reservationproject.domain.facility.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RoomRepository extends JpaRepository<Room,Long> {
  @Modifying
  @Transactional
  @Query("UPDATE Room r "
      + "SET r.previewRoomPhotoUrl = :previewRoomPhotoUrl, r.previewRoomPhotoName = :previewRoomPhotoName "
      + "WHERE r.id = :roomId")
  void updatePreviewPhoto(
      @Param("roomId") Long roomId,
      @Param("previewRoomPhotoUrl") String previewRoomPhotoUrl,
      @Param("previewRoomPhotoName") String previewPhotoName);
}
