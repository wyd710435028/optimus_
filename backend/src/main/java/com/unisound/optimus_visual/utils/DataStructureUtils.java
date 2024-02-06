package com.unisound.optimus_visual.utils;

import com.unisound.optimus_visual.modules.comment.entity.Comment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据结构工具
 */
public class DataStructureUtils {

    /**
     * 构建评论树
     * @param commentList 原始的评论数据(从Mysql查出的评论数据)
     * @return 带有层级结构的评论树
     */
    public static List<Comment> processCommentTree(List<Comment> commentList){
        List<Comment> result = new ArrayList<>();
        Map<Long,Comment> map = new LinkedHashMap<>();
        //根评论
        for (Comment comment:commentList){
            if (comment.getParentId()==null){
                result.add(comment);
            }
            map.put(comment.getId(),comment);
        }

        //子评论加入到父评论的child
        for (Comment comment:commentList){
            Long parentId = comment.getParentId();
            if (parentId!=null){
                //生成多层级结构
                Comment parentComment = map.get(parentId);
                if (parentComment==null){
                    return result;
                }
                if (parentComment.getChild()==null){
                    parentComment.setChild(new ArrayList<>());
                }
                parentComment.getChild().add(comment);
            }
        }
        return result;
    }
}
