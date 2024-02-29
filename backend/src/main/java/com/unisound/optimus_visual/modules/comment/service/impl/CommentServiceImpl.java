package com.unisound.optimus_visual.modules.comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unisound.optimus_visual.modules.comment.dao.ResultCommentMapper;
import com.unisound.optimus_visual.modules.comment.dao.UserMapper;
import com.unisound.optimus_visual.modules.comment.entity.Comment;
import com.unisound.optimus_visual.modules.comment.entity.ResultComment;
import com.unisound.optimus_visual.modules.comment.entity.User;
import com.unisound.optimus_visual.modules.comment.service.CommentService;
import com.unisound.optimus_visual.modules.comment.dao.CommentMapper;
import com.unisound.optimus_visual.utils.DataStructureUtils;
import com.unisound.optimus_visual.utils.ParamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ResultCommentMapper resultCommentMapper;

    /**
     * 查询评论列表
     * @return
     */
    @Override
    public List<Comment> getCommentList() {
        List<Comment> result = new ArrayList<>();
        List<Comment> commentList = commentMapper.getCommonList();
        if (!CollectionUtils.isEmpty(commentList)){
            result = DataStructureUtils.processCommentTree(commentList);
            //转换result为二级树
            for (Comment comment:result){
                //定义子评论列表(最多二级)
                //todo 获取评论下的所有子评论
                List<Comment> allSubCommentsInOneComment = this.getAllSubCommentsInOneComment(comment);
                if (!CollectionUtils.isEmpty(allSubCommentsInOneComment)){
                    comment.setChild(allSubCommentsInOneComment);
                }
            }
        }
        return result;
    }

    /**
     * 保存评论
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> saveComment(String param) {
        Map<String,Object> result = new LinkedHashMap<>();
        if (StringUtils.isBlank(param)){
            return null;
        }
        JSONObject paramsJson = JSON.parseObject(param);
        JSONObject jsonObject = (JSONObject) paramsJson.get("params");
        Long parentId = jsonObject.getLong("parentId");
        Long userId = jsonObject.getLong("userId");
        Long rootParentId = jsonObject.getLong("rootParentId");
        String content = jsonObject.getString("content");
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setCreateTime(new Date());
        comment.setParentId(parentId);
        comment.setRootParentId(rootParentId);
        if (Objects.nonNull(userId)){
            User user = userMapper.selectById(userId);
            comment.setUserName(user.getUserName());
            comment.setAvatarUrl(user.getAvatarUrl());
        }
        int insert = commentMapper.insert(comment);
        if (insert>0){
            result.put("code","200");
        }
        return result;
    }

    @Override
    public Map<String, Object> deleteCommentById(String param) {
        Map<String,Object> result = new LinkedHashMap<>();
        if (StringUtils.isBlank(param)){
            return null;
        }
        JSONObject paramsJson = JSON.parseObject(param);
        JSONObject jsonObject = (JSONObject) paramsJson.get("params");
        Long id = jsonObject.getLong("id");
        //查看当前评论是否包含子评论
        List<Comment> subCommentList = commentMapper.getByParentId(id);
        List<Long> idList = new ArrayList<>();
        if (CollectionUtils.isEmpty(subCommentList)){
            //不包含子评论,直接删除即可
            idList.add(id);
        }else {
            List<Comment> allSubCommentsInOneComment = new ArrayList<>();
            List<Comment> allSubComments = this.getAllSubComments(id, allSubCommentsInOneComment);
            idList = allSubComments.stream().map(Comment::getId).distinct().collect(Collectors.toList());
            idList.add(id);
        }
        log.info("删除的id有:{}",idList);
        commentMapper.logicalDeleteBatchById(idList);
        return result;
    }

    /**
     * 创建根评论(新评论)
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> createNewRootComment(String param) {
        Map<String,Object> result = new LinkedHashMap<>();
        JSONObject jsonObject = ParamUtils.getCommonParams(param);
        Long userId = jsonObject.getLong("userId");
        String rootCommentContent = jsonObject.getString("rootCommentContent");
        if (Objects.isNull(userId)||StringUtils.isBlank(rootCommentContent)){
            return result;
        }
        Comment comment = new Comment();
        comment.setContent(rootCommentContent);
        comment.setUserId(userId);
        comment.setCreateTime(new Date());
        commentMapper.insert(comment);
        comment.setRootParentId(comment.getId());
        commentMapper.updateById(comment);
        return result;
    }

    /**
     * 创建新的理解结果评论
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> createNewResultComment(String param) {
        //todo 待修改
        Map<String,Object> result = new LinkedHashMap<>();
        JSONObject jsonObject = ParamUtils.getCommonParams(param);
        Long userId = jsonObject.getLong("userId");
        String rootCommentContent = jsonObject.getString("rootCommentContent");
        String keyWords = jsonObject.getString("keyWords");
        String fileId = jsonObject.getString("fileId");
        String nodeName = jsonObject.getString("nodeName");
        String labelName = jsonObject.getString("labelName");
        String docName = jsonObject.getString("docName");
        if (Objects.isNull(userId)||StringUtils.isBlank(rootCommentContent)){
            return result;
        }
        ResultComment resultComment = new ResultComment();
        resultComment.setKeyWords(keyWords);
        resultComment.setFileId(fileId);
        resultComment.setDocName(docName);
        resultComment.setNodeName(nodeName);
        resultComment.setLabelName(labelName);
        resultComment.setCreateTime(new Date());
        resultComment.setContent(rootCommentContent);
        resultComment.setUserId(userId);
        resultCommentMapper.insert(resultComment);
        return result;
    }

    @Override
    public List<ResultComment> getCommentHistoryList(String keyWords, String fileId, String nodeName, String labelName) {
        //定义返回变量
        List<ResultComment> result = new ArrayList<>();
        //参数校验
        if (StringUtils.isBlank(keyWords)||StringUtils.isBlank(fileId)||StringUtils.isBlank(nodeName)||StringUtils.isBlank(labelName)){
            return result;
        }
        result = resultCommentMapper.getCommentHistoryList(keyWords,fileId,nodeName,labelName);
        return result;
    }


    public List<Comment> getAllSubCommentsInOneComment(Comment comment){
        List<Comment> result = new ArrayList<>();
        if (comment == null){
            return result;
        }
//        if (CollectionUtils.isEmpty(comment.getChild())){
//            result.add(comment);
//            return result;
//        }
        if (!CollectionUtils.isEmpty(comment.getChild())){
            for (Comment subComment:comment.getChild()){
                //给parentName赋值
                Long parentId = subComment.getParentId();
                User user = userMapper.getByParentCommentId(parentId);
                if (Objects.nonNull(user)){
                    subComment.setParentName(user.getUserName());
                }
                result.add(subComment);
                result.addAll(getAllSubCommentsInOneComment(subComment));
            }
        }
        return result;
    }

    public List<Comment> getAllSubComments(Long id,List<Comment> result){
        if (id == null || id<=0){
            return result;
        }
        List<Comment> subComments = commentMapper.getByParentId(id);
        if (!CollectionUtils.isEmpty(subComments)){
            for (Comment subComment:subComments){
                result.add(subComment);
                id = subComment.getId();
                List<Comment> allSubComments = getAllSubComments(id, result);
                if (!result.containsAll(allSubComments)){
                    result.addAll(allSubComments);
                }
            }
        }
        return result;
    }
}
