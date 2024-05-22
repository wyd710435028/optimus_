package com.unisound.optimus_visual.modules.medicalrecord.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_span_error_marked")
public class SpanErrorMarked {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 医院编号
     */
    private String hospitalId;

    /**
     * 流水号
     */
    private String admissionId;

    /**
     * emr号
     */
    private String emrNo;

    /**
     * 文书名称
     */
    private String docName;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * span文本片段内容
     */
    private String spanTextContent;

    /**
     * span标签
     */
    private String spanLabel;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
