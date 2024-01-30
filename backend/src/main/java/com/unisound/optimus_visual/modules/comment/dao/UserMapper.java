package com.unisound.optimus_visual.modules.comment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unisound.optimus_visual.modules.comment.entity.Comment;
import com.unisound.optimus_visual.modules.comment.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User getByParentCommentId(@Param("parentId") Long parentId);
}
