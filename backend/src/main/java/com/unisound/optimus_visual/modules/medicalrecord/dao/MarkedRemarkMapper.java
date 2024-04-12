package com.unisound.optimus_visual.modules.medicalrecord.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unisound.optimus_visual.modules.medicalrecord.entity.MarkedDoc;
import com.unisound.optimus_visual.modules.medicalrecord.entity.MarkedRemark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface MarkedRemarkMapper extends BaseMapper<MarkedRemark> {

    List<MarkedRemark> getByFileId(@Param("fileId") String fileId);

    List<MarkedRemark> getByHospitalAndAdmissionId(@Param("hospitalId") String hospitalId,@Param("admissionId") String admissionId);
}
