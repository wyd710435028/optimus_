package com.unisound.optimus_visual.modules.medicalrecord.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_marked_doc")
public class MarkedDoc {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 医院编号
     */
    private String hospitalNo;

    /**
     * 流水号
     */
    private String admissionNo;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 删除状态:1->已删除，0->未删除 操作人
     */
    private Long deleteFlag;

}
