package org.boot.reservationproject.domain.facility.repository;

import org.boot.reservationproject.domain.facility.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Long> {

}
