package org.boot.reservationproject.global.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.facility.dto.response.FileUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
  private final AmazonS3Client amazonS3Client;
  @Value("${cloud.aws.s3.bucket}")
  private String bucket;
  @Transactional
  public FileUploadResponse uploadFiles(Long userId, MultipartFile multipartFile, String dirName) throws IOException {
    log.info("여기까지오나?-3");
    File uploadFile = convert(multipartFile)
        .orElseThrow(() -> new IllegalArgumentException("Error: MultipartFile -> File로 전환이 실패했습니다."));
    log.info("여기까지오나?-7 {}", uploadFile.getPath());
    return upload(userId, uploadFile, dirName);
  }
  @Transactional
  public FileUploadResponse upload(Long userId, File uploadFile, String filePath) {
    String fileName = filePath + "/" + uploadFile.getName(); // S3에 저장된 파일 이름
    log.info("여기까지오나?-8 {}", fileName);
    String uploadImageUrl = putS3(uploadFile, fileName); // S3로 업로드
    log.info("uploadImageUrl = " + uploadImageUrl);
    removeNewFile(uploadFile);

    //FileUploadResponse DTO로 반환해준다.
    return new FileUploadResponse(fileName, uploadImageUrl);
  }

  // S3로 업로드
  private String putS3(File uploadFile, String fileName) {
    try {
      amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
          CannedAccessControlList.PublicRead));
      return amazonS3Client.getUrl(bucket, fileName).toString();
    } catch (Exception e) {
      log.error("S3 업로드 중 오류 발생", e);
      throw new RuntimeException("S3 업로드 중 오류 발생", e);
    }
  }

  // 로컬에 저장된 이미지 지우기
  private void removeNewFile(File targetFile) {
    if (targetFile.delete()) {
      log.info("파일이 삭제되었습니다.");
    } else {
      log.info("파일이 삭제되지 못했습니다.");
    }
  }

  // 로컬에 파일 업로드 하기
  private Optional<File> convert(MultipartFile file) throws IOException {
    log.info("여기까지오나?-4");
    File convertFile =  new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
    log.info("여기까지오나?-5 {}",convertFile.getName());
    if(convertFile.createNewFile()) {
      log.info("여기까지오나?-6");
      try (FileOutputStream fos = new FileOutputStream(convertFile)) {
        fos.write(file.getBytes());
      }
      return Optional.of(convertFile);
    }
    return Optional.empty();
  }
}
