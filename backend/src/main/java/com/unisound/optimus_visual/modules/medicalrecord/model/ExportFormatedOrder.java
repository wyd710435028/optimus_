package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

/**
 * 导出医嘱格式(包含 临时/长期 医嘱)
 */
@Data
public class ExportFormatedOrder {

    //流水号
    private String admissionId;

    //医嘱类型: 临时/长期
    private String orderType;

    //医嘱id
    private String orderId;

    //医嘱内容
    private String orderContent;

    //云知声项目类别
    private String yzsProjectType;

    //项目大类
    private String projectCategories;

}
