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

/**
* 
* @TableName tb_order_comment
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_order_comment")
public class OrderComment implements Serializable {

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
     * 流水号
     */
    private String admissionNo;

    /**
     * 医院编号
     */
    private String hospitalNo;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 医嘱内容
     */
    private String orderContent;

    /**
     * 医嘱执行时间
     */
    private String executeTime;

    /**
     * 执行人签名
     */
    private String executorSign;

    /**
     * 评论用户id
     */
    private Long userId;

    /**
     * 用户名称(不存在于表中)
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 评论创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date createTime;

    /**
     * 医嘱唯一标识 unisoundId
     */
    private String unisoundId;

    /**
     * 评论状态，开启/关闭->1 开启,0 关闭
     */
    private Integer orderCommentStatus;

}
