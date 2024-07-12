package org.boot.reservationproject.domain.facility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.boot.reservationproject.domain.review.entity.Review;
import org.boot.reservationproject.global.BaseEntity;

@Entity
@Table(name = "photo")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Photo extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "facility_id")
  private Facility facility; // 시설

  @ManyToOne
  @JoinColumn(name = "rood_id")
  private Room room; // 객실

  @ManyToOne
  @JoinColumn(name = "review_id")
  private Review review; // 리뷰

  @Column(name = "photo_url", nullable = false, length = 300)
  private String photoUrl;

  @Column(name = "photo_name", nullable = false, length = 30)
  private String photoName;
}
