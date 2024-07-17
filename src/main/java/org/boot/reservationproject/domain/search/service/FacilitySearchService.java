package org.boot.reservationproject.domain.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.search.document.RoomDocument;
import org.boot.reservationproject.domain.search.dto.RoomDocsPerFacility;
import org.boot.reservationproject.domain.search.dto.SearchKeywordResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.facility.repository.FacilityRepository;
import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilitySearchService {
  private final FacilityRepository facilityRepository;
  private final ElasticsearchClient elasticsearchClient;
  private final ElasticsearchOperations elasticsearchOperations;

  // (임시) 테스트 끝날 때까진 이걸로 마이그레이션 계속.
  public void migrateFacilities() {
    List<Facility> facilities = facilityRepository.findAll();
    facilities.forEach(this::saveToElasticsearch);
  }

  private void saveToElasticsearch(Facility facility) {
    List<RoomDocument> roomDocuments = facility.getRooms().stream()
        .map(room -> RoomDocument.builder()
            .roomIdx(room.getId())
            .roomName(room.getRoomName())
            .checkInTime(room.getCheckInTime())
            .checkOutTime(room.getCheckOutTime())
            .minPeople(room.getMinPeople())
            .maxPeople(room.getMaxPeople())
            .price(room.getPrice())
            .status(room.getStatus())
            .build())
        .toList();

    FacilityDocument facilityDocument = FacilityDocument.builder()
        .id(facility.getId())
        .facilityName(facility.getFacilityName())
        .category(facility.getCategory())
        .region(facility.getRegion())
        .location(facility.getLocation())
        .averageRating(facility.getAverageRating())
        .numberOfReviews(facility.getNumberOfReviews())
        .previewFacilityPhotoUrl(facility.getPreviewFacilityPhotoUrl())
        .previewFacilityPhotoName(facility.getPreviewFacilityPhotoName())
        .facilityName_ngram(facility.getFacilityName())
        .region_ngram(facility.getRegion())
        .location_ngram(facility.getLocation())
        .rooms(roomDocuments)
        .build();

    Optional<IndexResponse> response = saveDocumentToElasticsearch(facilityDocument);

    response.ifPresentOrElse(
        r -> {
          log.info("Indexed with version {}", r.version());
          try {
            elasticsearchClient.indices().refresh(refreshRequest -> refreshRequest.index("facilities"));
          } catch (IOException e) {
            log.error("Failed to refresh index: {}", e.getMessage());
          }
        },
        () -> log.error("Failed to index facility: {}", facility.getId())
    );
  }

  private Optional<IndexResponse> saveDocumentToElasticsearch(FacilityDocument facilityDocument) {
    try {
      IndexResponse response = elasticsearchClient.index(i -> i
          .index("facilities")
          .id(String.valueOf(facilityDocument.getId()))
          .document(facilityDocument)
      );
      return Optional.of(response);
    } catch (Exception e) {
      log.error("Error indexing document: {}", e.getMessage());
      return Optional.empty();
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
        .map(Hit::source).filter(Objects::nonNull)
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  private SearchKeywordResponse convertToDto(FacilityDocument document) {
    List<RoomDocsPerFacility> roomDocs = document.getRooms().stream()
        .map(room -> RoomDocsPerFacility.builder()
            .roomIdx(room.getRoomIdx())
            .roomName(room.getRoomName())
            .checkInTime(room.getCheckInTime())
            .checkOutTime(room.getCheckOutTime())
            .minPeople(room.getMinPeople())
            .maxPeople(room.getMaxPeople())
            .price(room.getPrice())
            .status(room.getStatus())
            .build())
        .toList();
    return SearchKeywordResponse.builder()
        .id(document.getId())
        .facilityName(document.getFacilityName())
        .category(document.getCategory())
        .region(document.getRegion())
        .location(document.getLocation())
        .averageRating(document.getAverageRating())
        .numberOfReviews(document.getNumberOfReviews())
        .previewFacilityPhotoUrl(document.getPreviewFacilityPhotoUrl())
        .previewFacilityPhotoName(document.getPreviewFacilityPhotoName())
        .rooms(roomDocs)
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
