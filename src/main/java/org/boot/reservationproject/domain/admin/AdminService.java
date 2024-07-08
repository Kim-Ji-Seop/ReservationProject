package org.boot.reservationproject.domain.admin;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.facility.entity.Subsidiary;
import org.boot.reservationproject.domain.facility.repository.SubsidiaryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final SubsidiaryRepository subsidiaryRepository;
  public void registerServiceOptions(List<String> services) {
    List<Subsidiary> subsidiaries = new ArrayList<>();
    for(String service : services){
      Subsidiary subsidiary = Subsidiary.builder()
          .subsidiaryInformation(service)
          .build();
      subsidiaries.add(subsidiary);
    }
    subsidiaryRepository.saveAll(subsidiaries);
  }
}
