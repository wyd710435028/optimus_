package com.unisound.optimus_visual.modules.comment.controller;

import com.unisound.optimus_visual.global.result.CommonResult;
import com.unisound.optimus_visual.modules.comment.entity.Comment;
import com.unisound.optimus_visual.modules.comment.service.CommentService;
import com.unisound.optimus_visual.modules.medicalrecord.model.Hospital;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Autowired
    CommentService commentService;

    /**
     * 获取评论列表
     * @return
     */
    @RequestMapping("/getCommentList")
    public CommonResult getCommentList() {
        List<Comment> commentList = commentService.getCommentList();
        return new CommonResult(commentList);
    }

    @RequestMapping("saveComment")
    public CommonResult saveComment(@RequestBody String param){
        Map<String,Object> result = commentService.saveComment(param);
        return new CommonResult(result);
    }

    @RequestMapping("deleteCommentById")
    public CommonResult deleteCommentById(@RequestBody String param){
        Map<String,Object> result = commentService.deleteCommentById(param);
        return new CommonResult(result);
    }
}
