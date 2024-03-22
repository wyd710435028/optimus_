<template>
  <router-view></router-view>
  <el-container>
    <!-- 公共头部 -->
    <common-header></common-header>
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
                         :value="item.value"></el-option>
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
              label="操作"
              width="520px"
          >
            <template v-slot="scope">
              <el-row>
                <el-button @click="reUnderstand(scope.row)" type="danger" :loading="scope.row.isSending">{{scope.row.reUnderstandButtonName==null?'重新理解':scope.row.reUnderstandButtonName}}</el-button>
                <el-button @click="docUnderstandResult(scope.row)" type="success">病历详情</el-button>
                <el-button @click="docQuery(scope.row)" type="warning">文书查询</el-button>
                <el-button @click="eventQuery(scope.row)" color="#478A97">事件查询</el-button>
                <el-button @click="commentDetail(scope.row)" color="#BA4A00">评论详情</el-button>
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
    <el-drawer
        v-model="openCommentDetail"
        title="评论详情"
        direction="ltr"
        size="50%"
    >
    <CommentList></CommentList>
    </el-drawer>
  </el-container>
<!--  <el-row>-->
<!--    <CommentList></CommentList>-->
<!--  </el-row>-->
</template>
<script>
  import {getRecordList} from "../apis/get";
  import {getHospitalDropDown} from "../apis/get"
  import axios from "axios";
  import {reactive} from "vue";
  import {ElMessage} from "element-plus";
  import CommentList from "@/views/CommentList.vue";
  import CommonHeader from "@/views/common/CommonHeader.vue";
  // import {} from "../apis/post";
  export default {
    components: {CommentList,CommonHeader},
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
        understandStatus:'',
        openCommentDetail:false,
        dialogVisible:false
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
        // alert('ho:'+this.hospitalNo);
        // alert('ad:'+this.admissionId);
        getRecordList(_this.hospitalNo,_this.admissionId,_this.pagination.pageSize,_this.pagination.currentPage).then(function (response){
          _this.tableData=response.data.data.records;
          _this.pagination.pageSize=response.data.data.size;
          _this.pagination.currentPage=response.data.data.current;
          _this.pagination.total=response.data.data.total;
          if ((_this.hospitalNo!=null&&_this.hospitalNo!="")||(_this.admissionId!=null&&_this.admissionId!="")){
            ElMessage({
              showClose: true,
              message: '查询完毕',
              type: 'success',
              duration: 3 * 1000
            })
          }
        })
      },
      reUnderstand(row) {
        // alert(row);
        row.isSending = true;
        console.log(row);
        let hospitalId = row.hospitalId;
        let admissionId = row.admissionId;
        let hospitalName = row.hospitalName;
        let stage = row.stage;
        var _this = this;
        row.reUnderstandButtonName = '理解中';
        //发送请求
        axios.get('/reUnderstand/optimus/test/understand/patient/'+hospitalId+'/'+admissionId, {params:{
            esRead:false,
            esWrite:true,
            scene:stage
          }}).then(function(response){
          console.log(response);
          _this.understandStatus=response.status;
          if (_this.understandStatus=='200'){
            row.isSending = false;
            row.reUnderstandButtonName = '重新理解';
            //更新理解时间

            ElMessage({
              showClose: true,
              message: '【'+hospitalId+'】'+hospitalName+'医院,'+'流水号【'+admissionId+'】理解成功!',
              type: 'success',
              duration: 3 * 1000
            })
          }else {
            row.isSending = false;
            row.reUnderstandButtonName = '理解出错';
            ElMessage({
              showClose: true,
              message: '【'+hospitalId+'】'+hospitalName+'医院,'+'流水号【'+admissionId+'】理解失败,请联系Optimus管理员!',
              type: 'error',
              duration: 3 * 1000
            })
          }
        });
      },
      //跳转到文书列表MedicalDocList页面
      docUnderstandResult(row){
        this.$router.push('/MedicalDocList/'+row.hospitalId+'/'+row.admissionId+'/'+row.stage+'/noFileId'+'/noDocType');
      },
      docQuery(row){
        this.$router.push('/DocQueryList/'+row.hospitalId+'/'+row.admissionId+'/'+row.stage);
      },
      eventQuery(row){
        this.$router.push('/EventQueryList/'+row.hospitalId+'/'+row.admissionId+'/'+row.stage);
      },
      commentDetail(row){
        let _this = this;
        _this.openCommentDetail = true
        // alert("右侧弹出评论列表");
      },
      //分页插件方法
      //切换当前页
      handleCurrentChange(val) {
        this.pagination.currentPage = val;
        this.queryList();
      },
      //切换每页显示条数
      handleSizeChange(val) {
        this.pagination.pageSize = val;
        this.queryList();
      },
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
.avatar{
  float:right;
  margin-top: 10px;
}
.avatar:hover{
  background-color: #B3C0D1;
  //border: 1px solid #4682B4;
}
</style>