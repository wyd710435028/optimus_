package com.unisound.optimus_visual.modules.comment.controller;

import com.unisound.optimus_visual.global.result.CommonResult;
import com.unisound.optimus_visual.modules.comment.entity.Comment;
import com.unisound.optimus_visual.modules.comment.entity.ResultComment;
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

    @RequestMapping("createNewRootComment")
    public CommonResult createNewRootComment(@RequestBody String param){
        Map<String,Object> result = commentService.createNewRootComment(param);
        return new CommonResult(result);
    }

    /**
     * 创建评论
     * @param param
     * @return
     */
    @RequestMapping("createNewResultComment")
    public CommonResult createNewResultComment(@RequestBody String param){
        Map<String,Object> result = commentService.createNewResultComment(param);
        return new CommonResult(result);
    }

    /**
     * 创建评论
     * @param param
     * @return
     */
    @RequestMapping("createNewOrderComment")
    public CommonResult createNewOrderComment(@RequestBody String param){
        Map<String,Object> result = commentService.createNewOrderComment(param);
        return new CommonResult(result);
    }

    /**
     * 获取评论历史列表
     * @return
     */
    @RequestMapping("/getCommentHistoryList")
    public CommonResult getCommentHistoryList(String keyWords,String fileId,String nodeName,String labelName) {
        List<ResultComment> commentList = commentService.getCommentHistoryList(keyWords, fileId, nodeName, labelName);
        return new CommonResult(commentList);
    }

    @RequestMapping("getOrderCommentHistoryList")
    public CommonResult getOrderCommentHistoryList(String fileId,String content,String executeTime,String executorSign) {
        List<ResultComment> commentList = commentService.getOrderCommentHistoryList(fileId,content,executeTime,executorSign);
        return new CommonResult(commentList);
    }
}
