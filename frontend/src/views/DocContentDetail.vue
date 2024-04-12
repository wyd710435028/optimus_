<template>
  <router-view></router-view>
  <el-container>
    <!-- 公共头部 -->
    <el-header style="background-color: #009688;">
      <common-header></common-header>
<!--      <el-text style="cursor: pointer;color: #ffffff;font-size: 20px" @click="returnIndex()">Optimus可视化系统</el-text>-->
    </el-header>
    <el-row class="container">
      <el-button color="#359894" :dark="isDark" style="margin-top: 15px;margin-right: 20px" @click="returnDocQueryList()">
        <el-icon>
          <Back/>
        </el-icon>
        <span>返回</span>
      </el-button>
      <el-row>
      </el-row>
    </el-row>
    <el-row>
<!--      <el-col :span="4">-->
<!--        <el-aside class="layout_col" style="width: unset">-->
<!--        </el-aside>-->
<!--      </el-col>-->
      <el-col :span="24">
        <el-main class="layout_col">
          <div v-if="showContent=='normal'">
            <h2 style="text-align: center;color: #c45656">{{fileName}}</h2>
            <el-divider></el-divider>
            <div v-for="item in nodeList" :key="item">
              <el-row>
                <el-col :span="4"><strong style="color:#4682B4">{{item.nodeName}}</strong></el-col>
                <el-col :span="20">
                  <strong style="color: #34495E">
                    {{item.nodeContent}}
                  </strong>
                </el-col>
                <el-divider></el-divider>
              </el-row>
            </div>
          </div>
          <div v-if="showContent=='statOrder'">
            <h2 style="text-align: center;color: #c45656">临时医嘱</h2>
            <el-row>
              <el-table
                  stripe
                  :height="table"
                  :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                  :data="statOrderTableData"
                  border
                  style="width: 100%;margin:10px">
                <el-table-column width="55px" type="index" label="序号"></el-table-column>
                <el-table-column prop="day" label="日期"></el-table-column>
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
            <h2 style="text-align: center;color: #c45656">长期医嘱</h2>
            <el-row>
              <el-table
                  stripe
                  :height="table"
                  :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                  :data="standingOrderTableData"
                  border
                  style="width: 100%;margin:10px;">
                <el-table-column width="55px" type="index" label="序号"></el-table-column>
                <el-table-column prop="openingTime" label="开立时间"></el-table-column>
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
<!--      <el-col :span="4">-->
<!--        <el-aside class="layout_col" style="width: unset">-->
<!--        </el-aside>-->
<!--      </el-col>-->
    </el-row>
  </el-container>
</template>

<script>
import {docContentDetail, getUnderstandResult} from "../apis/get"
import CommonHeader from "@/views/common/CommonHeader.vue";
import {ArrowRight, Back} from "@element-plus/icons-vue";
export default {
  computed: {
    ArrowRight() {
      return ArrowRight
    }
  },
  components: {Back, CommonHeader},
  data(){
    return{
      showContent:'',//有三个选项: 1.normal->正常文书,2.statOrder->临时医嘱,3.standingOrder->长期医嘱
      nodeList:[
      ],
      fileName:'',
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
      table:window.innerHeight - 107           //固定表头高度
    }
  },
  //script脚本
  methods:{
    getDocContentDetail(){
      var _this = this;
      let hospitalId = _this.$route.params.hospitalId;
      let admissionId = _this.$route.params.admissionId;
      let stage = _this.$route.params.stage;
      let fileId = _this.$route.params.fileId;
      let showContent = _this.$route.params.showContent;
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      // alert(fileId);
      docContentDetail(hospitalId,admissionId,stage,fileId).then(function (response){
        _this.nodeList = response.data.data.nodeList;
        _this.fileName = response.data.data.fileName;
        if (showContent=='statOrder'){
          _this.showContent = 'statOrder';
          _this.statOrderTableData = response.data.data.statOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
        }else if (showContent=='standingOrder'){
          _this.showContent = 'standingOrder';
          _this.standingOrderTableData = response.data.data.standingOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
        }else {
          //正常文书
          _this.nodeList = response.data.data.nodeList;
          _this.fileName = response.data.data.fileName;
        }
      })
      console.log(hospitalId);
    },
    returnDocQueryList(){
      let hospitalId = this.$route.params.hospitalId;
      let admissionId = this.$route.params.admissionId;
      let stage = this.$route.params.stage;
      this.$router.push('/DocQueryList/'+hospitalId+'/'+admissionId+'/'+stage);
    },
    filterHandler(value, row, column) {const property = column["property"];return row[property] === value;},
    returnIndex(){
      this.$router.push('/');
    }
  },
  mounted(){
    this.showContent = this.$route.params.showContent;
    //获取文书内容详情
    this.getDocContentDetail();
  }
}
</script>

<style>
</style>