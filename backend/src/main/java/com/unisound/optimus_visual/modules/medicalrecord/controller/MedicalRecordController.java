package com.unisound.optimus_visual.modules.medicalrecord.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.unisound.optimus_visual.elasticsearch.dao.IndexDataFetcher;
import com.unisound.optimus_visual.global.pagination.PageInfo;
import com.unisound.optimus_visual.global.result.CommonResult;
import com.unisound.optimus_visual.modules.medicalrecord.model.*;
import com.unisound.optimus_visual.modules.medicalrecord.service.MedicalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.unisound.optimus_visual.base.ResourceLoad.globalHospitaiMap;

@CrossOrigin
@RestController
@RequestMapping("/medicalrecord")
@Slf4j
public class MedicalRecordController {

    @Autowired
    MedicalRecordService medicalRecordService;
    @Autowired
    IndexDataFetcher indexDataFetcher;

    /**
     * 获取医院信息
     * @return
     */
    @RequestMapping("/getHospitalList")
    public CommonResult getHospitalList() {
        List<Hospital> hospitals = indexDataFetcher.getHospitalsEntity();
        return new CommonResult(hospitals);
    }

    /**
     * 查询病历列表
     * @param hospitalId 查询条件:所属医院id
     * @param admissionId 查询条件:流水号
     * @param pageSize 每页容量
     * @param pageNum 页码
     * @return
     */
    @RequestMapping("getMedicalRecordList")
    public CommonResult getMedicalRecordList(@Param("hospitalId") String hospitalId,@Param("admissionId") String admissionId,@Param("pageSize")Integer pageSize,@Param("pageNum") Integer pageNum){
        log.info("查询的页码为{},查询条件为医院编号:{},流水号:{}",pageNum,hospitalId,admissionId);
        PageInfo<MedicalRecordVo> medicalRecordVoList = medicalRecordService.getMedicalRecordList(hospitalId,admissionId,pageSize,pageNum);
        log.info("查询病历列表结束...");
        return new CommonResult(medicalRecordVoList);
    }

    /**
     * 获取医院信息下拉框
     * @return
     */
    @RequestMapping("getHospitalDropDown")
    public CommonResult getHospitalDropDown(){
        List<Map<String,String>> hospitalList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(globalHospitaiMap)){
            for (String key:globalHospitaiMap.keySet()){
                String hospitalName = globalHospitaiMap.get(key);
                String show = null;
                if (StringUtils.isNotBlank(hospitalName)){
                     show = "["+key+"]:"+hospitalName;
                }
                LinkedHashMap<String,String> hospitalMap = new LinkedHashMap<>();
                hospitalMap.put("label",show);
                hospitalMap.put("value",key);
                hospitalList.add(hospitalMap);
            }
        }
        return new CommonResult(hospitalList);
    }

    /**
     * 获取病历理解结果
     * @param admissionId 流水号
     * @param hospitalId 医院id
     * @param stage 病历所属阶段
     * @return
     */
    @RequestMapping("getUnderstandResult")
    public CommonResult getUnderstandResult(String hospitalId,String admissionId,String stage) throws JsonProcessingException {
        //查询es中的病历理解数据
        Map<String,Object> result = medicalRecordService.getUnderstandResult(hospitalId,admissionId,stage);
        return new CommonResult(result);
    }

    @RequestMapping("getNodeByFileId")
    public CommonResult getNodeByFileId(@RequestBody String param){
        Map<String,Object> result = medicalRecordService.getNodeByFileId(param);
        return new CommonResult(result);
    }

    @RequestMapping("getNodeByFileIdWithHighLight")
    public CommonResult getNodeByFileIdWithHighLight(@RequestBody String param){
        Map<String,Object> result = medicalRecordService.getNodeByFileIdWithHighLight(param);
        return new CommonResult(result);
    }

    /**
     * 根据单个标签高亮一段文本
     * @param param
     * @return
     */
    @RequestMapping("hightTextByOneTag")
    public CommonResult hightTextByOneTag(@RequestBody String param){
        Map<String,Object> result = medicalRecordService.hightTextByOneTag(param);
        return new CommonResult(result);
    }

    @RequestMapping("transLabelList")
    public CommonResult transLabelList(@RequestBody String param){
        Map<String,Object> result = medicalRecordService.getNodeByFileId(param);
        return new CommonResult(result);
    }

    /**
     * 查询病历列表
     * @param hospitalId 查询条件:所属医院id
     * @param admissionId 查询条件:流水号
     * @param pageSize 每页容量
     * @param pageNum 页码
     * @return
     */
    @RequestMapping("docQueryList")
    public CommonResult docQueryList(String hospitalId,String admissionId,String stage,String docName,String emrNo,String tags,Integer pageSize,Integer pageNum){
        PageInfo<ShowDocModel> docLis = medicalRecordService.docQueryList(hospitalId,admissionId,stage,docName,emrNo,tags,pageSize,pageNum);
        return new CommonResult(docLis);
    }

    /**
     * 查询事件列表
     * @param hospitalId 查询条件:所属医院id
     * @param admissionId 查询条件:流水号
     * @param pageSize 每页容量
     * @param pageNum 页码
     * @return
     */
    @RequestMapping("eventQueryList")
    public CommonResult eventQueryList(String hospitalId,String admissionId,String stage,String eventName,Integer pageSize,Integer pageNum){
        PageInfo<ShowEventModelVo> docLis = medicalRecordService.eventQueryList(hospitalId,admissionId,stage,eventName,pageSize,pageNum);
        return new CommonResult(docLis);
    }

    /**
     * 获取文书内容详情
     * @param hospitalId 医院id
     * @param admissionId 流水号
     * @param stage 所属阶段
     * @param fileId 文件id
     * @return 文书内容详情
     */
    @RequestMapping("docContentDetail")
    public CommonResult docContentDetail(String hospitalId,String admissionId,String stage,String fileId){
        //查询es中的病历理解数据
        Map<String,Object> result = medicalRecordService.getDocContentDetail(hospitalId,admissionId,stage,fileId);
        return new CommonResult(result);
    }

    /**
     * 获取病历理解结果
     * @param admissionId 流水号
     * @param hospitalId 医院id
     * @param stage 病历所属阶段
     * @return
     */
    @RequestMapping("queryStatisticsData")
    public CommonResult queryStatisticsData(String hospitalId,String admissionId,String stage) throws JsonProcessingException {
        //查询es中的病历理解数据
        Map<String,Object> result = medicalRecordService.queryStatisticsData(hospitalId,admissionId,stage);
        return new CommonResult(result);
    }

    /**
     * 获取病历下的span信息详情
     * @param hospitalId 医院id
     * @param admissionId 流水号
     * @param stage 病历环环节
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping("getSpanListInMedicRecord")
    public CommonResult getSpanListInMedicRecord(String hospitalId,String admissionId,String stage,String docGroupName,Integer pageSize,Integer pageNum,String spanName) throws JsonProcessingException {
        //查询es中的病历理解数据
        Map<String,Object> result = medicalRecordService .getSpanListInMedicRecord(hospitalId,admissionId,stage,docGroupName,pageSize,pageNum,spanName);
        return new CommonResult(result);
    }

    /**
     * 生成指定格式的json(每一行表示一条json)
     * @param hospitalId 医院id
     * @param scene 病历阶段
     * @param emrNo emr号
     * @param size 生成条数
     * @param keysStr 包含的key
     * @param preCondition 前置条件
     * @return 生成的json结果(包含多条)
     */
    @RequestMapping("generateSpecificFormatJsonByEmrNo")
    public String generateSpecificFormatJsonByEmrNo(String hospitalId,String scene,String emrNo,Integer size,String keysStr,String preCondition){
        String result = medicalRecordService.generateSpecificFormatJsonByEmrNo(hospitalId,scene,emrNo,size,keysStr,preCondition);
        return result;
    }

    /**
     * 追加节点内容
     * @param fileUrl 文件路径
     * @return
     */
    @RequestMapping("appendNodeContentByFile")
    public String appendNodeContentByFile(String fileUrl){
        String result = medicalRecordService.appendNodeContentByFile(fileUrl);
        return result;
    }

    @RequestMapping("combineTheLastTwoColumnsInTxt")
    public void combineTheLastTwoColumnsInTxt (String inputTxtUrl){
        medicalRecordService.combineTheLastTwoColumnsInTxt(inputTxtUrl);
    }

}
