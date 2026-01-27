package com.tv.movie.controller;

import com.tv.movie.dto.request.VideoRequest;
import com.tv.movie.dto.response.*;
import com.tv.movie.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping ("/admin")
    public ApiResponse<MessageResponse> createVideoByAdmin(@Valid @RequestBody VideoRequest videoRequest) {
        return ApiResponse.<MessageResponse>builder()
                .result(videoService.createVideoByAdmin(videoRequest))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<PageResponse<VideoResponse>> getAllAdmVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ApiResponse.<PageResponse<VideoResponse>>builder()
                .result(videoService.getAllAdmVideos(page, size, search))
                .build();

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ApiResponse<MessageResponse> updateVideoByAdmin(@PathVariable Long id, @Valid @RequestBody VideoRequest videoRequest) {
        return ApiResponse.<MessageResponse>builder()
                .result(videoService.updateVideoByAdmin(id, videoRequest))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ApiResponse<MessageResponse> deleteVideoByAdmin(@PathVariable Long id) {
        return ApiResponse.<MessageResponse>builder()
                .result(videoService.deleteVideoByAdmin(id))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{id}/publish")
    public ApiResponse<MessageResponse> togglePublishVideo(@PathVariable Long id, @RequestParam boolean published){
        return ApiResponse.<MessageResponse>builder()
                .result(videoService.togglePublishVideo(id, published))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stats")
    public ApiResponse<VideoStatsResponse> getAdmStats() {
        return ApiResponse.<VideoStatsResponse>builder()
                .result(videoService.getAdmStats())
                .build();
    }

    @GetMapping("/published")
    public ApiResponse<PageResponse<VideoResponse>> getAllPublishedVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Authentication authentication
            ){
            String email = authentication.getName();
        return ApiResponse.<PageResponse<VideoResponse>>builder()
                .result(videoService.getAllPublishedVideos(page, size, search, email))
                .build();
    }

    @GetMapping("/featured")
    public ApiResponse<List<VideoResponse>> getFeaturedVideos() {
        return ApiResponse.<List<VideoResponse>>builder()
                .result(videoService.getFeaturedVideos())
                .build();
    }
}
