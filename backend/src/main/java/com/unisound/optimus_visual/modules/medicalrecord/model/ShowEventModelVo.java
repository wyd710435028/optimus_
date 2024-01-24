package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

@Data
public class ShowEventModelVo {
    /**
     * id
     */
    String id;

    /**
     * 事件名称
     */
    String eventName;

    /**
     * 事件时间
     */
    String eventTime;

    /**
     * 事件唯一标识
     */
    String eventIdentity;

    /**
     * 文书map
     */
    String docMap;

    /**
     * 医嘱map
     */
    String orderMap;

    /**
     * 事件属性集合
     */
    String attributeMap;

    /**
     * 高亮信息
     */
    String highlightInfo;

    /**
     * allDocFileIdSet
     */
    String allDocFileIdSet;

    /**
     * 时间属性
     */
    String timeAttribute;

    /**
     * 唯一属性
     */
    String identityAttribute;

    /**
     * 总文本
     */
    String text;
}
