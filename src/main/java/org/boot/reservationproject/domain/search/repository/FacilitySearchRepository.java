package org.boot.reservationproject.domain.search.repository;

import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilitySearchRepository extends ElasticsearchRepository<FacilityDocument, Long> {
}
