package org.boot.reservationproject.domain.search;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.search.dto.SearchKeywordResponse;
import org.boot.reservationproject.domain.search.service.FacilitySearchService;
import org.boot.reservationproject.global.error.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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


  /*
   * 검색 API
   */
  @GetMapping
  public ResponseEntity<BaseResponse<List<SearchKeywordResponse>>>
    searchFacilities(
        @RequestParam("keyword") String keyword,
        @RequestParam("checkIn") LocalDate checkInDate,
        @RequestParam("checkOut") LocalDate checkOutDate,
        @RequestParam("personal") int personal) throws IOException {

    List<SearchKeywordResponse> results = facilitySearchService
        .searchByKeyword(keyword, checkInDate, checkOutDate, personal);

    return ResponseEntity.ok(new BaseResponse<>(results));
  }
  /*
   * 자동완성 API
   */
  @GetMapping("/auto-complete")
  public ResponseEntity<BaseResponse<List<String>>> autocompleteFacilities(@RequestParam("keyword") String keyword) throws IOException {
    List<String> results = facilitySearchService.autocompleteSearch(keyword);
    return ResponseEntity.ok(new BaseResponse<>(results));
  }
}
