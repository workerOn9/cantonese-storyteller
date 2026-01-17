package com.storyteller.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 语音合成任务实体类
 * 表示将文本章节合成为语音的任务，跟踪合成进度和结果
 * 
 * 功能：
 * - 管理语音合成任务的生命周期
 * - 跟踪任务状态（待处理、进行中、完成、失败）
 * - 存储合成结果或错误信息
 * - 记录任务创建和完成时间
 */
@Entity
@Table(name = "synthesis_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SynthesisTask {
    
    /** 主键ID，自动生成 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 任务ID，业务逻辑中的唯一标识符 */
    @Column(name = "task_id", nullable = false, unique = true)
    private String taskId;
    
    /** 用户ID，关联到用户 */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /** 章节ID，要合成的章节 */
    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;
    
    /** 声音模型ID，用于合成的个性化声音 */
    @Column(name = "voice_model_id", nullable = false)
    private String voiceModelId;
    
    /** 任务状态，默认为"PENDING"（待处理） */
    @Column(name = "status", nullable = false)
    private String status = "PENDING";
    
    /** 合成音频的URL地址（任务成功时） */
    @Column(name = "audio_url")
    private String audioUrl;
    
    /** 错误信息（任务失败时） */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    /** 任务创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /** 任务完成时间 */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    /**
     * 实体持久化前的回调方法
     * 自动生成任务ID和创建时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        taskId = "task_" + System.currentTimeMillis();
    }
}