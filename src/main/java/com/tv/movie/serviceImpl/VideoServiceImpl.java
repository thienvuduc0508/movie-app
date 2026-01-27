package com.tv.movie.serviceImpl;

import com.tv.movie.dao.UserRepository;
import com.tv.movie.dao.VideoRepository;
import com.tv.movie.dto.request.VideoRequest;
import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.dto.response.PageResponse;
import com.tv.movie.dto.response.VideoResponse;
import com.tv.movie.dto.response.VideoStatsResponse;
import com.tv.movie.entity.Video;
import com.tv.movie.exception.CustomMessageException;
import com.tv.movie.mapper.VideoMapper;
import com.tv.movie.service.VideoService;
import com.tv.movie.utils.PaginationUtils;
import com.tv.movie.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;
    private final ServiceUtils serviceUtils;
    private final UserRepository userRepository;

    @Override
    public MessageResponse createVideoByAdmin(VideoRequest videoRequest) {
        Video video = videoMapper.toVideo(videoRequest);
        video.setCategories(videoRequest.getCategories() != null ? videoRequest.getCategories() : List.of()) ;
        videoRepository.save(video);
        return new MessageResponse("Video created successfully!");
    }

    @Override
    public PageResponse<VideoResponse> getAllAdmVideos(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "id");
        Page<Video> videoPage;
        if (search != null && !search.trim().isEmpty()) {
            videoPage = videoRepository.searchVideos(search.trim(), pageable);
        } else {
            videoPage = videoRepository.findAll(pageable);
        }
        return PaginationUtils.toPageResponse(videoPage, VideoResponse::fromEntity);
    }

    @Override
    public MessageResponse updateVideoByAdmin(Long id, VideoRequest videoRequest) {
        Video video = serviceUtils.getVideoById(id);
        videoMapper.updateVideoFromRequest(videoRequest, video);
        if (videoRequest.getCategories() != null) {
            video.setCategories(videoRequest.getCategories());
        }
        videoRepository.save(video);
        return new MessageResponse("Video updated successfully!");
    }

    @Override
    public MessageResponse deleteVideoByAdmin(Long id) {
        if(!videoRepository.existsById(id)) {
            throw  new CustomMessageException("Video not found!");
        }
        videoRepository.deleteById(id);
        return new MessageResponse("Video deleted successfully!");
    }

    @Override
    public MessageResponse togglePublishVideo(Long id, boolean published) {
        Video video = serviceUtils.getVideoById(id);
        video.setPublished(published);
        videoRepository.save(video);
        return new MessageResponse("Video publish status updated successfully!");
    }

    @Override
    public VideoStatsResponse getAdmStats() {
        long totalVideos = videoRepository.count();
        long publishedVideos = videoRepository.countByPublished(true);
        long totalDuration = videoRepository.getTotalDuration();
        return new VideoStatsResponse(totalVideos, publishedVideos, totalDuration);
    }


    @Override
    public PageResponse<VideoResponse> getAllPublishedVideos(int page, int size, String search, String email) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "id");
        Page<Video> videoPage;
        if (search != null && !search.trim().isEmpty()) {
            videoPage = videoRepository.searchPublishedVideos(search.trim(), pageable);
        } else {
            videoPage = videoRepository.findPublishedVideos(pageable);
        }
        List<Video> videos = videoPage.getContent();
        Set<Long> watchListIds = Set.of();
        if(!videos.isEmpty()) {
            try {
                List<Long> videoIds = videos.stream().map(Video::getId).toList();
                watchListIds = userRepository.findWatchListVideoIds(email, videoIds);
            } catch (Exception e) {
                watchListIds = Set.of();
            }
        }
        Set<Long> finalWatchlistIds = watchListIds;
        videos.forEach(video -> video.setIsInWatchlist(finalWatchlistIds.contains(video.getId())));
        List<VideoResponse> videoResponses = videos.stream().map(VideoResponse::fromEntity).toList();
        return PaginationUtils.toPageResponse(videoPage, videoResponses);
    }

    @Override
    public List<VideoResponse> getFeaturedVideos() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Video> videos = videoRepository.findRandomPublishedVideos(pageable);
        return videos.stream().map(VideoResponse::fromEntity).toList();
    }
}
