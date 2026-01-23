package com.tv.movie.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class VideoRequest {

    @NotBlank(message = "REQUIRED_TITLE")
    private String title;

    @Size(max = 4000, message = "MAX_DESCRIPTION")
    private String description;

    private Integer year;
    private String rating;
    private Integer duration;
    private String src;
    private String poster;
    private Boolean published;
    private List<String> categories;

}
