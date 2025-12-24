package com.growing.app.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.growing.app.dto.*;
import com.growing.app.model.User;
import com.growing.app.repository.UserRepository;
import com.growing.app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${google.client.id:85048175163-25v00bc8dflnpfq04hgsc5brls1fv4h2.apps.googleusercontent.com}")
    private String googleClientId;

    /**
     * 用户登录
     */
    public AuthResponse login(LoginRequest request) {
        // 查找用户（支持用户名或邮箱登录）
        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new RuntimeException("用户名或邮箱不存在"));

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("密码错误");
        }

        // 生成 JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // 返回认证响应
        UserDTO userDTO = userService.convertToDTO(user);
        return new AuthResponse(token, userDTO);
    }

    /**
     * 用户注册
     */
    @Transactional
    public AuthResponse register(CreateUserRequest request) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已被使用");
        }

        // 创建用户
        UserDTO userDTO = userService.createUser(request);

        // 获取完整的用户对象
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("用户创建失败"));

        // 生成 JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        return new AuthResponse(token, userDTO);
    }

    /**
     * Google 登录
     */
    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        try {
            // 验证 Google ID Token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getCredential());
            if (idToken == null) {
                throw new RuntimeException("无效的 Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // 查找或创建用户
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // 创建新用户（使用 Google 账号）
                        CreateUserRequest createRequest = new CreateUserRequest();
                        createRequest.setEmail(email);
                        // 从邮箱生成用户名（去掉 @domain）
                        String username = email.split("@")[0];
                        // 如果用户名已存在，添加随机后缀
                        String finalUsername = username;
                        int suffix = 1;
                        while (userRepository.findByUsername(finalUsername).isPresent()) {
                            finalUsername = username + suffix++;
                        }
                        createRequest.setUsername(finalUsername);
                        createRequest.setFullName(name);
                        // Google 登录不需要密码，设置随机密码
                        createRequest.setPassword(java.util.UUID.randomUUID().toString());

                        UserDTO newUserDTO = userService.createUser(createRequest);
                        return userRepository.findById(newUserDTO.getId())
                                .orElseThrow(() -> new RuntimeException("用户创建失败"));
                    });

            // 生成 JWT token
            Map<String, Object> claims = new HashMap<>();
            claims.put("provider", "google");
            String token = jwtUtil.generateToken(user.getUsername(), claims);

            UserDTO userDTO = userService.convertToDTO(user);
            return new AuthResponse(token, userDTO);

        } catch (Exception e) {
            throw new RuntimeException("Google 登录失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户是否为管理员
     */
    public boolean isAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return "admin".equals(user.getRole());
    }

    /**
     * 从 token 中检查用户是否为管理员
     */
    public boolean isAdminByToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        return isAdmin(username);
    }

    /**
     * 从 token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return user.getId();
    }

    /**
     * 验证 token 并获取用户信息
     */
    public UserDTO validateToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        if (!jwtUtil.validateToken(token, username)) {
            throw new RuntimeException("无效的 token");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return userService.convertToDTO(user);
    }

    /**
     * 从 token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    /**
     * 根据用户名获取用户ID
     */
    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return user.getId();
    }
}
