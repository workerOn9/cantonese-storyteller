package com.storyteller.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.storyteller.service.VoiceService;

/**
 * 声音控制器
 * 处理与声音模型相关的HTTP请求
 * 
 * 功能：
 * - 训练粤语声音模型
 * - 获取用户的声音模型列表
 * - 处理音频文件上传
 */
@RestController
@RequestMapping("/voice")
public class VoiceController {
    
    @Autowired
    private VoiceService voiceService;
    
    /**
     * 训练声音模型
     * 
     * 接收用户上传的粤语音频文件，训练个性化的声音模型
     * 
     * @param userId 用户ID
     * @param dialect 方言类型（如"cantonese"）
     * @param audioFile 音频文件（支持WAV格式）
     * @return 训练完成的声音模型ID，或错误信息
     */
    @PostMapping("/train")
    public ResponseEntity<String> trainVoiceModel(
            @RequestParam("userId") Long userId,
            @RequestParam("dialect") String dialect,
            @RequestParam("audio") MultipartFile audioFile) {
        
        try {
            // 调用声音服务进行模型训练
            String modelId = voiceService.trainVoiceModel(userId, dialect, audioFile);
            return ResponseEntity.ok(modelId);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("处理音频文件失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("训练失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的声音模型列表
     * 
     * 查询指定用户的所有训练完成的声音模型
     * 
     * @param userId 用户ID
     * @return 用户的声音模型列表
     */
    @GetMapping("/models/{userId}")
    public ResponseEntity<? > getUserVoiceModels(@PathVariable Long userId) {
        return ResponseEntity.ok(voiceService.getUserVoiceModels(userId));
    }
}