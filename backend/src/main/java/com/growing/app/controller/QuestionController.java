package com.growing.app.controller;

import com.growing.app.dto.QuestionDTO;
import com.growing.app.dto.UserQuestionNoteDTO;
import com.growing.app.model.User;
import com.growing.app.repository.UserRepository;
import com.growing.app.service.AuthService;
import com.growing.app.service.QuestionService;
import com.growing.app.service.UserQuestionNoteService;
import com.growing.app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:3000")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserQuestionNoteService noteService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    /**
     * 获取Focus Area下的试题列表（公共 + 用户自己的）
     */
    @GetMapping("/focus-areas/{focusAreaId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByFocusArea(
            @PathVariable Long focusAreaId,
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        List<QuestionDTO> questions = questionService.getQuestionsByFocusAreaId(focusAreaId, user.getId());
        return ResponseEntity.ok(questions);
    }

    /**
     * 获取试题详情（包含答案要求、Red Flags和用户笔记）
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        QuestionDTO question = questionService.getQuestionByIdForUser(id, user.getId());
        return ResponseEntity.ok(question);
    }

    /**
     * 用户添加个人试题
     */
    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(
            @RequestBody QuestionDTO questionDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        QuestionDTO created = questionService.createQuestion(questionDTO, user.getId(), false);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新试题（只能更新自己创建的）
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable Long id,
            @RequestBody QuestionDTO questionDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        boolean isAdmin = authService.isAdminByToken(authHeader.replace("Bearer ", ""));
        QuestionDTO updated = questionService.updateQuestion(id, questionDTO, user.getId(), isAdmin);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除试题（只能删除自己创建的）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        boolean isAdmin = authService.isAdminByToken(authHeader.replace("Bearer ", ""));
        questionService.deleteQuestion(id, user.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取用户对某试题的笔记
     */
    @GetMapping("/{id}/note")
    public ResponseEntity<UserQuestionNoteDTO> getQuestionNote(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        UserQuestionNoteDTO note = noteService.getNoteByQuestionIdAndUserId(id, user.getId());
        return ResponseEntity.ok(note);
    }

    /**
     * 为试题添加笔记（如果已有笔记则更新）
     */
    @PostMapping("/{id}/note")
    public ResponseEntity<UserQuestionNoteDTO> saveOrUpdateNote(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        String noteContent = request.get("noteContent");
        if (noteContent == null || noteContent.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "笔记内容不能为空");
        }

        UserQuestionNoteDTO note = noteService.saveOrUpdateNote(id, noteContent, user.getId());

        // 判断是创建还是更新
        HttpStatus status = note.getCreatedAt().equals(note.getUpdatedAt())
            ? HttpStatus.CREATED
            : HttpStatus.OK;

        return ResponseEntity.status(status).body(note);
    }

    /**
     * 删除用户对某试题的笔记
     */
    @DeleteMapping("/{id}/note")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        noteService.deleteNote(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
