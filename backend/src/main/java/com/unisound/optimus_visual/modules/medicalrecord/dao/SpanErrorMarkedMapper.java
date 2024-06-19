package com.unisound.optimus_visual.modules.medicalrecord.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unisound.optimus_visual.modules.medicalrecord.entity.MarkedDoc;
import com.unisound.optimus_visual.modules.medicalrecord.entity.SpanErrorMarked;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface SpanErrorMarkedMapper extends BaseMapper<SpanErrorMarked> {

    SpanErrorMarked getOne(@Param("hospitalId") String hospitalId,@Param("admissionId") String admissionId,@Param("emrNo") String emrNo,@Param("docName") String docName,@Param("nodeName") String nodeName,@Param("spanTextContent") String spanTextContent,@Param("spanLabel") String spanLabel);
}
