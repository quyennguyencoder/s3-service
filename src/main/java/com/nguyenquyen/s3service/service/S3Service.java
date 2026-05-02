package com.nguyenquyen.s3service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {


    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    // 1. Hàm Upload File
    public String uploadImage(MultipartFile file) throws IOException {
        boolean isPublic = true; // Hoặc có thể lấy từ tham số nếu muốn phân biệt public/private
        String folderPrefix = isPublic ? "public/" : "private/";
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFileName = folderPrefix + UUID.randomUUID().toString() + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(file.getContentType())
                .build();

        // Đẩy file lên S3
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return uniqueFileName; // Trả về tên file đã lưu trên S3
    }

    // 2. Hàm lấy URL để xem ảnh
    public String getImageUrl(String fileName) {
        // Cách đơn giản nhất nếu Bucket của bạn được cấu hình Public Policy cho phép đọc
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }
}