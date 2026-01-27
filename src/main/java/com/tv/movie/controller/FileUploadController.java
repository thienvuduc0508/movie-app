package com.tv.movie.controller;

import com.tv.movie.dto.response.ApiResponse;
import com.tv.movie.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping("/upload/video")
    public ApiResponse<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file){
        String uuid = fileUploadService.storeVideoFile(file);
        return ApiResponse.<Map<String, String>>builder()
                .result(buildUploadResponse(uuid, file))
                .build();
    }

    @PostMapping("upload/image")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file){
        String uuid = fileUploadService.storeImageFile(file);
        return ApiResponse.<Map<String, String>>builder()
                .result(buildUploadResponse(uuid, file))
                .build();
    }

    @GetMapping("/video/{uuid}")
    public ApiResponse<ResponseEntity<Resource>> serveVideo(
            @PathVariable String uuid,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            @RequestHeader(value = "token", required = false) String tokenParam
    ) {
        return ApiResponse.<ResponseEntity<Resource>>builder()
                .result(fileUploadService.serveVideo(uuid, rangeHeader))
                .build();
    }

    @GetMapping("/image/{uuid}")
    public ApiResponse<ResponseEntity<Resource>> serveImage(
            @PathVariable String uuid
    ) {
        return ApiResponse.<ResponseEntity<Resource>>builder()
                .result(fileUploadService.serveImage(uuid))
                .build();
    }


    private Map<String, String> buildUploadResponse(String uuid, MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        response.put("uuid", uuid);
        response.put("filename", file.getOriginalFilename());
        response.put("size", String.valueOf(file.getSize()));
        return response;
    }
}
