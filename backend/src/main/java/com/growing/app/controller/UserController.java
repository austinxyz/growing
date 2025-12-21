package com.growing.app.controller;

import com.growing.app.dto.CreateUserRequest;
import com.growing.app.dto.UpdateUserRequest;
import com.growing.app.dto.UserDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "用户管理API")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * 验证管理员权限
     */
    private void requireAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未授权：缺少认证信息");
        }
        String token = authHeader.substring(7);
        if (!authService.isAdminByToken(token)) {
            throw new RuntimeException("权限不足：需要管理员权限");
        }
    }

    @GetMapping
    @Operation(summary = "获取所有用户", description = "获取系统中所有用户列表（需要管理员权限）")
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        requireAdmin(authHeader);
        List<UserDTO> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("data", users);
        response.put("total", users.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "获取指定ID的用户详细信息")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "创建用户", description = "创建一个新用户（需要管理员权限）")
    public ResponseEntity<UserDTO> createUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CreateUserRequest request) {
        requireAdmin(authHeader);
        UserDTO user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新指定ID的用户信息（需要管理员权限）")
    public ResponseEntity<UserDTO> updateUser(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @PathVariable Long id,
        @RequestBody UpdateUserRequest request
    ) {
        requireAdmin(authHeader);
        UserDTO user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定ID的用户（需要管理员权限）")
    public ResponseEntity<Map<String, String>> deleteUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        requireAdmin(authHeader);
        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }
}
