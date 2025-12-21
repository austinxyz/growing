package com.growing.app.dto;

import com.growing.app.model.CareerPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String role;
    private Set<CareerPath> careerPaths;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
