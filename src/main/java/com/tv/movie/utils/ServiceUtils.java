package com.tv.movie.utils;

import com.tv.movie.dao.UserRepository;
import com.tv.movie.dao.VideoRepository;
import com.tv.movie.entity.User;
import com.tv.movie.entity.Video;
import com.tv.movie.exception.AppException;
import com.tv.movie.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceUtils {
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
    public Video getVideoById(Long id) {
        return videoRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_FOUND));
    }
}
