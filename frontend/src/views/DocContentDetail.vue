<template>
  <router-view></router-view>
  <el-container>
    <el-header style="background-color: #009688;cursor: pointer" @click="returnIndex()">Optimus可视化系统</el-header>
    <el-row class="container">
      <el-button color="#359894" :dark="isDark" style="margin-top: 15px;margin-right: 20px" @click="returnDocQueryList()">返回</el-button>
    </el-row>
    <el-row>
<!--      <el-col :span="4">-->
<!--        <el-aside class="layout_col" style="width: unset">-->
<!--        </el-aside>-->
<!--      </el-col>-->
      <el-col :span="24">
        <el-main class="layout_col">
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
export default {
  data(){
    return{
      showContent:'normal',//有三个选项: 1.normal->正常文书,2.statOrder->临时医嘱,3.standingOrder->长期医嘱
      nodeList:[
      ],
      fileName:''
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
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      // alert(fileId);
      docContentDetail(hospitalId,admissionId,stage,fileId).then(function (response){
        _this.nodeList = response.data.data.nodeList;
        _this.fileName = response.data.data.fileName;
      })
      console.log(hospitalId);
    },
    returnDocQueryList(){
      let hospitalId = this.$route.params.hospitalId;
      let admissionId = this.$route.params.admissionId;
      let stage = this.$route.params.stage;
      this.$router.push('/DocQueryList/'+hospitalId+'/'+admissionId+'/'+stage);
    },
    returnIndex(){
      this.$router.push('/');
    }
  },
  mounted(){
    //获取文书内容详情
    this.getDocContentDetail();
  }
}
</script>

<style>
</style>