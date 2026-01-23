package com.tv.movie.controller;


import com.tv.movie.dto.request.UserRequest;
import com.tv.movie.dto.response.ApiResponse;
import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.dto.response.PageResponse;
import com.tv.movie.dto.response.UserResponse;
import com.tv.movie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ApiResponse<MessageResponse> createUser(@Valid @RequestBody UserRequest userRequest){
        return ApiResponse.<MessageResponse>builder()
                .result(userService.createUser(userRequest))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<MessageResponse> updateUser(@Valid @PathVariable Long id, @RequestBody UserRequest userRequest){
        return ApiResponse.<MessageResponse>builder()
                .result(userService.updateUser(id, userRequest))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(userService.getUsers(page, size, search))
                .build();
    }

    @PutMapping("/{id}/toggle-status")
    public ApiResponse<MessageResponse> toogleStatus(@PathVariable Long id, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        return ApiResponse.<MessageResponse>builder()
                .result(userService.toggleStatus(id, currentUserEmail))
                .build();
    }

    @PutMapping("/{id}/change-role")
    public ApiResponse<MessageResponse> changeRole(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        return ApiResponse.<MessageResponse>builder()
                .result(userService.changeRole(id, userRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<MessageResponse> deleteUser(@PathVariable Long id, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        return ApiResponse.<MessageResponse>builder()
                .result(userService.deleteUser(id, currentUserEmail))
                .build();
    }

}
