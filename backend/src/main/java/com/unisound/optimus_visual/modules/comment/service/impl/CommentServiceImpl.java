package com.unisound.optimus_visual.modules.comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unisound.optimus_visual.modules.comment.dao.UserMapper;
import com.unisound.optimus_visual.modules.comment.entity.Comment;
import com.unisound.optimus_visual.modules.comment.entity.User;
import com.unisound.optimus_visual.modules.comment.service.CommentService;
import com.unisound.optimus_visual.modules.comment.dao.CommentMapper;
import com.unisound.optimus_visual.utils.DataStructureUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserMapper userMapper;

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
        if (CollectionUtils.isEmpty(subCommentList)){
            //不包含子评论,直接删除即可
            commentMapper.deleteById(id);
        }else {
//            //删除直接子评论
//            Map<String,Object> deleteSubConditionMap = new LinkedHashMap<>();
//            deleteSubConditionMap.put("parent_id",id);
//            commentMapper.deleteByMap(deleteSubConditionMap);
//
//            //删除间接子评论
//            Map<String,Object> deleteRootCOnditionMap = new LinkedHashMap<>();
//            deleteRootCOnditionMap.put("root_parent_id",id);
//            commentMapper.deleteByMap(deleteRootCOnditionMap);

            //查询待删除评论信息
//            Comment comment = commentMapper.selectById(id);
//            List<Comment> allSubCommentsInOneComment = new ArrayList<>();
//            if (!CollectionUtils.isEmpty(comment.getChild())){
//                List<Comment> commentList = DataStructureUtils.processCommentTree(comment.getChild());
//                for (int i=0;i<commentList.size();i++){
//                    if (commentList.get(i).getId().equals(id)){
//                         allSubCommentsInOneComment = this.getAllSubCommentsInOneComment(commentList.get(i));
//                         break;
//                    }
//                }
//            }
            List<Comment> allSubCommentsInOneComment = new ArrayList<>();
            List<Comment> allSubComments = this.getAllSubComments(id, allSubCommentsInOneComment);
            List<Long> idList = allSubComments.stream().map(Comment::getId).distinct().collect(Collectors.toList());
            idList.add(id);
            log.info("删除的id有:{}",idList);
//            commentMapper.deleteBatchIds(idList);
            commentMapper.updateBatchById(idList);
        }
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
