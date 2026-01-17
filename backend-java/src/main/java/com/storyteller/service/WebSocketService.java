package com.storyteller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket服务
 * 处理实时进度更新和通知的WebSocket消息发送
 * 
 * 功能：
 * - 发送合成进度更新
 * - 发送任务完成通知
 * - 发送错误信息
 * - 支持按用户ID进行消息推送
 */
@Service
public class WebSocketService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 发送进度更新
     * 
     * 向指定用户发送合成任务的进度信息
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param progress 进度百分比（0-100）
     */
    public void sendProgress(Long userId, String taskId, int progress) {
        String destination = "/topic/progress/" + userId;
        ProgressMessage message = new ProgressMessage(taskId, progress, "PROCESSING");
        messagingTemplate.convertAndSend(destination, message);
    }
    
    /**
     * 发送完成通知
     * 
     * 向指定用户发送合成任务完成的消息
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param audioUrl 合成音频的URL地址
     */
    public void sendCompletion(Long userId, String taskId, String audioUrl) {
        String destination = "/topic/progress/" + userId;
        CompletionMessage message = new CompletionMessage(taskId, 100, "COMPLETED", audioUrl);
        messagingTemplate.convertAndSend(destination, message);
    }
    
    /**
     * 发送错误信息
     * 
     * 向指定用户发送合成任务失败的消息
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param errorMessage 错误信息
     */
    public void sendError(Long userId, String taskId, String errorMessage) {
        String destination = "/topic/progress/" + userId;
        ErrorMessage message = new ErrorMessage(taskId, 0, "FAILED", errorMessage);
        messagingTemplate.convertAndSend(destination, message);
    }
    
    // 消息类定义
    /**
     * 进度消息类
     * 用于发送合成进度更新
     */
    public static class ProgressMessage {
        /** 任务ID */
        private String taskId;
        /** 进度百分比 */
        private int progress;
        /** 任务状态 */
        private String status;
        
        public ProgressMessage(String taskId, int progress, String status) {
            this.taskId = taskId;
            this.progress = progress;
            this.status = status;
        }
        
        public String getTaskId() { return taskId; }
        public int getProgress() { return progress; }
        public String getStatus() { return status; }
    }
    
    /**
     * 完成消息类
     * 用于发送任务完成通知，包含音频URL
     */
    public static class CompletionMessage extends ProgressMessage {
        /** 合成音频的URL地址 */
        private String audioUrl;
        
        public CompletionMessage(String taskId, int progress, String status, String audioUrl) {
            super(taskId, progress, status);
            this.audioUrl = audioUrl;
        }
        
        public String getAudioUrl() { return audioUrl; }
    }
    
    /**
     * 错误消息类
     * 用于发送任务失败通知，包含错误信息
     */
    public static class ErrorMessage extends ProgressMessage {
        /** 错误信息 */
        private String errorMessage;
        
        public ErrorMessage(String taskId, int progress, String status, String errorMessage) {
            super(taskId, progress, status);
            this.errorMessage = errorMessage;
        }
        
        public String getErrorMessage() { return errorMessage; }
    }
}