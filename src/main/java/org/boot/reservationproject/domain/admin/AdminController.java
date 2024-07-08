package org.boot.reservationproject.domain.admin;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
  private final AdminService adminService;
  @PostMapping("/registration/service-options")
  private void registerServiceOptions(@RequestBody List<String> services){
    adminService.registerServiceOptions(services);
  }
}
