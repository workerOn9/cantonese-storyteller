package com.storyteller.service;

import com.storyteller.entity.SynthesisTask;
import com.storyteller.repository.SynthesisTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 语音合成服务
 * 处理文本到语音合成的核心业务逻辑
 * 
 * 功能：
 * - 创建和管理语音合成任务
 * - 异步处理合成过程
 * - 通过WebSocket发送实时进度更新
 * - 处理合成成功和失败的情况
 */
@Service
public class SynthesisService {
    
    @Autowired
    private SynthesisTaskRepository synthesisTaskRepository;
    
    @Autowired
    private TTSService ttsService;
    
    @Autowired
    private WebSocketService webSocketService;
    
    /**
     * 创建语音合成任务
     * 
     * 接收用户的合成请求，创建新的合成任务并异步处理
     * 
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @param voiceModelId 声音模型ID
     * @param text 要合成的文本内容
     * @return 合成任务ID
     */
    public String createSynthesisTask(Long userId, Long chapterId, String voiceModelId, String text) {
        // 创建新的合成任务
        SynthesisTask task = createNewTask(userId, chapterId, voiceModelId);
        SynthesisTask savedTask = synthesisTaskRepository.save(task);
        
        // 异步处理合成任务
        CompletableFuture.runAsync(() -> processSynthesis(savedTask, text));
        
        return savedTask.getTaskId();
    }
    
    /**
     * 创建新的合成任务
     * 
     * 初始化合成任务的基本信息
     * 
     * @param userId 用户ID
     * @param chapterId 章节ID
     * @param voiceModelId 声音模型ID
     * @return 新的合成任务实体
     */
    private SynthesisTask createNewTask(Long userId, Long chapterId, String voiceModelId) {
        SynthesisTask task = new SynthesisTask();
        task.setUserId(userId);
        task.setChapterId(chapterId);
        task.setVoiceModelId(voiceModelId);
        task.setStatus("PENDING");  // 设置初始状态为待处理
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }
    
    /**
     * 处理语音合成
     * 
     * 异步执行语音合成过程，包括进度更新和结果处理
     * 
     * @param task 合成任务
     * @param text 要合成的文本
     */
    private void processSynthesis(SynthesisTask task, String text) {
        try {
            // 更新任务状态为处理中
            updateTaskStatus(task, "PROCESSING");
            webSocketService.sendProgress(task.getUserId(), task.getTaskId(), 25);
            
            // 模拟合成处理（MVP版本）
            simulateSynthesisProcessing();
            webSocketService.sendProgress(task.getUserId(), task.getTaskId(), 75);
            
            // 调用TTS服务进行语音合成
            String audioUrl = ttsService.synthesizeSpeech(text, task.getVoiceModelId());
            completeTask(task, audioUrl);
            
            // 发送完成通知
            webSocketService.sendProgress(task.getUserId(), task.getTaskId(), 100);
            webSocketService.sendCompletion(task.getUserId(), task.getTaskId(), audioUrl);
            
        } catch (Exception e) {
            handleTaskFailure(task, e);
        }
    }
    
    /**
     * 更新任务状态
     * 
     * @param task 合成任务
     * @param status 新状态
     */
    private void updateTaskStatus(SynthesisTask task, String status) {
        task.setStatus(status);
        synthesisTaskRepository.save(task);
    }
    
    /**
     * 模拟合成处理（MVP版本）
     * 
     * 在生产环境中，这里将调用实际的语音合成API
     * 
     * @throws InterruptedException 线程中断异常
     */
    private void simulateSynthesisProcessing() throws InterruptedException {
        Thread.sleep(2000);  // 模拟2秒处理时间
    }
    
    /**
     * 完成任务
     * 
     * 设置任务状态为完成，保存合成结果
     * 
     * @param task 合成任务
     * @param audioUrl 合成音频的URL
     */
    private void completeTask(SynthesisTask task, String audioUrl) {
        task.setStatus("COMPLETED");
        task.setAudioUrl(audioUrl);
        task.setCompletedAt(LocalDateTime.now());
        synthesisTaskRepository.save(task);
    }
    
    /**
     * 处理任务失败
     * 
     * 设置任务状态为失败，记录错误信息
     * 
     * @param task 合成任务
     * @param e 异常信息
     */
    private void handleTaskFailure(SynthesisTask task, Exception e) {
        task.setStatus("FAILED");
        task.setErrorMessage(e.getMessage());
        task.setCompletedAt(LocalDateTime.now());
        synthesisTaskRepository.save(task);
        webSocketService.sendError(task.getUserId(), task.getTaskId(), e.getMessage());
    }
    
    /**
     * 获取任务状态
     * 
     * @param taskId 任务ID
     * @return 合成任务信息
     */
    public SynthesisTask getTaskStatus(String taskId) {
        return synthesisTaskRepository.findByTaskId(taskId);
    }
}