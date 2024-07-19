package org.boot.reservationproject.domain.facility.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.domain.reservation.entity.Reservation;
import org.boot.reservationproject.global.BaseEntity;

@Entity
@Table(name = "room")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Room extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "accomodation_id")
  private Facility facility;

  @Column(name = "room_name",nullable = false,length = 50)
  private String roomName; // 객실 이름

  @Column(name = "min_people",nullable = false)
  private int minPeople; // 최소 인원

  @Column(name = "max_people",nullable = false)
  private int maxPeople; // 최대 인원

  @Column(name = "check_in_time",nullable = false, length = 7) // 15 : 00
  private String checkInTime; // 체크인 가능 시간

  @Column(name = "check_out_time",nullable = false, length = 7) // 11 : 00
  private String checkOutTime; // 체크아웃 시간

  @Column(name = "price",nullable = false)
  private int price; // 가격

  @Column(name = "preview_room_photo_url", length = 300)
  private String previewRoomPhotoUrl;

  @Column(name = "preview_room_photo_name", length = 30)
  private String previewRoomPhotoName;

  @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Reservation> reservations;
}
