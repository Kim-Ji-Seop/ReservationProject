package org.boot.reservationproject.domain.search.repository;

import java.util.List;
import java.util.Optional;
import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilitySearchRepository extends ElasticsearchRepository<FacilityDocument, Long> {

}
