package com.unisound.optimus_visual.modules.medicalrecord.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unisound.optimus_visual.base.ResourceLoad;
import com.unisound.optimus_visual.elasticsearch.dao.IndexDataFetcher;
import com.unisound.optimus_visual.elasticsearch.dao.PatientDataFetcher;
import com.unisound.optimus_visual.global.enums.emrNoConstant;
import com.unisound.optimus_visual.global.pagination.PageInfo;
import com.unisound.optimus_visual.modules.comment.dao.OrderCommentMapper;
import com.unisound.optimus_visual.modules.comment.entity.OrderComment;
import com.unisound.optimus_visual.modules.medicalrecord.model.*;
import com.unisound.optimus_visual.modules.medicalrecord.service.MedicalRecordService;
import com.unisound.optimus_visual.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Wrapper;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.unisound.optimus_visual.base.ResourceLoad.*;

@Slf4j
@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    @Autowired
    IndexDataFetcher indexDataFetcher;

    /**
     * 黑名单节点列表
     */
    @Value("${NodeShowBlackList:}")
    String nodeShowBlackList;

    /**
     * 黑名单适用的文书列表(默认为空:表示所有文书)
     */
    @Value("${UseBlackListOnDocList:}")
    String useBlackListOnDocList;

    @Autowired
    PatientDataFetcher patientDataFetcher;

    @Autowired
    OrderCommentMapper orderCommentMapper;

    /**
     * 查询病历列表
     * @param hospitalId 查询条件:所属医院id
     * @param admissionId 查询条件:流水号
     * @return 病历列表对象
     */
    @Override
    public PageInfo<MedicalRecordVo> getMedicalRecordList(String hospitalId, String admissionId, Integer pageSize, Integer pageNum) {
        Map<String,Object> resultMap = new LinkedHashMap<>();
        List<MedicalRecordVo> medicalRecordVoList = new ArrayList<>();
        //查询所有的医院
        List<Hospital> hospitalList = indexDataFetcher.getHospitalsEntity();
        if (!CollectionUtils.isEmpty(hospitalList)){
            //处理查询条件

            //既有流水号又有医院编号
            if (StringUtils.isNotBlank(hospitalId)&&StringUtils.isNotBlank(admissionId)){
                List<MedicalRecordVo> vosByAdId = new ArrayList<>();
                Integer total = 0;
                for (Hospital hospital:hospitalList){
                    if (hospital.getHospitalId().equals(hospitalId)){
                        resultMap = this.queryByAdmissionId(hospital, admissionId);
                        if (!CollectionUtils.isEmpty(resultMap)&&resultMap.containsKey("admissionIdList")){
                            List<MedicalRecordVo> voList = (List<MedicalRecordVo>) resultMap.get("admissionIdList");
                            Integer subTotal = (Integer) resultMap.get("total");
                            total+=subTotal;
                            vosByAdId.addAll(voList);
                        }
                    }
                }
                return pageInfo(vosByAdId,pageSize,pageNum,total);
            }

            //有医院编号查询条件,没有流水号查询条件
            if (StringUtils.isNotBlank(hospitalId)&&StringUtils.isBlank(admissionId)){
                List<MedicalRecordVo> vosByhoId = new ArrayList<>();
                for (Hospital hospital:hospitalList){
                    if (hospital.getHospitalId().equalsIgnoreCase(hospitalId)){
                        resultMap = queryByHospital(hospital,pageNum,pageSize);
                        break;
                    }
                }
                vosByhoId = (List<MedicalRecordVo>) resultMap.get("admissionIdList");
                Integer total = (Integer) resultMap.get("total");
                return pageInfo(vosByhoId,pageSize,pageNum,total);
            }

            //没有医院编号查询条件,只有流水号查询条件
            if (StringUtils.isBlank(hospitalId)&&StringUtils.isNotBlank(admissionId)){
                List<MedicalRecordVo> vosByAdId = new ArrayList<>();
                Integer total = 0;
                for (Hospital hospital:hospitalList){
                    resultMap = queryByAdmissionId(hospital,admissionId);
                    if (!CollectionUtils.isEmpty(resultMap)&&resultMap.containsKey("admissionIdList")){
                        List<MedicalRecordVo> voList = (List<MedicalRecordVo>) resultMap.get("admissionIdList");
                        Integer subTotal = (Integer) resultMap.get("total");
                        total+=subTotal;
                        vosByAdId.addAll(voList);
                    }
                }
                return pageInfo(vosByAdId,pageSize,pageNum,total);
            }
            //查询所有
            //查询第一个医院的
            Integer total = 0;
            Hospital hospital = hospitalList.get(1);
            resultMap = queryByHospital(hospital,pageNum,pageSize);
            if (!CollectionUtils.isEmpty(resultMap)){
                List<MedicalRecordVo> voList = (List<MedicalRecordVo>) resultMap.get("admissionIdList");
                //根据病历理解时间倒排
//                Collections.sort(voList,Comparator.comparing(MedicalRecordVo::getTimeStamp).reversed());
                Integer subTotal = (Integer) resultMap.get("total");
                total+=subTotal;
                if (!CollectionUtils.isEmpty(voList)){
                    medicalRecordVoList.addAll(voList);
                }
            }
            //分页处理
            return pageInfo(medicalRecordVoList,pageSize,pageNum,total);
        }else {
            return pageInfo(medicalRecordVoList,pageSize,pageNum,null);
        }
    }


    /**
     * 根据医院和流水号信息查询
     * @param hospital
     * @param admissionId
     * @return
     */
    private Map<String, Object> queryByAdmissionId(Hospital hospital, String admissionId) {
        Map<String,Object> result = new LinkedHashMap<>();
        List<MedicalRecordVo> medicalRecordVoList = new ArrayList<>();
        //获取医院id
        String id = hospital.getHospitalId();
        if (id.startsWith("%")){
            return null;
        }
        String index = id+"_"+hospital.getScene();
        Map<String,Object> admissionMap = patientDataFetcher.getByAdmissionId(index,admissionId);
        if (admissionMap.containsKey("admissionIdList")){
            List<String> admissionIdList = (List<String>) admissionMap.get("admissionIdList");
            if (!CollectionUtils.isEmpty(admissionIdList)){
                for (String s:admissionIdList){
                    if (globalHospitaiMap.containsKey(id)){
                        //定义病历对象
                        MedicalRecordVo medicalRecordVo = new MedicalRecordVo();
                        medicalRecordVo.setAdmissionId(s);
                        medicalRecordVo.setStage(hospital.getScene());
                        medicalRecordVo.setHospitalId(id);
                        medicalRecordVo.setHospitalName(globalHospitaiMap.get(id));
                        medicalRecordVoList.add(medicalRecordVo);
                    }
                }
            }
        }
        if (admissionMap.containsKey("total")){
            Integer total = Integer.parseInt(admissionMap.get("total").toString());
            result.put("total",total);
        }
        result.put("admissionIdList",medicalRecordVoList);
        return result;
    }

    /**
     * 获取病历理解结果
     * @param admissionId 流水号
     * @param hospitalId 医院id
     * @param stage 病历所属阶段
     * @return Map<String,Map<String,Object>>
     *     {
     *     fileId:"",
     *
 *         labelList:[
 *          {
     *          labelContent:"",
     *          labelColor:""
     *      }
 *         ],
 *         nodeList:[
 *          nodeName:"",
 *          nodeContent:"",
 *          entityNum:"",
 *          eventNum:"",
 *          spanNum:"",
 *          entityList:{
 *              name:"",
 *              start:"",
 *              end:"",
 *              label:"",
 *              text:""
 *          }
 *          eventLabelList:"",
 *          spanList:""
 *         ]
     *     }
     */
    @Override
    public Map<String, Object> getUnderstandResult(String hospitalId, String admissionId, String stage) {
        Map<String,Object> result = new LinkedHashMap<>();
        String understandResultStr = this.getUnderstandResultStr(hospitalId, admissionId, stage);
        if (StringUtils.isNotBlank(understandResultStr)){
            //格式化理解结果,便于前端展示
            result = this.formatResultStr(understandResultStr);
            if (StringUtils.isNotBlank(nodeShowBlackList)){
                //如果指定了使用黑名单的文书
                result = this.filterByBlackList(nodeShowBlackList,result,useBlackListOnDocList);
            }
        }
        return result;
    }

    /**
     * 获取理解结果字符串
     * @param hospitalId 医院id
     * @param admissionId 流水号
     * @param stage 病历阶段
     * @return 理解结果(字符串)
     */
    public String getUnderstandResultStr(String hospitalId, String admissionId, String stage){
        String esDoc = "optimus_data_"+hospitalId+"_"+(StringUtils.isBlank(stage)?"main":stage);
        //参考格式:http://10.128.3.122:9200/optimus_data_10046_main/_doc/609680
        String url = "http://10.128.3.122:9200/"+esDoc+"/_doc/"+admissionId;
        log.info("请求es的链接为{}",url);
        return HttpUtils.httpGet(url);
    }

    /**
     * 获取一个病历下的所有文书
     * @param hospitalId 医院id
     * @param admissionId 流水号
     * @param stage 病历阶段
     * @return
     */
    public List<Map<String,Object>> getAllDocsInOneRecord(String hospitalId, String admissionId, String stage){
        List<Map<String,Object>> docList = new ArrayList<>();
        //查询逻辑
        String understandResultStr = this.getUnderstandResultStr(hospitalId, admissionId, stage);
        if (StringUtils.isNotBlank(understandResultStr)) {
            Map<String, Object> parseMap = JSON.parseObject(understandResultStr, Map.class);
            if (!CollectionUtils.isEmpty(parseMap) && parseMap.containsKey("_source")) {
                Map<String, Object> sourceMap = (Map<String, Object>) parseMap.get("_source");
                if (!CollectionUtils.isEmpty(sourceMap) && sourceMap.containsKey("data")) {
                    Map<String, Object> dataMap = (Map) sourceMap.get("data");
                    if (!CollectionUtils.isEmpty(dataMap) && dataMap.containsKey("patient")) {
                        Map<String, Object> patientMap = (Map<String, Object>) dataMap.get("patient");
                        if (!CollectionUtils.isEmpty(patientMap) && patientMap.containsKey("medicalRecord")) {
                            Map<String, Object> medicalRecordMap = (Map<String, Object>) patientMap.get("medicalRecord");
                            if (!CollectionUtils.isEmpty(medicalRecordMap) && medicalRecordMap.containsKey("docs")) {
                                docList = (List<Map<String, Object>>) medicalRecordMap.get("docs");
                            }
                        }
                    }
                }
            }
        }
        return docList;
    }

    /**
     * 获取一个病历下的所有事件
     * @param hospitalId 医院id
     * @param admissionId 流水号
     * @param stage 病历阶段
     * @return
     */
    public Map<String,Object> getAllEventsInOneRecord(String hospitalId, String admissionId, String stage){
        Map<String,Object> resultMap = new LinkedHashMap<>();
        //查询逻辑
        String understandResultStr = this.getUnderstandResultStr(hospitalId, admissionId, stage);
        if (StringUtils.isNotBlank(understandResultStr)) {
            Map<String, Object> parseMap = JSON.parseObject(understandResultStr, Map.class);
            if (!CollectionUtils.isEmpty(parseMap) && parseMap.containsKey("_source")) {
                Map<String, Object> sourceMap = (Map<String, Object>) parseMap.get("_source");
                if (!CollectionUtils.isEmpty(sourceMap) && sourceMap.containsKey("data")) {
                    Map<String, Object> dataMap = (Map) sourceMap.get("data");
                    if (!CollectionUtils.isEmpty(dataMap) && dataMap.containsKey("patient")) {
                        Map<String, Object> patientMap = (Map<String, Object>) dataMap.get("patient");
                        if (!CollectionUtils.isEmpty(patientMap) && patientMap.containsKey("inHospitalInfo")) {
                            resultMap = (Map<String, Object>) patientMap.get("inHospitalInfo");
                        }
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * 根据黑名单配置筛选
     * @param nodeShowBlackList 不显示的节点名称
     * @param result
     * @param useBlackListOnDocList 黑名单作用在那些文书上（逗号分隔）
     * @return 筛选之后的结果
     */
    private Map<String, Object> filterByBlackList(String nodeShowBlackList, Map<String, Object> result, String useBlackListOnDocList) {
        List<ShowDocModel> docModelList = (List<ShowDocModel>) result.get("docList");
        if (!CollectionUtils.isEmpty(docModelList)){
            for (ShowDocModel model:docModelList){
                List<ShowNodeModel> nodeModelList = model.getNodeList();
                //如果指定了作用的文书
                if (StringUtils.isNotBlank(useBlackListOnDocList)){
                    if (useBlackListOnDocList.contains(model.getDocType())){
                        List<ShowNodeModel> nodeModels = removeNodeOnBlackList(nodeModelList,nodeShowBlackList);
                        model.setNodeList(nodeModels);
                    }
                }else {
                    //未指定文书,则处理所有文书
                    List<ShowNodeModel> nodeModels = removeNodeInAllDocOnBlackList(model,nodeShowBlackList);
                    model.setNodeList(nodeModels);
                }
            }
        }
        result.put("docList",docModelList);
        return result;
    }

    private List<ShowNodeModel> removeNodeInAllDocOnBlackList(ShowDocModel model, String nodeShowBlackList) {
        List<ShowNodeModel> nodeModelList = model.getNodeList();
        if (CollectionUtils.isEmpty(nodeModelList)||StringUtils.isBlank(nodeShowBlackList)){
            return nodeModelList;
        }
        //返回结果
        List<ShowNodeModel> showNodeModelList = new ArrayList<>();
        for (ShowNodeModel node:nodeModelList){
            if (!this.containsNodeNames(node.getNodeName(),nodeShowBlackList)){
                showNodeModelList.add(node);
            }
        }
        return showNodeModelList;
    }

    /**
     * 根据黑名单移除节点
     * @param nodeModelList 节点
     * @return
     */
    private List<ShowNodeModel> removeNodeOnBlackList(List<ShowNodeModel> nodeModelList,String nodeShowBlackList) {
        if (StringUtils.isBlank(nodeShowBlackList)){
            return nodeModelList;
        }
        List<ShowNodeModel> showNodeModelList = new ArrayList<>();
        for (ShowNodeModel node:nodeModelList){
            if (!this.containsNodeNames(node.getNodeName(),nodeShowBlackList)){
                showNodeModelList.add(node);
            }
        }
        return showNodeModelList;
    }

    private boolean containsNodeNames(String nodeName, String nodeShowBlackList) {
        String[] nodeNames = nodeShowBlackList.split(",");
        for (int i=0;i<nodeNames.length;i++){
            if (nodeName.contains(nodeNames[i])){
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String,Object> getNodeByFileId(String understandResultJson) {
        Map<String,Object> result = new LinkedHashMap<>();
        List<ShowNodeModel> nodeModelList = new ArrayList<>();
        List<ShowLabelModel> labelModelList = new ArrayList<>();
        List<StatOrderModel> statOrderModelList = new ArrayList<>();
        List<StandingOrderModel> standingOrderModelList = new ArrayList<>();
        if (Objects.nonNull(understandResultJson)){
            JSONObject jsonObject = JSON.parseObject(understandResultJson);
            if (Objects.nonNull(jsonObject)){
                String fileId = (String) jsonObject.get("fileId");
                String nodeListStr = jsonObject.getString("nodeList");
                List<ShowDocModel> docModelList = JSON.parseObject(nodeListStr, new TypeReference<List<ShowDocModel>>() {
                });
                //医嘱评论数量
                //todo 根据fileId和unisoundId分组查询评论数量放入一个Map：key-> fileId+unisoundId, value->数量
//                Map<String,Integer> orderCommentCountMap = orderCommentMapper.getCommentCount();
                List<OrderComment> commentList = orderCommentMapper.getByFileId(fileId);
                Map<String, Long> orderCommentCountMap = new LinkedHashMap<>();
                if (!CollectionUtils.isEmpty(commentList)){
                    orderCommentCountMap = commentList.stream().collect(Collectors.groupingBy(OrderComment::getUnisoundId, Collectors.counting()));
                }
                if (!CollectionUtils.isEmpty(docModelList)){
                    for (ShowDocModel docModel:docModelList){
                        if (fileId.equals(docModel.getFileId())){
                            if (docModel.getDocType().equals("EMR110001")){
                                //长期医嘱
                                if (!CollectionUtils.isEmpty(docModel.getStandingOrderList())){
                                    List<StandingOrderModel> standingOrderModels = docModel.getStandingOrderList();
                                    if (!CollectionUtils.isEmpty(standingOrderModels)){
                                        //统计每个医嘱的评论数量
                                        for (StandingOrderModel standingOrderModel:standingOrderModels){
                                            String unisoundId = standingOrderModel.getUnisoundId();
                                            if (orderCommentCountMap.containsKey(unisoundId)){
                                                Long integer = orderCommentCountMap.get(unisoundId);
                                                standingOrderModel.setCommentNum(Integer.parseInt(integer.toString()));
                                            }
                                        }
                                    }
                                    standingOrderModelList.addAll(standingOrderModels);
                                }
                            } else if (docModel.getDocType().equals("EMR110002")){
                                //临时医嘱
                                if (!CollectionUtils.isEmpty(docModel.getStatOrderList())){
                                    List<StatOrderModel> statOrderModels = docModel.getStatOrderList();
                                    if (!CollectionUtils.isEmpty(statOrderModels)){
                                        //统计每个医嘱的评论数量
                                        for (StatOrderModel statOrderModel:statOrderModels){
                                            String unisoundId = statOrderModel.getUnisoundId();
                                            if (orderCommentCountMap.containsKey(unisoundId)){
                                                Long integer = orderCommentCountMap.get(unisoundId);
                                                statOrderModel.setCommentNum(Integer.parseInt(integer.toString()));
                                            }
                                        }
                                    }
                                    statOrderModelList.addAll(statOrderModels);
                                }
                            }else {
                                nodeModelList =  docModel.getNodeList();
                                labelModelList = docModel.getLabelList();
                                //转换label为中文
                                ResourceLoad.convertLabelListContentToChinese(labelModelList);
                                //做高亮处理
                                //doc下的节点列表
                                for (ShowNodeModel nodeModel:nodeModelList){
                                    nodeModel.setFileId(fileId);
                                    List<EntityOrSpanModel> entityList = nodeModel.getEntityList();
                                    String nodeContent = nodeModel.getNodeContent();
                                    //entity高亮
//                                    String entityHighted = highLightText(nodeContent,entityList,labelModelList);
                                    String entityHighted = highLightText(nodeContent,entityList,labelModelList,fileId,nodeModel.getNodeName());
                                    nodeModel.setEntityHightLighted(entityHighted);
                                    //span高亮
                                    List<EntityOrSpanModel> spanList = nodeModel.getSpanList();
//                                    String spanHighted = highLightText(nodeContent, spanList, labelModelList);
                                    String spanHighted = highLightText(nodeContent,spanList,labelModelList,fileId,nodeModel.getNodeName());
                                    nodeModel.setSpanHightLighted(spanHighted);
                                    //事件处理
                                    List<EventModel> eventList = nodeModel.getEventList();
                                    nodeModel.setEventHightLighted(nodeContent);
                                }
                            }
                        }
                    }
                }
            }
        }
        List<Map<String,String>> yzsProjectTypeList = new ArrayList<>();
        List<Map<String,String>> projectCategoriesList = new ArrayList<>();
        List<String> yzsProjectTypes = new ArrayList<>();
        List<String> projectCategories = new ArrayList<>();
        if (!CollectionUtils.isEmpty(statOrderModelList)){
            //临时医嘱
            for (StatOrderModel statOrderModel:statOrderModelList){
                if (StringUtils.isNotBlank(statOrderModel.getYzsProjectType())){
                    if (!yzsProjectTypes.contains(statOrderModel.getYzsProjectType())){
                        yzsProjectTypes.add(statOrderModel.getYzsProjectType());
                    }
                }
                if (StringUtils.isNotBlank(statOrderModel.getProjectCategories())){
                    if (!projectCategories.contains(statOrderModel.getProjectCategories())){
                        projectCategories.add(statOrderModel.getProjectCategories());
                    }
                }
            }
        } else if (!CollectionUtils.isEmpty(standingOrderModelList)){
            //长期医嘱
            for (StandingOrderModel standingOrderModel:standingOrderModelList){
                if (StringUtils.isNotBlank(standingOrderModel.getYzsProjectType())){
                    if (!yzsProjectTypes.contains(standingOrderModel.getYzsProjectType())){
                        yzsProjectTypes.add(standingOrderModel.getYzsProjectType());
                    }
                }
                if (StringUtils.isNotBlank(standingOrderModel.getProjectCategories())){
                    if (!projectCategories.contains(standingOrderModel.getProjectCategories())){
                        projectCategories.add(standingOrderModel.getProjectCategories());
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(yzsProjectTypes)){
            for (String yzs:yzsProjectTypes){
                Map<String,String> yzsMap = new LinkedHashMap<>();
                yzsMap.put("text",yzs);
                yzsMap.put("value",yzs);
                yzsProjectTypeList.add(yzsMap);
            }
        }
        if (!CollectionUtils.isEmpty(projectCategories)){
            for (String pro:projectCategories){
                Map<String,String> map = new LinkedHashMap<>();
                map.put("text",pro);
                map.put("value",pro);
                projectCategoriesList.add(map);
            }
        }

        result.put("nodeList",nodeModelList);
        result.put("labelList",labelModelList);
        result.put("statOrderList",statOrderModelList);
        result.put("standingOrderList",standingOrderModelList);
        result.put("yzsProjectTypeList",yzsProjectTypeList);
        result.put("projectCategoriesList",projectCategoriesList);
        return result;
    }

    private String highLightText(String nodeContent, List<EntityOrSpanModel> entityList, List<ShowLabelModel> labelModelList, String fileId, String nodeName) {
        String result = "";
        if (StringUtils.isBlank(nodeContent)){
            return result;
        }
        if (CollectionUtils.isEmpty(entityList)||CollectionUtils.isEmpty(labelModelList)){
            return nodeContent;
        }else {
            //高亮逻辑
            Map<String,String> keyMap = new LinkedHashMap<>();
            for (EntityOrSpanModel entity:entityList){
                Map<String,String> colorMap = this.convertUnifyToMap(labelModelList);
                String entityName = entity.getName();
                String color = colorMap.get(entity.getLabel());
                keyMap.put(entityName,color);
            }
            String highlightedText = Highlighter.highlightKeywords(nodeContent,keyMap,fileId,nodeName);
            result =highlightedText;
        }
        return result;
    }

    @Override
    public Map<String,Object> getNodeByFileIdWithHighLight(String understandResultJson) {
        Map<String,Object> result = new LinkedHashMap<>();
        List<ShowNodeModel> nodeModelList = new ArrayList<>();
        List<StatOrderModel> statOrderModelList = new ArrayList<>();
        List<StandingOrderModel> standingOrderModelList = new ArrayList<>();
        List<ShowLabelModel> labelModelList = new ArrayList<>();
        if (Objects.nonNull(understandResultJson)){
            JSONObject jsonObject = JSON.parseObject(understandResultJson);
            if (Objects.nonNull(jsonObject)){
                String fileId = jsonObject.getString("fileId");
                String labelContent = jsonObject.getString("labelContent");
                String labelColor = jsonObject.getString("labelColor");

                String nodeListStr = jsonObject.getString("nodeList");
                List<ShowDocModel> docModelList = JSON.parseObject(nodeListStr, new TypeReference<List<ShowDocModel>>() {
                });
                if (!CollectionUtils.isEmpty(docModelList)){
                    for (ShowDocModel docModel:docModelList){
                        if (fileId.equals(docModel.getFileId())){
                            if (docModel.getDocType().equals("EMR110001")){
                                //长期医嘱
                                if (!CollectionUtils.isEmpty(docModel.getStandingOrderList())){
                                    standingOrderModelList.addAll(docModel.getStandingOrderList());
                                }
                            } else if (docModel.getDocType().equals("EMR110002")){
                                //临时医嘱
                                if (!CollectionUtils.isEmpty(docModel.getStatOrderList())){
                                    statOrderModelList.addAll(docModel.getStatOrderList());
                                }
                            }else {
                                nodeModelList =  docModel.getNodeList();
                                List<ShowLabelModel> hightLabelList = new ArrayList<>();
                                ShowLabelModel hightLabel = new ShowLabelModel(labelContent,labelColor,null,getLabelChineseName(labelContent));
                                hightLabelList.add(hightLabel);
                                labelModelList = docModel.getLabelList();
                                ResourceLoad.convertLabelListContentToChinese(labelModelList);
                                //做高亮处理
                                //doc下的节点列表
                                for (ShowNodeModel nodeModel:nodeModelList){
                                    nodeModel.setFileId(fileId);
                                    List<EntityOrSpanModel> entityList = nodeModel.getEntityList();
                                    String nodeContent = nodeModel.getNodeContent();
                                    //entity高亮
                                    String entityHighted = highLightText(nodeContent,entityList,hightLabelList,fileId,nodeModel.getNodeName());
                                    nodeModel.setEntityHightLighted(entityHighted);
                                    //span高亮
                                    List<EntityOrSpanModel> spanList = nodeModel.getSpanList();
                                    String spanHighted = highLightText(nodeContent, spanList, labelModelList,fileId,nodeModel.getNodeName());
                                    nodeModel.setSpanHightLighted(spanHighted);
                                    //事件处理
                                    List<EventModel> eventList = nodeModel.getEventList();
                                    nodeModel.setEventHightLighted(nodeContent);
                                }
                            }
                        }
                    }
                }
            }
        }
        List<Map<String,String>> yzsProjectTypeList = new ArrayList<>();
        List<Map<String,String>> projectCategoriesList = new ArrayList<>();
        List<String> yzsProjectTypes = new ArrayList<>();
        List<String> projectCategories = new ArrayList<>();
        if (!CollectionUtils.isEmpty(statOrderModelList)){
            //临时医嘱
            for (StatOrderModel statOrderModel:statOrderModelList){
                if (StringUtils.isNotBlank(statOrderModel.getYzsProjectType())){
                    if (!yzsProjectTypes.contains(statOrderModel.getYzsProjectType())){
                        yzsProjectTypes.add(statOrderModel.getYzsProjectType());
                    }
                }
                if (StringUtils.isNotBlank(statOrderModel.getProjectCategories())){
                    if (!projectCategories.contains(statOrderModel.getProjectCategories())){
                        projectCategories.add(statOrderModel.getProjectCategories());
                    }
                }
            }
        } else if (!CollectionUtils.isEmpty(standingOrderModelList)){
            //长期医嘱
            for (StandingOrderModel standingOrderModel:standingOrderModelList){
                if (StringUtils.isNotBlank(standingOrderModel.getYzsProjectType())){
                    if (!yzsProjectTypes.contains(standingOrderModel.getYzsProjectType())){
                        yzsProjectTypes.add(standingOrderModel.getYzsProjectType());
                    }
                }
                if (StringUtils.isNotBlank(standingOrderModel.getProjectCategories())){
                    if (!projectCategories.contains(standingOrderModel.getProjectCategories())){
                        projectCategories.add(standingOrderModel.getProjectCategories());
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(yzsProjectTypes)){
            for (String yzs:yzsProjectTypes){
                Map<String,String> yzsMap = new LinkedHashMap<>();
                yzsMap.put("text",yzs);
                yzsMap.put("value",yzs);
                yzsProjectTypeList.add(yzsMap);
            }
        }
        if (!CollectionUtils.isEmpty(projectCategories)){
            for (String pro:projectCategories){
                Map<String,String> map = new LinkedHashMap<>();
                map.put("text",pro);
                map.put("value",pro);
                projectCategoriesList.add(map);
            }
        }

        result.put("nodeList",nodeModelList);
        result.put("labelList",labelModelList);
        result.put("statOrderList",statOrderModelList);
        result.put("standingOrderList",standingOrderModelList);
        result.put("yzsProjectTypeList",yzsProjectTypeList);
        result.put("projectCategoriesList",projectCategoriesList);
        return result;
    }

    /**
     * 查询某一病历下的指定文书列表
     * @param hospitalId 医院id
     * @param admissionId 流水号id
     * @param docName 查询条件:文书名称
     * @param emrNo 查询条件: emr号
     * @param pageSize 每页容量
     * @param pageNum 页码
     * @return 病历列表
     */
    @Override
    public PageInfo<ShowDocModel> docQueryList(String hospitalId, String admissionId,String stage, String docName, String emrNo,String tags, Integer pageSize, Integer pageNum) {
        List<ShowDocModelVo> docModelList = new ArrayList<>();
        Map<String,ShowDocModel> docMap = new LinkedHashMap<>();
        List<Map<String, Object>> docList = this.getAllDocsInOneRecord(hospitalId, admissionId, stage);
        if (!docList.isEmpty()){
            for (Map<String,Object> doc:docList){
                if (doc.isEmpty()){
                    continue;
                }
                ShowDocModelVo docModel = new ShowDocModelVo();
                docModel.setFileId(doc.get("fileId")==null?"":doc.get("fileId").toString());
                docModel.setFileTime(doc.get("fileTime")==null?"":doc.get("fileTime").toString());
                docModel.setFileName(doc.get("fileName")==null?"":doc.get("fileName").toString());
                docModel.setDocType(doc.get("docType")==null?"":doc.get("docType").toString());
                docModel.setOperateTime(doc.get("operateTime")==null?"":doc.get("operateTime").toString());
                docModel.setTags(doc.get("tags")==null?"":doc.get("tags").toString());
                docModel.setDocName(doc.get("docName")==null?"":doc.get("docName").toString());
                docModel.setRecordTime(doc.get("recordTime")==null?"":doc.get("recordTime").toString());
                docModel.setCreateTime(doc.get("createTime")==null?"":doc.get("createTime").toString());
                docModel.setDocClassName(doc.get("docClassName")==null?"":doc.get("docClassName").toString());
                JSONObject jsonObject = doc.get("relatedDocIds")==null?new JSONObject():(JSONObject) doc.get("relatedDocIds");
                if (!jsonObject.isEmpty()&&jsonObject.size()>0){
                    List<Map<String,String>> fileIdList = new ArrayList<>();
                    for (String key:jsonObject.keySet()){
                        JSONArray jsonArray = (JSONArray) jsonObject.get(key);
                        if (!jsonArray.isEmpty()){
                            for (int i = 0;i<jsonArray.size();i++){
                                Map<String,String> map = new LinkedHashMap<>();
                                String fId = (String) jsonArray.get(i);
                                map.put("relateDocDesc",key);
                                map.put("relateDocId",fId);
                                fileIdList.add(map);
                            }
                        }
                    }
                    docModel.setRelatedDocIds(fileIdList);
                }
                docModelList.add(docModel);
                ShowDocModel showDocModel = new ShowDocModel();
                BeanUtils.copyProperties(docModel,showDocModel);
                docMap.put(docModel.getFileId(),showDocModel);
            }
        }
        //处理关联文书
        for (ShowDocModelVo model:docModelList){
            List<Map<String,String>> relatedDocIds = model.getRelatedDocIds();
            if (relatedDocIds!=null&&!relatedDocIds.isEmpty()){
                List<ShowDocModel> modelList = new ArrayList<>();
                for (Map<String,String> docIdMap:relatedDocIds){
                    if (!docIdMap.isEmpty()){
                        String relateDocId = docIdMap.get("relateDocId");
                        ShowDocModel showDocModel = new ShowDocModel();
                        ShowDocModel referDoc = docMap.get(relateDocId);
                        BeanUtils.copyProperties(referDoc,showDocModel);
                        showDocModel.setFileDesc(docIdMap.get("relateDocDesc"));
                        modelList.add(showDocModel);
                    }
                }
                model.setRelatedDocs(modelList);
            }
        }

        //处理搜索条件
        if (StringUtils.isNotBlank(emrNo)){
            //emr号模糊搜索
            docModelList = docModelList.stream().filter(e->e.getDocType().contains(emrNo)).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(docName)){
            //文书名称模糊搜索
            docModelList = docModelList.stream().filter(d->d.getDocName().contains(docName)||d.getDocClassName().contains(docName)).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(tags)){
            //Tags模糊搜索
            docModelList = docModelList.stream().filter(d->d.getTags().contains(tags)).collect(Collectors.toList());
        }
        //分页
        if (pageSize==null||pageSize<=0){
            pageSize=10;
        }
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        List<ShowDocModelVo> collect = docModelList.stream().skip(pageSize * (pageNum-1)).limit(pageSize).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setRecords(collect);
        pageInfo.setTotal(docModelList.size());
        pageInfo.setCurrent(pageNum==null?1:pageNum);
        pageInfo.setSize(pageSize==null?10:pageSize);
        return pageInfo;
    }

    /**
     * 获取指定文书内容
     * @param admissionId 流水号
     * @param hospitalId 医院id
     * @param stage 病历所属阶段
     * @param fileId 文书id
     * @return 文书内容详情
     */
    @Override
    public Map<String, Object> getDocContentDetail(String hospitalId, String admissionId, String stage, String fileId) {
        Map<String,Object> result = new LinkedHashMap<>();
        if (StringUtils.isBlank(fileId)){
            return result;
        }
        List<Map<String, Object>> allDocsInOneRecord = this.getAllDocsInOneRecord(hospitalId, admissionId, stage);
        if (!allDocsInOneRecord.isEmpty()&&allDocsInOneRecord.size()>0){
            for (Map<String,Object> doc:allDocsInOneRecord){
                String docFileId = doc.get("fileId")==null?"":doc.get("fileId").toString();
                if (StringUtils.isNotBlank(docFileId)){
                    if (fileId.equals(docFileId)){
                        //判断是普通文书还是医嘱
                        String docType = doc.get("docType") == null ? "" : doc.get("docType").toString();
                        if(emrNoConstant.standingOrderEmrNo.equals(docType)){
                            //todo 长期医嘱
                            Map<String,Map<String,Map<String,Object>>> formatedDocMap = this.formatDocList(allDocsInOneRecord);
                            List<ShowLabelModel> unifyLabels = this.generateUnifyLabel(formatedDocMap);
                            List<ShowDocModel> resultDocList = this.convertDocMapToModelList(formatedDocMap,unifyLabels);
                            //处理医嘱
                            if (!CollectionUtils.isEmpty(resultDocList)){
                                resultDocList = formateOrder(resultDocList);
                            }
                            List<StandingOrderModel> standingOrderModelList = new ArrayList<>();
                            for (ShowDocModel docModel:resultDocList){
                                if (docModel.getDocType().equalsIgnoreCase(emrNoConstant.standingOrderEmrNo)){
                                    standingOrderModelList.addAll(docModel.getStandingOrderList());
                                }
                            }
                            List<Map<String,String>> yzsProjectTypeList = new ArrayList<>();
                            List<Map<String,String>> projectCategoriesList = new ArrayList<>();
                            List<String> yzsProjectTypes = new ArrayList<>();
                            List<String> projectCategories = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(standingOrderModelList)){
                                //临时医嘱
                                for (StandingOrderModel standingOrderModel:standingOrderModelList){
                                    if (StringUtils.isNotBlank(standingOrderModel.getYzsProjectType())){
                                        if (!yzsProjectTypes.contains(standingOrderModel.getYzsProjectType())){
                                            yzsProjectTypes.add(standingOrderModel.getYzsProjectType());
                                        }
                                    }
                                    if (StringUtils.isNotBlank(standingOrderModel.getProjectCategories())){
                                        if (!projectCategories.contains(standingOrderModel.getProjectCategories())){
                                            projectCategories.add(standingOrderModel.getProjectCategories());
                                        }
                                    }
                                }
                            }

                            if (!CollectionUtils.isEmpty(yzsProjectTypes)){
                                for (String yzs:yzsProjectTypes){
                                    Map<String,String> yzsMap = new LinkedHashMap<>();
                                    yzsMap.put("text",yzs);
                                    yzsMap.put("value",yzs);
                                    yzsProjectTypeList.add(yzsMap);
                                }
                            }
                            if (!CollectionUtils.isEmpty(projectCategories)){
                                for (String pro:projectCategories){
                                    Map<String,String> map = new LinkedHashMap<>();
                                    map.put("text",pro);
                                    map.put("value",pro);
                                    projectCategoriesList.add(map);
                                }
                            }
                            result.put("standingOrderList",standingOrderModelList);
                            result.put("yzsProjectTypeList",yzsProjectTypeList);
                            result.put("projectCategoriesList",projectCategoriesList);
                        } else if (emrNoConstant.statOrderEmrNo.equals(docType)){
                            //todo 临时医嘱
                            Map<String,Map<String,Map<String,Object>>> formatedDocMap = this.formatDocList(allDocsInOneRecord);
                            List<ShowLabelModel> unifyLabels = this.generateUnifyLabel(formatedDocMap);
                            List<ShowDocModel> resultDocList = this.convertDocMapToModelList(formatedDocMap,unifyLabels);
                            //处理医嘱
                            if (!CollectionUtils.isEmpty(resultDocList)){
                                resultDocList = formateOrder(resultDocList);
                            }
                            List<StatOrderModel> statOrderList = new ArrayList<>();
                            for (ShowDocModel docModel:resultDocList){
                                if (docModel.getDocType().equalsIgnoreCase(emrNoConstant.statOrderEmrNo)){
                                    statOrderList.addAll(docModel.getStatOrderList());
                                }
                            }
                            List<Map<String,String>> yzsProjectTypeList = new ArrayList<>();
                            List<Map<String,String>> projectCategoriesList = new ArrayList<>();
                            List<String> yzsProjectTypes = new ArrayList<>();
                            List<String> projectCategories = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(statOrderList)){
                                //临时医嘱
                                for (StatOrderModel statOrderModel:statOrderList){
                                    if (StringUtils.isNotBlank(statOrderModel.getYzsProjectType())){
                                        if (!yzsProjectTypes.contains(statOrderModel.getYzsProjectType())){
                                            yzsProjectTypes.add(statOrderModel.getYzsProjectType());
                                        }
                                    }
                                    if (StringUtils.isNotBlank(statOrderModel.getProjectCategories())){
                                        if (!projectCategories.contains(statOrderModel.getProjectCategories())){
                                            projectCategories.add(statOrderModel.getProjectCategories());
                                        }
                                    }
                                }
                            }

                            if (!CollectionUtils.isEmpty(yzsProjectTypes)){
                                for (String yzs:yzsProjectTypes){
                                    Map<String,String> yzsMap = new LinkedHashMap<>();
                                    yzsMap.put("text",yzs);
                                    yzsMap.put("value",yzs);
                                    yzsProjectTypeList.add(yzsMap);
                                }
                            }
                            if (!CollectionUtils.isEmpty(projectCategories)){
                                for (String pro:projectCategories){
                                    Map<String,String> map = new LinkedHashMap<>();
                                    map.put("text",pro);
                                    map.put("value",pro);
                                    projectCategoriesList.add(map);
                                }
                            }
                            result.put("statOrderList",statOrderList);
                            result.put("yzsProjectTypeList",yzsProjectTypeList);
                            result.put("projectCategoriesList",projectCategoriesList);
                        } else {
                            //普通文书
                            //获取文书下的所有节点
                            List<ShowNodeModel> nodes = this.getAllNodesInDoc(doc);
                            result.put("nodeList",nodes);
                            result.put("fileName",doc.get("fileName")==null?"":doc.get("fileName"));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 生成指定格式的json(每一行表示一条json)
     * @param hospitalId 医院id
     * @param scene 病历阶段
     * @param emrNo emr号
     * @param size 生成条数
     * @param keysStr 包含的key
     * @return 生成的json结果(包含多条)
     */
    @Override
    public String generateSpecificFormatJsonByEmrNo(String hospitalId, String scene, String emrNo, Integer size, String keysStr,String preCondition) {
        Hospital hospital = new Hospital();
        hospital.setHospitalId(hospitalId);
        hospital.setScene(scene);
        //查询流水号
        Map<String, Object> map = queryByHospital(hospital, 1, size);
        StringBuffer sb = new StringBuffer();
//        String medicalJSONStr = HttpUtils.httpGet("http://10.128.1.217" + ":" + "9916"
//                + "/app_wheeljack_match/standardMedicalRecord/queryAllAdmissionList?hospitalId=" + hospitalId
//                + "&dataScope=all&sendKafka=0");
//        JSONObject jsonObject = JSON.parseObject(medicalJSONStr);
//        JSONArray jsonArray = jsonObject.getJSONArray("data");
//        for (int i = 0; i<jsonArray.size();i++){
//            JSONObject obj = (JSONObject) jsonArray.get(i);
//            String admissionId = (String) obj.get("admissionId");
//            String dataHospitalId = obj.get("dataHospitalId").toString();
//            String stage = "main";
//            if (StringUtils.isNotBlank(preCondition)){
//                JSONObject conditionJsonObject = JSON.parseObject(preCondition);
//                String dependEmrNoStr = conditionJsonObject.get("dependEmrNo").toString();
//                String dependNodeNameStr = conditionJsonObject.get("dependNodeName").toString();
//                String regexStr = conditionJsonObject.get("regexStr").toString();
//                PageInfo<ShowDocModel> pageInfo = this.docQueryList(dataHospitalId, admissionId, stage, "住院病案首页", dependEmrNoStr, null, 100, 1);
//                List<ShowDocModel> records = pageInfo.getRecords();
//                ShowDocModelVo vo = records.get(0);
//                String fileId = vo.getFileId();
//                Map<String, Object> docContentDetail = this.getDocContentDetail(dataHospitalId, admissionId, stage, fileId);
//                List<ShowNodeModel> nodes = (List<ShowNodeModel>) docContentDetail.get("nodeList");
//                boolean b = this.preJudge(nodes,dependNodeNameStr,regexStr);
//                if (!b){
//                    //如果不满足前置条件,则跳过
//                    continue;
//                }
//            }
//            //查询文书描述信息
//            PageInfo<ShowDocModel> showDocModelPageInfo = this.docQueryList(dataHospitalId, admissionId, stage, null, emrNo, null, 100, 1);
//            List<ShowDocModel> records = showDocModelPageInfo.getRecords();
//            if (showDocModelPageInfo!=null&&!CollectionUtils.isEmpty(records)&&records.size()>0){
//                for (ShowDocModelVo nodeModelVo:records){
//                    //查询文书内容信息
//                    Map<String, Object> docContentDetail = this.getDocContentDetail(dataHospitalId, admissionId,stage, nodeModelVo.getFileId());
//                    //对文书内容做处理生成json
//                    String generateJson = dealDocContent(admissionId,nodeModelVo.getFileId(),dataHospitalId,docContentDetail,keysStr);
//                    if (StringUtils.isNotBlank(generateJson)){
//                        sb.append(generateJson);
//                        sb.append("\n");
//                    }
//                }
//            }
//        }
        if (!map.isEmpty()&&map.containsKey("admissionIdList")){
            List<MedicalRecordVo> voList = (List<MedicalRecordVo>) map.get("admissionIdList");
            for (MedicalRecordVo vo:voList){
                String voHospitalId = vo.getHospitalId();
                String voAdmissionId = vo.getAdmissionId();
                String voStage = vo.getStage();
                //前置条件处理
                if (StringUtils.isNotBlank(preCondition)){
                    JSONObject conditionJsonObject = JSON.parseObject(preCondition);
                    String dependEmrNoStr = conditionJsonObject.get("dependEmrNo").toString();
                    String dependNodeNameStr = conditionJsonObject.get("dependNodeName").toString();
                    String regexStr = conditionJsonObject.get("regexStr").toString();
                    PageInfo<ShowDocModel> pageInfo = this.docQueryList(voHospitalId, voAdmissionId, voStage, "住院病案首页", dependEmrNoStr, null, 100, 1);
                    List<ShowDocModel> records = pageInfo.getRecords();
                    ShowDocModelVo modelVo = records.get(0);
                    String fileId = modelVo.getFileId();
                    Map<String, Object> docContentDetail = this.getDocContentDetail(voHospitalId, voAdmissionId, voStage, fileId);
                    List<ShowNodeModel> nodes = (List<ShowNodeModel>) docContentDetail.get("nodeList");
                    boolean b = this.preJudge(nodes,dependNodeNameStr,regexStr);
                    if (!b){
                        //如果不满足前置条件,则跳过
                        continue;
                    }
                }
                //查询文书描述信息
                PageInfo<ShowDocModel> showDocModelPageInfo = this.docQueryList(voHospitalId, voAdmissionId, voStage, null, emrNo, null, 100, 1);
                List<ShowDocModel> records = showDocModelPageInfo.getRecords();
                if (showDocModelPageInfo!=null&&!CollectionUtils.isEmpty(records)&&records.size()>0){
                    for (ShowDocModelVo nodeModelVo:records){
                        //查询文书内容信息
                        Map<String, Object> docContentDetail = this.getDocContentDetail(vo.getHospitalId(), vo.getAdmissionId(), vo.getStage(), nodeModelVo.getFileId());
                        //对文书内容做处理生成json
                        String generateJson = dealDocContent(voAdmissionId,nodeModelVo.getFileId(),voHospitalId,docContentDetail,keysStr);
                        if (StringUtils.isNotBlank(generateJson)){
                            sb.append(generateJson);
                            sb.append("\n");
                        }
                    }
                }
            }
        }

        //写入txt文件
//        if (sb!=null&&sb.length()>0){
//            File file = new File("D:\\formated_json.txt");
//            try {
//                FileWriter fileWriter = new FileWriter(file);
//                fileWriter.write(sb.toString());
//                fileWriter.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        return "success!";
    }

    /**
     * 判断文书列表中的指定节点的值是否包含某个值
     * @param nodes 文书列表
     * @param dependNodeNameStr 文书中的节点名称
     * @param regexStr 节点内容是否包含的正则判断
     * @return true 包含，false 不包含
     */
    private boolean preJudge(List<ShowNodeModel> nodes, String dependNodeNameStr, String regexStr) {
        if (!CollectionUtils.isEmpty(nodes)){
            for (ShowNodeModel nodeModel:nodes){
                if (dependNodeNameStr.equalsIgnoreCase(nodeModel.getNodeName())){
                    String nodeContent = nodeModel.getNodeContent();
                    Pattern pattern = Pattern.compile(regexStr);
                    Matcher matcher = pattern.matcher(nodeContent);
                    if (matcher.matches()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 追加节点内容
     * @param fileUrl 文件路径
     * @return
     */
    @Override
    public String appendNodeContentByFile(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)){
            fileUrl="D:\\cbzqx.txt";
        }
        StringBuffer sb = new StringBuffer();
        File file = new File(fileUrl);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            //存放导出到excel的数据,外层List中的每个List<String>表示一行
            List<List<String>> excelList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                if (split.length==5){
                    String hospitalId = split[0];
                    String admissionId = split[1];
                    String nodeName = split[2];
                    String fileId = split[3];
                    String defect = split[4];
                    Map<String, Object> docContentMap = this.getDocContentDetail(hospitalId, admissionId, "main", fileId);
                    if (!docContentMap.isEmpty()){
                        List<ShowNodeModel> nodeModelList = (List<ShowNodeModel>) docContentMap.get("nodeList");
                        //全值匹配
                        for (ShowNodeModel node:nodeModelList){
                            String nodeNodeName = node.getNodeName();
                            if (nodeNodeName.equals(nodeName)){
                                String nodeContent = node.getNodeContent();
                                //加入sb
//                                sb.append(hospitalId);
//                                sb.append("\t");
//                                sb.append(admissionId);
//                                sb.append("\t");
//                                sb.append(nodeName);
//                                sb.append("\t");
//                                sb.append(fileId);
//                                sb.append("\t");
//                                sb.append(defect);
//                                sb.append("\t");
//                                sb.append(nodeContent);
//                                sb.append("\n");
                                List<String> stringList = new ArrayList<>();
                                stringList.add(hospitalId);
                                stringList.add(admissionId);
                                stringList.add(nodeName);
                                stringList.add(fileId);
                                stringList.add(defect);
                                stringList.add(nodeContent);
                                excelList.add(stringList);
                                break;
                            }
                        }
                    }
                }
            }
            //写入txt文件
//            if (sb!=null&&sb.length()>0){
//                File newFile = new File("D:\\appended_defect_json.txt");
//                try {
//                    FileWriter fileWriter = new FileWriter(newFile);
//                    fileWriter.write(sb.toString());
//                    fileWriter.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
            //写入excel
            if (!CollectionUtils.isEmpty(excelList)){
                //创建工作簿和第1张表格
                XSSFWorkbook workbook = new XSSFWorkbook ();
                Sheet sheet = workbook.createSheet("sheet1");
                for (int i=0;i<excelList.size();i++){
                    //创建行
                    Row row = sheet.createRow(i+1);
                    List<String> strings = excelList.get(i);
                    if (!CollectionUtils.isEmpty(strings)){
                        for (int j=0;j<strings.size();j++){
                            row.createCell(j).setCellValue(strings.get(j));
                        }
                    }
                }
                //保存Excel文件
                FileOutputStream fileOut = new FileOutputStream("D:\\exported.xlsx");
                workbook.write(fileOut);
                fileOut.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void combineTheLastTwoColumnsInTxt(String inputTxtUrl) {
        FileUtils.combineTheLastTwoColumnsInTxt(inputTxtUrl);
    }

    /**
     * 根据单个标签高亮一段文本
     * @return
     */
    @Override
    public Map<String, Object> hightTextByOneTag(String param) {
        Map<String,Object> result = new LinkedHashMap<>();
        String hightedText = "";
        if (StringUtils.isNotBlank(param)){
            JSONObject jsonObject = JSON.parseObject(param);
            if (Objects.nonNull(jsonObject)){
                JSONObject paramsJson = (JSONObject) jsonObject.get("params");
                String labelContent = paramsJson.getString("labelContent");
                String entityOrSpanListStr = paramsJson.getString("entityOrSpanListStr");
                String text = paramsJson.getString("text");
                String labelColor = paramsJson.getString("labelColor");
                String fileId = paramsJson.getString("fileId");
                String nodeName = paramsJson.getString("nodeName");
                JSONArray jsonArray = JSON.parseArray(entityOrSpanListStr);
                List<EntityOrSpanModel> entityOrSpanModels = JSONArray.parseArray(jsonArray.toJSONString(), EntityOrSpanModel.class);
                List<EntityOrSpanModel> toDealList = new ArrayList<>();
                ShowLabelModel showLabelModel = new ShowLabelModel(labelContent,labelColor,null,getLabelChineseName(labelContent));
                List<ShowLabelModel> singleLabelList = new ArrayList<>();
                singleLabelList.add(showLabelModel);
                if (!jsonArray.isEmpty()){
                    for (EntityOrSpanModel model:entityOrSpanModels){
                        if (model.getLabel().equalsIgnoreCase(labelContent)){
                            toDealList.add(model);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(toDealList)){
                    hightedText = this.highLightText(text,toDealList,singleLabelList,fileId,nodeName);
                }
            }

        }
        result.put("hightedText",hightedText);
        return result;
    }

    /**
     * 查询事件列表
     * @param hospitalId 查询条件:所属医院id
     * @param admissionId 查询条件:流水号
     * @param pageSize 每页容量
     * @param pageNum 页码
     * @return 事件查询列表
     */
    @Override
    public PageInfo<ShowEventModelVo> eventQueryList(String hospitalId, String admissionId, String stage,String eventName, Integer pageSize, Integer pageNum) {
        List<ShowEventModelVo> showEventModelVoList = new ArrayList<>();
        if (StringUtils.isBlank(hospitalId)||StringUtils.isBlank(admissionId)||StringUtils.isBlank(stage)){
            return null;
        }
        //查询事件信息
        Map<String, Object> allEventsInOneRecord = this.getAllEventsInOneRecord(hospitalId, admissionId, stage);
        if (allEventsInOneRecord.isEmpty()){
            return null;
        }
        //todo 创建ShowEventModelVo对象集合
        if (allEventsInOneRecord.containsKey("eventMap")){
            JSONObject eventMap = (JSONObject) allEventsInOneRecord.get("eventMap");
            for (String key:eventMap.keySet()){
                List<JSONObject> jsonArray= (List<JSONObject>) eventMap.get(key);
                if (!jsonArray.isEmpty()&&jsonArray.size()>0){
                    for (JSONObject jsonObject:jsonArray){
                        ShowEventModelVo eventModelVo = new ShowEventModelVo();
                        eventModelVo.setId(Objects.isNull(jsonObject.get("id"))?"":jsonObject.get("id").toString());
                        eventModelVo.setEventName(Objects.isNull(jsonObject.get("eventName"))?"":jsonObject.get("eventName").toString());
                        eventModelVo.setEventTime(Objects.isNull(jsonObject.get("eventTime"))?"":jsonObject.get("eventTime").toString());
                        eventModelVo.setEventIdentity(Objects.isNull(jsonObject.get("eventIdentity"))?"":jsonObject.get("eventIdentity").toString());
                        eventModelVo.setDocMap(Objects.isNull(jsonObject.get("docMap"))?"":jsonObject.get("docMap").toString());
                        eventModelVo.setOrderMap(Objects.isNull(jsonObject.get("orderMap"))?"":jsonObject.get("orderMap").toString());
                        eventModelVo.setAttributeMap(Objects.isNull(jsonObject.get("attributeMap"))?"":jsonObject.get("attributeMap").toString());
                        eventModelVo.setHighlightInfo(Objects.isNull(jsonObject.get("highlightInfo"))?"":jsonObject.get("highlightInfo").toString());
                        eventModelVo.setAllDocFileIdSet(Objects.isNull(jsonObject.get("allDocFileIdSet"))?"":jsonObject.get("allDocFileIdSet").toString());
                        eventModelVo.setTimeAttribute(Objects.isNull(jsonObject.get("timeAttribute"))?"":jsonObject.get("timeAttribute").toString());
                        eventModelVo.setIdentityAttribute(Objects.isNull(jsonObject.get("identityAttribute"))?"":jsonObject.get("identityAttribute").toString());
                        showEventModelVoList.add(eventModelVo);
                    }
                }
            }
        }
        //开始分页
        if (pageSize==null||pageSize<=0){
            pageSize=10;
        }
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        if (StringUtils.isNotBlank(eventName)){
            showEventModelVoList = showEventModelVoList.stream().filter(e->e.getEventName().contains(eventName)).collect(Collectors.toList());
        }
        List<ShowEventModelVo> collect = showEventModelVoList.stream().skip(pageSize * (pageNum-1)).limit(pageSize).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setRecords(collect);
        pageInfo.setTotal(showEventModelVoList.size());
        pageInfo.setCurrent(pageNum==null?1:pageNum);
        pageInfo.setSize(pageSize==null?10:pageSize);
        return pageInfo;
    }

    /**
     * 处理Doc内容生成json
     * @param voAdmissionId 流水号
     * @param fileId 文书id
     * @param voHospitalId 医院编号
     * @param docContentDetail 文书内容
     * @param keysStr 需要的key,用英文逗号(,)分割
     * @return
     */
    private String dealDocContent(String voAdmissionId, String fileId, String voHospitalId, Map<String, Object> docContentDetail, String keysStr) {
        Map<String,String> result = new LinkedHashMap<>();
        if (docContentDetail.isEmpty()||!docContentDetail.containsKey("nodeList")){
            return null;
        }
        List<ShowNodeModel> nodeModelList = (List<ShowNodeModel>) docContentDetail.get("nodeList");
        result.put("流水号",voAdmissionId);
        result.put("文书id",fileId);
        result.put("医院编号",voHospitalId);
        //处理keysStr:检查所见:检查所见,检查结论:检查结论,检查名称:检查单名称|检查项目中文名称|检查项目类型名称,性别:一般情况/性别,年龄:一般情况/年龄,检查部位:检查部位名称
        if (StringUtils.isNotBlank(keysStr)){
            String[] split = keysStr.split(",");
            if (split!=null&&split.length>0){
                for (int i=0;i<split.length;i++){
                    //检查所见:检查所见:检查所见,检查结论:检查结论,检查名称:检查单名称|检查项目中文名称|检查项目类型名称,性别:一般情况/性别,年龄:一般情况/年龄,检查部位:检查部位名称
                    String key = split[i];
                    String[] split1 = key.split(":");
                    if (split1[1].contains("|")){
                        //在多个字段中取最长的
                        String[] split2 = split1[1].split("\\|");
                        List<String> list = Arrays.asList(split2);
                        Integer length = 0;
                        String nodeContent ="";
                        for (ShowNodeModel node:nodeModelList){
                            String nodeName = node.getNodeName();
                            if (list.contains(nodeName)){
                                String content = node.getNodeContent();
                                if (content.length()>length){
                                    nodeContent = content;
                                    length = content.length();
                                }
                            }
                        }
                        result.put(split1[0],nodeContent);
                    }else {
                        //全值匹配
                        for (ShowNodeModel node:nodeModelList){
                            String nodeName = node.getNodeName();
                            if (nodeName.equals(split1[1])){
                                result.put(split1[0],node.getNodeContent());
                            }
                        }
                    }
                }
            }
        }
        String jsonString = JSONObject.toJSONString(result);
        return jsonString;
    }

    /**
     * 获取doc下的所有节点
     * @param doc
     * @return
     */
    private List<ShowNodeModel> getAllNodesInDoc(Map<String, Object> doc) {
        List<ShowNodeModel> nodeList = new ArrayList<>();
        if (doc.get("nodes")!=null){
            List<Map<String,Object>> nodes = (List<Map<String, Object>>) doc.get("nodes");
            if (nodes!=null&&nodes.size()>0){
                for (Map<String,Object> node:nodes){
                    Map<String,Map<String,Object>> result = new LinkedHashMap<>();
                    getAllNodesInOneNode(node,result,null);
                    if (result.size()>0){
                        for (String key:result.keySet()){
                            Map<String, Object> map = result.get(key);
                            ShowNodeModel showNodeModel = new ShowNodeModel();
                            showNodeModel.setNodeName(key);
                            showNodeModel.setNodeContent(map.get("content")==null?"":map.get("content").toString());
                            nodeList.add(showNodeModel);
                        }
                    }
                }
            }
        }
        return nodeList;
    }

    /**
     * 高亮文本的一段内容
     * @param nodeContent 文本内容
     * @param entityList 包含的实体
     * @param labelModelList 实体对应的标签信息
     * @return
     */
    private String highLightText(String nodeContent, List<EntityOrSpanModel> entityList, List<ShowLabelModel> labelModelList) {
        String result = "";
        if (StringUtils.isBlank(nodeContent)){
            return result;
        }
        if (CollectionUtils.isEmpty(entityList)||CollectionUtils.isEmpty(labelModelList)){
            return nodeContent;
        }else {
            //高亮逻辑
            Map<String,String> keyMap = new LinkedHashMap<>();
            for (EntityOrSpanModel entity:entityList){
                Map<String,String> colorMap = this.convertUnifyToMap(labelModelList);
                String entityName = entity.getName();
                String color = colorMap.get(entity.getLabel());
                keyMap.put(entityName,color);
            }
            String highlightedText = Highlighter.highlightKeywords(nodeContent,keyMap);
            result =highlightedText;
        }
        return result;
    }

    /**
     * 转换labelModelList格式
     * @param labelModelList
     * @return Key:标签内容,value:标签颜色&标签内容,说明: value中的标签内容用于实体链接地址的参数
     */
    private Map<String, String> convertUnifyToMap(List<ShowLabelModel> labelModelList) {
        Map<String,String> colorMap = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(labelModelList)){
            for (ShowLabelModel labelModel : labelModelList){
                colorMap.put(labelModel.getLabelContent(),labelModel.getLabelColor()+"&"+labelModel.getLabelContent());
            }
        }
        return colorMap;
    }

    /**
     * 格式化理解结果,用于前端展示
     * @param resultStr 理解结果
     * @return
     */
    private Map<String, Object> formatResultStr(String resultStr) {
        Map<String,Object> result = new LinkedHashMap<>();
        Map<String,Object> parseMap = JSON.parseObject(resultStr,Map.class);
        if (!CollectionUtils.isEmpty(parseMap)&&parseMap.containsKey("_source")){
            Map<String,Object> sourceMap = (Map<String, Object>) parseMap.get("_source");
            if (!CollectionUtils.isEmpty(sourceMap)&&sourceMap.containsKey("data")){
                Map<String,Object> dataMap = (Map) sourceMap.get("data");
                if (!CollectionUtils.isEmpty(dataMap)&&dataMap.containsKey("patient")){
                    Map<String,Object> patientMap = (Map<String, Object>) dataMap.get("patient");
                    if (!CollectionUtils.isEmpty(patientMap)&&patientMap.containsKey("medicalRecord")){
                        Map<String,Object> medicalRecordMap= (Map<String, Object>) patientMap.get("medicalRecord");
                        if (!CollectionUtils.isEmpty(medicalRecordMap)&&medicalRecordMap.containsKey("docs")){
                            List<Map<String,Object>> docList = (List<Map<String, Object>>) medicalRecordMap.get("docs");
                            if (!CollectionUtils.isEmpty(docList)){
                                //左侧文书树,根据doc集合生成树形结构
                                List<Map<String,Object>> treeData = this.generateTree(docList);
                                result.put("treeData",treeData);

                                //文书内容和标签内容
                                //{文书id:[节点名称:{content:节点内容,numTable:[{entityNum:3,},{eventNum:2},{spanNum:3}]}]}
                                Map<String,Map<String,Map<String,Object>>> formatedDocMap = this.formatDocList(docList);
                                List<ShowLabelModel> unifyLabels = this.generateUnifyLabel(formatedDocMap);
                                List<ShowLabelModel> entityLabels = new ArrayList<>();
                                List<ShowLabelModel> spanLabels = new ArrayList<>();
                                for (ShowLabelModel labelModel:unifyLabels){
                                    if (labelModel.getLabelType().equals("entity")){
                                        entityLabels.add(labelModel);
                                    }
                                    if (labelModel.getLabelType().equals("span")){
                                        spanLabels.add(labelModel);
                                    }
                                }
                                result.put("labelsInRecord",unifyLabels);
                                result.put("entityLabels",entityLabels);
                                result.put("spanLabels",spanLabels);
                                List<ShowDocModel> resultDocList = this.convertDocMapToModelList(formatedDocMap,unifyLabels);
                                //处理医嘱
                                if (!CollectionUtils.isEmpty(resultDocList)){
                                    resultDocList = formateOrder(resultDocList);
                                }
                                result.put("docList",resultDocList);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 格式化医嘱
     * @param resultDocList
     * @return
     */
    private List<ShowDocModel> formateOrder(List<ShowDocModel> resultDocList) {
        if (!CollectionUtils.isEmpty(resultDocList)){
            for (ShowDocModel docModel:resultDocList){
                if (docModel.getDocType().equals("EMR110001")){
                    //长期医嘱的处理
                    List<StandingOrderModel> orderModelList = docModel.getStandingOrderList();
                    if (CollectionUtils.isEmpty(orderModelList)){
                        orderModelList = new ArrayList<>();
                    }
                    List<ShowNodeModel> nodeList = docModel.getNodeList();
                    if (!CollectionUtils.isEmpty(nodeList)){
                        StandingOrderModel standingOrderModel = new StandingOrderModel();
                        for (ShowNodeModel node:nodeList){
                            String nodeName = node.getNodeName();
                            String nodeContent = node.getNodeContent();
                            if (nodeName.contains("开立时间")){
                                standingOrderModel.setOpeningTime(nodeContent);
                            }
                            if (nodeName.contains("开始时间")){
                                standingOrderModel.setStartTime(nodeContent);
                            }
                            if (nodeName.contains("医嘱内容")){
                                standingOrderModel.setContent(nodeContent);
                            }
                            if (nodeName.contains("开立医师签名")){
                                standingOrderModel.setOpeningPhysicianSign(nodeContent);
                            }
                            if (nodeName.contains("执行时间")){
                                standingOrderModel.setExecuteTime(nodeContent);
                            }
                            //todo 存疑 执行护士签名是不是执行人签名
                            if (nodeName.contains("执行护士签名")){
                                standingOrderModel.setExecutorSign(nodeContent);
                            }
                            if (nodeName.contains("停止时间")){
                                standingOrderModel.setStopTime(nodeContent);
                            }
                            if (nodeName.contains("停止医师签名")){
                                standingOrderModel.setStopPhysicianSign(nodeContent);
                            }
                            //todo 存疑 停止护士签名是不是 停止执行人签名
                            if (nodeName.contains("停止护士签名")){
                                standingOrderModel.setStartTime(nodeContent);
                            }
                            if (nodeName.contains("云知声项目类别")){
                                standingOrderModel.setYzsProjectType(nodeContent);
                            }
                        }
                        //todo node对standingOrderModel赋值
                        orderModelList.add(standingOrderModel);
                    }
                    docModel.setStandingOrderList(orderModelList);
                }
                if (docModel.getDocType().equals("EMR110002")){
                    //临时医嘱的处理
                    List<StatOrderModel> orderModelList = docModel.getStatOrderList();
                    if (CollectionUtils.isEmpty(orderModelList)){
                        orderModelList = new ArrayList<>();
                    }
                    List<ShowNodeModel> nodeList = docModel.getNodeList();
                    if (!CollectionUtils.isEmpty(nodeList)){
                        StatOrderModel statOrderModel = new StatOrderModel();
                        for (ShowNodeModel node:nodeList){
                            //todo node对statOrderModel赋值
                            String nodeName = node.getNodeName();
                            String nodeContent = node.getNodeContent();
                            //日期/时间是不是开立时间
                            if (nodeName.contains("开立时间")){
                                String[] split = nodeContent.split(" ");
                                statOrderModel.setDay(split[0]);
                                statOrderModel.setTime(split[1]);
                            }
                            if (nodeName.contains("医嘱内容")){
                                statOrderModel.setContent(nodeContent);
                            }
                            //医师签名是指 医嘱开立医师签名还是医嘱取消医师签名
                            if (nodeName.contains("医师签名")){
                                statOrderModel.setPhysicianSign(nodeContent);
                            }
                            if (nodeName.contains("执行时间")){
                                statOrderModel.setExecuteTime(nodeContent);
                            }
                            //todo 执行人签名是不是执行护士签名
                            if (nodeName.contains("执行护士签名")){
                                statOrderModel.setExecutorSign(nodeContent);
                            }
                            if (nodeName.contains("云知声项目类别")){
                                statOrderModel.setYzsProjectType(nodeContent);
                            }
                        }
                        orderModelList.add(statOrderModel);
                    }
                    docModel.setStatOrderList(orderModelList);
                }
            }
        }
        return resultDocList;
    }

    /**
     * 将docMap转换为DocModelList
     * @param formatedDocMap docMap
     * @param unifyLabels 标准的标签信息(病历下涉及到的所有标签的标准信息,每个文书中的标签信息都是该标准信息的子集)
     * @return
     */
    private List<ShowDocModel> convertDocMapToModelList(Map<String, Map<String, Map<String, Object>>> formatedDocMap, List<ShowLabelModel> unifyLabels) {
        List<ShowDocModel> docModelList = new ArrayList<>();
        if (CollectionUtils.isEmpty(formatedDocMap)){
            return docModelList;
        }
        //处理标签信息
//        Map<String,String> colorMap = new LinkedHashMap<>();
        Map<String,Map<String,String>> colorMap = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(unifyLabels)){
            Map<String,String> entityColorMap = new LinkedHashMap<>();
            Map<String,String> spanColorMap = new LinkedHashMap<>();
            Map<String,String> eventColorMap = new LinkedHashMap<>();
            for (ShowLabelModel labelModel : unifyLabels){
                if (labelModel.getLabelType().equals("entity")){
                    entityColorMap.put(labelModel.getLabelContent(),labelModel.getLabelColor());
                }
                if (labelModel.getLabelType().equals("span")){
                    spanColorMap.put(labelModel.getLabelContent(),labelModel.getLabelColor());
                }
                if (labelModel.getLabelType().equals("event")){
                    eventColorMap.put(labelModel.getLabelContent(),labelModel.getLabelColor());
                }
            }
            colorMap.put("entity",entityColorMap);
            colorMap.put("span",spanColorMap);
            colorMap.put("event",eventColorMap);
        }
        //开始处理
        for (String key:formatedDocMap.keySet()){
            //ShowDocModel对象
            ShowDocModel showDocModel = new ShowDocModel();
            String[] split = key.split("&&");
            if (split==null||StringUtils.isBlank(split[0])||StringUtils.isBlank(split[1])){
                continue;
            }
            showDocModel.setFileId(split[0]);
            showDocModel.setDocType(split[1]);
            Map<String, Map<String, Object>> nodes = formatedDocMap.get(key);
            if (split[1].equals("EMR110001")){
                //长期医嘱
                LinkedHashMap linkedHashMap = (LinkedHashMap) nodes.get("standingOrderList");
                List<StandingOrderModel> standingOrderModelList = (List<StandingOrderModel>) linkedHashMap.get("standingOrder");
                showDocModel.setStandingOrderList(standingOrderModelList);
            } else if (split[1].equals("EMR110002")){
                //临时医嘱
                LinkedHashMap hashMap = (LinkedHashMap) nodes.get("statOrderList");
                List<StatOrderModel> statOrderModelList = (List<StatOrderModel>) hashMap.get("statOrder");
                showDocModel.setStatOrderList(statOrderModelList);
            }else {
                //常规文书
                List<ShowNodeModel> nodeList = new ArrayList<>();
                List<ShowLabelModel> labelList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(nodes)){
                    for (String nodeName:nodes.keySet()){
                        Map<String, Object> node = nodes.get(nodeName);
                        ShowNodeModel nodeModel = new ShowNodeModel();
                        List<EventModel> eventModelList = new ArrayList<>();
                        nodeModel.setNodeName(nodeName);
                        if (!CollectionUtils.isEmpty(node)){
                            if (node.containsKey("content")){
                                String content = (String) node.get("content");
                                nodeModel.setNodeContent(content);
                            }
                            if (node.containsKey("numTable")){
                                List<Map<String,Object>> numTableList = (List<Map<String, Object>>) node.get("numTable");
                                Map<String,Object> numTable = numTableList.get(0);

                                Integer entityNum = 0;
                                Integer eventNum = 0;
                                Integer spanNum = 0;
                                if (numTable.containsKey("entities")){
                                    List<Map<String,Object>> entities = (List<Map<String, Object>>) numTable.get("entities");
                                    List<ShowLabelModel> entityLabelList = new ArrayList<>();
                                    Set<String> entityLabelContents = new HashSet<>();
                                    if (!CollectionUtils.isEmpty(entities)){
                                        entityNum = entities.size();
                                        List<EntityOrSpanModel> entityModelList = new ArrayList<>();
                                        for (Map<String,Object> entity:entities){
                                            EntityOrSpanModel entityModel = new EntityOrSpanModel();
                                            String label = (String) entity.get("label");
                                            Integer start = (Integer) entity.get("start");
                                            Integer end = (Integer) entity.get("end");
                                            BigDecimal score = (BigDecimal) entity.get("score");
                                            String name = (String) entity.get("text");
                                            entityModel.setLabel(label);
                                            entityModel.setStart(start);
                                            entityModel.setEnd(end);
                                            entityModel.setScore(score);
                                            entityModel.setName(name);
                                            entityModelList.add(entityModel);
                                            if (colorMap.containsKey("entity")){
                                                Map<String, String> entityColor = colorMap.get("entity");
                                                if (entityColor.containsKey(label)&&!entityLabelContents.contains(entityColor.get(label))){
                                                    ShowLabelModel labelModel = new ShowLabelModel();
                                                    labelModel.setLabelContent(label);
                                                    labelModel.setLabelColor(entityColor.get(label));
                                                    labelModel.setLabelType("entity");
                                                    entityLabelList.add(labelModel);
                                                    entityLabelContents.add(entityColor.get(label));
                                                    labelList.add(labelModel);
                                                }
                                            }
                                        }
                                        nodeModel.setEntityList(entityModelList);
                                        nodeModel.setEntityLabelList(entityLabelList);
                                    }
                                }

                                if (numTable.containsKey("spans")){
                                    List<Map<String,Object>> spans = (List<Map<String, Object>>) numTable.get("spans");
                                    List<ShowLabelModel> spanLabelList = new ArrayList<>();
                                    Set<String> spanLabelContents = new HashSet<>();
                                    if (!CollectionUtils.isEmpty(spans)){
                                        spanNum = spans.size();
                                        List<EntityOrSpanModel> entityModelList = new ArrayList<>();
                                        for (Map<String,Object> span:spans){
                                            EntityOrSpanModel spanModel = new EntityOrSpanModel();
                                            String label = (String) span.get("label");
                                            Integer start = (Integer) span.get("start");
                                            Integer end = (Integer) span.get("end");
                                            BigDecimal score = (BigDecimal) span.get("score");
                                            String name = (String) span.get("text");
                                            spanModel.setLabel(label);
                                            spanModel.setStart(start);
                                            spanModel.setEnd(end);
                                            spanModel.setScore(score);
                                            spanModel.setName(name);
                                            entityModelList.add(spanModel);
                                            if (colorMap.containsKey("span")){
                                                Map<String, String> spanColor = colorMap.get("span");
                                                if (spanColor.containsKey(label)&&!spanLabelContents.contains(spanColor.get(label))){
                                                    ShowLabelModel labelModel = new ShowLabelModel();
                                                    labelModel.setLabelContent(label);
                                                    labelModel.setLabelColor(spanColor.get(label));
                                                    labelModel.setLabelType("span");
                                                    spanLabelList.add(labelModel);
                                                    spanLabelContents.add(spanColor.get(label));
                                                    labelList.add(labelModel);
                                                }
                                            }
                                        }
                                        nodeModel.setSpanList(entityModelList);
                                        nodeModel.setSpanLabelList(spanLabelList);
                                    }
                                }
                                //todo 事件处理
                                if (numTable.containsKey("events")){
                                    List<Map<String,Object>> events = (List<Map<String, Object>>) numTable.get("events");
                                    eventNum = events.size();
                                    if (events.size()>0){
                                        for (Map<String,Object> event:events){
                                            EventModel eventModel = new EventModel();
                                            eventModel.setText(nodeModel.getNodeContent());
                                            if (event.containsKey("id")){
                                                eventModel.setId(event.get("id").toString());
                                            }
                                            if (event.containsKey("eventName")){
                                                eventModel.setEventName(event.get("eventName").toString());
                                            }
                                            if (event.containsKey("eventIdentity")){
                                                eventModel.setEventIdentity(event.get("eventIdentity").toString());
                                            }
                                            if (event.containsKey("attributeMap")){
                                                eventModel.setAttributeMap(event.get("attributeMap").toString());
                                            }
                                            eventModelList.add(eventModel);
                                        }
                                    }
                                }
                                nodeModel.setEventList(eventModelList);
                                nodeModel.setEntityNum(entityNum);
                                nodeModel.setEventNum(eventNum);
                                nodeModel.setSpanNum(spanNum);
                            }
                        }
                        nodeList.add(nodeModel);
                    }
                }
                showDocModel.setNodeList(nodeList);
                //处理颜色
                if (!CollectionUtils.isEmpty(labelList)){
                    Map<String,ShowLabelModel> labelMap = new LinkedHashMap<>();
                    List<ShowLabelModel> list = new ArrayList<>();
                    for (ShowLabelModel label:labelList){
                        labelMap.put(label.getLabelContent(),label);
                    }
                    if (!CollectionUtils.isEmpty(labelMap)){
                        for (String content:labelMap.keySet()){
                            list.add(labelMap.get(content));
                        }
                    }
                    showDocModel.setLabelList(list);
                }else {
                    showDocModel.setLabelList(labelList);
                }
            }
            docModelList.add(showDocModel);
        }
        return docModelList;
    }

    /**
     * 生成标准的标签(获取docMap下的所有标签,作为单个文书的标签参考,实现病历下的各个文书中标签的颜色统一)
     * @param formatedDocMap
     * @return
     */
    private List<ShowLabelModel> generateUnifyLabel(Map<String, Map<String, Map<String, Object>>> formatedDocMap) {
        List<ShowLabelModel> labelModelList = new ArrayList<>();
//        Map<String,String> referColorMap = new LinkedHashMap<>();
        //key:entity/span/event,value<Key:label,Value:color>
        Map<String,Map<String,String>> referColorMap = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(formatedDocMap)){
            return labelModelList;
        }
        //key:label,value:color
        Map<String,String> entityColorMap = new LinkedHashMap<>();
        Map<String,String> spanColorMap = new LinkedHashMap<>();
        List<String> entityColorCodeList = new ArrayList<>();
        List<String> spanColorCodeList = new ArrayList<>();
        //开始处理
        for (String fileId:formatedDocMap.keySet()){
            Map<String, Map<String, Object>> nodes = formatedDocMap.get(fileId);
            if (!CollectionUtils.isEmpty(nodes)){
                for (String nodeName:nodes.keySet()){
                    Map<String, Object> node = nodes.get(nodeName);
                    if (!CollectionUtils.isEmpty(node)&&node.containsKey("numTable")){
                        List<Map<String,Object>> numTableList = (List<Map<String, Object>>) node.get("numTable");
                        Map<String,Object> numTable = numTableList.get(0);
                        //处理entity
                        List<Map<String,Object>> entities = (List<Map<String, Object>>) numTable.get("entities");
                        if (!CollectionUtils.isEmpty(entities)){
                            for (Map<String,Object> entity:entities){
                                String label = (String) entity.get("label");
//                                String colorCode = "#"+GenerateColorCode.randomHexStr(6);
                                String colorCodeStr = GenerateColorCode.getColorCodeStr(colorList,entityColorCodeList,colorList.size());
                                entityColorCodeList.add(colorCodeStr);
                                entityColorMap.put(label,colorCodeStr);

                            }
                        }

                        //处理span
                        List<Map<String,Object>> spans = (List<Map<String, Object>>) numTable.get("spans");
                        if (!CollectionUtils.isEmpty(spans)){
                            for (Map<String,Object> span:spans){
                                String label = (String) span.get("label");
//                                String colorCode = "#"+GenerateColorCode.randomHexStr(6);
                                String colorCodeStr = GenerateColorCode.getColorCodeStr(colorList,spanColorCodeList,colorList.size());
                                spanColorCodeList.add(colorCodeStr);
                                spanColorMap.put(label,colorCodeStr);
                            }
                        }

                        //todo 处理event的标签颜色
                    }
                }
            }
        }
        referColorMap.put("entity",entityColorMap);
        referColorMap.put("span",spanColorMap);
        //处理颜色
        if (!CollectionUtils.isEmpty(referColorMap)){
            if (referColorMap.containsKey("entity")){
                Map<String,String> entityMap = referColorMap.get("entity");
                for (String label:entityMap.keySet()){
                    ShowLabelModel labelModel = new ShowLabelModel();
                    labelModel.setLabelType("entity");
                    labelModel.setLabelContent(label);
                    labelModel.setLabelColor(entityMap.get(label));
                    labelModel.setLabelChineseName(getLabelChineseName(label));
                    labelModelList.add(labelModel);
                }
            }
            if (referColorMap.containsKey("span")){
                Map<String,String> spanMap = referColorMap.get("span");
                for (String label:spanMap.keySet()){
                    ShowLabelModel labelModel = new ShowLabelModel();
                    labelModel.setLabelType("span");
                    labelModel.setLabelContent(label);
                    labelModel.setLabelColor(spanMap.get(label));
                    labelModel.setLabelChineseName(getLabelChineseName(label));
                    labelModelList.add(labelModel);
                }
            }
            if (referColorMap.containsKey("event")){
                Map<String,String> eventMap = referColorMap.get("event");
                for (String label:eventMap.keySet()){
                    ShowLabelModel labelModel = new ShowLabelModel();
                    labelModel.setLabelType("event");
                    labelModel.setLabelContent(label);
                    labelModel.setLabelColor(eventMap.get(label));
                    labelModel.setLabelChineseName(getLabelChineseName(label));
                    labelModelList.add(labelModel);
                }
            }

        }
        //将label内容英文转换为中文
        //根据labelMapping进行转换,英文转换为中文
        ResourceLoad.convertLabelListContentToChinese(labelModelList);
        return labelModelList;
    }

    /**
     * 格式化文书列表
     * @param docList 文书列表
     * @return:格式参考对应json如下:
     * 返回的json格式参考:
     * {fileId:[{nodeName:{content:节点内容,numTable:[{entityNum:3},{eventNum:2},{spanNum:3}]}}]}
     *
     */
    private Map<String,Map<String,Map<String,Object>>> formatDocList(List<Map<String, Object>> docList) {
        Map<String,Map<String,Map<String,Object>>> result = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(docList)){
            return result;
        }
        for (Map<String,Object> docMap:docList){
            Map<String, Map<String, Object>> resultNodeMap = new LinkedHashMap<>();
            String docType = (String) docMap.get("docType");
            if (docMap.containsKey("nodes")){
                List<Map<String,Object>> nodeList = (List<Map<String, Object>>) docMap.get("nodes");
                if (!CollectionUtils.isEmpty(nodeList)){
                    if (docType.equalsIgnoreCase("EMR110001")){
                        //长期医嘱
                        List<StandingOrderModel> orderModelList = new ArrayList<>();
                        Map<String,Object> orderMap = new LinkedHashMap<>();
                        for (Map<String,Object> node:nodeList){
                            String name = (String) node.get("name");
                            if (name.equalsIgnoreCase("医嘱")){
                                if (node.containsKey("chd")){
                                    List<Map<String,String>> orderList = (List<Map<String, String>>) node.get("chd");
                                    if (!CollectionUtils.isEmpty(orderList)){
                                        StandingOrderModel standingOrderModel = new StandingOrderModel();
                                        //设置唯一表示unisoundId
                                        String unisoundId = (String) node.get("unisoundId");
                                        standingOrderModel.setUnisoundId(unisoundId);
                                        for (Map<String,String> order:orderList){
                                            if (order.get("name").equals("医嘱开立时间")){
                                                standingOrderModel.setOpeningTime(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱开始时间")){
                                                standingOrderModel.setStartTime(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱内容")){
                                                standingOrderModel.setContent(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱开立医师签名")){
                                                standingOrderModel.setOpeningPhysicianSign(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱执行时间")){
                                                standingOrderModel.setExecuteTime(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱执行护士签名")){
                                                standingOrderModel.setExecutorSign(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱停止时间")){
                                                standingOrderModel.setStopTime(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱停止医师签名")){
                                                standingOrderModel.setStopPhysicianSign(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱停止护士签名")){
                                                standingOrderModel.setStopExecutorSign(order.get("text"));
                                            }
                                            if (order.get("name").equals("云知声项目类别")){
                                                standingOrderModel.setYzsProjectType(order.get("text"));
                                            }
                                            if (order.get("name").equals("项目类别")){
                                                standingOrderModel.setProjectCategories(order.get("text"));
                                            }
                                        }
                                        orderModelList.add(standingOrderModel);
                                    }
                                }
                            }
                        }
                        orderMap.put("standingOrder",orderModelList);
                        resultNodeMap.put("standingOrderList",orderMap);
                    }else if (docType.equalsIgnoreCase("EMR110002")){
                        //临时医嘱
                        List<StatOrderModel> statOrderModelList = new ArrayList<>();
                        Map<String,Object> orderMap = new LinkedHashMap<>();
                        for (Map<String,Object> node:nodeList){
                            String name = (String) node.get("name");
                            if (name.equalsIgnoreCase("医嘱")){
                                if (node.containsKey("chd")){
                                    List<Map<String,String>> orderList = (List<Map<String, String>>) node.get("chd");
                                    if (!CollectionUtils.isEmpty(orderList)){
                                        StatOrderModel statOrderModel = new StatOrderModel();
                                        //设置唯一表示unisoundId
                                        String unisoundId = (String) node.get("unisoundId");
                                        statOrderModel.setUnisoundId(unisoundId);
                                        for (Map<String,String> order:orderList){
                                            if (order.get("name").equals("医嘱开立时间")){
                                                statOrderModel.setDay(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱开立时间")){
                                                statOrderModel.setTime(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱内容")){
                                                statOrderModel.setContent(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱开立医师签名")){
                                                statOrderModel.setPhysicianSign(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱执行时间")){
                                                statOrderModel.setExecuteTime(order.get("text"));
                                            }
                                            if (order.get("name").equals("医嘱执行护士签名")){
                                                statOrderModel.setExecutorSign(order.get("text"));
                                            }
                                            if (order.get("name").equals("云知声项目类别")){
                                                statOrderModel.setYzsProjectType(order.get("text"));
                                            }
                                            if (order.get("name").equals("项目类别")){
                                                statOrderModel.setProjectCategories(order.get("text"));
                                            }
                                        }
                                        statOrderModelList.add(statOrderModel);
                                    }
                                }
                            }
                        }
                        orderMap.put("statOrder",statOrderModelList);
                        resultNodeMap.put("statOrderList",orderMap);
                    }else {
                        resultNodeMap = formateNodeList(nodeList);
                    }
                }
            }
            String fileId = (String) docMap.get("fileId");
            String key = fileId+"&&"+docType;
            if (StringUtils.isNotBlank(key)){
                result.put(key,resultNodeMap);
            }
        }
        return result;
    }

    /**
     * 格式化node节点内容
     * @param nodeList 节点内容list
     * @return 格式化之后的结果
     */
    private Map<String, Map<String, Object>> formateNodeList(List<Map<String, Object>> nodeList) {
        Map<String,Map<String,Object>> result = new LinkedHashMap<>();
        for (Map<String,Object> node:nodeList){
            result.putAll(getAllNodesInOneNode(node,result,null));
        }
        return result;
    }

    public Map<String,Map<String,Object>> getAllNodesInOneNode(Map<String,Object> node,Map<String,Map<String,Object>> result,String parentNodeName){
        Map<String,Object> nodeDetailMap =new LinkedHashMap<>();
        String text = (String) node.get("text");
        nodeDetailMap.put("content",text);

        List<Map<String,Object>> numTableList = new ArrayList<>();
        Map<String,Object> numTableMap = new LinkedHashMap<>();
        Integer entityNum = 0;
        Integer eventNum = 0;
        Integer spanNum = 0;
        if (node.containsKey("entities")){
            List<Map<String,Object>> entityList = (List) node.get("entities");
            entityNum = entityList.size();
            numTableMap.put("entities",entityList);
        }
        if (node.containsKey("events")){
            List<Map<String,Object>> eventList = (List) node.get("events");
            eventNum = eventList.size();
            numTableMap.put("events",eventList);
        }
        if (node.containsKey("spans")){
            List<Map<String,Object>> spanList = (List) node.get("spans");
            spanNum = spanList.size();
            numTableMap.put("spans",spanList);
        }
        numTableMap.put("entityNum",entityNum);
        numTableMap.put("eventNum",eventNum);
        numTableMap.put("spanNum",spanNum);
        numTableList.add(numTableMap);
        nodeDetailMap.put("numTable",numTableList);


        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(parentNodeName)){
            sb.append(parentNodeName);
            sb.append("/");
        }
        sb.append(node.get("name"));
        //处理nodeName
        result.put(sb.toString(),nodeDetailMap);
        if (node.containsKey("chd")){
            Map<String,Object> chd;
            List nodeList = (List) node.get("chd");
            if (!CollectionUtils.isEmpty(nodeList)){
                for (Object childNode:nodeList){
                    chd=(Map<String, Object>)childNode;
                    getAllNodesInOneNode(chd,result, sb.toString());
                }
            }
        }
        return result;
    }

    /**
     * 对doc进行归类,生成树形结构
     * @param docList
     * @return
     */
    private List<Map<String, Object>> generateTree(List<Map<String, Object>> docList) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String,List> referMap = new LinkedHashMap<>();
        //对docList排序
        List<Map<String, Object>> sortedDocList = docList.stream().sorted((o1, o2) -> ChineseComparator.compareString(o1.get("createTime")+"",o2.get("createTime")+"")).collect(Collectors.toList());
        for (Map<String,Object> doc:sortedDocList){
            String emrNo = (String) doc.get("docType");
            if (StringUtils.isNotBlank(emrNo)){
                //查询对应的分组
                for (String treeKey:globalTreeMapping.keySet()){
                    if (globalTreeMapping.get(treeKey).contains(emrNo)){
                        Map<String,Object> leafMap = new LinkedHashMap<>();
                        StringBuffer sb = new StringBuffer();
                        if (doc.containsKey("fileName")){
                            sb.append(doc.get("fileName").toString());
                        }
                        if (doc.containsKey("createTime")){
                            sb.append("[");
                            sb.append(doc.get("createTime").toString());
                            sb.append("]");
                        }
                        leafMap.put("label",sb.toString());
                        leafMap.put("children",null);
                        leafMap.put("fileId",doc.get("fileId"));
                        leafMap.put("docType",doc.get("docType"));
                        if (referMap.containsKey(treeKey)){
                            referMap.get(treeKey).add(leafMap);
                        }else {
                            List<Map<String,Object>> leaf = new ArrayList<>();
                            leaf.add(leafMap);
                            referMap.put(treeKey,leaf);
                        }
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(referMap)){
            Map<String,List> sortedMap = new LinkedHashMap<>();
            for (String key:globalTreeMapping.keySet()){
                if (!CollectionUtils.isEmpty(referMap.get(key))){
                    sortedMap.put(key,referMap.get(key));
                }
            }
            for (String referKey:sortedMap.keySet()){
                Map<String,Object> resultMap = new LinkedHashMap<>();
                if (referMap.get(referKey).size()==1){
                    resultMap.put("label",referKey);
                    resultMap.put("children",null);
                    Map<String,Object> map = (Map<String, Object>) referMap.get(referKey).get(0);
                    resultMap.put("fileId",map.get("fileId"));
                    resultMap.put("docType",map.get("docType"));
                }else {
                    resultMap.put("label",referKey);
                    resultMap.put("children",referMap.get(referKey));
                }
                result.add(resultMap);
            }
        }
        return result;
    }

    /**
     * 查询医院下的所有病历（带分页）
     * @param hospital
     * @return
     */
    public Map<String,Object> queryByHospital(Hospital hospital,Integer pageNum,Integer pageSize){
        Map<String,Object> result = new LinkedHashMap<>();
        List<MedicalRecordVo> medicalRecordVoList = new ArrayList<>();
        //获取医院id
        String id = hospital.getHospitalId();
        if (id.startsWith("%")){
            return null;
        }
        String index = id+"_"+hospital.getScene();
        Map<String,Object> admissionMap = patientDataFetcher.getAdmissionNoInHospital(index,pageNum,pageSize);
        //查询医院下的病历
//        String url = "http://10.128.3.122:8083/recordCache?hospitalId="+id+"&scene="+hospital.getScene();
//        String url = "http://10.128.3.122:9200/optimus_data_"+id+"_"+hospital.getScene()+"/_search";
//        Map<String,Object> requestBody = new LinkedHashMap<>();
//        Map<String,Object> match_all = new LinkedHashMap<>();
//        match_all.put("match_all",match_all);
//        requestBody.put("query",match_all);
//        requestBody.put("size",10);
//        requestBody.put("_source",false);
//        String httpGet = HttpUtils.httpPost(url,requestBody);
//        String admissionIdsStr = httpGet.replaceAll("\\[|\\]|\"","");
//        List<String> admissionIdList = Arrays.asList(admissionIdsStr.split(","));
        if (admissionMap.containsKey("admissionIdList")&&admissionMap.containsKey("timestampList")){
            List<String> admissionIdList = (List<String>) admissionMap.get("admissionIdList");
            List<String> timestampList = (List<String>) admissionMap.get("timestampList");
            if (!CollectionUtils.isEmpty(admissionIdList)&&!CollectionUtils.isEmpty(timestampList)){
                for (int i=0;i<admissionIdList.size();i++){
                    if (globalHospitaiMap.containsKey(id)){
                        //定义病历对象
                        MedicalRecordVo medicalRecordVo = new MedicalRecordVo();
                        medicalRecordVo.setAdmissionId(admissionIdList.get(i));
                        medicalRecordVo.setTimeStamp(timestampList.get(i));
                        medicalRecordVo.setStage(hospital.getScene());
                        medicalRecordVo.setHospitalId(id);
                        medicalRecordVo.setHospitalName(globalHospitaiMap.get(id));
                        medicalRecordVoList.add(medicalRecordVo);
                    }
                }
            }
        }
        if (admissionMap.containsKey("total")){
            Integer total = Integer.parseInt(admissionMap.get("total").toString());
            result.put("total",total);
        }
        result.put("admissionIdList",medicalRecordVoList);
        return result;
    }

    /**
     * 手动分页
     */
    public PageInfo<MedicalRecordVo> pageInfo(List<MedicalRecordVo> medicalRecordVoList,Integer pageSize,Integer pageNum,Integer total){
        if (CollectionUtils.isEmpty(medicalRecordVoList)){
            return new PageInfo();
        }
        //处理分页参数
        if (pageNum==null||pageNum==0){
            pageNum=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=10;
        }
        //分页
//        List<MedicalRecordVo> collect = medicalRecordVoList.stream().skip(pageSize * (pageNum-1)).limit(pageSize).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setRecords(medicalRecordVoList);
        if (total!=null){
            pageInfo.setTotal(total);
        }else {
            pageInfo.setTotal(medicalRecordVoList.size());
        }
        pageInfo.setCurrent(pageNum);
        pageInfo.setSize(pageSize);
        return pageInfo;
    }
}
