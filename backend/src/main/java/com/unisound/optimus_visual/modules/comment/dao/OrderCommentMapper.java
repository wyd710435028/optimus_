package com.unisound.optimus_visual.modules.comment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unisound.optimus_visual.modules.comment.entity.OrderComment;
import com.unisound.optimus_visual.modules.comment.entity.ResultComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderCommentMapper extends BaseMapper<OrderComment> {
    List<ResultComment> getOrderCommentHistoryList(@Param("fileId") String fileId,@Param("content") String content,@Param("executeTime") String executeTime,@Param("executorSign") String executorSign);
}
