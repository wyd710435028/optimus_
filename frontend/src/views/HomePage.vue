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
                placeholder="请选择医院编号"
                style="width: 240px"
                @visible-change="visibleChange"
            >
              <el-option v-for="item in options"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value"></el-option>
              <template #footer>
                <el-button v-if="!isAdding" type="text" bg size="small" @click="onAddOption">
                  添加医院
                </el-button>
                <template v-else>
                  <el-form
                      ref="addHospitalFormRef"
                      :model="addHospitalForm"
                      :rules="addHospitalFormRules"
                      status-icon
                      :label-position="'left'">
                    <el-form-item label="医院编号:" prop="optionHospitalNo">
                      <el-input
                          v-model.number="addHospitalForm.optionHospitalNo"
                          clearable
                          class="option-input"
                          placeholder="请输入医院编号"
                          size="small"
                      />
                    </el-form-item>
                    <el-form-item label="医院名称:" prop="optionHospitalName">
                      <el-input
                          v-model="addHospitalForm.optionHospitalName"
                          clearable
                          class="option-input"
                          placeholder="请输入医院名称"
                          size="small"
                      />
                    </el-form-item>
                  </el-form>
                  <el-button type="primary" size="small" style="margin-top: 10px" @click="onConfirm">
                    <span>确定</span>
                  </el-button>
                  <el-button size="small" @click="clear" style="margin-top: 10px">
                    <span>取消</span>
                  </el-button>
                </template>
              </template>
            </el-select>
          </el-form-item>
          <el-form-item label="流水号">
            <el-input v-model="admissionId" placeholder="请输入流水号" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button color="#009688" @click="queryList">
              <el-icon>
                <Search/>
              </el-icon>
              <span>查询</span>
            </el-button>
            <el-button color="#009688" @click="toMarkedSpanList">
              <el-icon>
                <List/>
              </el-icon>
              <span>标记的Span列表</span>
            </el-button>
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
              prop="orderMarkedFlag"
              label="医嘱标记状态">
            <template #default="scope">
              <span v-if="scope.row.orderMarkedFlag == true">
                <el-text style="color: #529b2e">【已标记】</el-text>
                <el-text>,有</el-text>
                <el-text style="color: #529b2e">【{{scope.row.remarkNum}}】</el-text>
                <el-text>条备注信息</el-text>
              </span>
              <el-text style="color: indianred" v-else>未标记</el-text>
<!--              <el-tag type="success" v-if="scope.row.orderMarkedFlag == true">已标记</el-tag>-->
<!--              <el-tag type="danger" v-else>未标记</el-tag>-->
            </template>
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
                <el-button @click="toMedicalRecordStatisticsChart(scope.row)" color="#5F433F">统计</el-button>
                <!--                <el-button @click="commentDetail(scope.row)" color="#BA4A00">评论详情</el-button>-->
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
            @size-change="handleSizeChange"
            :background="true"
        >
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
  import {getHospitalDropDown} from "../apis/get";
  import {addHospital} from "../apis/post"
  import axios from "axios";
  import {reactive} from "vue";
  import {ElMessage} from "element-plus";
  import CommentList from "@/views/CommentList.vue";
  import CommonHeader from "@/views/common/CommonHeader.vue";
  import {Search} from "@element-plus/icons-vue";
  // import {} from "../apis/post";
  export default {
    components: {List, Right, Search, CommentList,CommonHeader},
    setup(){
      const addHospitalForm = reactive({
        optionHospitalNo:'',
        optionHospitalName:''
      });
      const addHospitalFormRules = {
        optionHospitalNo:[
          { required: true, message: '医院编号不能为空', trigger: 'blur' },
          { type: 'number', message: '医院编号必须为数字' }
        ],
        optionHospitalName:[
          { required: true, message: '医院名称不能为空', trigger: 'blur' },
        ]
      };
      return {
        addHospitalForm,
        addHospitalFormRules
      };
    },
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
        dialogVisible:false,
        isAdding:false
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
      onConfirm(){
        var _this = this;
        //表单校验
        const validate = _this.$refs.addHospitalFormRef.validate();
        if (validate) {
          // 表单验证通过，这里可以进行提交操作
          console.log('表单提交', this.addDefectForm);
          let hospitalNo = _this.addHospitalForm.optionHospitalNo;
          let hospitalName = _this.addHospitalForm.optionHospitalName;
          addHospital(hospitalNo,hospitalName).then(function (response){
            //todo 添加医院信息逻辑
            getHospitalDropDown().then(function (response){
              console.log(response);
              _this.options=response.data.data;
              _this.isAdding = false;
            })
          })
        } else {
          console.log('表单验证失败');
        }
      },
      /**
       * 点击 "添加医院" 按钮
       */
      onAddOption(){
        var _this = this;
        _this.isAdding = true;
        // _this.addHospitalForm.optionHospitalNo='';
        // _this.addHospitalForm.optionHospitalName='';
      },

      /**
       * 选择框关闭触发的事件，用于保证每次打开时都显示"添加医院"
       * @param isVisible
       */
      visibleChange(isVisible){
        var  _this = this;
        if (!isVisible){
          _this.isAdding = false;
        }
      },

      /**
       * 取消添加医院功能
       */
      clear(){
        var _this = this;
        _this.isAdding = false;
      },

      /**
       * 查询列表
       */
      queryList(){
        let _this = this;
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
      },
      toMedicalRecordStatisticsChart(row){
        console.log(row);
        let hospitalName = row.hospitalName;
        let hospitalId = row.hospitalId;
        let admissionId = row.admissionId;
        this.$router.push('/MedicalRecordStatisticsChart/'+row.hospitalId+'/'+row.hospitalName+'/'+row.admissionId+'/'+'span');
      },
      toMarkedSpanList(){
        this.$router.push('/MarkedSpanList');
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