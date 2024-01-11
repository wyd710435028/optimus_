package com.unisound.optimus_visual.utils;

import java.io.*;

/**
 * 文件操作工具类
 */
public class FileUtils {

    public static void combineTheLastTwoColumnsInTxt(String txtUrl){
        StringBuffer sb = new StringBuffer();
        File file = new File(txtUrl);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                if (split.length==3){
                    String split2 = split[2];
                    if (split[2].contains("；")){
                         split2 = split[2].replaceAll("；", "|");
                    }
                    String str = split[0]+"\t"+split[1]+"|"+split2;
                    sb.append(str);
                    sb.append("\n");
                }else {
                    sb.append(line);
                    sb.append("\n");
                }
            }
            //写入txt文件
            outputTxtByStringBuffer(sb,"D:\\combined.txt");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将
     * @param stringBuffer
     * @param outPutUrl
     */
    public static void outputTxtByStringBuffer(StringBuffer stringBuffer,String outPutUrl){
        if (stringBuffer!=null&&stringBuffer.length()>0){
            File newFile = new File(outPutUrl);
            try {
                FileWriter fileWriter = new FileWriter(newFile);
                fileWriter.write(stringBuffer.toString());
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
