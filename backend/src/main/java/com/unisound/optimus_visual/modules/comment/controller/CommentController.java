package com.unisound.optimus_visual.modules.comment.controller;

import com.unisound.optimus_visual.global.result.CommonResult;
import com.unisound.optimus_visual.modules.comment.entity.Comment;
import com.unisound.optimus_visual.modules.comment.entity.OrderComment;
import com.unisound.optimus_visual.modules.comment.entity.ResultComment;
import com.unisound.optimus_visual.modules.comment.service.CommentService;
import com.unisound.optimus_visual.modules.medicalrecord.model.Hospital;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation("创建新的根评论")
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
    @ApiOperation("创建新的普通文书评论")
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
    @ApiOperation("创建新的医嘱评论")
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
    public CommonResult getOrderCommentHistoryList(String fileId,String unisoundId) {
        List<OrderComment> commentList = commentService.getOrderCommentHistoryList(fileId,unisoundId);
        return new CommonResult(commentList);
    }

    @ApiOperation("更新医嘱评论状态")
    @RequestMapping("updateOrderCommentStatus")
    public CommonResult updateOrderCommentStatus(@RequestBody String param) {
        Integer result = commentService.updateOrderCommentStatus(param);
        return new CommonResult(result);
    }
}
