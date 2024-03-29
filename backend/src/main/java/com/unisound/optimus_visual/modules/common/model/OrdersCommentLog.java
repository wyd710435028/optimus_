package com.unisound.optimus_visual.modules.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 医嘱操作日志
 */
@Data
@EqualsAndHashCode
@TableName("tb_orders_comment_log")
public class OrdersCommentLog {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作用户
     */
    private String userName;

    /**
     * 操作时间
     */
//    private Long startTime;

    /**
     * 消耗时间
     */
//    private Integer spendTime;

    /**
     * 根路径
     */
//    private String basePath;

    /**
     * URI
     */
//    private String uri;

    /**
     * URL
     */
//    private String url;

    /**
     * 请求类型
     */
//    private String method;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 请求参数
     */
//    private Object parameter;

    /**
     * 返回结果
     */
//    private Object result;

    private String fileId;

    private String unisoundId;
}
