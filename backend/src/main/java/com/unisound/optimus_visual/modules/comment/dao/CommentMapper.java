package com.unisound.optimus_visual.modules.comment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unisound.optimus_visual.modules.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询评论列表
     * @return
     */
    List<Comment> getCommonList();

    List<Comment> getByParentId(@Param("parentId") Long parentId);

    void updateBatchById(@Param("idList") List<Long> idList);
}
