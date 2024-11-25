package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调用match接口返回的病历映射实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchMedicalRecordModel {

    String leaveTime;
    String clientId;
    Integer medVersion;
    String qcStatus;
    String admissionId;
    String optTime;
    String dataHospitalId;

    public MatchMedicalRecordModel(String admissionId, String dataHospitalId, String optTime,String leaveTime) {
        this.admissionId = admissionId;
        this.dataHospitalId = dataHospitalId;
        this.optTime = optTime;
        this.leaveTime = leaveTime;
    }
}
