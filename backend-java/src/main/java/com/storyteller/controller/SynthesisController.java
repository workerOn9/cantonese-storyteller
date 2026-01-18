package com.storyteller.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyteller.entity.SynthesisTask;
import com.storyteller.service.SynthesisService;

/**
 * 语音合成控制器
 * 处理文本到语音合成的HTTP请求
 * 
 * 功能：
 * - 创建语音合成任务
 * - 查询合成任务状态
 * - 处理粤语章节文本合成
 */
@RestController
@RequestMapping("/synthesis")
public class SynthesisController {
    
    @Autowired
    private SynthesisService synthesisService;
    
    /**
     * 请求语音合成
     * 
     * 接收用户的合成请求，创建新的语音合成任务
     * 
     * @param request 合成请求，包含用户ID、章节ID、声音模型ID和文本内容
     * @return 合成任务ID，或错误信息
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestSynthesis(@RequestBody SynthesisRequest request) {
        try {
            // 调用合成服务创建新的合成任务
            String taskId = synthesisService.createSynthesisTask(
                request.getUserId(),
                request.getChapterId(),
                request.getVoiceModelId(),
                request.getText()
            );
            return ResponseEntity.ok(taskId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("创建合成任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取任务状态
     * 
     * 查询指定合成任务的当前状态和结果
     * 
     * @param taskId 合成任务ID
     * @return 合成任务信息，或404未找到
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<SynthesisTask> getTaskStatus(@PathVariable String taskId) {
        SynthesisTask task = synthesisService.getTaskStatus(taskId);
        if (task != null) {
            return ResponseEntity.ok(task);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 合成请求内部类
     * 用于接收HTTP请求体的JSON数据
     */
    // 内部类，用于请求体
    public static class SynthesisRequest {
        /** 用户ID */
        private Long userId;
        /** 章节ID */
        private Long chapterId;
        /** 声音模型ID */
        private String voiceModelId;
        /** 要合成的文本内容 */
        private String text;
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getChapterId() { return chapterId; }
        public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
        
        public String getVoiceModelId() { return voiceModelId; }
        public void setVoiceModelId(String voiceModelId) { this.voiceModelId = voiceModelId; }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}