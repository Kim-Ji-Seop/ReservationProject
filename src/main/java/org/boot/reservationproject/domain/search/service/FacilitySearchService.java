package org.boot.reservationproject.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.boot.reservationproject.domain.search.repository.FacilitySearchRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilitySearchService {
  private final ElasticsearchOperations elasticsearchOperations;
  private final FacilitySearchRepository facilitySearchRepository;
  public void save(Facility facility){
    elasticsearchOperations.save(FacilityDocument.from(facility));
  }
}
