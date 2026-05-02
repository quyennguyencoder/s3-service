package com.nguyenquyen.s3service.controller;

import com.nguyenquyen.s3service.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    // API Upload Ảnh
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = s3Service.uploadImage(file);
            String imageUrl = s3Service.getImageUrl(fileName);

            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("url", imageUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // API Lấy URL Ảnh (Tùy chọn)
    @GetMapping("/{fileName}")
    public ResponseEntity<String> getImageUrl(@PathVariable String fileName) {
        String url = s3Service.getImageUrl(fileName);
        return ResponseEntity.ok(url);
    }
}