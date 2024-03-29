package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HistogramSeries {
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
    String name;
    String type;
    List<Integer> data;
    Map<String,String> itemStyle;
}
