package com.unisound.optimus_visual.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 关于参数的工具类
 */
public class ParamUtils {

    public static JSONObject getCommonParams(String param){
        if (StringUtils.isBlank(param)){
            return null;
        }
        JSONObject paramsJson = JSON.parseObject(param);
        JSONObject jsonObject = (JSONObject) paramsJson.get("params");
        return jsonObject;
    }
}
