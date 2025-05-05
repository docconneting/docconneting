// package com.example.docconneting.domain.user.service;
//
// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.model.ObjectMetadata;
// import com.amazonaws.services.s3.model.PutObjectRequest;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
//
// import java.io.IOException;
// import java.net.MalformedURLException;
// import java.net.URL;
// import java.util.UUID;
//
// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class S3Service {
//     private final AmazonS3 amazonS3;
//
//     @Value("${spring.cloud.aws.s3.bucket}")
//     private String bucket;
//
//     //S3에 이미지 업로드
//     public String uploadImage(long userId, MultipartFile image) throws IOException {
//         String fileName = userId + "_" + UUID.randomUUID() + "_" + image.getOriginalFilename(); // 고유한 파일 이름 생성
//
//         // 메타데이터 설정
//         ObjectMetadata metadata = new ObjectMetadata();
//         metadata.setContentType(image.getContentType());
//         metadata.setContentLength(image.getSize());
//
//         // S3에 파일 업로드 요청 생성
//         PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata);
//
//         // S3에 파일 업로드
//         amazonS3.putObject(putObjectRequest);
//
//         return getPublicUrl(fileName);
//     }
//
//     //S3에 있는 이미지 수정
//     public String updateImage(Long userId, String oldImageURL, MultipartFile image) throws Exception {
//         // 기존 이미지 S3에서 삭제
//         if (oldImageURL != null && !oldImageURL.isEmpty()) {
//             String oldFileKey = extractFileKeyFromUrl(oldImageURL);
//             if (amazonS3.doesObjectExist(bucket, oldFileKey)) {
//                 amazonS3.deleteObject(bucket, oldFileKey);
//             }
//         }
//         return uploadImage(userId, image);
//     }
//
//     //이미지를 url 형식으로 변환
//     private String getPublicUrl(String fileName) {
//         return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), fileName);
//     }
//
//     //URL에서 https://%s.s3.%s.amazonaws.com/ 부분 삭제하고 filename(키값) 추출
//     private String extractFileKeyFromUrl(String imageUrl) throws Exception {
//         URL url = new URL(imageUrl);
//         return url.getPath().substring(1); // 앞에 "/" 제거
//     }
// }
