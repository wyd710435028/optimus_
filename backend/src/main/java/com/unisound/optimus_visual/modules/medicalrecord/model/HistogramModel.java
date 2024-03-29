package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Echarts 柱状图数据通用实体
 */
@Data
public class HistogramModel {
//    spanOption:{
//        title: {
//            text: 'Span数统计: x->文书组名称,y->文书组下的Span数'
//        },
//        tooltip: {},
//        xAxis: {
//            data: ['衬衫', '羊毛衫', '雪纺衫', '裤子', '高跟鞋', '袜子']
//        },
//        yAxis: {},

//        series: [
//        {
//            name: '个数',
//            type: 'bar',
//            data: [5, 20, 36, 10, 116, 20],
//            itemStyle: {
//                borderWidth: 1,
//            }
//        }
//        ]
//    },
    /**
     * title中的key:text,value:对应的中文
     */
    Map<String,String> title;

    Map tooltip;

    /**
     * xAxis key->data,value->文书组
     */
    List<String> xAxis;

    /**
     * y轴
     */
    Map yAxis;

    List<HistogramSeries> series;

}
