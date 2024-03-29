<template>
  <router-view></router-view>
  <el-container>
    <!-- 公共头部 -->
    <el-header style="background-color: #009688;">
      <common-header></common-header>
    </el-header>
    <el-main>
      <el-row class="container">
        <el-button color="#359894" :dark="isDark" style="margin-right: 10px" @click="returnHomePage()">返回</el-button>
      </el-row>
      <el-tabs v-model="activeName" type="card" @tab-click="handleTabClick">
<!--        <el-tab-pane label="Entity" name="entity">-->
<!--          &lt;!&ndash; 基本信息 &ndash;&gt;-->
<!--          <el-card shadow="hover" header="基本信息">-->
<!--            <el-descriptions title="" column="4">-->
<!--              <el-descriptions-item label="医院编号:">{{hospitalId}}</el-descriptions-item>-->
<!--              <el-descriptions-item label="医院名称:">{{hospitalName}}</el-descriptions-item>-->
<!--              <el-descriptions-item label="流水号:">{{admissionId}}</el-descriptions-item>-->
<!--              <el-descriptions-item label="数量:">-->
<!--                <el-link type="primary" @click="toStatisticsList('entity')">{{entityTotal}}</el-link>-->
<!--              </el-descriptions-item>-->
<!--            </el-descriptions>-->
<!--          </el-card>-->
<!--          &lt;!&ndash;          <el-button @click="showSpanCharts">显示图表</el-button>&ndash;&gt;-->
<!--          &lt;!&ndash;          <el-button @click="closeSpanCharts">关闭图表</el-button>&ndash;&gt;-->
<!--          &lt;!&ndash; 统计图表 &ndash;&gt;-->
<!--          <el-row>-->
<!--            <div id="entityEcharts" style="margin-top: 50px; width: 100%;height:400px;"></div>-->
<!--          </el-row>-->
<!--        </el-tab-pane>-->
        <el-tab-pane label="Span" name="span">
          <!-- 基本信息 -->
          <el-card shadow="hover" header="基本信息">
            <el-descriptions title="" column="4">
              <el-descriptions-item label="医院编号:">{{ hospitalId }}</el-descriptions-item>
              <el-descriptions-item label="医院名称:">{{ hospitalName }}</el-descriptions-item>
              <el-descriptions-item label="流水号:">{{ admissionId }}</el-descriptions-item>
              <el-descriptions-item label="数量:">
                <el-link type="primary" @click="toStatisticsList('span')">{{spanTotal}}</el-link>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
<!--          <el-row style="margin-top: 20px">-->
<!--              <el-button type="primary" @click="showSpanCharts">显示图表</el-button>-->
<!--              <el-button type="danger" @click="closeSpanCharts">关闭图表</el-button>-->
<!--          </el-row>-->
          <!-- 统计图表 -->
          <el-row>
            <el-col :span="10">
              <div id="spanEcharts" style="margin-top: 25px;width:800px;height:550px;"></div>
            </el-col>
            <el-col :span="14">
              <el-row style="margin-top: 25px;">
                <el-form :inline="true" :model="formInline" class="demo-form-inline">
                  <el-form-item label="Span标签">
                    <el-input v-model="spanName" placeholder="请输入Span标签名称" clearable></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button color="#009688" @click="getSpanList">查询</el-button>
                  </el-form-item>
                </el-form>
              </el-row>
              <el-row>
                <el-table
                    :header-cell-style="{background:'#AED6F1',color:'#2E4053'}"
                    :data="spanTableData" style="width:100%;height:430px;margin-top:8px;" border
                >
                  <el-table-column width="55px" type="index" prop="emrNo" label="序号"/>
                  <el-table-column width="105px" prop="emrNo" label="EMR号"/>
                  <el-table-column width="110px" prop="docName" label="文书名称" show-overflow-tooltip="true"/>
                  <el-table-column width="120px" prop="nodeName" label="所在节点" show-overflow-tooltip="true"/>
                  <el-table-column width="400px" prop="nodeContent" label="节点内容"/>
                  <el-table-column width="200px" prop="spanTextContent" label="span文本片段"/>
                  <el-table-column width="90px" prop="spanLabel" label="span标签"/>
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
<!--                <el-pagination-->
<!--                    @current-change="handleCurrentChange"-->
<!--                    :current-page="pagination.currentPage"-->
<!--                    :page-size="pagination.pageSize"-->
<!--                    layout="->,total, sizes, prev, pager, next, jumper"-->
<!--                    :total="pagination.total"-->
<!--                    :page-sizes="[10,50,100,200,300,500]"-->
<!--                    @size-change="handleSizeChange">-->
<!--                </el-pagination>-->
              </el-row>
            </el-col>
          </el-row>
        </el-tab-pane>
<!--        <el-tab-pane label="Event" name="event">-->
<!--          &lt;!&ndash; 基本信息 &ndash;&gt;-->
<!--          <el-card shadow="hover" header="基本信息">-->
<!--            <el-descriptions title="" column="4">-->
<!--              <el-descriptions-item label="医院编号:">{{ hospitalId }}</el-descriptions-item>-->
<!--              <el-descriptions-item label="医院名称:">{{ hospitalName }}</el-descriptions-item>-->
<!--              <el-descriptions-item label="流水号:">{{ admissionId }}</el-descriptions-item>-->
<!--              <el-descriptions-item label="数量:">-->
<!--                <el-link type="primary" @click="toStatisticsList('event')">{{eventTotal}}</el-link>-->
<!--              </el-descriptions-item>-->
<!--            </el-descriptions>-->
<!--          </el-card>-->
<!--          &lt;!&ndash;          <el-button @click="showSpanCharts">显示图表</el-button>&ndash;&gt;-->
<!--          &lt;!&ndash;          <el-button @click="closeSpanCharts">关闭图表</el-button>&ndash;&gt;-->
<!--          &lt;!&ndash; 统计图表 &ndash;&gt;-->
<!--          <div id="eventEcharts" style="margin-top: 20px; width: 1000px;height:400px;">-->
<!--          </div>-->
<!--        </el-tab-pane>-->
      </el-tabs>
    </el-main>
    <!-- 页面主体部分 -->
  </el-container>
</template>

<script>
import CommonHeader from "@/views/common/CommonHeader.vue";
import * as echarts from 'echarts';
import {getSpanListInMedicRecord, queryStatisticsData} from "@/apis/get";
import {useRouter} from "vue-router";
import {nextTick, onMounted, ref, watch} from "vue";
import router from "@/router";
export default {
  router,
  components: {CommonHeader},
  //变量
  data() {
    return {
      hospitalName:'',
      hospitalId:'',
      admissionId:'',
      stage:'',
      spanOption:{
        title: {
          text: 'Span数统计: x->文书组名称,y->文书组下的Span数'
        },
        tooltip: {},
        xAxis: {
          //设置0显示所有柱状图
          axisLabel:{interval: 0},
          data: []
        },
        yAxis: {},
        series: [
          {
            name: '数量/个',
            type: 'bar',
            data: [],
            showBackground: true,
            itemStyle: {
              color: '#5499C7'
            }
          }
        ]
      },
      entityOption:{},
      eventOption:{},
      activeName: 'span',
      spanTableData: [
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // },
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // },
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // },
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // },
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // },
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // },
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // },
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // },
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // },
        // {
        //   emrNo: 'EMR080001',
        //   docName: '病案首页',
        //   nodeName: '主诉',
        //   nodeContent: '反复关节疼痛6年余，加重半年余',
        //   spanTextContent: '关节疼痛',
        //   span标签: 'symptom',
        // }
      ],
      entityTableData:[],
      eventTableData:[],
      spanTotal:0,
      entityTotal:0,
      eventTotal:0,
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      spanName:'',
      selectedDocGroupName:''
    }
  },
  //方法
  methods:{
    showSpanCharts(){
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
      // 获取DOM元素并且进行初始化
      let spanChart = echarts.init(document.getElementById('spanEcharts'));
      // 创建图表
      spanChart.setOption(_this.spanOption);
      //点击跳转到统计列表页
      spanChart.on('click',function (params){
        let docGroupName = params.name;
        _this.selectedDocGroupName = docGroupName;
        //将搜索框内容重置
        _this.spanName = '';
        console.log(params);
        if (params.componentType === 'series') {
          var dataIndex = params.dataIndex;
          // 设置选中的柱子样式
          spanChart.setOption({
            series: [{
              // 这里的dataIndex是点击的这个柱子的索引
              data: _this.spanOption.series[0].data.map(function (value, index) {
                // 将其他的柱子颜色还原
                return {
                  value: value,
                  itemStyle: {
                    color: index === dataIndex ? 'indianred' : '#5499C7'
                  }
                };
              })
            }]
          });
        }
        // alert('跳转到:【'+params.name+'】的页面');
        //todo,点击不同的柱状图，会显示不同分组下的span信息
        let pageSize = _this.pagination.pageSize;
        let pageNum = _this.pagination.currentPage;
        let spanName = _this.spanName;
        // alert(pageSize);
        // alert(pageNum);
        // alert(spanName);
        getSpanListInMedicRecord(hospitalId,admissionId,stage,docGroupName,pageSize,pageNum,spanName).then(function (response){
          _this.spanTableData = response.data.data.spanStatisticsList;
          _this.pagination.pageSize=response.data.data.size;
          _this.pagination.currentPage=response.data.data.current;
          _this.pagination.total=response.data.data.total;
        });
      });

      //右边表格部分赋值
      getSpanListInMedicRecord(hospitalId,admissionId,stage,null,pageSize,pageNum,null).then(function (response){
        _this.spanTableData = response.data.data.spanStatisticsList;
        _this.pagination.pageSize=response.data.data.size;
        _this.pagination.currentPage=response.data.data.current;
        _this.pagination.total=response.data.data.total;
      });
    },
    getSpanList(){
      let _this = this;
      let hospitalId = _this.hospitalId;
      let hospitalName = _this.hospitalName;
      let admissionId = _this.admissionId;
      let stage = _this.stage;
      let pageSize = _this.pagination.pageSize;
      let pageNum = _this.pagination.currentPage;
      let spanName = _this.spanName;
      let docGroupName = _this.selectedDocGroupName;
      // alert(pageSize);
      // alert(pageNum);
      // alert(spanName);
      // alert(docGroupName);
      getSpanListInMedicRecord(hospitalId,admissionId,stage,docGroupName,pageSize,pageNum,spanName).then(function (response){
        _this.spanTableData = response.data.data.spanStatisticsList;
        _this.pagination.pageSize=response.data.data.size;
        _this.pagination.currentPage=response.data.data.current;
        _this.pagination.total=response.data.data.total;
      });
    },
    showEntityCharts(){
      let _this = this;
      // 获取DOM元素并且进行初始化
      var myChart = echarts.init(document.getElementById('entityEcharts'));
      // 创建图标
      myChart.setOption(_this.entityOption);
    },
    showEventCharts(){
      let _this = this;
      // 获取DOM元素并且进行初始化
      var eventChart = echarts.init(document.getElementById('eventEcharts'));
      // 创建图标
      eventChart.setOption(_this.eventOption);
    },
    closeSpanCharts(){
      echarts.dispose(document.getElementById('spanEcharts'));
    },
    toStatisticsList(statisticstype){
      // alert(statisticstype);
      let _this = this;
      // alert(_this.stage);
      //todo 跳转到统计列表
      _this.$router.push('/StatisticsList/'+_this.hospitalId+'/'+_this.admissionId+'/'+_this.hospitalName+'/'+statisticstype);
    },
    handleTabClick (tab,event){
      let _this = this;
      let paneName = tab.paneName;
      _this.activeName = tab.paneName;
      let activeName = _this.activeName;
      //默认显示span图表
      if (activeName==null||activeName==''){
        _this.showSpanCharts();
      }

      if (activeName=='span'){
        _this.showSpanCharts();
      }

      if (activeName=='entity'){
        _this.showEntityCharts();
      }

      if (activeName=='event'){
        _this.showEventCharts();
      }
    },
    //获取统计数据
    getStatisticsData(){
      //医院编号
      //流水号
      let _this = this;
      let hospitalId = _this.hospitalId;
      let hospitalName = _this.hospitalName;
      let admissionId = _this.admissionId;
      let stage = _this.stage;
      queryStatisticsData(hospitalId,admissionId,stage).then(function (response){
        //todo 后续赋值操作
        // alert(response.data.data.spanOption.xAxis);
        // console.log(response.data.data.spanOption.xaxis);
        _this.spanOption.xAxis.data = response.data.data.spanOption.xaxis;
        _this.spanOption.series[0].data = response.data.data.spanOption.series[0].data;
        _this.spanTotal = response.data.data.spanTotal;
        _this.entityTotal = response.data.data.entityTotal;
        _this.eventTotal = response.data.data.eventTotal;
      });
    },
    //切换当前页
    handleCurrentChange(val) {
      // alert(val);
      this.pagination.currentPage = val;
      //查询
      this.getSpanList();
    },
    //切换每页显示条数
    handleSizeChange(val) {
      // alert(val);
      this.pagination.pageSize = val;
      //查询
      this.getSpanList();
    },
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
    }
  },

  //加载时执行
  mounted() {
    let _this = this;
    //头部卡片赋值
    _this.hospitalId = _this.$route.params.hospitalId;
    _this.hospitalName = _this.$route.params.hospitalName;
    _this.admissionId = _this.$route.params.admissionId;
    _this.stage = _this.$route.params.stage;
    _this.activeName = _this.$route.params.statisticstype;
    //给charts赋值
    _this.getStatisticsData();
    //图表部分
    //增加定时,必须定时，等dom元素生成之后才可以加载图表
    setTimeout(() => {
      //图表默认显示span图表
      _this.showSpanCharts();
    }, 1000);
  }
}
</script>

<!--样式-->
<style>
</style>