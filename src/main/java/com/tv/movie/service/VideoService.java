package com.tv.movie.service;

import com.tv.movie.dto.request.VideoRequest;
import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.dto.response.PageResponse;
import com.tv.movie.dto.response.VideoResponse;
import com.tv.movie.dto.response.VideoStatsResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface VideoService {
    MessageResponse createVideoByAdmin( VideoRequest videoRequest);

    PageResponse<VideoResponse> getAllAdmVideos(int page, int size, String search);

    MessageResponse updateVideoByAdmin(Long id, VideoRequest videoRequest);

    MessageResponse deleteVideoByAdmin(Long id);

    MessageResponse togglePublishVideo(Long id, boolean published);

    VideoStatsResponse getAdmStats();

    PageResponse<VideoResponse> getAllPublishedVideos(int page, int size, String search, String email);

    List<VideoResponse> getFeaturedVideos();
}
