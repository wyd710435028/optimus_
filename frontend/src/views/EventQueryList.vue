
<template>
  <router-view></router-view>
  <el-container>
    <!-- 公共头部 -->
    <common-header></common-header>
<!--    <el-header style="background-color: #009688;">-->
<!--      <el-text style="cursor: pointer;color: #ffffff;font-size: 20px" @click="returnIndex()">Optimus可视化系统</el-text>-->
<!--    </el-header>-->
    <el-main style="border: 2px">

      <!-- 搜索框 -->
      <el-row>
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="事件名称">
            <el-input v-model="eventName" placeholder="请输入事件名称" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button color="#359894" :dark="isDark" @click="queryList">查询</el-button>
          </el-form-item>
        </el-form>
      </el-row>

      <!-- 表格内容 -->
      <el-row>
        <el-table border :data="tableData" :default-sort="{ prop: 'createTime', order: 'descending' }" stripe style="width: 100%">
          <el-table-column type="index" label="序号" :index="true" width="55"/>
          <el-table-column label="id" prop="id"/>
          <el-table-column label="事件名称" prop="eventName" show-overflow-tooltip="true"/>
          <el-table-column label="事件时间" prop="eventTime" show-overflow-tooltip="true"/>
          <el-table-column label="事件唯一标识" prop="eventIdentity" show-overflow-tooltip="true"/>
          <el-table-column label="文书Map" prop="docMap" show-overflow-tooltip="true"/>
          <el-table-column label="医嘱Map" prop="orderMap" show-overflow-tooltip="true"/>
          <el-table-column label="属性Map" prop="attributeMap" show-overflow-tooltip="true"/>
          <el-table-column label="高亮内容" prop="highlightInfo" show-overflow-tooltip="true"/>
          <el-table-column label="文书FileId" prop="allDocFileIdSet" show-overflow-tooltip="true"/>
        </el-table>
      </el-row>
    </el-main>

    <!-- 底部分页插件 -->
    <el-footer>
      <div class="block">
        <el-pagination
            @current-change="handleCurrentChange"
            :current-page="pagination.currentPage"
            :page-size="pagination.pageSize"
            layout="->,total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            @size-change="handleSizeChange"
        />
      </div>
    </el-footer>
  </el-container>
</template>
<script>
import {docQueryList, eventQueryList} from "../apis/get";
import {ElMessage} from "element-plus";
import CommonHeader from "@/views/common/CommonHeader.vue";
export default {
  components: {CommonHeader},
  data(){
    return{
      tableData : [
      ],
      eventName:'',
      pagination: {
        currentPage: null,
        pageSize: null,
        total: null
      }
    }
  },
  methods:{
    //查询列表
    queryList(){
      var _this = this;
      // alert('ad:'+_this.fileName);
      // alert('emr:'+_this.emrNo);
      let hospitalId = _this.$route.params.hospitalId;
      let admissionId = _this.$route.params.admissionId;
      let stage = _this.$route.params.stage;
      let eventName = _this.eventName;
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      //发送请求
      eventQueryList(hospitalId,admissionId,stage,eventName,_this.pagination.pageSize,_this.pagination.currentPage).then(function (response){
        console.log(response);
        if (response.data.code==200){
          if (_this.eventName !=null && _this.eventName!=''){
            ElMessage({
              showClose: true,
              message: '查询完毕!',
              type: 'success',
              duration: 3 * 1000
            })
          }
          if (response.data.data!=null){
            _this.tableData = response.data.data.records;
            _this.pagination.pageSize=response.data.data.size;
            _this.pagination.currentPage=response.data.data.current;
            _this.pagination.total=response.data.data.total;
          }
        }else {
          ElMessage({
            showClose: true,
            message: '查询错误,编号为'+response.data.code,
            type: 'error',
            duration: 3 * 1000
          })
        }
      })
    },
    toDocDetail(row){
      let hospitalId = this.$route.params.hospitalId;
      let admissionId = this.$route.params.admissionId;
      let stage = this.$route.params.stage;
      let emrNo = row.docType;
      let showContent = '';
      if (emrNo=='EMR110001'){
        showContent = 'standingOrder';
      }else if (emrNo =='EMR110002'){
        showContent = 'statOrder';
      }else {
        showContent = 'normal';
      }
      // alert(emrNo);
      // alert(showContent);
      this.$router.push('/DocContentDetail/'+hospitalId+'/'+admissionId+'/'+stage+'/'+row.fileId+'/'+showContent);
      // alert(row.fileId);
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      // console.log(row);
    },
    //分页插件方法
    //切换当前页
    handleCurrentChange(val) {
      // alert(val);
      this.pagination.currentPage = val;
      this.queryList();
    },
    //切换每页显示条数
    handleSizeChange(val) {
      // alert(`每页 ${val} 条`);
      this.pagination.pageSize = val;
      // alert(this.pagination.pageSize);
      this.queryList();
    },
    returnIndex(){
      this.$router.push('/');
    }
  },
  mounted(){
    //todo 加载初始化操作
    this.hospitalId = this.$route.params.hospitalId;
    this.admissionId = this.$route.params.admissionId;
    this.stage = this.$route.params.stage;
    this.queryList();
  }
}
</script>
<style></style>