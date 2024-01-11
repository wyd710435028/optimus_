package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EntityOrSpanModel {
    /**
     * 实体名称
     */
    String name;
    Integer start;
    Integer end;

    /**
     * 标签
     */
    String label;

    /**
     * 总文本
     */
    String text;

    BigDecimal score;

}
