package org.boot.reservationproject.domain.facility.repository;

import java.util.List;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.entity.Photo;
import org.boot.reservationproject.domain.facility.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

  List<Photo> findByFacility(Facility facility);

  List<Photo> findByRoom(Room room);

  @Modifying
  @Transactional
  @Query("DELETE FROM Photo p WHERE p.facility.id = :facilityId AND p.room IS NULL")
  void deleteFacilityPhotos(@Param("facilityId") Long facilityId);

  @Modifying
  @Transactional
  @Query("DELETE FROM Photo p WHERE p.facility.id = :facilityId AND p.room.id = :roomId")
  void deleteRoomPhotos(@Param("facilityId") Long facilityId, @Param("roomId") Long roomId);
}
