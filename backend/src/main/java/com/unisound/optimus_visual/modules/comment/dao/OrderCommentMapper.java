package com.unisound.optimus_visual.modules.comment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unisound.optimus_visual.modules.comment.entity.OrderComment;
import com.unisound.optimus_visual.modules.comment.entity.ResultComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderCommentMapper extends BaseMapper<OrderComment> {
    List<ResultComment> getOrderCommentHistoryList(@Param("fileId") String fileId,@Param("content") String content,@Param("executeTime") String executeTime,@Param("executorSign") String executorSign);

    List<OrderComment> getOrderCommentHistoryByUnisoundId(@Param("fileId") String fileId,@Param("unisoundId") String unisoundId);

    /**
     * 查找fileId下的所有开启状态的评论
     * @param fileId
     * @return
     */
    List<OrderComment> getByFileId(@Param("fileId") String fileId);

    Integer updateOrderCommentStatus(@Param("id") Integer id,@Param("orderCommentStatus") String orderCommentStatus);
}
