package com.unisound.optimus_visual.modules.medicalrecord.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.unisound.optimus_visual.global.pagination.PageInfo;
import com.unisound.optimus_visual.modules.medicalrecord.model.Hospital;
import com.unisound.optimus_visual.modules.medicalrecord.model.MedicalRecordVo;
import com.unisound.optimus_visual.modules.medicalrecord.model.ShowDocModel;
import com.unisound.optimus_visual.modules.medicalrecord.model.ShowNodeModel;

import java.util.List;
import java.util.Map;

public interface MedicalRecordService {

    PageInfo<MedicalRecordVo> getMedicalRecordList(String hospitalId, String admissionId, Integer pageSize, Integer pageNum);

    Map<String, Object> getUnderstandResult(String hospitalId, String admissionId, String stage) throws JsonProcessingException;

    Map<String,Object> getNodeByFileId(String fileId);

    Map<String,Object> getNodeByFileIdWithHighLight(String params);

    PageInfo<ShowDocModel> docQueryList(String hospitalId, String admissionId,String stage, String docName, String emrNo,String tags,Integer pageSize, Integer pageNum);

    Map<String, Object> getDocContentDetail(String hospitalId, String admissionId, String stage, String fileId);

    String generateSpecificFormatJsonByEmrNo(String hospitalId, String scene,String emrNo, Integer size, String keysStr,String preCondition);

    String appendNodeContentByFile(String fileUrl);

    void combineTheLastTwoColumnsInTxt(String inputTxtUrl);

    Map<String, Object> hightTextByOneTag(String param);
}
