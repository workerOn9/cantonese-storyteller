package com.storyteller.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 章节实体类
 * 表示评书的有声读物章节，存储章节文本和生成的音频信息
 * 
 * 功能：
 * - 存储章节内容和标题
 * - 跟踪音频合成状态
 * - 记录创建和更新时间
 */
@Entity
@Table(name = "chapters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    
    /** 主键ID，自动生成 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 章节ID，业务逻辑中的唯一标识符 */
    @Column(name = "chapter_id", nullable = false, unique = true)
    private Long chapterId;
    
    /** 章节标题 */
    @Column(name = "title", nullable = false)
    private String title;
    
    /** 章节文本内容（长文本） */
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;
    
    /** 合成音频的URL地址（如果有的话） */
    @Column(name = "audio_url")
    private String audioUrl;
    
    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /** 记录最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 实体持久化前的回调方法
     * 自动设置创建时间和初始更新时间
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    
    /**
     * 实体更新前的回调方法
     * 自动更新修改时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}