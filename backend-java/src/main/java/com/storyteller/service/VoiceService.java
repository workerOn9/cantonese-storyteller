package com.storyteller.service;

import com.storyteller.entity.VoiceModel;
import com.storyteller.repository.VoiceModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 声音服务
 * 处理声音模型训练和管理的核心业务逻辑
 * 
 * 功能：
 * - 验证音频文件
 * - 调用TTS服务训练声音模型
 * - 管理用户的声音模型
 */
@Service
public class VoiceService {
    
    @Autowired
    private VoiceModelRepository voiceModelRepository;
    
    @Autowired
    private TTSService ttsService;
    
    /**
     * 训练声音模型
     * 
     * 验证音频文件并调用TTS服务训练个性化的粤语声音模型
     * 
     * @param userId 用户ID
     * @param dialect 方言类型（如"cantonese"）
     * @param audioFile 音频文件
     * @return 训练完成的声音模型ID
     * @throws IOException 音频文件处理错误
     * @throws IllegalArgumentException 音频文件验证失败
     */
    public String trainVoiceModel(Long userId, String dialect, MultipartFile audioFile) throws IOException {
        // 验证音频文件
        if (audioFile.isEmpty()) {
            throw new IllegalArgumentException("音频文件为空");
        }
        
        if (!audioFile.getContentType().startsWith("audio/")) {
            throw new IllegalArgumentException("文件必须是音频文件");
        }
        
        // 对于MVP版本，我们模拟训练过程
        // 在生产环境中，这将调用实际的TTS服务
        String modelId = "cantonese_" + userId + "_" + System.currentTimeMillis();
        
        // 模拟调用TTS服务API
        String trainedModelId = ttsService.trainVoiceModel(userId, dialect, audioFile);
        
        // 将声音模型保存到数据库
        VoiceModel voiceModel = new VoiceModel();
        voiceModel.setUserId(userId);
        voiceModel.setModelId(trainedModelId);
        voiceModel.setDialect(dialect);
        voiceModel.setTrainedAt(LocalDateTime.now());
        voiceModel.setStatus("active");  // 设置模型状态为活跃
        
        voiceModelRepository.save(voiceModel);
        
        return trainedModelId;
    }
    
    /**
     * 获取用户的声音模型列表
     * 
     * 查询指定用户的所有训练完成的声音模型
     * 
     * @param userId 用户ID
     * @return 用户的声音模型列表
     */
    public List<VoiceModel> getUserVoiceModels(Long userId) {
        return voiceModelRepository.findByUserId(userId);
    }
}