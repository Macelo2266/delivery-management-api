package com.macelo.delivery.service;

import com.macelo.delivery.dto.request.UserRequest;
import com.macelo.delivery.dto.request.UserStatusRequest;
import com.macelo.delivery.dto.response.UserResponse;
import com.macelo.delivery.entity.User;
import com.macelo.delivery.exception.BusinessException;
import com.macelo.delivery.exception.ResourceNotFoundException;
import com.macelo.delivery.mapper.UserMapper;
import com.macelo.delivery.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }
        User user = userMapper.toEntity(request);
        return UserResponse.from(userRepository.save(user));
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(getUserOrThrow(id));
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = getUserOrThrow(id);

        boolean emailChanged = !user.getEmail().equalsIgnoreCase(request.getEmail());
        if (emailChanged && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }

        userMapper.updateEntity(user, request);
        return UserResponse.from(user); // gerenciado pelo JPA — @Transactional garante o flush ao final
    }

    @Transactional
    public UserResponse updateStatus(Long id, UserStatusRequest request) {
        User user = getUserOrThrow(id);
        user.setActive(request.getActive());
        return UserResponse.from(user);
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: id " + id));
    }
}
