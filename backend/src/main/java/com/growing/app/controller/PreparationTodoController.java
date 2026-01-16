package com.growing.app.controller;

import com.growing.app.dto.PreparationTodoDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.PreparationTodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PreparationTodoController {

    private final PreparationTodoService todoService;
    private final AuthService authService;

    public PreparationTodoController(PreparationTodoService todoService, AuthService authService) {
        this.todoService = todoService;
        this.authService = authService;
    }

    /**
     * 获取面试阶段的所有TODO（仅TODO，不含Checklist）
     * GET /api/interview-stages/{stageId}/todos
     */
    @GetMapping("/interview-stages/{stageId}/todos")
    public ResponseEntity<List<PreparationTodoDTO>> getTodosByStage(
            @PathVariable Long stageId,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        List<PreparationTodoDTO> todos = todoService.getTodosByStageId(stageId, userId);
        return ResponseEntity.ok(todos);
    }

    /**
     * 获取面试阶段的所有准备项（Checklist + TODO统一列表）
     * GET /api/interview-stages/{stageId}/todos-with-checklist
     */
    @GetMapping("/interview-stages/{stageId}/todos-with-checklist")
    public ResponseEntity<List<PreparationTodoDTO>> getTodosWithChecklist(
            @PathVariable Long stageId,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        List<PreparationTodoDTO> todos = todoService.getTodosWithChecklistByStageId(stageId, userId);
        return ResponseEntity.ok(todos);
    }

    /**
     * 创建TODO
     * POST /api/interview-stages/{stageId}/todos
     */
    @PostMapping("/interview-stages/{stageId}/todos")
    public ResponseEntity<PreparationTodoDTO> createTodo(
            @PathVariable Long stageId,
            @RequestBody PreparationTodoDTO dto,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        dto.setInterviewStageId(stageId);
        PreparationTodoDTO created = todoService.createTodo(dto, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * 更新TODO
     * PUT /api/todos/{id}
     */
    @PutMapping("/todos/{id}")
    public ResponseEntity<PreparationTodoDTO> updateTodo(
            @PathVariable Long id,
            @RequestBody PreparationTodoDTO dto,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        PreparationTodoDTO updated = todoService.updateTodo(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * 标记TODO完成/未完成
     * PATCH /api/todos/{id}/complete
     */
    @PatchMapping("/todos/{id}/complete")
    public ResponseEntity<PreparationTodoDTO> toggleComplete(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        Boolean isCompleted = (Boolean) request.get("isCompleted");
        String completionNotes = (String) request.get("completionNotes");

        PreparationTodoDTO updated = todoService.toggleComplete(id, isCompleted, completionNotes, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除TODO
     * DELETE /api/todos/{id}
     */
    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        todoService.deleteTodo(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量更新TODO顺序
     * PATCH /api/interview-stages/{stageId}/todos/reorder
     */
    @PatchMapping("/interview-stages/{stageId}/todos/reorder")
    public ResponseEntity<Void> reorderTodos(
            @PathVariable Long stageId,
            @RequestBody List<PreparationTodoService.ReorderRequest> reorderRequests,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        todoService.reorderTodos(stageId, reorderRequests, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 为checklist项创建详细TODO（展开checklist）
     * POST /api/checklist/{checklistId}/expand
     */
    @PostMapping("/checklist/{checklistId}/expand")
    public ResponseEntity<PreparationTodoDTO> expandChecklist(
            @PathVariable Long checklistId,
            @RequestBody PreparationTodoDTO dto,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        PreparationTodoDTO created = todoService.createTodoFromChecklist(checklistId, dto, userId);
        return ResponseEntity.ok(created);
    }
}
