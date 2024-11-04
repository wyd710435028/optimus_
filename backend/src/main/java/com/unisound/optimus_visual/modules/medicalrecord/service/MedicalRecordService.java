package com.unisound.optimus_visual.modules.medicalrecord.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.unisound.optimus_visual.global.pagination.PageInfo;
import com.unisound.optimus_visual.modules.medicalrecord.entity.SpanErrorMarked;
import com.unisound.optimus_visual.modules.medicalrecord.model.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    PageInfo<ShowEventModelVo> eventQueryList(String hospitalId, String admissionId, String stage, String docName, Integer pageSize, Integer pageNum);

    Map<String, Object> queryStatisticsData(String hospitalId, String admissionId, String stage);

    Map<String,Object> getSpanListInMedicRecord(String hospitalId, String admissionId, String stage,String docGroupName,Integer pageSize,Integer pageNum,String spanNam,Boolean paginationOrNot) throws UnsupportedEncodingException;

    Map<String, Object> exportSpanToXlsx(String hospitalId, String admissionId, String stage, String selectedDocGroupName, String spanName) throws IOException;

    Map<String, Object> markDoc(String param);

    Map<String, Object> addMarkedRemark(String param);

    Map<String, Object> getRemarkByFileId(String fileId,Integer pageSize,Integer pageNum);

    Map<String, Object> markSpanError(String param);

    Map<String, Object> cancelSpanMark(String param);

    PageInfo<SpanErrorMarked> queryMarkedSpanList(String conditionAdmissionId, Integer pageSize, Integer pageNum);

    Map<String, Object> deleteMarkedSpanById(Long id);

    Map<String, Object> addHospital(String param);

    List<ExportFormatedOrder> downLoadOrderByHospitalIdAndAdmissionIds(HttpServletResponse response,String hospitalId, String admissionIds, String stage) throws IOException;
}
