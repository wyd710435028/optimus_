package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

import java.util.List;

@Data
public class ShowNodeModel {
    String fileId;
    String nodeName;
    String nodeContent;
    Integer entityNum;
    Integer eventNum;
    Integer spanNum;
    /**
     * 包含的实体信息
     */
    List<EntityOrSpanModel> entityList;
    /**
     * 包含的事件信息
     */
    List<EventModel> eventList;
    /**
     * 包含的span信息
     */
    List<EntityOrSpanModel> spanList;

    /**
     * 实体包含的标签
     */
    List<ShowLabelModel> entityLabelList;

    /**
     * 事件包含的标签
     */
    List<ShowLabelModel> eventLabelList;

    /**
     * span包含的标签
     */
    List<ShowLabelModel> spanLabelList;

    /**
     * 高亮之后的entity文本
     */
    String entityHightLighted;

    /**
     * 高亮之后的span文本
     */
    String spanHightLighted;

    /**
     * 高亮之后的event文本
     */
    String eventHightLighted;

}
