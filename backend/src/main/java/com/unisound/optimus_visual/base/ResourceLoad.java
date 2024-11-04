package com.unisound.optimus_visual.base;

import com.unisound.optimus_visual.modules.medicalrecord.model.ShowLabelModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

@Component
@Slf4j
public class ResourceLoad {

    /**
     * 医院信息Map: key->医院编号,value->医院名称
     */
    public static Map<String,String> globalHospitaiMap = new LinkedHashMap<>();

    public static Map<String,String> globalTreeMapping = new LinkedHashMap<>();

    /**
     * 全局的label映射Map
     */
    public static Map<String,String> globalLabelMapping = new LinkedHashMap<>();

    /**
     * 颜色list(用于标签背景颜色显示)
     */
    public static List<String> colorList = new ArrayList<>();

    @Autowired
    ResourceLoader resourceLoader;
    @Value("${BaseResourceUrl:}")
    String baseResourceUrl;

    @PostConstruct
    public void initGlobalHospitaiMap(){
        //读取hospital文件内容,并放入globalHospitaiMap
        //todo 路径改为可配置
//        File file = new File("D:\\wyd\\dev_projects\\optimus_visual\\backend\\src\\main\\resources\\hospital.txt");
//        Resource resource = resourceLoader.getResource("classpath:hospital.txt");
//        File file = resource.getFile();
        String url = baseResourceUrl + File.separator + "hospital.txt";
        File file = new File(url);
//        InputStream inputStream = resource.getInputStream();
//        File file = new ClassPathResource("hospital.txt").getFile();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("#")||StringUtils.isBlank(line)) {
                    continue;
                }
                String[] columns = line.split("\t");
                if (columns.length == 2){
                    globalHospitaiMap.put(columns[0],columns[1]);
                }
            }
            log.info("加载了{}个医院的编号信息",globalHospitaiMap.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void initGlobalTreeMapping(){
//        Resource resource = resourceLoader.getResource("classpath:treeMapping.txt");
//        File file = resource.getFile();
//        InputStream inputStream = resource.getInputStream();
//        File file = new ClassPathResource("treeMapping.txt").getFile();
//        File file = new File("D:\\wyd\\dev_projects\\optimus_visual\\backend\\src\\main\\resources\\treeMapping");
//        File file = ResourceUtils.getFile("classpath:treeMapping.txt");
        String url = baseResourceUrl + File.separator + "treeMapping.txt";
        File file = new File(url);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("#")) {
                    continue;
                }
                String[] columns = line.split("\t");
                globalTreeMapping.put(columns[0],columns[1]);
            }
            log.info("加载了{}个文书分组",globalTreeMapping.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化颜色编码
     */
    @PostConstruct
    public void initColorCodeList(){
        String url = baseResourceUrl + File.separator + "color.txt";
        File file = new File(url);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                colorList.add(line);
            }
            log.info("加载了{}个颜色编码信息",colorList.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化标签映射信息
     */
    @PostConstruct
    public void initLabelMapping(){
        String url = baseResourceUrl + File.separator + "labelMapping.txt";
        File file = new File(url);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                //#号开头的忽略,作为注释
                if(line.startsWith("#")) {
                    continue;
                }
                String[] columns = line.split("\t");
                globalLabelMapping.put(columns[0],columns[1]);
            }
            log.info("加载了{}个标签映射信息",globalLabelMapping.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将labelList的内容转换为中文
     * @param labelModelList
     */
    public static void convertLabelListContentToChinese(List<ShowLabelModel> labelModelList){
        //根据labelMapping进行转换,英文转换为中文
        for (ShowLabelModel showLabelModel:labelModelList){
            String labelContent = showLabelModel.getLabelContent();
            if (globalLabelMapping.containsKey(labelContent)){
                String labelChineseName = globalLabelMapping.get(labelContent);
                if (StringUtils.isNotBlank(labelChineseName)){
                    showLabelModel.setLabelChineseName(labelChineseName);
                }
            }else {
                showLabelModel.setLabelChineseName(labelContent);
            }
        }
    }

    /**
     * 获取label中文名称
     * @param labelContent
     * @return
     */
    public static String getLabelChineseName(String labelContent){
        if (StringUtils.isBlank(labelContent)){
            return "";
        }
        if (globalLabelMapping.containsKey(labelContent)){
            return globalLabelMapping.get(labelContent);
        }
        return labelContent;
    }

}
