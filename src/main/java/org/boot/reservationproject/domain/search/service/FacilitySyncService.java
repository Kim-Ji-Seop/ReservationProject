package org.boot.reservationproject.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.repository.FacilityRepository;
import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.boot.reservationproject.domain.search.repository.FacilitySearchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FacilitySyncService {

  private final FacilityRepository facilityRepository;

  private final FacilitySearchRepository facilitySearchRepository;

  @Transactional
  public void syncFacility(Long facilityId) {
    Facility facility = facilityRepository.findById(facilityId)
        .orElseThrow(() -> new RuntimeException("Facility not found"));
    FacilityDocument document = new FacilityDocument();
    document.setId(facility.getId());
    document.setFacilityName(facility.getFacilityName());
    document.setRegion(facility.getRegion());

    facilitySearchRepository.save(document);
  }
}
