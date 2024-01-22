<template>
  <router-view></router-view>
  <el-container>
    <el-header style="background-color: #009688;">
      <el-text style="cursor: pointer;color: #ffffff;font-size: 20px" @click="returnIndex()">Optimus可视化系统</el-text>
    </el-header>
    <el-row class="container">
      <el-button color="#359894" :dark="isDark" style="margin-top: 15px;margin-right: 20px" @click="returnHomePage()">返回</el-button>
    </el-row>
    <el-row>
      <el-col :span="4">
        <el-aside class="layout_col" style="width: unset">
          <el-tree :data="treeData" :props="defaultProps" @node-click="getDocByFileId" style="font-size: 18px"></el-tree>
        </el-aside>
      </el-col>
      <el-col :span="16">
        <el-main class="layout_col">
          <div v-if="showContent=='normal'">
            <div v-for="item in nodeList" :key="item">
              <el-row>
                <el-col :span="4"><strong style="color: indianred">{{item.nodeName}}</strong></el-col>
                <el-col :span="20">
<!--                  <div v-if="item.entityNum==0">-->
<!--                    <label>{{item.nodeContent}}</label>-->
<!--                  </div>-->
<!--                  <div v-if="item.entityNum>0">-->
                    <label v-html=item.entityHightLighted></label>
<!--                  </div>-->
                  <el-row>
                    <el-table
                        :data="tableData"
                        border
                        style="width: 100%;margin:10px">
                      <el-table-column fixed prop="entityNum" label="entity数量">
                        <template v-slot="{}">
                          <el-link type="text" @click="toDocUnderstandDetail(item.nodeName,item.nodeContent,item.entityHightLighted,item.spanHightLighted,item.eventHightLighted,item.entityLabelList,item.spanLabelList,item.eventList,item.fileId,item.entityList,item.spanList)">{{item.entityNum}}</el-link>
                        </template>
                      </el-table-column>
                      <el-table-column prop="eventNum" label="event数量">
                        <template v-slot="{}">
                          <el-link type="text" @click="toDocUnderstandDetail(item.nodeName,item.nodeContent,item.entityHightLighted,item.spanHightLighted,item.eventHightLighted,item.entityLabelList,item.spanLabelList,item.eventList,item.fileId,item.entityList,item.spanList)">{{item.eventNum}}</el-link>
                        </template>
                      </el-table-column>
                      <el-table-column prop="spanNum" label="span数量">
                        <template v-slot="{}">
                          <el-link type="text" @click="toDocUnderstandDetail(item.nodeName,item.nodeContent,item.entityHightLighted,item.spanHightLighted,item.eventHightLighted,item.entityLabelList,item.spanLabelList,item.eventList,item.fileId,item.entityList,item.spanList)">{{item.spanNum}}</el-link>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-row>
                </el-col>
              </el-row>
              <el-divider style="background-color: #c45656"></el-divider>
            </div>
          </div>
          <div v-if="showContent=='statOrder'">
            <el-row>
              <el-text style="color: #529b2e;font-weight: bold" class="mx-1" size="large" tag="ins">临时医嘱</el-text>
            </el-row>
            <el-row>
              <el-table
                  :data="statOrderTableData"
                  border
                  stripe
                  :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                  style="width: 100%;margin:10px">
                <el-table-column fixed prop="day" label="日期"></el-table-column>
                <el-table-column prop="time" label="时间"></el-table-column>
                <el-table-column prop="content" label="医嘱内容"></el-table-column>
                <el-table-column prop="physicianSign" label="医师签名"></el-table-column>
                <el-table-column prop="executeTime" label="执行时间"></el-table-column>
                <el-table-column prop="executorSign" label="执行人签名"></el-table-column>
                <el-table-column prop="yzsProjectType" label="云知声项目类别" :filters="yzsProjectType" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="projectCategories" label="项目大类" :filters="projectType" :filter-method="filterHandler"></el-table-column>
              </el-table>
            </el-row>
          </div>
          <div v-if="showContent=='standingOrder'">
            <el-row>
              <el-text style="color: #529b2e;font-weight: bold" class="mx-1" type="warning" size="large" tag="ins">长期医嘱</el-text>
            </el-row>
            <el-row>
              <el-table
                  :data="standingOrderTableData"
                  border
                  stripe
                  :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                  style="width: 100%;margin:10px;">
                <el-table-column fixed prop="openingTime" label="开立时间"></el-table-column>
                <el-table-column prop="startTime" label="开始时间"></el-table-column>
                <el-table-column prop="content" label="医嘱内容"></el-table-column>
                <el-table-column prop="openingPhysicianSign" label="开立医师签名"></el-table-column>
                <el-table-column prop="executeTime" label="执行时间"></el-table-column>
                <el-table-column prop="executorSign" label="执行人签名"></el-table-column>
                <el-table-column prop="stopTime" label="停止时间"></el-table-column>
                <el-table-column prop="stopPhysicianSign" label="停止医师签名"></el-table-column>
                <el-table-column prop="stopExecutorSign" label="停止执行人签名"></el-table-column>
                <el-table-column prop="yzsProjectType" label="云知声项目类别" :filters="yzsProjectType" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="projectCategories" label="项目大类" :filters="projectType" :filter-method="filterHandler"></el-table-column>
              </el-table>
            </el-row>
          </div>
        </el-main>
      </el-col>
      <el-col :span="4">
        <el-aside class="layout_col">
<!--          <span>entity标签列表</span>-->
          <div v-for="entityTag in allEntityLabelList" :key="entityTag">
            <el-tag v-if="entityTag.labelType=='entity'" :color="entityTag.labelColor" style="margin-top: 5px" @click="clickTag(entityTag.labelContent,entityTag.labelColor)"><label style="color:#303133">{{entityTag.labelContent}}</label></el-tag>
          </div>
        </el-aside>
      </el-col>
    </el-row>
  </el-container>

  <el-dialog title="实体链接弹窗" v-model="dialogVisible" width="35%">
    <el-card class="box-card">
      <div v-for="(value, key) in encyclopedia" :key="key" class="text item">
        <div style="font-weight: bold;font-size: 18px; margin-top: 12px; margin-bottom: 4px;">
          {{key}}
        </div>
        <div style="white-space: pre-wrap;">
          {{value}}
        </div>
      </div>
    </el-card>
  </el-dialog>
</template>
<script>
import {getUnderstandResult} from "../apis/get";
import {getNodeByFileId} from "../apis/post";
import {transLabelList} from "../apis/post";
import {getNodeByFileIdWithHighLight} from "../apis/post";
import {ElMessage} from "element-plus";
import entitylinkDialog from "./EntityLinkJump"
import { ref } from 'vue'

const dialogVisible = ref(false);
export default {
  components:{
    entitylinkDialog
  },
  data() {
    return {
      //左侧树结构
      treeData: [
      ],
      defaultProps: {
        children: 'children',
        label: 'label',
        fileId:'fileId',
        docType:'docType'
      },
      showContent:'',//有三个选项: 1.normal->正常文书,2.statOrder->临时医嘱,3.standingOrder->长期医嘱
      statOrderTableData:[{
        day:'',
        time:'',
        content:'',
        physicianSign:'',
        executeTime:'',
        executorSign:'',
        projectCategories:''
      }],//临时医嘱
      standingOrderTableData:[{
        openingTime:'',
        startTime:'',
        content:'',
        openingPhysicianSign:'',
        executeTime:'',
        executorSign:'',
        stopTime:'',
        stopPhysicianSign:'',
        stopExecutorSign:'',
        projectCategories:''
      }],//长期医嘱
      projectType:[
        // { text: '检验', value: '检验' },
        // { text: '2016-05-02', value: '2016-05-02' },
        // { text: '2016-05-03', value: '2016-05-03' },
      ],
      yzsProjectType:[
        // { text: '检验', value: '检验' },
        // { text: '2016-05-02', value: '2016-05-02' },
        // { text: '2016-05-03', value: '2016-05-03' },
      ],
      //数量表格数据
      tableData: [{
        entityNum: '3',
        eventNum: '2',
        spanNum: '3',
      }],
      selectNodeName:"",
      formatedDocMap:{},
      nodeList:[
        // {
        //   "nodeName":"一般情况/aa",
        //   "nodeContent":"asdjasdasdasdasdasdasa",
        //   "entityNum":36,
        //   "eventNum":4,
        //   "spanNum":3
        // }
      ],
      //右侧总标签数据
      allEntityLabelList:[
        // {
        //   "labelColor":"#b3e19d",
        //   "labelContent":"标签一"
        // },
        // {
        //   "labelColor":"#eebe77",
        //   "labelContent":"标签二"
        // },
        // {
        //   "labelColor":"#f89898",
        //   "labelContent":"标签三"
        // }
      ],
      allSpanLabelList:[],
      hightLight:'',
      docDetail:{},
      entityLabelList:[],
      spanLabelList:[],
      docType:'',
      fileId:'',
      Visiable:false,
      detailVisible:false
    }
  },
  methods: {
    //获取病历下所有文书
    getAllDoc(){
      let hospitalId = this.$route.params.hospitalId;
      let admissionId = this.$route.params.admissionId;
      let stage = this.$route.params.stage;
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      let _this = this;
      getUnderstandResult(hospitalId,admissionId,stage).then(function (response){
        console.log(response);
        _this.treeData=response.data.data.treeData;
        _this.allEntityLabelList=response.data.data.entityLabels;
        _this.allSpanLabelList=response.data.data.spanLabels;
        // _this.formatedDocMap=response.data.data.formatedDocMap;
        _this.nodeMap=response.data.data.docList;
        _this.docDetail=response.data.data;
      })

    },

    /**
     * 获取指定文书
     * @param data
     */
    getDocByFileId: function (data) {
      if (data.children == null) {
        // let docName = data.label;
        // let hospitalId = this.$route.params.hospitalId;
        // let admissionId = this.$route.params.admissionId;
        // let stage = this.$route.params.stage;
        //todo 调用后台查询对应文书
        // alert(docName);
        // alert(hospitalId);
        // alert(admissionId);
        // alert(stage);
        let fId = data.fileId;
        this.fileId = fId;
        // alert(fId);
        let docType = data.docType;
        this.docType = docType;
        // alert(docType);
        let _this = this;
        console.log("----------总的结果----------");
        console.log(_this.nodeMap);
        getNodeByFileId(fId, JSON.stringify(_this.nodeMap)).then(function (response) {
          if (docType=='EMR110001'){
            //长期医嘱
            _this.showContent = 'standingOrder';
            _this.standingOrderTableData = response.data.data.standingOrderList;
            _this.projectType = response.data.data.projectCategoriesList;
            _this.yzsProjectType = response.data.data.yzsProjectTypeList;
          }else if (docType=='EMR110002'){
            //临时医嘱
            _this.showContent = 'statOrder';
            _this.statOrderTableData = response.data.data.statOrderList;
            _this.projectType = response.data.data.projectCategoriesList;
            _this.yzsProjectType = response.data.data.yzsProjectTypeList;
          }else {
            //正常文书
            _this.showContent = 'normal';
            _this.nodeList = response.data.data.nodeList;
            _this.allEntityLabelList = response.data.data.labelList;
          }
        })
      } else {
        return;
      }
    },
    addHightLight(nodeContent){
      // this.hightLight="<span style='background-color: #409EFF;'>病史</span>及辅检无特殊补充。。";
      // alert(nodeContent);
      this.hightLight=nodeContent;
      // document.getElementById('elseHigh').innerHTML = nodeContent;
      // this.$refs.elseHigh.innerHTML=nodeContent;
    },

    toDocUnderstandDetail(nodeName,nodeContent,entityHightLighted,spanHightLighted,eventHightLighted,entityLabelList,spanLabelList,eventList,fileId,entityList,spanList){
      let hospitalId = this.$route.params.hospitalId;
      let admissionId = this.$route.params.admissionId;
      let stage = this.$route.params.stage;
      // alert(recoreId);
      // let str = JSON.stringify(this.allEntityLabelList);
      let  entityLabelListStr = JSON.stringify(entityLabelList);
      let spanLabelListStr = JSON.stringify(spanLabelList);
      let eventListStr = JSON.stringify(eventList);
      let entityListStr = JSON.stringify(entityList);
      let  spanListStr = JSON.stringify(spanList);
      // alert("传递的参数:"+str);
      // console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+str)
      // this.$router.push({
      //   name:'MedicalNodeDetail',
      //   params:{
      //     nodeName:nodeName,
      //     entityHightLighted:entityHightLighted,
      //     spanHightLighted:spanHightLighted,
      //     eventHightLighted:eventHightLighted,
      //     entityLabelList:entityLabelListStr,
      //     spanLabelList:spanLabelListStr,
      //     eventList:eventListStr
      //   }});
      this.$router.push(
          {
            name:'MedicalNodeDetail',
            params:{
              nodeName:nodeName,
              nodeContent:nodeContent,
              entityHightLighted:entityHightLighted,
              spanHightLighted:spanHightLighted,
              eventHightLighted:eventHightLighted,
              entityLabelList:entityLabelListStr,
              spanLabelList:spanLabelListStr,
              eventList:eventListStr,
              entityList:entityListStr,
              spanList:spanListStr,
              hospitalId:hospitalId,
              admissionId:admissionId,
              stage:stage
            }
        });
    },
    filterHandler(value, row, column) {const property = column["property"];return row[property] === value;},
    returnHomePage(){
      let hospitalId = this.$route.params.hospitalId;
      // let admissionId = this.$route.params.admissionId;
      // let stage = this.$route.params.stage;
      let pageSize = this.$route.params.pageSize;
      let currentPage = this.$route.params.currentPage;
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      // alert("返回首页!");
      this.$router.push('/MedicalRecordList/'+hospitalId);
      // this.$router.push('/');
      // this.$router.back();
    },
    beforeRouteLeave:(to,from,next)=>{
      if (confirm("确定离开此页面吗?")==true){
        next();
      }else {
        next(false);
      }
    },

    /**
     * 点击标签触发的事件
     * @param labelContent
     */
    clickTag(labelContent,labelColor) {
      let _this = this;
      let docType = _this.docType;
      let fileId = _this.fileId;
      getNodeByFileIdWithHighLight(fileId,labelContent,labelColor,JSON.stringify(_this.nodeMap)).then(function (response) {
        if (docType=='EMR110001'){
          //长期医嘱
          _this.showContent = 'standingOrder';
          _this.standingOrderTableData = response.data.data.standingOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
        }else if (docType=='EMR110002'){
          //临时医嘱
          _this.showContent = 'statOrder';
          _this.statOrderTableData = response.data.data.statOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
        }else {
          //正常文书
          _this.showContent = 'normal';
          _this.nodeList = response.data.data.nodeList;
          _this.allEntityLabelList = response.data.data.labelList;
        }
      })
    },
    returnIndex(){
      this.$router.push('/');
    }
  },
  mounted(){
    //todo 加载初始化操作
    this.getAllDoc();
  }
};
</script>
<style>
  .el-header{
    background-color: #409EFF;
    color: #ffffff;
    text-align: left;
    line-height: 60px;
  }
  .side-bar {
    width: 30%;
  }
  .layout_col{
    height: 1060px;
    border: 1px solid;
    border-color:#EBEDF0;
    margin-top: 20px;
  }
  .container {
    display: flex;
    justify-content: flex-end;
  }
</style>