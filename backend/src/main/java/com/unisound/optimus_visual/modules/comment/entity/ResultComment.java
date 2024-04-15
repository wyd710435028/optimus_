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
* @TableName tb_result_comment
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_result_comment")
public class ResultComment implements Serializable {

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
     * 文书名称
     */
    private String docName;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 所属标签名称
     */
    private String labelName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date createTime;

    /**
     * 评论用户id
     */
    private Long userId;

    /**
     * 评论用户名称
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 关键词
     */
    private String keyWords;

}
