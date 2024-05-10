<template>
  <router-view></router-view>
  <el-container>
    <!-- 公共头部 -->
    <common-header></common-header>
    <el-main style="border: 2px">
      <el-row class="container">
        <el-button color="#359894" :dark="isDark" style="margin-right: 10px" @click="returnHomePage()">
          <el-icon>
            <Back/>
          </el-icon>
          <span>返回首页</span>
        </el-button>
      </el-row>
      <el-row>
        <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <el-form-item label="流水号">
            <el-input v-model="conditionAdmissionId" placeholder="请输入流水号" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button color="#009688" @click="getMarkedSpanList">
              <el-icon>
                <Search/>
              </el-icon>
              <span>查询</span>
            </el-button>
          </el-form-item>
        </el-form>
      </el-row>
      <el-row>
        <el-table :data="tableData" border style="width: 100%">
          <el-table-column v-if="false" prop="id"></el-table-column>
          <el-table-column fixed type="index" label="序号" width="60">
          </el-table-column>
          <el-table-column
              prop="hospitalId"
              label="医院编号">
          </el-table-column>
          <el-table-column
              prop="admissionId"
              label="流水号">
          </el-table-column>
          <el-table-column
              prop="emrNo"
              label="emr编号">
          </el-table-column>
          <el-table-column
              prop="docName"
              label="文书名称">
          </el-table-column>
          <el-table-column
              prop="nodeName"
              label="节点名称">
          </el-table-column>
          <el-table-column
              prop="spanTextContent"
              label="span片段内容">
          </el-table-column>
          <el-table-column
              prop="spanLabel"
              label="span标签">
          </el-table-column>
          <el-table-column
              prop="createTime"
              label="创建时间">
          </el-table-column>
<!--          <el-table-column-->
<!--              prop="updateTime"-->
<!--              label="更新时间">-->
<!--          </el-table-column>-->
          <el-table-column width="240px" label="操作">
            <template #default="scope">
              <el-button @click="deleteMark(scope.row.id)" type="danger">删除</el-button>
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
            @size-change="handleSizeChange"
            :background="true"
        >
        </el-pagination>
      </div>
    </el-footer>
  </el-container>

</template>
<script>
import {deleteMarkedSpanById, getRecordList, queryMarkedSpanList} from "../apis/get";
  import {getHospitalDropDown} from "../apis/get"
  import axios from "axios";
  import {reactive} from "vue";
  import {ElMessage} from "element-plus";
  import CommentList from "@/views/CommentList.vue";
  import CommonHeader from "@/views/common/CommonHeader.vue";
  import {Back, List, Right, Search} from "@element-plus/icons-vue";
import router from "@/router";
  // import {} from "../apis/post";
  export default {
    components: {Back, List, Right, Search, CommentList,CommonHeader},
    data() {
      return {
        tableData: [],
        pagination: {
          currentPage: null,
          pageSize: null,
          total: null
        },
        options: [],
        conditionAdmissionId:''
      }
    },
    methods:{
      getMarkedSpanList(){
        let _this = this;
        let conditionAdmissionId = _this.conditionAdmissionId;
        queryMarkedSpanList(conditionAdmissionId,_this.pagination.pageSize,_this.pagination.currentPage).then(function (response){
          console.log(response);
          _this.tableData=response.data.data.records;
          _this.pagination.pageSize=response.data.data.size;
          _this.pagination.currentPage=response.data.data.current;
          _this.pagination.total=response.data.data.total;
          // if (response.status=='200'){
          //   ElMessage({
          //     showClose: true,
          //     message: response.data.msg,
          //     type: 'success',
          //     duration: 3 * 1000
          //   })
          // }else {
          //   ElMessage({
          //     showClose: true,
          //     message: response.data.msg,
          //     type: 'error',
          //     duration: 3 * 1000
          //   })
          // }
        });
      },
      returnHomePage(){
        this.$router.push('/');
      },
      deleteMark(id){
        deleteMarkedSpanById(id).then(function (response){
          if (response.status=='200'){
            ElMessage({
              showClose: true,
              message: response.data.msg,
              type: 'success',
              duration: 3 * 1000
            });
            location.reload();
          }else {
            ElMessage({
              showClose: true,
              message: response.data.msg,
              type: 'error',
              duration: 3 * 1000
            });
            location.reload();
          }
        });
      },
      //切换当前页
      handleCurrentChange(val) {
        // alert(val);
        this.pagination.currentPage = val;
        this.getMarkedSpanList();
      },
      //切换每页显示条数
      handleSizeChange(val) {
        this.pagination.pageSize = val;
        this.getMarkedSpanList();
      },
    },
    mounted() {
      this.getMarkedSpanList();
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