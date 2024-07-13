package org.boot.reservationproject.global.elastic_search.sync;

import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.search.service.FacilitySyncService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class FacilityEventListener {
  private FacilitySyncService facilitySyncService;

  @TransactionalEventListener
  public void handleFacilitySaveOrUpdate(Facility facility) {
    facilitySyncService.syncFacility(facility.getId());
  }
}
