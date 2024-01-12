<template>
  <router-view></router-view>
  <el-container>
    <el-header style="background-color: #009688;cursor: pointer" @click="returnIndex()">Optimus可视化系统</el-header>
    <el-main style="border: 2px">
      <el-row>
        <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <el-form-item label="医院编号">
            <el-select
                v-model="hospitalNo"
                clearable
                filterable
                allow-create
                default-first-option
                placeholder="请选择医院编号">
              <el-option v-for="item in options"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="流水号">
            <el-input v-model="admissionId" placeholder="请输入流水号" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button color="#009688" @click="queryList">查询</el-button>
          </el-form-item>
        </el-form>
      </el-row>
      <el-row>
        <el-table :data="tableData" border style="width: 100%">
          <el-table-column fixed type="index" label="序号" width="60">
          </el-table-column>
          <el-table-column
              prop="admissionId"
              label="流水号">
          </el-table-column>
          <el-table-column
              prop="hospitalName"
              label="所属医院">
          </el-table-column>
          <el-table-column
              prop="stage"
              label="病历阶段">
          </el-table-column>
          <el-table-column
              prop="timeStamp"
              label="病历理解时间">
          </el-table-column>
          <el-table-column
              fixed="right"
              label="操作">
            <template v-slot="scope">
              <el-row>
                <el-button @click="reUnderstand(scope.row)" type="danger" :loading="isSending">{{reUnderstandButtonName}}</el-button>
                <el-button @click="docUnderstandResult(scope.row)" type="success">病历详情</el-button>
                <el-button @click="docQuery(scope.row)" type="warning">文书查询</el-button>
              </el-row>
            </template>
          </el-table-column>
        </el-table>
      </el-row>
    </el-main>
    <el-footer>
      <div class="block">
        <el-pagination
            @current-change="handleCurrentChange"
            :current-page="pagination.currentPage"
            :page-size="pagination.pageSize"
            layout="->,total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            :page-sizes="[10,50,100,200,300,500]"
            @size-change="handleSizeChange">
        </el-pagination>
      </div>
    </el-footer>
  </el-container>
</template>
<script>
import {getRecordList} from "../apis/get";
import {getHospitalDropDown} from "../apis/get"
import axios from "axios";
import {ElMessage} from "element-plus";
// import {} from "../apis/post";
export default {
  data() {
    return {
      tableData: [],
      options: [],
      admissionId:'',
      hospitalNo:'',
      pagination: {
        currentPage: null,
        pageSize: null,
        total: null
      },
      currentPageJudge:1, //0表示用户自己选的,1表示返回的
      isSending:false,
      reUnderstandButtonName:'重新理解',
      understandStatus:''
    }
  },
  methods:{
    //获取医院下拉框信息
    hospitalDropDown(){
      var _this = this;
      getHospitalDropDown().then(function (response){
        console.log(response);
        _this.options=response.data.data;
      })
    },
    //查询列表
    queryList(){
      var _this = this;
      let hospitalId = this.$route.params.hospitalId;
      // let admissionId = this.$route.params.admissionId;
      // let stage = this.$route.params.stage;
      // let pageSize = this.$route.params.pageSize;
      // let currentPage = this.$route.params.currentPage;
      // alert("pagesize:"+pageSize);
      // alert("currentPage:"+currentPage);
      if (_this.hospitalNo ==null ||_this.hospitalNo==''){
        _this.hospitalNo = hospitalId;
      }
      // if (_this.pagination.pageSize == null){
      //   _this.pagination.pageSize = pageSize;
      // }
      // if (_this.currentPageJudge == 1){
      //   _this.pagination.currentPage = currentPage;
      // }
      // if (_this.currentPageJudge !=0&&_this.currentPageJudge!=1){
      //   _this.pagination.currentPage = 1;
      // }
      // if (_this.admissionId == null || _this.admissionId==''){
      //   _this.admissionId = admissionId;
      // }
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      // alert('ho:'+this.hospitalNo);
      // alert('ad:'+this.admissionId);
      getRecordList(_this.hospitalNo,_this.admissionId,_this.pagination.pageSize,_this.pagination.currentPage).then(function (response){
        _this.tableData=response.data.data.records;
        _this.pagination.pageSize=response.data.data.size;
        _this.pagination.currentPage=response.data.data.current;
        _this.pagination.total=response.data.data.total;
      })
    },
    reUnderstand(row) {
      // alert(row);
      console.log(row);
      let hospitalId = row.hospitalId;
      let admissionId = row.admissionId;
      let hospitalName = row.hospitalName;
      let srtage = row.stage;
      var _this = this;
      _this.isSending = true;
      _this.reUnderstandButtonName = '理解中';
      //发送请求
      // alert('http://10.128.3.237:8851/optimus/test/understand/patient/'+hospitalId+'/'+admissionId+'?esRead=false&esWrite=true&scene='+srtage);
      axios.get('/reUnderstand/optimus/test/understand/patient/'+hospitalId+'/'+admissionId+'?esRead=false&esWrite=true&scene='+srtage).then(function(response){
        console.log(response);
        _this.understandStatus=response.status;
        // alert(_this.understandStatus);
        if (_this.understandStatus=='200'){
          _this.reUnderstandButtonName = '重新理解';
          _this.isSending = false;
          ElMessage({
            showClose: true,
            message: '【'+hospitalId+'】'+hospitalName+'医院,'+',流水号【'+admissionId+'】理解成功!',
            type: 'success',
            duration: 3 * 1000
          })
        }else {
          ElMessage({
            showClose: true,
            message: '【'+hospitalId+'】'+hospitalName+'医院,'+',流水号【'+admissionId+'】理解失败,请联系Optimus管理员!',
            type: 'error',
            duration: 3 * 1000
          })
        }
      });
    },
    //跳转到文书列表MedicalDocList页面
    docUnderstandResult(row){
      // alert(row.hospitalId);
      // alert(row.admissionId);
      // alert(row.stage);
      var _this = this;
      // var pageSize = _this.pagination.pageSize;
      // var currentPage = _this.pagination.currentPage;
      this.$router.push('/MedicalDocList/'+row.hospitalId+'/'+row.admissionId+'/'+row.stage);
    },
    docQuery(row){
      // alert(row.hospitalId);
      // alert(row.admissionId);
      // alert(row.stage);
      this.$router.push('/DocQueryList/'+row.hospitalId+'/'+row.admissionId+'/'+row.stage);
    },
    //分页插件方法
    //切换当前页
    handleCurrentChange(val) {
      // alert(val);
      this.pagination.currentPage = val;
      this.currentPageJudge = 0;
      this.queryList();
    },
    //切换每页显示条数
    handleSizeChange(val) {
      // alert(`每页 ${val} 条`);
      this.pagination.pageSize = val;
      this.queryList();
    },
    // setCurrentJudge(e){
    //   alert(e.target.value);
    //   if(e.target.value){
    //     this.currentPageJudge =2;
    //   }
    // }
    returnIndex(){
      this.$router.push('/');
    }
  },
  mounted() {
    this.hospitalDropDown();
    this.queryList();
  }
}
</script>
<style>
.el-header{
  background-color: #409EFF;
  color: #ffffff;
  text-align: left;
  line-height: 60px;
  font-size: 20px;
//font-family:"微软雅黑";
}
.el-footer {
  line-height: 60px;
}
.el-main {
}
</style>