package com.unisound.optimus_visual.modules.medicalrecord.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ShowDocModelVo {
    /**
     * 文件id
     */
    String fileId;

    /**
     * 文件时间
     */
    String fileTime;

    /**
     * 文件名称
     */
    String fileName;

    /**
     * 文书类型(emr号)
     */
    String docType;

    /**
     * 操作时间
     */
    String operateTime;

    /**
     * tags
     */
    String tags;

    /**
     * 文书名称
     */
    String docName;

    /**
     *
     */
    String recordTime;

    String createTime;

    String docClassName;

    /**
     * 包含的节点
     */
    List<ShowNodeModel> nodeList;

    /**
     * 包含的标签
     */
    List<ShowLabelModel> labelList;

    /**
     * 临时医嘱列表
     */
    List<StatOrderModel> statOrderList;

    /**
     * 长期医嘱列表
     */
    List<StandingOrderModel> standingOrderList;

    /**
     * 关联文书id列表
     */
    List<Map<String,String>> relatedDocIds;

    /**
     * 关联文书列表
     */
    List<ShowDocModel> relatedDocs;
}
