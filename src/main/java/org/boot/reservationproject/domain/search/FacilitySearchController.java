package org.boot.reservationproject.domain.search;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.search.document.FacilityDocument;
import org.boot.reservationproject.domain.search.dto.SearchKeywordResponse;
import org.boot.reservationproject.domain.search.service.FacilitySearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class FacilitySearchController {
  private final FacilitySearchService facilitySearchService;

  @PostMapping("/migrations") // ES에 데이터넣기 (테스트)
  public void migrateFacilities(){
    facilitySearchService.migrateFacilities();
  }


  @GetMapping
  public ResponseEntity<List<SearchKeywordResponse>> searchFacilities(@RequestParam("keyword") String keyword) throws IOException {
    try {
      List<SearchKeywordResponse> results = facilitySearchService.searchByKeyword(keyword);
      return ResponseEntity.ok(results);
    } catch (IOException e) {
      return ResponseEntity.status(500).build();
    }
  }

  @GetMapping("/autocomplete")
  public ResponseEntity<List<String>> autocompleteFacilities(@RequestParam("keyword") String keyword) throws IOException {
    try {
      List<String> results = facilitySearchService.autocompleteSearch(keyword);
      return ResponseEntity.ok(results);
    } catch (IOException e) {
      return ResponseEntity.status(500).build();
    }
  }
}
