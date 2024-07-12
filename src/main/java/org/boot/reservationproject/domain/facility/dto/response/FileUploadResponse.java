package org.boot.reservationproject.domain.facility.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Getter
@Setter
public class FileUploadResponse {
  private String fileName;
  private String uploadImageUrl;
}
