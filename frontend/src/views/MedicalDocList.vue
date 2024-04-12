<template>
  <router-view></router-view>
  <el-container>
    <!-- 公共头部 -->
    <el-header style="background-color: #009688;">
      <common-header></common-header>
    </el-header>

    <!-- 主体内容 -->
    <el-main style="border: 2px">
    <el-row class="container">
      <el-button color="#359894" :dark="isDark" style="margin-right: 10px" @click="returnHomePage()">
        <el-icon>
          <Back/>
        </el-icon>
        <span>返回</span>
      </el-button>
    </el-row>
    <el-row>
      <el-breadcrumb :separator-icon="ArrowRight">
        <el-breadcrumb-item :to="{ path: '/' }"><el-text size="large" style="color: #4682B4">首页</el-text></el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-row>
      <el-col :span="4">
        <el-aside class="layout_col">
            <el-tree
              class="flow-tree"
              :data="treeData"
              :props="defaultProps"
              :highlight-current="true"
              :render-content="renderContent"
              @node-click="getDocByFileId"
              style="font-size: 18px"
            >
            </el-tree>
        </el-aside>
      </el-col>
      <el-col :span="16">
        <el-main class="layout_col">
          <div v-if="showContent=='normal'">
            <div v-for="item in nodeList" :key="item">
              <el-row>
                <el-col :span="4">
                  <el-text style="color: indianred;font-size: 16px">{{item.nodeName}}</el-text>
<!--                  <strong style="color: indianred;font-size: 16px">{{item.nodeName}}</strong>-->
                </el-col>
                <el-col :span="20">
<!--                  <div v-if="item.entityNum==0">-->
<!--                    <label>{{item.nodeContent}}</label>-->
<!--                  </div>-->
<!--                  <div v-if="item.entityNum>0">-->
                    <el-text v-html=item.entityHightLighted></el-text>
<!--                    <label v-html=item.entityHightLighted></label>-->
<!--                  </div>-->
                  <!-- 当entity、span、event中有一个数量大于0时才显示表格 -->
                  <el-row v-if="item.entityNum>0||item.spanNum>0||item.eventNum>0">
                    <el-table
                        :data="tableData"
                        :header-cell-style="{background:'#AED6F1',color:'#2E4053'}"
                        border
                        style="width: 100%;margin:10px;"
                    >
                      <el-table-column fixed prop="entityNum" label="entity数量">
                        <template v-slot="{}">
                          <el-link type="text" @click="toDocUnderstandDetail(item.nodeName,item.nodeContent,item.entityHightLighted,item.spanHightLighted,item.eventHightLighted,item.entityLabelList,item.spanLabelList,item.eventList,item.fileId,item.entityList,item.spanList)">{{item.entityNum}}</el-link>
                        </template>
                      </el-table-column>
                      <el-table-column prop="eventNum" label="event数量">
                        <template v-slot="{}">
                          <el-link type="text" @click="toDocUnderstandDetail(item.nodeName,item.nodeContent,item.entityHightLighted,item.spanHightLighted,item.eventHightLighted,item.entityLabelList,item.spanLabelList,item.eventList,item.fileId,item.entityList,item.spanList)">{{item.eventNum}}</el-link>
                        </template>
                      </el-table-column>
                      <el-table-column prop="spanNum" label="span数量">
                        <template v-slot="{}">
                          <el-link type="text" @click="toDocUnderstandDetail(item.nodeName,item.nodeContent,item.entityHightLighted,item.spanHightLighted,item.eventHightLighted,item.entityLabelList,item.spanLabelList,item.eventList,item.fileId,item.entityList,item.spanList)">{{item.spanNum}}</el-link>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-row>
                </el-col>
              </el-row>
              <el-divider style="background-color: #c45656"></el-divider>
            </div>
          </div>
          <div v-if="showContent=='statOrder'">
            <el-row>
              <el-text style="color: #529b2e;font-weight: bold" class="mx-1" size="large" tag="ins">临时医嘱</el-text>
            </el-row>
            <el-row>
              <el-table
                  :data="statOrderTableData"
                  border
                  stripe
                  :height="table"
                  :header-cell-style="{background:'#AED6F1',color:'#2E4053'}"
                  style="width: 100%;margin:10px">
<!--                <el-table-column fixed prop="unisoundId" label="unisoundId" width="165px"></el-table-column>-->
                <el-table-column fixed type="index" label="序号" width="60"></el-table-column>
                <el-table-column fixed prop="day" label="日期"></el-table-column>
                <el-table-column prop="time" label="时间"></el-table-column>
                <el-table-column prop="content" label="医嘱内容">
                  <template v-slot="scope">
                    <el-link v-if="ifOrderCanLink(scope.row.yzsProjectType)" type="primary" @click="toEntityLink(scope.row)">{{scope.row.content}}</el-link>
                    <span v-else>{{scope.row.content}}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="physicianSign" label="医师签名"></el-table-column>
                <el-table-column prop="executeTime" label="执行时间"></el-table-column>
                <el-table-column prop="executorSign" label="执行人签名"></el-table-column>
                <el-table-column prop="statOrderExecuteStatus" label="医嘱执行状态" :filters="statOrderExecuteStatusCategories" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="statOrderStatus" label="医嘱状态" :filters="statOrderStatusCategories" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="yzsProjectType" label="云知声项目类别" width="150px" :filters="yzsProjectType" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="projectCategories" label="项目大类" :filters="projectType" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="operation" label="操作" width="130px">
                  <template v-slot:default="scope">
                    <el-badge v-if ="scope.row.commentNum>0" :value="scope.row.commentNum" :max="99" class="item">
                      <el-button type="primary" @click="openTheComment(scope.row)">评论</el-button>
                    </el-badge>
                    <el-button v-else plain type="primary" @click="openTheComment(scope.row)">评论</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-row>
          </div>
          <div v-if="showContent=='standingOrder'">
            <el-row>
              <el-text style="color: #529b2e;font-weight: bold" class="mx-1" type="warning" size="large" tag="ins">长期医嘱</el-text>
            </el-row>
            <el-row>
              <el-table
                  :data="standingOrderTableData"
                  border
                  stripe
                  :height="table"
                  :header-cell-style="{background:'#AED6F1',color:'#2E4053'}"
                  style="width: 100%;margin:10px;">
                <el-table-column fixed type="index" label="序号" width="60"></el-table-column>
<!--                <el-table-column fixed prop="unisoundId" label="unisoundId"></el-table-column>-->
                <el-table-column fixed prop="openingTime" label="开立时间"></el-table-column>
                <el-table-column prop="startTime" label="开始时间"></el-table-column>
                <el-table-column prop="content" label="医嘱内容">
                  <template v-slot="scope">
                    <el-link v-if="ifOrderCanLink(scope.row.yzsProjectType)" type="primary" @click="toEntityLink(scope.row)">{{scope.row.content}}</el-link>
                    <span v-else>{{scope.row.content}}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="openingPhysicianSign" label="开立医师签名"></el-table-column>
                <el-table-column prop="executeTime" label="执行时间"></el-table-column>
                <el-table-column prop="executorSign" label="执行人签名"></el-table-column>
                <el-table-column prop="stopTime" label="停止时间"></el-table-column>
                <el-table-column prop="stopPhysicianSign" label="停止医师签名"></el-table-column>
                <el-table-column prop="stopExecutorSign" label="停止执行人签名"></el-table-column>
                <el-table-column prop="standingOrderExecuteStatus" label="医嘱执行状态" :filters="statOrderExecuteStatusCategories" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="standingOrderStatus" label="医嘱状态" :filters="statOrderStatusCategories" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="yzsProjectType" label="云知声项目类别" :filters="yzsProjectType" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="projectCategories" label="项目大类" :filters="projectType" :filter-method="filterHandler"></el-table-column>
                <el-table-column prop="operation" label="操作" width="130px">
                  <template v-slot="scope">
                    <el-badge v-if ="scope.row.commentNum>0" :value="scope.row.commentNum" :max="99" class="item">
                      <el-button type="primary" @click="openTheComment(scope.row)">评论</el-button>
                    </el-badge>
                    <el-button v-else plain type="primary" @click="openTheComment(scope.row)">评论</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-row>
          </div>
        </el-main>
      </el-col>
      <!-- 正常文书时显示 -->
      <el-col v-if="showContent=='normal'||showContent==null||showContent==''" :span="4">
        <el-aside class="layout_col" style="margin-left: 10px">
<!--          <span>entity标签列表</span>-->
          <div v-for="entityTag in allEntityLabelList" :key="entityTag">
            <el-tag v-if="entityTag.labelType=='entity'" :color="entityTag.labelColor" style="margin-top: 5px;float: left;margin-left: 5px" @click="clickTag(entityTag.labelContent,entityTag.labelColor)"><label style="color:#303133">{{entityTag.labelChineseName}}</label></el-tag>
          </div>
        </el-aside>
      </el-col>
      <!-- 临时/长期医嘱时显示 -->
      <el-col v-if="showContent=='statOrder'||showContent=='standingOrder'" :span="4">
        <el-aside class="layout_col" style="margin-left: 10px">
          <el-card shadow="hover" header="标记">
            <el-descriptions title="" column="1">
              <el-descriptions-item label="状态:">
                <el-tag v-if="marked" size="default" type="success" effect="dark">已阅</el-tag>
                <el-tag v-else size="default" type="error" effect="dark">未标记</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="操作:">
                <el-button v-if="!marked" type="primary" size="small" @click="markedOrCancelMarkDoc">
                  <el-icon><EditPen/></el-icon>
                  <span>标记为已阅</span>
                </el-button>
                <el-button v-else type="primary" size="small" @click="markedOrCancelMarkDoc">
                  <el-icon><Delete/></el-icon>
                  <span>取消标记</span>
                </el-button>
                <el-button v-if="marked" type="primary" size="small" @click="remarkDialogFormVisible = true">
                  <el-icon><Edit/></el-icon>
                  <span>添加备注</span>
                </el-button>
              </el-descriptions-item>
            </el-descriptions>
<!--            <el-descriptions title="备注列表" size="small" column="2" border>\-->
<!--              <div v-for="(item,index) in remarkContentTableData" :key="index">-->
<!--                <el-descriptions-item :label="index+1">-->
<!--                  <el-text>{{item.remarkContent}}</el-text>-->
<!--                  <el-text>{{item.createTime}}</el-text>-->
<!--                </el-descriptions-item>-->
<!--              </div>-->
<!--            </el-descriptions>-->
          </el-card>
          <!--表格形式-->
          <el-card shadow="hover" header="备注列表" style="margin-top: 10px">
            <el-table
                :data="remarkContentTableData"
                style="width: 100%;"
            >
              <el-table-column fixed type="index" label="序号" width="55"></el-table-column>
              <el-table-column prop="remarkContent" label="备注内容" show-overflow-tooltip="true"></el-table-column>
              <el-table-column prop="createTime" label="创建时间" show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <el-row style="margin-top:10px;margin-bottom:10px;float: right">
              <el-pagination
                  small
                  @current-change="handleCurrentChange"
                  :current-page="pagination.currentPage"
                  :page-size="pagination.pageSize"
                  layout="->,total, prev, pager, next"
                  :total="pagination.total"
                  @size-change="handleSizeChange"
                  :background="true"
              />
            </el-row>
          </el-card>

              <!--操作日志-->
<!--          <el-card shadow="hover" header="操作日志" style="margin-top: 10px">-->
<!--            <el-descriptions title="" column="1">-->
<!--              <el-descriptions-item label="1.">-->
<!--                <el-text style="color: #529b2e">张三在xxx时间取消了标记.</el-text>-->
<!--              </el-descriptions-item>-->
<!--            </el-descriptions>-->
<!--          </el-card>-->
        </el-aside>
      </el-col>
    </el-row>
    </el-main>

    <!-- 实体链接弹窗 -->
    <el-dialog title="实体链接弹窗" v-model="dialogVisible" width="35%">
      <el-card class="box-card">
        <div v-for="(value, key) in encyclopedia" :key="key" class="text item">
          <div style="font-weight: bold;font-size: 18px; margin-top: 12px; margin-bottom: 4px;">
            {{key}}
          </div>
          <div style="white-space: pre-wrap;">
            {{value}}
          </div>
        </div>
      </el-card>
    </el-dialog>
    <!-- 医嘱理解结果评论弹窗 -->
    <el-dialog @close="closeOrderCommentDialog" align-center="true" v-model="orderDialogFormVisible" title="评论" width="35%">
      <div style="align-items: center;margin-left: 25px">
        <el-row>
          <strong>FileId: </strong>
          <span class="orderDialogContentStyle">{{fileId}}</span>
        </el-row>
        <el-row style="margin-top: 10px">
          <strong>UnisoundId: </strong>
          <span class="orderDialogContentStyle">{{orderDialog.unisoundId}}</span>
        </el-row>
        <el-row style="margin-top: 10px">
          <strong>日期: </strong>
          <span class="orderDialogContentStyle">{{orderDialog.day}}</span>
        </el-row>
        <el-row style="margin-top: 10px">
          <strong>时间: </strong>
          <span class="orderDialogContentStyle">{{orderDialog.time}}</span>
        </el-row>
        <el-row style="margin-top: 10px">
          <strong>医嘱内容: </strong>
          <span class="orderDialogContentStyle">{{orderDialog.content}}</span>
        </el-row>
        <el-row style="margin-top: 10px">
          <strong>医师签名: </strong>
          <span class="orderDialogContentStyle">{{orderDialog.physicianSign}}</span>
        </el-row>
        <el-row style="margin-top: 10px">
          <strong>执行时间: </strong>
          <span class="orderDialogContentStyle">{{orderDialog.executeTime}}</span>
        </el-row>
        <el-row style="margin-top: 10px">
          <strong>执行人签名: </strong>
          <span class="orderDialogContentStyle">{{orderDialog.executorSign}}</span>
        </el-row>
        <el-row style="margin-top: 10px">
          <strong>云知声项目类别: </strong>
          <span class="orderDialogContentStyle">{{orderDialog.yzsProjectType}}</span>
        </el-row>
        <el-row style="margin-top: 10px">
          <strong>项目大类: </strong>
          <span class="orderDialogContentStyle">{{orderDialog.projectCategories}}</span>
        </el-row>
      </div>
      <el-divider>★★★</el-divider>
      <!-- 评论组件 -->
      <EmojiPanel @ifSendSuccess="handleSend" :file-id="fileId" :order-content="orderDialog.content" :execute-time="orderDialog.executeTime" :executor-sign="orderDialog.executorSign" :unisound-id="orderDialog.unisoundId"></EmojiPanel>
      <el-divider>★★★</el-divider>
      <span class="post-btn" @click="commentHistory">历史评论</span>
      <div v-if="showCmmentHistoryList==true">
        <el-table :data="commentHistoryTab">
          <el-table-column prop="content" label="内容" width="180">
            <!-- 支持html格式 -->
            <template v-slot="{ row }">
              <span v-html="row.content"></span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" width="180" />
          <el-table-column prop="userName" label="评论人" />
          <el-table-column prop="" label="操作">
            <template v-slot="scope">
              <el-switch
                  inline-prompt
                  active-text="点击关闭"
                  inactive-text="点击开启" :value="scope.row.orderCommentStatus==1?true:false" @change="orderCommentStatusChange(scope.row)"/>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 添加备注弹出框 -->
    <el-dialog v-model="remarkDialogFormVisible" title="添加备注" width="500">
      <el-form>
        <el-form-item label="备注:">
          <el-input v-model="remarkContent" maxlength="300" placeholder="请在此输入备注..." type="textarea" show-word-limit style="width: 100%;height: 100%"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="remarkDialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="addRemark">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </el-container>
</template>
<script setup lang="jsx">
import {
  Back,
  Check,
  CircleClose, Close, CloseBold,
  Delete,
  Edit,
  EditPen,
  Hide,
  Search, Select,
  SemiSelect,
  SuccessFilled,
  Notebook,
  List,
  Document, DArrowRight, ArrowRight
} from "@element-plus/icons-vue";

const renderContent = (h, { node, data }) => {
    // console.log('====node====');
    // console.log(node);
    // console.log('====data====');
    // console.log(data);
    // console.log(data.marked);
    // console.log(node.children);
    // if (data.marked){
    //   return (
    //     <span>
    //       <el-tag type="danger" effect="dark" size="small">已阅</el-tag>
    //       <el-icon><Notebook /></el-icon>
    //       {node.label}
    //     </span>
    //   );
    // }else {
    //   return (
    //     <span>
    //       <el-icon><List /></el-icon>
    //       {node.label}
    //     </span>
    //   )
    // }
  if (data.children==null){
    return (
        <span>
          <el-icon size="large" style="position:relative;top:3px" ><Document /> </el-icon>
          <el-text size="large">{node.label}</el-text>
        </span>
    )
  }else {
    return (
          <el-row>
            <el-icon size="large"><Notebook /></el-icon>
            <el-text size="large">{node.label}</el-text>
          </el-row>
    )
  }
};

</script>
<script lang="jsx">
import {getOrderCommentHistoryList, getRemarkByFileId, getUnderstandResult} from "../apis/get";
import {addMarkedRemark, getNodeByFileId, markDoc, updateOrderCommentStatus} from "../apis/post";
import {getNodeByFileIdWithHighLight} from "../apis/post";
import {ElMessage} from "element-plus";
import entitylinkDialog from "./EntityLinkJump";
import { ref } from 'vue';
import CommonHeader from "@/views/common/CommonHeader.vue";
import EmojiPanel from "@/views/emoji/EmojiPanel.vue";
import CommentList from "@/views/CommentList.vue";

export default {
  components:{
    CommentList,
    EmojiPanel,
    CommonHeader,
    entitylinkDialog
  },
  methods: {
    //获取病历下所有文书
    getAllDoc(){
      let hospitalId = this.$route.params.hospitalId;
      let admissionId = this.$route.params.admissionId;
      let stage = this.$route.params.stage;
      let fileId = this.$route.params.fileId;
      let docType = this.$route.params.docType;
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      let _this = this;
      getUnderstandResult(hospitalId,admissionId,stage).then(function (response){
        console.log(response);
        _this.treeData=response.data.data.treeData;
        _this.allEntityLabelList=response.data.data.entityLabels;
        _this.allSpanLabelList=response.data.data.spanLabels;
        // _this.formatedDocMap=response.data.data.formatedDocMap;
        _this.nodeMap=response.data.data.docList;
        _this.docDetail=response.data.data;
        // alert(fileId);
        // alert(docType);
        if (fileId!='noFileId'&&docType!='noDocType'){
          getNodeByFileId(fileId,JSON.stringify(response.data.data.docList)).then(function (response) {
            if (docType!=null){
              if (docType=='EMR110001'){
                //长期医嘱
                _this.showContent = 'standingOrder';
                _this.standingOrderTableData = response.data.data.standingOrderList;
                _this.projectType = response.data.data.projectCategoriesList;
                _this.yzsProjectType = response.data.data.yzsProjectTypeList;
              }else if (docType=='EMR110002'){
                //临时医嘱
                _this.showContent = 'statOrder';
                _this.statOrderTableData = response.data.data.statOrderList;
                _this.projectType = response.data.data.projectCategoriesList;
                _this.yzsProjectType = response.data.data.yzsProjectTypeList;
              }else {
                //正常文书
                _this.showContent = 'normal';
                _this.nodeList = response.data.data.nodeList;
                _this.allEntityLabelList = response.data.data.labelList;
              }
            }
          })
        }
      })
    },

    /**
     * 获取指定文书
     * @param data
     */
    getDocByFileId: function (data) {
      if (data.children == null) {
        // let docName = data.label;
        // let hospitalId = this.$route.params.hospitalId;
        // let admissionId = this.$route.params.admissionId;
        // let stage = this.$route.params.stage;
        //todo 调用后台查询对应文书
        // alert(docName);
        // alert(hospitalId);
        // alert(admissionId);
        // alert(stage);
        let fId = data.fileId;
        this.fileId = fId;
        // alert(fId);
        let docType = data.docType;
        this.docType = docType;
        // alert(docType);
        let _this = this;
        console.log("----------总的结果----------");
        console.log(_this.nodeMap);
        getNodeByFileId(fId, JSON.stringify(_this.nodeMap)).then(function (response) {
          if (docType=='EMR110001'){
            //长期医嘱
            _this.showContent = 'standingOrder';
            _this.standingOrderTableData = response.data.data.standingOrderList;
            _this.projectType = response.data.data.projectCategoriesList;
            _this.yzsProjectType = response.data.data.yzsProjectTypeList;
            _this.remarkContentTableData = response.data.data.remarkList;
            let fileId = _this.fileId;
            let pageSize = _this.pagination.pageSize;
            let pageNum = _this.pagination.currentPage;
            _this.statOrderExecuteStatusCategories = response.data.data.statOrderExecuteStatusListMap;
            _this.statOrderStatusCategories = response.data.data.statOrderStatusListMap;
            getRemarkByFileId(fileId,pageSize,pageNum).then(function (res){
              _this.pagination.pageSize = res.data.data.size;
              _this.pagination.currentPage = res.data.data.current;
              _this.pagination.total = res.data.data.total;
              _this.remarkContentTableData = res.data.data.remarkList;
            });
          }else if (docType=='EMR110002'){
            //临时医嘱
            _this.showContent = 'statOrder';
            _this.statOrderTableData = response.data.data.statOrderList;
            _this.projectType = response.data.data.projectCategoriesList;
            _this.yzsProjectType = response.data.data.yzsProjectTypeList;
            _this.remarkContentTableData = response.data.data.remarkList;
            let fileId = _this.fileId;
            let pageSize = _this.pagination.pageSize;
            let pageNum = _this.pagination.currentPage;
            _this.statOrderExecuteStatusCategories = response.data.data.statOrderExecuteStatusListMap;
            _this.statOrderStatusCategories = response.data.data.statOrderStatusListMap;
            getRemarkByFileId(fileId,pageSize,pageNum).then(function (res){
              _this.pagination.pageSize = res.data.data.size;
              _this.pagination.currentPage = res.data.data.current;
              _this.pagination.total = res.data.data.total;
              _this.remarkContentTableData = res.data.data.remarkList;
            });
          }else {
            //正常文书
            _this.showContent = 'normal';
            _this.nodeList = response.data.data.nodeList;
            _this.allEntityLabelList = response.data.data.labelList;
          }
          _this.marked = response.data.data.marked;
          // console.log("是否已标记?");
          // console.log(_this.marked);
        })
      } else {
        return;
      }
    },
    addHightLight(nodeContent){
      // this.hightLight="<span style='background-color: #409EFF;'>病史</span>及辅检无特殊补充。。";
      // alert(nodeContent);
      this.hightLight=nodeContent;
      // document.getElementById('elseHigh').innerHTML = nodeContent;
      // this.$refs.elseHigh.innerHTML=nodeContent;
    },

    toDocUnderstandDetail(nodeName,nodeContent,entityHightLighted,spanHightLighted,eventHightLighted,entityLabelList,spanLabelList,eventList,fileId,entityList,spanList){
      let hospitalId = this.$route.params.hospitalId;
      let admissionId = this.$route.params.admissionId;
      let stage = this.$route.params.stage;
      // alert(recoreId);
      // let str = JSON.stringify(this.allEntityLabelList);
      let  entityLabelListStr = JSON.stringify(entityLabelList);
      let spanLabelListStr = JSON.stringify(spanLabelList);
      let eventListStr = JSON.stringify(eventList);
      let entityListStr = JSON.stringify(entityList);
      let  spanListStr = JSON.stringify(spanList);
      // alert("传递的参数:"+str);
      // console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+str)
      // this.$router.push({
      //   name:'MedicalNodeDetail',
      //   params:{
      //     nodeName:nodeName,
      //     entityHightLighted:entityHightLighted,
      //     spanHightLighted:spanHightLighted,
      //     eventHightLighted:eventHightLighted,
      //     entityLabelList:entityLabelListStr,
      //     spanLabelList:spanLabelListStr,
      //     eventList:eventListStr
      //   }});
      // alert(fileId);
      this.$router.push(
          {
            name:'MedicalNodeDetail',
            params:{
              nodeName:nodeName,
              nodeContent:nodeContent,
              entityHightLighted:entityHightLighted,
              spanHightLighted:spanHightLighted,
              eventHightLighted:eventHightLighted,
              entityLabelList:entityLabelListStr,
              spanLabelList:spanLabelListStr,
              eventList:eventListStr,
              entityList:entityListStr,
              spanList:spanListStr,
              hospitalId:hospitalId,
              admissionId:admissionId,
              stage:stage,
              fileId:fileId
            }
          });
    },
    filterHandler(value, row, column) {const property = column["property"];return row[property] === value;},
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
    },
    beforeRouteLeave:(to,from,next)=>{
      if (confirm("确定离开此页面吗?")==true){
        next();
      }else {
        next(false);
      }
    },

    /**
     * 点击标签触发的事件
     * @param labelContent
     */
    clickTag(labelContent,labelColor) {
      let _this = this;
      let docType = _this.docType;
      let fileId = this.$route.params.fileId=='noFileId'?_this.fileId:this.$route.params.fileId;
      getNodeByFileIdWithHighLight(fileId,labelContent,labelColor,JSON.stringify(_this.nodeMap)).then(function (response) {
        if (docType=='EMR110001'){
          //长期医嘱
          _this.showContent = 'standingOrder';
          _this.standingOrderTableData = response.data.data.standingOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
        }else if (docType=='EMR110002'){
          //临时医嘱
          _this.showContent = 'statOrder';
          _this.statOrderTableData = response.data.data.statOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
        }else {
          //正常文书
          _this.showContent = 'normal';
          _this.nodeList = response.data.data.nodeList;
          _this.allEntityLabelList = response.data.data.labelList;
        }
      })
    },
    returnIndex(){
      this.$router.push('/');
    },
    /**
     * 控制评价弹出框的关闭
     * @param ifSendSuccess
     */
    handleSend(ifSendSuccess){
      let _this = this;
      if (ifSendSuccess){
        _this.orderDialogFormVisible=false;
        //刷新评论条数
        getNodeByFileId(_this.fileId, JSON.stringify(_this.nodeMap)).then(function (response) {
          if (_this.fileId.startsWith('CQYZ')){
            //长期医嘱
            _this.showContent = 'standingOrder';
            _this.standingOrderTableData = response.data.data.standingOrderList;
            _this.projectType = response.data.data.projectCategoriesList;
            _this.yzsProjectType = response.data.data.yzsProjectTypeList;
          }else if (_this.fileId.startsWith('LSYZ')){
            //临时医嘱
            _this.showContent = 'statOrder';
            _this.statOrderTableData = response.data.data.statOrderList;
            _this.projectType = response.data.data.projectCategoriesList;
            _this.yzsProjectType = response.data.data.yzsProjectTypeList;
          }else {
            //正常文书
            _this.showContent = 'normal';
            _this.nodeList = response.data.data.nodeList;
            _this.allEntityLabelList = response.data.data.labelList;
          }
        })
      }
    },
    closeOrderCommentDialog(){
      // _this.orderDialogFormVisible=false;
      let _this = this;
      // alert("关闭了dialog");
      //刷新评论条数
      getNodeByFileId(_this.fileId, JSON.stringify(_this.nodeMap)).then(function (response) {
        if (_this.fileId.startsWith('CQYZ')){
          //长期医嘱
          _this.showContent = 'standingOrder';
          _this.standingOrderTableData = response.data.data.standingOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
        }else if (_this.fileId.startsWith('LSYZ')){
          //临时医嘱
          _this.showContent = 'statOrder';
          _this.statOrderTableData = response.data.data.statOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
        }else {
          //正常文书
          _this.showContent = 'normal';
          _this.nodeList = response.data.data.nodeList;
          _this.allEntityLabelList = response.data.data.labelList;
        }
      })
    },
    openTheComment(row){
      let _this=this;
      _this.orderDialogFormVisible = !_this.orderDialogFormVisible;
      // console.log(row);
      _this.orderDialog.day = row.day;
      _this.orderDialog.time = row.time;
      _this.orderDialog.content = row.content;
      _this.orderDialog.physicianSign = row.physicianSign;
      _this.orderDialog.executeTime = row.executeTime;
      _this.orderDialog.executorSign = row.executorSign;
      _this.orderDialog.yzsProjectType = row.yzsProjectType;
      _this.orderDialog.projectCategories = row.projectCategories;
      _this.orderDialog.unisoundId = row.unisoundId
      // KeyWordsOption.queryCommentHistoryList('', fileId, nodeName, labelName);
      getOrderCommentHistoryList(_this.fileId,row.unisoundId).then(function(response){
        _this.commentHistoryTab = response.data.data;
      })
    },
    commentHistory(){
      let _this = this;
      _this.showCmmentHistoryList = !_this.showCmmentHistoryList;
    },
    orderCommentStatusChange(row){
      // 更新状态
      updateOrderCommentStatus(row.id,row.orderCommentStatus).then(function (response){
        row.orderCommentStatus =row.orderCommentStatus ==0?1:0;
        // 消息提示
        if (response.status=='200'){
          ElMessage({
            showClose: true,
            message: '评论状态更新成功!',
            type: 'success',
            duration: 3 * 1000
          })
        }else {
          ElMessage({
            showClose: true,
            message: '评论状态更新失败!',
            type: 'error',
            duration: 3 * 1000
          })
        }
      })
    },
    toEntityLink(row){
      // alert(row.content);
      let keyWords = row.content;
      //todo 判断是否在白名单内，如果在白名单内才跳?
      // window.location.href="http://10.128.1.114:8019/graph/view/entity?s="+keyWords+"&knowledgeId=ee12d7ee74a7484e90f736c5eba65e5b";
      //keywords进行格式化
      keyWords = keyWords.split(' ').join('');
      alert(keyWords);
      //在新的标签页打开
      window.open("http://10.128.1.114:8019/graph/view/entity?s="+keyWords+"&knowledgeId=ee12d7ee74a7484e90f736c5eba65e5b",'_blank');
    },

    //判断医嘱是否可以跳转实体链接，true:可以；false：不可以
    ifOrderCanLink(classify){
      let result = false;
      // alert(classify);
      if (classify!=null){
        if (classify.includes('药品')||classify.includes('手术')||classify.includes('操作')||classify.includes('检查')||classify.includes('检验')){
          result =true;
        }
      }
      return result;
    },
    markedOrCancelMarkDoc(){
      let _this = this;
      let hospitalId = _this.$route.params.hospitalId;
      let admissionId = _this.$route.params.admissionId;
      let stage = _this.$route.params.stage;
      let fileId = _this.fileId;
      let docType= _this.docType;
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      // alert(fileId);
      markDoc(hospitalId,admissionId,stage,fileId).then(function (response){
        console.log(response);
        if (response.status=='200'){
          ElMessage({
            showClose: true,
            message: '操作成功!',
            type: 'success',
            duration: 3 * 1000
          })
        }else {
          ElMessage({
            showClose: true,
            message: '操作失败',
            type: 'error',
            duration: 3 * 1000
          })
        }
      });
      getNodeByFileId(fileId, JSON.stringify(_this.nodeMap)).then(function (response) {
        if (docType=='EMR110001'){
          //长期医嘱
          _this.showContent = 'standingOrder';
          _this.standingOrderTableData = response.data.data.standingOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
          let fileId = _this.fileId;
          let pageSize = _this.pagination.pageSize;
          let pageNum = _this.pagination.currentPage;
          getRemarkByFileId(fileId,pageSize,pageNum).then(function (res){
            _this.pagination.pageSize = res.data.data.size;
            _this.pagination.currentPage = res.data.data.current;
            _this.pagination.total = res.data.data.total;
            _this.remarkContentTableData = res.data.data.remarkList;
          });
        }else if (docType=='EMR110002'){
          //临时医嘱
          _this.showContent = 'statOrder';
          _this.statOrderTableData = response.data.data.statOrderList;
          _this.projectType = response.data.data.projectCategoriesList;
          _this.yzsProjectType = response.data.data.yzsProjectTypeList;
          let fileId = _this.fileId;
          let pageSize = _this.pagination.pageSize;
          let pageNum = _this.pagination.currentPage;
          getRemarkByFileId(fileId,pageSize,pageNum).then(function (res){
            _this.pagination.pageSize = res.data.data.size;
            _this.pagination.currentPage = res.data.data.current;
            _this.pagination.total = res.data.data.total;
            _this.remarkContentTableData = res.data.data.remarkList;
          });
        }else {
          //正常文书
          _this.showContent = 'normal';
          _this.nodeList = response.data.data.nodeList;
          _this.allEntityLabelList = response.data.data.labelList;
        }
        _this.marked = response.data.data.marked;
        // console.log("是否已标记?");
        // console.log(_this.marked);
      })
    },
    addRemark(){
      // alert('添加备注.');
      let _this = this;
      let remarkContent = _this.remarkContent;
      let hospitalId = _this.$route.params.hospitalId;
      let admissionId = _this.$route.params.admissionId;
      let stage = _this.$route.params.stage;
      let fileId = _this.fileId;
      let docType= _this.docType;
      let pageSize = _this.pagination.pageSize;
      let pageNum = _this.pagination.currentPage;
      // alert(hospitalId);
      // alert(admissionId);
      // alert(stage);
      // alert(fileId);
      // alert(docType);
      // alert(remarkContent);
      addMarkedRemark(hospitalId,admissionId,stage,fileId,remarkContent).then(function(response){
        console.log(response);
        if (response.status=='200'){
          ElMessage({
            showClose: true,
            message: '操作成功!',
            type: 'success',
            duration: 3 * 1000
          });
          //获取最新remark列表
          getRemarkByFileId(fileId,pageSize,pageNum).then(function (res){
            _this.pagination.pageSize = res.data.data.size;
            _this.pagination.currentPage = res.data.data.current;
            _this.pagination.total = res.data.data.total;
            _this.remarkContentTableData = res.data.data.remarkList;
          });

        }else {
          ElMessage({
            showClose: true,
            message: '操作失败',
            type: 'error',
            duration: 3 * 1000
          })
        }
      });
      _this.remarkDialogFormVisible = false
    },
    //切换当前页
    handleCurrentChange(val) {
      let _this = this;
      _this.pagination.currentPage = val;
      let fileId = _this.fileId;
      let pageSize = _this.pagination.pageSize;
      let pageNum = _this.pagination.currentPage;
      getRemarkByFileId(fileId,pageSize,pageNum).then(function (res){
        _this.pagination.pageSize = res.data.data.size;
        _this.pagination.currentPage = res.data.data.current;
        _this.pagination.total = res.data.data.total;
        _this.remarkContentTableData = res.data.data.remarkList;
      });
    },
    //切换每页显示条数
    handleSizeChange(val) {
      let _this = this;
      _this.pagination.pageSize = val;
      let fileId = _this.fileId;
      let pageSize = _this.pagination.pageSize;
      let pageNum = _this.pagination.currentPage;
      getRemarkByFileId(fileId,pageSize,pageNum).then(function (res){
        _this.pagination.pageSize = res.data.data.size;
        _this.pagination.currentPage = res.data.data.current;
        _this.pagination.total = res.data.data.total;
        _this.remarkContentTableData = res.data.data.remarkList;
      });
    },

  },
  data() {
    return {
      //左侧树结构
      treeData: [
      ],
      defaultProps: {
        children: 'children',
        label: 'label',
        fileId:'fileId',
        docType:'docType',
        marked:false
      },
      showContent:'',//有三个选项: 1.normal->正常文书,2.statOrder->临时医嘱,3.standingOrder->长期医嘱
      statOrderTableData:[{
        day:'',
        time:'',
        content:'',
        physicianSign:'',
        executeTime:'',
        executorSign:'',
        projectCategories:'',
        commentNum:'',
        statOrderExecuteStatus:'',
        statOrderStatus:''
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
        projectCategories:'',
        commentNum:'',
        standingOrderStatus:'',
        standingOrderExecuteStatus:''
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
      statOrderExecuteStatusCategories:[],
      statOrderStatusCategories:[],
      //数量表格数据
      tableData: [{
        entityNum: '3',
        eventNum: '2',
        spanNum: '3',
      }],
      selectNodeName:"",
      formatedDocMap:{},
      nodeList:[
        // {
        //   "nodeName":"一般情况/aa",
        //   "nodeContent":"asdjasdasdasdasdasdasa",
        //   "entityNum":36,
        //   "eventNum":4,
        //   "spanNum":3
        // }
      ],
      //右侧总标签数据
      allEntityLabelList:[
        // {
        //   "labelColor":"#b3e19d",
        //   "labelContent":"标签一"
        // },
        // {
        //   "labelColor":"#eebe77",
        //   "labelContent":"标签二"
        // },
        // {
        //   "labelColor":"#f89898",
        //   "labelContent":"标签三"
        // }
      ],
      allSpanLabelList:[],
      hightLight:'',
      docDetail:{},
      entityLabelList:[],
      spanLabelList:[],
      docType:'',
      fileId:'',
      Visiable:false,
      detailVisible:false,
      orderDialogFormVisible:false,
      orderDialog:[
        {
          day:'',
          time:'',
          content:'',
          physicianSign:'',
          executeTime:'',
          executorSign:'',
          yzsProjectType:'',
          projectCategories:'',
          unisoundId:''
        }
      ],
      showCmmentHistoryList:false,
      commentHistoryTab:[
        {
          id:1,
          content: '2016-05-03',
          userName: 'Tom',
          createTime: 'No. 189, Grove St, Los Angeles',
          orderCommentStatus:1
        },
        {
          id:2,
          content: '2016-05-03',
          userName: 'Tom',
          createTime: 'No. 189, Grove St, Los Angeles',
          orderCommentStatus:1
        }
      ],
      table:window.innerHeight - 107,           //固定表头高度
      marked:false,
      remarkDialogFormVisible : false,
      remarkContent:'',
      remarkContentTableData:[
        {
          remarkContent:'',
          createTime:''
        }
      ],
      pagination: {
        currentPage: 1,
        pageSize: 5,
        total: 0
      },
    }
  },
  mounted(){
    //todo 加载初始化操作
    this.getAllDoc();
  }
};
</script>
<style>
  .layout_col{
    height: 85vh;
    border: 1px solid;
    border-color:#EBEDF0;
    margin-top: 20px;
  }
  .container {
    display: flex;
    justify-content: flex-end;
  }

  /*设置横向滚动条*/
  .el-tree {
    min-width: 100%;
    display: inline-block !important;
  }
  .orderDialogContentStyle{
    color: #c45656;
    margin-left: 10px;
  }

  .post-btn {
    float: right;
    cursor: pointer;
    border-radius: 4px;
    padding: 5px 20px;
    background: dodgerblue;
    color: white;
    margin-bottom: 15px;
  }

  .item {
    margin-top: 10px;
    margin-right: 40px;
  }

  .tree-style{
    div{
      display: flex;
      align-items: center;
    }
  }
</style>