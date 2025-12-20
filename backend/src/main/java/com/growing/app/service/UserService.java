package com.growing.app.service;

import com.growing.app.dto.CreateUserRequest;
import com.growing.app.dto.UpdateUserRequest;
import com.growing.app.dto.UserDTO;
import com.growing.app.model.CareerPath;
import com.growing.app.model.User;
import com.growing.app.repository.CareerPathRepository;
import com.growing.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final CareerPathRepository careerPathRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // TODO: Hash password properly (use BCrypt in production)
        user.setPasswordHash(request.getPassword());
        user.setFullName(request.getFullName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setBio(request.getBio());

        // Set career paths
        if (request.getCareerPathIds() != null && !request.getCareerPathIds().isEmpty()) {
            Set<CareerPath> careerPaths = new HashSet<>();
            for (Long careerPathId : request.getCareerPathIds()) {
                CareerPath careerPath = careerPathRepository.findById(careerPathId)
                    .orElseThrow(() -> new RuntimeException("Career path not found with id: " + careerPathId));
                careerPaths.add(careerPath);
            }
            user.setCareerPaths(careerPaths);
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        // Update career paths
        if (request.getCareerPathIds() != null) {
            Set<CareerPath> careerPaths = new HashSet<>();
            for (Long careerPathId : request.getCareerPathIds()) {
                CareerPath careerPath = careerPathRepository.findById(careerPathId)
                    .orElseThrow(() -> new RuntimeException("Career path not found with id: " + careerPathId));
                careerPaths.add(careerPath);
            }
            user.setCareerPaths(careerPaths);
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setCareerPaths(user.getCareerPaths());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
