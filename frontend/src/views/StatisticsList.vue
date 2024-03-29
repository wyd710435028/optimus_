<template>
  <router-view></router-view>
  <el-container>
    <!-- 公共头部 -->
    <el-header style="background-color: #009688;">
      <common-header></common-header>
    </el-header>
    <el-main>
      <el-row class="container">
        <el-button color="#359894" :dark="isDark" style="margin-right: 10px" @click="returnMedicalRecordStatisticsChartPage()">返回</el-button>
      </el-row>
      <el-tabs v-model="activeName" type="card" style="margin-top: 20px" @tab-click="handleTabClick">
<!--        <el-tab-pane label="Entity" name="entity">-->
<!--          <el-row>-->
<!--            <el-form :inline="true" :model="formInline" class="demo-form-inline">-->
<!--              <el-form-item label="Entity名称">-->
<!--                <el-input v-model="entityName" placeholder="请输入Entity名称" clearable></el-input>-->
<!--              </el-form-item>-->
<!--              <el-form-item>-->
<!--                <el-button color="#009688" @click="getStatisticsListByEntityName(entityName)">查询</el-button>-->
<!--              </el-form-item>-->
<!--            </el-form>-->
<!--          </el-row>-->
<!--          <el-row>-->
<!--            <el-empty description="entity内容待填充" />-->
<!--          </el-row>-->
<!--        </el-tab-pane>-->
        <el-tab-pane label="Span" name="span">
          <el-row>
            <el-form :inline="true" :model="formInline" class="demo-form-inline">
              <el-form-item label="Span标签">
                <el-input v-model="spanName" placeholder="请输入Span标签名称" clearable></el-input>
              </el-form-item>
              <el-form-item>
                <el-button color="#009688" @click="showSpanList">查询</el-button>
              </el-form-item>
            </el-form>
          </el-row>
          <el-row>
            <el-table
                :header-cell-style="{background:'#AED6F1',color:'#2E4053'}"
                :data="spanTableData" style="width:100%;height:600px;margin-top:8px;" border
            >
              <el-table-column width="55px"  type="index" label="序号"/>
              <el-table-column width="105px"  prop="emrNo" label="EMR号"/>
              <el-table-column width="120px"  prop="docName" label="文书名称" show-overflow-tooltip="true"/>
              <el-table-column width="130px"   prop="nodeName" label="所在节点" show-overflow-tooltip="true"/>
              <el-table-column prop="nodeContent" label="节点内容"/>
              <el-table-column prop="spanTextContent" label="span文本片段"/>
              <el-table-column width="120px"  prop="spanLabel" label="span标签"/>
            </el-table>
          </el-row>
          <el-row style="margin-top: 25px;float: right">
            <el-pagination
                @current-change="handleCurrentChange"
                :current-page="pagination.currentPage"
                :page-size="pagination.pageSize"
                layout="->,total, sizes, prev, pager, next, jumper"
                :total="pagination.total"
                :page-sizes="[5,10,20,50,100,300]"
                @size-change="handleSizeChange"
                :background="true"
            />
          </el-row>
        </el-tab-pane>
<!--        <el-tab-pane label="Event" name="event">-->
<!--          <el-row>-->
<!--            <el-form :inline="true" :model="formInline" class="demo-form-inline">-->
<!--              <el-form-item label="Event名称">-->
<!--                <el-input v-model="eventName" placeholder="请输入Event名称" clearable></el-input>-->
<!--              </el-form-item>-->
<!--              <el-form-item>-->
<!--                <el-button color="#009688" @click="getStatisticsListByEventName(eventName)">查询</el-button>-->
<!--              </el-form-item>-->
<!--            </el-form>-->
<!--          </el-row>-->
<!--          <el-row>-->
<!--            <el-empty description="event待填充" />-->
<!--          </el-row>-->
<!--        </el-tab-pane>-->
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "@/views/common/CommonHeader.vue";
import {getSpanListInMedicRecord} from "@/apis/get";
export default {
  components: {CommonHeader},
  //变量
  data() {
    return {
      spanName:'',
      entityTableData: [
        {
          date: '2016-05-03',
          name: 'Tom',
          address: 'No. 189, Grove St, Los Angeles',
        },
        {
          date: '2016-05-02',
          name: 'Tom',
          address: 'No. 189, Grove St, Los Angeles',
        },
        {
          date: '2016-05-04',
          name: 'Tom',
          address: 'No. 189, Grove St, Los Angeles',
        },
        {
          date: '2016-05-01',
          name: 'Tom',
          address: 'No. 189, Grove St, Los Angeles',
        },
      ],
      activeName: 'span',
      spanTableData:[],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      }
    }
  },

  //方法
  methods:{
    getStatisticsListBySpanName(spanName){
      // alert(spanName);
    },
    handleTabClick (tab,event){
      let _this = this;
      let paneName = tab.paneName;
      _this.activeName = tab.paneName;
      let activeName = _this.activeName;
      //默认显示span图表
      if (activeName==null||activeName==''){
        _this.showSpanList();
      }

      if (activeName=='span'){
        _this.showSpanList();
      }

      if (activeName=='entity'){
        _this.showEntityList();
      }

      if (activeName=='event'){
        _this.showEventList();
      }
    },
    showSpanList(){
      let _this = this;
      let hospitalId = _this.hospitalId;
      let hospitalName = _this.hospitalName;
      let admissionId = _this.admissionId;
      let stage = _this.stage;
      let pageSize = _this.pagination.pageSize;
      let pageNum = _this.pagination.currentPage;
      let spanName = _this.spanName;
      // alert(pageSize);
      // alert(pageNum);
      // alert(spanName);
      getSpanListInMedicRecord(hospitalId,admissionId,stage,null,pageSize,pageNum,spanName).then(function (response){
        _this.spanTableData = response.data.data.spanStatisticsList;
        // console.log(response.data.data);
        _this.pagination.pageSize=response.data.data.size;
        _this.pagination.currentPage=response.data.data.current;
        _this.pagination.total=response.data.data.total;
      });
    },
    showEntityList(){

    },
    showEventList(){

    },
    //切换当前页
    handleCurrentChange(val) {
      // alert(val);
      this.pagination.currentPage = val;
      //查询
      this.showSpanList();
    },
    //切换每页显示条数
    handleSizeChange(val) {
      // alert(val);
      this.pagination.pageSize = val;
      //查询
      this.showSpanList();
    },
    returnMedicalRecordStatisticsChartPage(){
      let _this = this;
      let hospitalId = _this.hospitalId;
      let hospitalName = _this.hospitalName;
      let admissionId = _this.admissionId;
      let activeName = _this.activeName;
      this.$router.push('/MedicalRecordStatisticsChart/'+hospitalId+'/'+hospitalName+'/'+admissionId+'/'+activeName);
    }
  },

  //加载时执行
  mounted() {
    let  _this = this;
    //头部卡片赋值
    _this.hospitalId = _this.$route.params.hospitalId;
    _this.hospitalName = _this.$route.params.hospitalName;
    _this.admissionId = _this.$route.params.admissionId;
    _this.stage = _this.$route.params.stage;
    _this.activeName = _this.$route.params.statisticstype;
    _this.showSpanList();
  }
}
</script>

<!--样式-->
<style>
  .container {
    display: flex;
    justify-content: flex-end;
  }
</style>