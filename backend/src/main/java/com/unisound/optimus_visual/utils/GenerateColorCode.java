package com.unisound.optimus_visual.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

@Slf4j
public class GenerateColorCode {
    /** 获取指定长度的16进制字符串. */
    public static String randomHexStr(int len) {
        try {
            StringBuffer result = new StringBuffer();
            for(int i=0;i<len;i++) {
                //随机生成0-15的数值并转换成16进制
                result.append(Integer.toHexString(new Random().nextInt(16)));
            }
            return result.toString().toUpperCase();
        } catch (Exception e) {
            log.error("获取16进制字符串异常，返回默认...");
            return "00CCCC";
        }
    }

    /**
     * 获取颜色编码
     */
    public static String getColorCodeStr(List<String> colorList,List<String> existColorCodeList,Integer colorListSize){
        if (CollectionUtils.isEmpty(colorList)){
            return null;
        }
        //随机取出一个
        int random = new Random().nextInt(colorList.size());
        String colorCode =  colorList.get(random);

        if (CollectionUtils.isEmpty(existColorCodeList)){
            //existColorCode为空，随机取一个即可
            return colorCode;
        }
//        if (existColorCodeList.size()<=colorList.size()&&existColorCodeList.contains(colorCode)&&colorListSize>0){
//            colorListSize--;
//            colorCode = getColorCodeStr(colorList,existColorCodeList,colorListSize);
//        }
        for (int i = 0;i<colorListSize && existColorCodeList.contains(colorCode);i++){
//            colorCode = getColorCodeStr(colorList,existColorCodeList,colorListSize);
            colorCode =  colorList.get(new Random().nextInt(colorList.size()));
        }
        return colorCode;
    }
}
