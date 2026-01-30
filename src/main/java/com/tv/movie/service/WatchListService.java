package com.tv.movie.service;

import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.dto.response.PageResponse;
import com.tv.movie.dto.response.VideoResponse;

public interface WatchListService {
    MessageResponse addToWatchlist(String email, Long videoId);

    MessageResponse removeFromWatchlist(String email, Long videoId);

    PageResponse<VideoResponse> getWatchlist(String email, int page, int size, String search);
}
