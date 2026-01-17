/**
 * 章节数据仓库
 * 提供对Chapter实体的数据库操作
 * 
 * 功能：
 * - 按章节ID查询章节
 * - 按标题模糊查询章节
 * - 继承JpaRepository提供标准CRUD操作
 */
package com.storyteller.repository;

import com.storyteller.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    /**
     * 按章节ID查询章节
     * 
     * @param chapterId 章节ID
     * @return 对应的章节
     */
    Chapter findByChapterId(Long chapterId);
    
    /**
     * 按标题模糊查询章节
     * 
     * @param title 标题关键词（支持模糊匹配）
     * @return 匹配的章节列表
     */
    List<Chapter> findByTitleContaining(String title);
}