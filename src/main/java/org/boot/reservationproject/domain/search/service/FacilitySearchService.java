package org.boot.reservationproject.domain.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.search.dto.SearchKeywordResponse;
import org.springframework.data.elasticsearch.core.SearchHit;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhrasePrefixQuery;
import co.elastic.clients.elasticsearch.core.search.Hit;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.repository.FacilityRepository;
import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilitySearchService {
  private final FacilityRepository facilityRepository;
  private final ElasticsearchClient elasticsearchClient;
  private final ElasticsearchOperations elasticsearchOperations;

  public void migrateFacilities() {
    List<Facility> facilities = facilityRepository.findAll();
    facilities.forEach(this::saveToElasticsearch);
  }

  private void saveToElasticsearch(Facility facility) {
    FacilityDocument facilityDocument = FacilityDocument.builder()
        .id(facility.getId())
        .facilityName(facility.getFacilityName())
        .category(facility.getCategory())
        .region(facility.getRegion())
        .location(facility.getLocation())
        .regCancelRefund(facility.getRegCancelRefund())
        .averageRating(facility.getAverageRating())
        .numberOfReviews(facility.getNumberOfReviews())
        .previewFacilityPhotoUrl(facility.getPreviewFacilityPhotoUrl())
        .previewFacilityPhotoName(facility.getPreviewFacilityPhotoName())
        .facilityName_ngram(facility.getFacilityName())
        .region_ngram(facility.getRegion())
        .location_ngram(facility.getLocation())
        .build();
    try {
      IndexResponse response = elasticsearchClient.index(i -> i
          .index("facilities")
          .id(String.valueOf(facilityDocument.getId()))
          .document(facilityDocument)
      );
      System.out.println("Indexed with version " + response.version());
      elasticsearchClient.indices().refresh(r -> r.index("facilities"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public List<SearchKeywordResponse> searchByKeyword(String keyword) throws IOException {
    SearchRequest searchRequest = new SearchRequest.Builder()
        .index("facilities")
        .query(q -> q
            .multiMatch(m -> m
                .fields("facilityName", "region", "location", "facilityName_ngram", "region_ngram", "location_ngram")
                .query(keyword)
            )
        ).build();

    SearchResponse<FacilityDocument> searchResponse = elasticsearchClient.search(searchRequest, FacilityDocument.class);
    log.info("검색 쿼리 : {}", searchRequest);
    log.info("검색 결과 : {}", searchResponse);
    return searchResponse.hits().hits().stream()
        .map(Hit::source)
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  private SearchKeywordResponse convertToDto(FacilityDocument document) {
    return SearchKeywordResponse.builder()
        .id(document.getId())
        .facilityName(document.getFacilityName())
        .category(document.getCategory())
        .region(document.getRegion())
        .location(document.getLocation())
        .regCancelRefund(document.getRegCancelRefund())
        .averageRating(document.getAverageRating())
        .numberOfReviews(document.getNumberOfReviews())
        .previewFacilityPhotoUrl(document.getPreviewFacilityPhotoUrl())
        .previewFacilityPhotoName(document.getPreviewFacilityPhotoName())
        .build();
  }

  public List<String> autocompleteSearch(String keyword) throws IOException {
    SearchRequest searchRequest = new SearchRequest.Builder()
        .index("facilities")
        .query(q -> q
            .multiMatch(m -> m
                .fields("facilityName_ngram", "region_ngram", "location_ngram")
                .query(keyword)
            )
        ).build();
    log.info("자동완성 쿼리 : {}", searchRequest);
    SearchResponse<FacilityDocument> searchResponse = elasticsearchClient.search(searchRequest, FacilityDocument.class);
    log.info("자동완성 결과 : {}", searchResponse);

    return searchResponse.hits().hits().stream()
        .flatMap(hit -> extractKeywords(hit.source(), keyword).stream())
        .distinct()
        .collect(Collectors.toList());
  }

  private List<String> extractKeywords(FacilityDocument document, String keyword) {
    return Stream.of(document.getFacilityName(), document.getRegion(), document.getLocation())
        .filter(value -> value != null && value.contains(keyword))
        .collect(Collectors.toList());
  }
}
