package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;


/**
 * 临时医嘱
 */
@Data
public class StatOrderModel {
    String day;
    String time;
    String content;
    String physicianSign;
    String  executeTime;
    String executorSign;
    String yzsProjectType;
    String projectCategories;
    /**
     * 医嘱id唯一标识
     */
    String unisoundId;

    /**
     * 评论数量
     */
    Integer commentNum=0;

    /**
     * 临时医嘱执行状态
     */
    String statOrderExecuteStatus;

    /**
     * 临时医嘱状态
     */
    String statOrderStatus;
}
