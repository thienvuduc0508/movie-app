package com.tv.movie.serviceImpl;

import com.tv.movie.dao.UserRepository;
import com.tv.movie.dao.VideoRepository;
import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.dto.response.PageResponse;
import com.tv.movie.dto.response.VideoResponse;
import com.tv.movie.entity.User;
import com.tv.movie.entity.Video;
import com.tv.movie.service.WatchListService;
import com.tv.movie.utils.PaginationUtils;
import com.tv.movie.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatchListServiceImpl implements WatchListService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final ServiceUtils serviceUtils;

    @Override
    public MessageResponse addToWatchlist(String email, Long videoId) {
        User user = serviceUtils.getUserByEmail(email);
        Video video = serviceUtils.getVideoById(videoId);
        user.addToWatchlist(video);
        userRepository.save(user);
        return new MessageResponse("Video added to watchlist successfully");
    }

    @Override
    public MessageResponse removeFromWatchlist(String email, Long videoId) {
        User user = serviceUtils.getUserByEmail(email);
        Video video = serviceUtils.getVideoById(videoId);
        user.removeFromWatchlist(video);
        userRepository.save(user);
        return new MessageResponse("Video removed from watchlist successfully");
    }

    @Override
    public PageResponse<VideoResponse> getWatchlist(String email, int page, int size, String search) {
        User user = serviceUtils.getUserByEmail(email);
        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Video> videoPage;
        if (search != null && !search.isEmpty()) {
            videoPage = videoRepository.searchWatchlistByUserId(user.getId(), search.trim(), pageable);
        } else {
            videoPage = videoRepository.findWatchlistByUserId(user.getId(), pageable);
        }
        return PaginationUtils.toPageResponse(videoPage, VideoResponse::fromEntity);
    }
}
