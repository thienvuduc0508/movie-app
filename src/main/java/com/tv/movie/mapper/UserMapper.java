package com.tv.movie.mapper;

import com.tv.movie.dto.request.UserRequest;
import com.tv.movie.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequest userRequest);
}
