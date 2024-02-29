package com.unisound.optimus_visual.modules.comment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unisound.optimus_visual.modules.comment.entity.ResultComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResultCommentMapper extends BaseMapper<ResultComment> {

    List<ResultComment> getCommentHistoryList(@Param("keyWords") String keyWords, @Param("fileId") String fileId, @Param("nodeName") String nodeName, @Param("labelName") String labelName);
}
