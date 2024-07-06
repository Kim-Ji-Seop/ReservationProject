package org.boot.reservationproject.domain.facility.dto.request;

import java.util.List;

public record RoomDetail(
  String roomName,
  List<byte[]> roomPhotos,
  int minPeople,
  int maxPeople,
  String checkInTime,
  String checkOutTime,
  int price

){}
