package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

/**
 * 病历返回对象
 */
@Data
public class MedicalRecordVo {

    /**
     * 流水号
     */
    public String admissionId;

    /**
     * 所属医院id
     */
    public String hospitalId;

    /**
     * 所属医院名称
     */
    public String hospitalName;

    /**
     * 病历阶段：终末
     */
    public String stage;

    /**
     * 病历理解时间
     */
    public String timeStamp;
}
