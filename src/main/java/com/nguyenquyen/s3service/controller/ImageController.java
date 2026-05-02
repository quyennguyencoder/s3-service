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

    /**
     * 1. API UPLOAD ẢNH
     * Frontend sẽ gửi file và kèm theo cờ isPublic (true/false)
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPublic", defaultValue = "true") boolean isPublic) {
        try {
            // Gọi Service đẩy lên S3 (Trả về "public/..." hoặc "private/...")
            String keyName = s3Service.uploadImage(file, isPublic);

            // Tạo URL tạm thời để Frontend có thể hiển thị ảnh vừa upload (Preview)
            String previewUrl = isPublic
                    ? s3Service.getPublicImageUrl(keyName)
                    : s3Service.getPrivateImageUrl(keyName);

            Map<String, String> response = new HashMap<>();
            // CỰC KỲ QUAN TRỌNG: Bạn lấy giá trị keyName này lưu vào Database
            response.put("keyName", keyName);
            response.put("previewUrl", previewUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 2. API LẤY LINK XEM ẢNH
     * Dùng khi Frontend đọc từ DB ra được cái keyName và muốn lấy link hiển thị
     */
    @GetMapping("/view")
    public ResponseEntity<Map<String, String>> getImageUrl(@RequestParam("keyName") String keyName) {
        String viewUrl;

        // Dựa vào tiền tố (prefix) để quyết định cách tạo link
        if (keyName.startsWith("public/")) {
            viewUrl = s3Service.getPublicImageUrl(keyName);
        } else if (keyName.startsWith("private/")) {
            viewUrl = s3Service.getPrivateImageUrl(keyName); // Sinh link sống 15 phút
        } else {
            return ResponseEntity.badRequest().build();
        }

        Map<String, String> response = new HashMap<>();
        response.put("url", viewUrl);
        return ResponseEntity.ok(response);
    }
}