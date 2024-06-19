package com.unisound.optimus_visual.modules.medicalrecord.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * entity和span的统计model
 */
@Data
public class EntityOrSpanStatisticsModel {

    @ExcelProperty(value = "EMR号",index = 0)
    String emrNo;
    @ExcelProperty(value = "文书名称",index = 1)
    String docName;
    @ExcelProperty(value = "所在节点",index = 2)
    String nodeName;
    @ExcelProperty(value = "节点内容",index = 3)
    String nodeContent;
    @ExcelProperty(value = "span文本片段",index = 4)
    String spanTextContent;
    @ExcelProperty(value = "span标签",index = 5)
    String spanLabel;
    @ExcelProperty(value = "纠错标记",index = 6)
    Boolean isRemark;
}
