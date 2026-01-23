package com.tv.movie.serviceImpl;

import com.tv.movie.dao.UserRepository;
import com.tv.movie.dto.request.UserRequest;
import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.dto.response.PageResponse;
import com.tv.movie.dto.response.UserResponse;
import com.tv.movie.entity.User;
import com.tv.movie.enums.Role;
import com.tv.movie.exception.AppException;
import com.tv.movie.exception.CustomMessageException;
import com.tv.movie.exception.ErrorCode;
import com.tv.movie.mapper.UserMapper;
import com.tv.movie.service.EmailService;
import com.tv.movie.service.UserService;
import com.tv.movie.utils.PaginationUtils;
import com.tv.movie.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ServiceUtils serviceUtils;
    private final EmailService emailService;

    @Override
    public MessageResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        validateRole(userRequest.getRole());

        User user = userMapper.toUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRepository.save(user);
        emailService.sendVerificationEmail(userRequest.getEmail(), user.getVerificationToken());
        return new MessageResponse("User created successfully!");
    }

    @Override
    public MessageResponse updateUser(Long id, UserRequest userRequest) {
        User user = serviceUtils.getUserById(id);
        ensureNotLastActiveAdm(user);
        validateRole(userRequest.getRole());
        user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
        user.setFullName(userRequest.getFullName());
        userRepository.save(user);
        return new MessageResponse("User updated successfully!");
    }

    @Override
    public PageResponse<UserResponse> getUsers(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "id");
        Page<User> userPage;
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.searchUsers(search.trim(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return PaginationUtils.toPageResponse(userPage, UserResponse::fromEntity);
    }

    @Override
    public MessageResponse deleteUser(Long id, String currentUserEmail) {
        User user = serviceUtils.getUserById(id);
        if (user.getEmail().equals(currentUserEmail)) {
            throw new AppException(ErrorCode.CANT_DELETE_OWN_ACCOUNT);
        }
        ensureNotLastAdm(user, "delete");
        userRepository.delete(user);
        return new MessageResponse("User deleted successfully!");
    }

    @Override
    public MessageResponse toggleStatus(Long id, String currentUserEmail) {
        User user = serviceUtils.getUserById(id);
        if (user.getEmail().equals(currentUserEmail)) {
            throw new CustomMessageException("You cannot deactivate your own account");
        }
        ensureNotLastActiveAdm(user);
        user.setActive(!user.isActive());
        userRepository.save(user);
        return new MessageResponse("User status updated successfully!");
    }

    @Override
    public MessageResponse changeRole(Long id, UserRequest userRequest) {
        User user = serviceUtils.getUserById(id);
        validateRole(userRequest.getRole());
        Role newRole = Role.valueOf(userRequest.getRole().toUpperCase());
        if(user.getRole() == Role.ADMIN && newRole == Role.USER) {
            ensureNotLastAdm(user, "change the role of");
        }
        user.setRole(newRole);
        userRepository.save(user);
        return new MessageResponse("User role updated successfully!");
    }

    private void ensureNotLastAdm(User user, String operation) {
        if (user.getRole() == Role.ADMIN) {
            long admAccount = userRepository.countByRole(Role.ADMIN);
            if (admAccount <= 1) {
                throw new CustomMessageException("You cannot " + operation + " the last admin account");
            }
        }

    }


    private void validateRole(String role) {
        if (Arrays.stream(Role.values()).noneMatch(r -> r.name().equalsIgnoreCase(role))) {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }
    }

    private void ensureNotLastActiveAdm(User user) {
        if (user.isActive() && user.getRole() == Role.ADMIN) {
            long activeAdmCount = userRepository.countByRoleAndActive(Role.ADMIN, true);
            if (activeAdmCount <= 1) {
                throw new AppException(ErrorCode.CANT_DEACTIVATE_LAST_ADMIN);
            }
        }
    }
}
