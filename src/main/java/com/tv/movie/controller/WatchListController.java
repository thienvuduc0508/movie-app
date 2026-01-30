package com.tv.movie.controller;

import com.tv.movie.dto.response.ApiResponse;
import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.dto.response.PageResponse;
import com.tv.movie.dto.response.VideoResponse;
import com.tv.movie.service.WatchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchListController {
    private final WatchListService watchListService;
    @PostMapping("/{videoId}")
    public ApiResponse<MessageResponse> addToWatchlist(@PathVariable Long videoId, Authentication authentication) {
        String email = authentication.getName();
        return ApiResponse.<MessageResponse>builder()
                .result(watchListService.addToWatchlist(email, videoId))
                .build();
    }

    @DeleteMapping("/{videoId}")
    public ApiResponse<MessageResponse> removeFromWatchlist(@PathVariable Long videoId, Authentication authentication) {
        String email = authentication.getName();
        return ApiResponse.<MessageResponse>builder()
                .result(watchListService.removeFromWatchlist(email, videoId))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<VideoResponse>> getWatchlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Authentication authentication) {
        String email = authentication.getName();
        return ApiResponse.<PageResponse<VideoResponse>>builder()
                .result(watchListService.getWatchlist(email, page, size, search))
                .build();
    }

}
