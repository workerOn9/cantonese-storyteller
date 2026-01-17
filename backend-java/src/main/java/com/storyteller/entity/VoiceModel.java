package com.storyteller.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 声音模型实体类
 * 表示用户训练的声音模型信息，存储在PostgreSQL数据库中
 * 
 * 功能：
 * - 存储用户的声音模型元数据
 * - 跟踪模型训练时间和状态
 * - 支持多种方言（主要为粤语）
 */
@Entity
@Table(name = "voice_models")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceModel {
    
    /** 主键ID，自动生成 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 用户ID，关联到用户表 */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /** 声音模型唯一标识符 */
    @Column(name = "model_id", nullable = false, unique = true)
    private String modelId;
    
    /** 方言类型，默认为"cantonese"（粤语） */
    @Column(name = "dialect", nullable = false)
    private String dialect = "cantonese";
    
    /** 模型训练完成时间 */
    @Column(name = "trained_at", nullable = false)
    private LocalDateTime trainedAt;
    
    /** 模型状态，默认为"active"（活跃） */
    @Column(name = "status", nullable = false)
    private String status = "active";
    
    /**
     * 实体持久化前的回调方法
     * 自动设置训练时间为当前时间
     */
    @PrePersist
    protected void onCreate() {
        trainedAt = LocalDateTime.now();
    }
}