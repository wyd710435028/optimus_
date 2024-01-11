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
}
