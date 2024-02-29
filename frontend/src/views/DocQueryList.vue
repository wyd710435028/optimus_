<template>
  <router-view></router-view>
  <el-container>
    <!-- 公共头部 -->
    <common-header></common-header>
<!--    <el-header style="background-color: #009688;">-->
<!--      <el-text style="cursor: pointer;color: #ffffff;font-size: 20px" @click="returnIndex()">Optimus可视化系统</el-text>-->
<!--    </el-header>-->
    <el-main style="border: 2px">
      <el-row>
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="文书名称/类型">
            <el-input v-model="fileName" placeholder="请输入文书名称/类型" clearable></el-input>
          </el-form-item>
          <el-form-item label="EMR编号">
            <el-input v-model="emrNo" placeholder="请输入EMR编号" clearable></el-input>
          </el-form-item>
          <el-form-item label="Tags">
            <el-input v-model="tags" placeholder="请输入Tags" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button color="#359894" :dark="isDark" @click="queryList">查询</el-button>
          </el-form-item>
        </el-form>
      </el-row>
      <el-row>
        <el-table border :data="tableData" :default-sort="{ prop: 'createTime', order: 'descending' }" stripe style="width: 100%">
          <el-table-column type="expand">
            <template #default="props">
              <el-table table-layout="auto" border size="small"  :header-row-style="{lineHeight:'50px'}" :header-cell-style="{backgroundColor:'#F5B7B1',color:'#566573',fontSize:'12px'}" :data="props.row.relatedDocs">
                <el-table-column type="index" label="序号" :index="true"></el-table-column>
                <el-table-column label="文书描述" prop="fileDesc"></el-table-column>
                <el-table-column label="文书Id" prop="fileId">
                  <template #default="props">
                    <el-link style="font-size:12px;color: #c45656" type="primary" @click="toDocDetail(props.row)" :underline="true">{{props.row.fileId}}</el-link>
                  </template>
                </el-table-column>
                <el-table-column label="文书名称" prop="fileName" />
                <el-table-column label="文书类型" prop="docClassName" />
                <el-table-column label="Emr编号" prop="docType" />
                <el-table-column label="Tags" prop="tags" />
                <el-table-column label="操作时间" prop="operateTime" />
                <el-table-column label="记录时间(recordTime)" prop="recordTime" />
                <el-table-column label="创建时间" prop="createTime" />
              </el-table>
            </template>
          </el-table-column>
          <el-table-column type="index" label="序号" :index="true"></el-table-column>
          <el-table-column label="文书Id" prop="fileId" >
            <template #default="props">
              <el-link type="primary" @click="toDocDetail(props.row)" :underline="true">{{props.row.fileId}}</el-link>
            </template>
          </el-table-column>
          <el-table-column label="文书名称" prop="fileName" />
          <el-table-column label="文书类型" prop="docClassName" />
          <el-table-column label="Emr编号" prop="docType" />
          <el-table-column label="Tags" prop="tags" />
          <el-table-column label="操作时间" prop="operateTime" />
          <el-table-column label="记录时间(recordTime)" prop="recordTime" />
          <el-table-column label="创建时间" sortable prop="createTime" />
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
            :page-sizes="[10, 20, 50, 100]"
            @size-change="handleSizeChange"
        />
      </div>
    </el-footer>
  </el-container>
</template>
<script>
import {docQueryList} from "../apis/get"
import CommonHeader from "@/views/common/CommonHeader.vue";
export default {
  components: {CommonHeader},
  data(){
    return{
      tableData : [],
      fileName:'',
      emrNo:'',
      tags:'',
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
      let fileName = _this.fileName;
      let emrNo = _this.emrNo;
      let tags = _this.tags;
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      //发送请求
      docQueryList(hospitalId,admissionId,stage,fileName,emrNo,tags,_this.pagination.pageSize,_this.pagination.currentPage).then(function (response){
        console.log(response);
        _this.tableData = response.data.data.records==null?null:response.data.data.records;
        _this.pagination.pageSize=response.data.data.size;
        _this.pagination.currentPage=response.data.data.current;
        _this.pagination.total=response.data.data.total;
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