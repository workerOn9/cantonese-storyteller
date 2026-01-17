/**
 * 声音模型数据仓库
 * 提供对VoiceModel实体的数据库操作
 * 
 * 功能：
 * - 按用户ID查询声音模型
 * - 按模型ID查询声音模型
 * - 继承JpaRepository提供标准CRUD操作
 */
package com.storyteller.repository;

import com.storyteller.entity.VoiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceModelRepository extends JpaRepository<VoiceModel, Long> {
    /**
     * 按用户ID查询声音模型列表
     * 
     * @param userId 用户ID
     * @return 该用户的所有声音模型
     */
    List<VoiceModel> findByUserId(Long userId);
    
    /**
     * 按模型ID查询声音模型
     * 
     * @param modelId 模型ID
     * @return 对应的声音模型
     */
    VoiceModel findByModelId(String modelId);
}