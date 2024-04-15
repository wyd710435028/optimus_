package com.unisound.optimus_visual.modules.comment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* 
* @TableName comment
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_comment")
public class Comment implements Serializable {

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 评论内容
    */
    private String content;

    /**
    * 用户id
    */
    private Long userId;

    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String avatarUrl;

    /**
    * 评论人名称
    */
    @TableField(exist = false)
    private String userName;

    /**
    * 创建时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date createTime;

    /**
    * 是否删除:0->未删除,1->已删除
    */
    private Integer isDelete;

    /**
    * 所属id(针对系统某个功能/针对某个文章等)
    */
    private Long belongsId;

    /**
    * 父评论id
    */
    private Long parentId;

    /**
    * 根评论id
    */
    private Long rootParentId;

    /**
     * 本评论下的子评论
     */
    @TableField(exist = false)
    private List<Comment> child;

    /**
     * 是否显示replay按钮,0:不显示,1:显示
     */
    @TableField(exist = false)
    private boolean ifShowReplayInput = false;

    @TableField(exist = false)
    private String parentName;

}
