package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowLabelModel {
    /**
     * 标签内容
     */
    String labelContent;

    /**
     * 标签颜色
     */
    String labelColor;

    /**
     * 标签类型: entity,span,event
     */
    String labelType;

    /**
     * 包含的
     */
}
