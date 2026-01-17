/**
 * 语音合成任务数据仓库
 * 提供对SynthesisTask实体的数据库操作
 * 
 * 功能：
 * - 按任务ID查询合成任务
 * - 按用户ID查询合成任务列表
 * - 按状态查询合成任务列表
 * - 继承JpaRepository提供标准CRUD操作
 */
package com.storyteller.repository;

import com.storyteller.entity.SynthesisTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SynthesisTaskRepository extends JpaRepository<SynthesisTask, Long> {
    /**
     * 按任务ID查询合成任务
     * 
     * @param taskId 任务ID
     * @return 对应的合成任务
     */
    SynthesisTask findByTaskId(String taskId);
    
    /**
     * 按用户ID查询合成任务列表
     * 
     * @param userId 用户ID
     * @return 该用户的所有合成任务
     */
    List<SynthesisTask> findByUserId(Long userId);
    
    /**
     * 按状态查询合成任务列表
     * 
     * @param status 任务状态（如"PENDING", "PROCESSING", "COMPLETED", "FAILED"）
     * @return 对应状态的合成任务列表
     */
    List<SynthesisTask> findByStatus(String status);
}