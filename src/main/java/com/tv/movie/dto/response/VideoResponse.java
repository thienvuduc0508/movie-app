package com.tv.movie.dto.response;

import com.tv.movie.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoResponse {
    private Long id;
    private String title;
    private String description;
    private Integer year;
    private String rating;
    private Integer duration;
    private String src;
    private String poster;
    private Boolean published;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> categories;
    private Boolean isInWatchlist;

    public static VideoResponse fromEntity(Video video) {
        return new VideoResponse(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getYear(),
                video.getRating(),
                video.getDuration(),
                video.getSrc(),
                video.getPoster(),
                video.isPublished(),
                video.getCreatedAt(),
                video.getUpdatedAt(),
                video.getCategories(),
                video.getIsInWatchlist()
        );
    }

}
