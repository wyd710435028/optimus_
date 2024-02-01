package com.unisound.optimus_visual.modules.comment.service;

import com.unisound.optimus_visual.modules.comment.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CommentService {
    List<Comment> getCommentList();
    Map<String, Object> saveComment(String param);
    Map<String, Object> deleteCommentById(String param);
    Map<String, Object> createNewRootComment(String param);
}
