package com.unisound.optimus_visual.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 高亮文本工具类
 */
@Slf4j
public class Highlighter {
    /**
     * 正则匹配的方式
     * @param text 文本内容
     * @param keyMap key:关键字,value:颜色
     * @return
     */
    public static String highlightKeywords(String text, Map<String,String> keyMap) {
        StringBuilder sb = new StringBuilder();
        String re = "";
        if (!CollectionUtils.isEmpty(keyMap)){
            for (String keyword:keyMap.keySet()){
                if (StringUtils.isNotBlank(re)){
                    re=re+"|"+keyword;
                }else {
                    re=keyword;
                }
            }
        }
        //处理"*"导致的正则匹配问题
//        String regex = "[`_《》!@#$%^&*()+=|{}':;',\\[\\].<>?！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//        re.replaceAll(regex,"");
        String s = re.replaceAll("\\*", "\\\\*").replaceAll("\\(","\\\\(").replaceAll("\\)","\\\\)").replaceAll("\\+","\\\\+").replaceAll("-","\\-").replaceAll("、","\\、");
        Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        int lastEnd = 0;
        while (matcher.find()) {
            sb.append(text, lastEnd, matcher.start());
            int start =matcher.start();
            int end = matcher.end();
            String substring = text.substring(start, end);
            String labelNameAndColor = StringUtils.isBlank(keyMap.get(substring))?"":keyMap.get(substring);
            String[] split = labelNameAndColor.split("&");
            String color = split[0];
            String labelName = split.length==2?split[1]:"";
            sb.append("<span title='点击跳转到实体链接页面' style='background-color: "+color+"'>");
            sb.append("<a style='color:black;text-decoration: none' target='_blank' href=#/EntityLinkJump/"+substring+"/"+labelName+">");
//            sb.append("<a style='color:black;text-decoration: none' onclick='showEntityLink("+substring+","+(StringUtils.isBlank(labelName)?"noValue":labelName)+")'"+">");
//            sb.append("<el-text @click=\"showEntityLink("+substring+","+labelName+")\"></el-text>");
//            log.info("<a style='color:black;text-decoration: none' href= 'javascript:void(0)' onclick='showEntityLink("+substring+","+(StringUtils.isBlank(labelName)?"noValue":labelName)+")'"+">");
            sb.append(text, matcher.start(), matcher.end());
            sb.append("</a>");
            sb.append("</span>");
            lastEnd = matcher.end();
        }

        sb.append(text.substring(lastEnd));
        return sb.toString();
    }

    /**
     * 高亮一段文本
     * @param text 文本
     * @param keyMap key:关键字 value:颜色
     * @return 高亮之后的文本
     */
    public static String hightLightUtils(String text,Map<String,String> keyMap){
        String res = text;
        if (!CollectionUtils.isEmpty(keyMap)){
            Set<String> keySet = keyMap.keySet();
            //开始处理
            for (String key:keySet){
                if (text.contains(key)){
                    String color = keyMap.get(key);
                    res = res.replaceAll(key,"<span style='background-color: "+color+"'>"+"<el-link href=\"www.baidu.com\">"+key+"</el-link>"+"</span>");
                }
            }
        }
        return res;
    }
}

