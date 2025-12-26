package com.growing.app.service;

import com.growing.app.dto.UserCaseNoteDTO;
import com.growing.app.model.SystemDesignCase;
import com.growing.app.model.User;
import com.growing.app.model.UserCaseNote;
import com.growing.app.repository.SystemDesignCaseRepository;
import com.growing.app.repository.UserCaseNoteRepository;
import com.growing.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserCaseNoteService {

    @Autowired
    private UserCaseNoteRepository noteRepository;

    @Autowired
    private SystemDesignCaseRepository caseRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户对某个案例的答题记录
     */
    public Optional<UserCaseNoteDTO> getUserNote(Long caseId, Long userId) {
        return noteRepository.findBySystemDesignCaseIdAndUserId(caseId, userId)
                .map(this::convertToDTO);
    }

    /**
     * 保存/更新用户答题记录（UPSERT逻辑）
     * 如果已存在则更新，否则创建新记录
     */
    @Transactional
    public UserCaseNoteDTO saveOrUpdateUserNote(Long caseId, Long userId, UserCaseNoteDTO dto) {
        SystemDesignCase designCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "案例不存在"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        // 查找是否已有记录（UPSERT）
        UserCaseNote note = noteRepository.findBySystemDesignCaseIdAndUserId(caseId, userId)
                .orElseGet(() -> {
                    UserCaseNote newNote = new UserCaseNote();
                    newNote.setSystemDesignCase(designCase);
                    newNote.setUser(user);
                    return newNote;
                });

        updateEntityFromDTO(note, dto);
        UserCaseNote saved = noteRepository.save(note);
        return convertToDTO(saved);
    }

    /**
     * 删除用户答题记录
     */
    @Transactional
    public void deleteUserNote(Long caseId, Long userId) {
        UserCaseNote note = noteRepository.findBySystemDesignCaseIdAndUserId(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "答题记录不存在"));

        // 验证用户只能删除自己的记录
        if (!note.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除他人的答题记录");
        }

        noteRepository.delete(note);
    }

    /**
     * 获取用户的学习总结（所有案例的要点汇总）
     */
    public List<Map<String, Object>> getUserSummary(Long userId) {
        // 获取用户的所有答题记录
        List<UserCaseNote> notes = noteRepository.findByUserId(userId);

        // 转换为汇总数据
        return notes.stream()
                .map(note -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("caseId", note.getSystemDesignCase().getId());
                    row.put("caseName", note.getSystemDesignCase().getTitle());
                    row.put("kpRequirement", note.getKpRequirement());
                    row.put("kpNfr", note.getKpNfr());
                    row.put("kpEntity", note.getKpEntity());
                    row.put("kpComponents", note.getKpComponents());
                    row.put("kpApi", note.getKpApi());
                    row.put("kpObject1", note.getKpObject1());
                    row.put("kpObject2", note.getKpObject2());
                    return row;
                })
                .collect(Collectors.toList());
    }

    // ==================== DTO转换方法 ====================

    private UserCaseNoteDTO convertToDTO(UserCaseNote entity) {
        UserCaseNoteDTO dto = new UserCaseNoteDTO();
        dto.setId(entity.getId());
        dto.setCaseId(entity.getSystemDesignCase().getId());
        dto.setUserId(entity.getUser().getId());
        // 结构化要点字段（Key Points）
        dto.setKpRequirement(entity.getKpRequirement());
        dto.setKpNfr(entity.getKpNfr());
        dto.setKpEntity(entity.getKpEntity());
        dto.setKpComponents(entity.getKpComponents());
        dto.setKpApi(entity.getKpApi());
        dto.setKpObject1(entity.getKpObject1());
        dto.setKpObject2(entity.getKpObject2());
        // 步骤字段
        dto.setStep1Requirements(entity.getStep1Requirements());
        dto.setStep2Entities(entity.getStep2Entities());
        dto.setStep3Api(entity.getStep3Api());
        dto.setStep4DataFlow(entity.getStep4DataFlow());
        dto.setStep5Architecture(entity.getStep5Architecture());
        dto.setStep6DeepDive(entity.getStep6DeepDive());
        dto.setArchitectureDiagramUrl(entity.getArchitectureDiagramUrl());
        dto.setKeyPoints(entity.getKeyPoints());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private void updateEntityFromDTO(UserCaseNote entity, UserCaseNoteDTO dto) {
        // 结构化要点字段（Key Points）
        entity.setKpRequirement(dto.getKpRequirement());
        entity.setKpNfr(dto.getKpNfr());
        entity.setKpEntity(dto.getKpEntity());
        entity.setKpComponents(dto.getKpComponents());
        entity.setKpApi(dto.getKpApi());
        entity.setKpObject1(dto.getKpObject1());
        entity.setKpObject2(dto.getKpObject2());
        // 步骤字段
        entity.setStep1Requirements(dto.getStep1Requirements());
        entity.setStep2Entities(dto.getStep2Entities());
        entity.setStep3Api(dto.getStep3Api());
        entity.setStep4DataFlow(dto.getStep4DataFlow());
        entity.setStep5Architecture(dto.getStep5Architecture());
        entity.setStep6DeepDive(dto.getStep6DeepDive());
        entity.setArchitectureDiagramUrl(dto.getArchitectureDiagramUrl());
        entity.setKeyPoints(dto.getKeyPoints());
    }
}
