package com.tv.movie.mapper;

import com.tv.movie.dto.request.VideoRequest;
import com.tv.movie.entity.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VideoMapper {

    @Mapping(target = "srcUuid", source = "src")
    @Mapping(target = "posterUuid", source = "poster")
    Video toVideo(VideoRequest videoRequest);

    @Mapping(target = "id", ignore = true) // Không cập nhật ID
    @Mapping(target = "srcUuid", source = "src")
    @Mapping(target = "posterUuid", source = "poster")
    void updateVideoFromRequest(VideoRequest videoRequest, @MappingTarget Video video);
}
