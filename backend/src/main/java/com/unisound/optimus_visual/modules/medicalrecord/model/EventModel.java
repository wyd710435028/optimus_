package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

@Data
public class EventModel {
    /**
     * id
     */
    String id;

    /**
     * 事件名称
     */
    String eventName;

    /**
     * 事件唯一标识
     */
    String eventIdentity;

    /**
     * 事件属性集合
     */
    String attributeMap;

    /**
     * 总文本
     */
    String text;
}
