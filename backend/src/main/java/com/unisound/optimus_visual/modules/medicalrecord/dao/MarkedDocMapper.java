package com.unisound.optimus_visual.modules.medicalrecord.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unisound.optimus_visual.modules.medicalrecord.entity.MarkedDoc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface MarkedDocMapper extends BaseMapper<MarkedDoc> {

    /**
     * 根据fileId查询: 带删除状态条件
     *
     * @param fileId
     * @return
     */
    List<MarkedDoc> getByFileIdWithDeleteFlagCondition(@Param("fileId") String fileId);

    /**
     * 根据fileId查询: 不判断删除状态条件
     *
     * @param fileId
     * @return
     */
    List<MarkedDoc> getAllByFileId(@Param("fileId") String fileId);

    List<MarkedDoc> getByHospitalAndAdmissionId(@Param("hospitalId") String hospitalId,@Param("admissionId") String admissionId);
}
