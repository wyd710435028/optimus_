<template>
  <router-view></router-view>
  <el-dialog
      v-model="dialogVisible"
      title="操作选择"
      width="500"
      :before-close="handleClose"
  >
  <el-row>
<!-- keyWords:'',
      fileId:'',
      nodeName:'',
      labelName:''   -->
    <div>
      <p>
        <span><strong>高亮的文本内容:  </strong></span>
        <span>{{this.keyWords}}</span>
      </p>
      <p>
        <span><strong>FileId: </strong></span>
        <span>{{this.fileId}}</span>
      </p>
<!--      <p>-->
<!--        <span><strong>DocName:  </strong></span>-->
<!--        <span>{{this.docName}}</span>-->
<!--      </p>-->
      <p>
        <span><strong>NodeName: </strong></span>
        <span>{{this.nodeName}}</span>
      </p>
      <p>
        <span><strong>LabelName:  </strong></span>
        <span>{{this.labelName}}</span>
      </p>
    </div>
  </el-row>
  <!-- 评论 -->
  <el-divider></el-divider>
  <el-row>
    <el-button type="primary" @click="comment">评论</el-button>
    <div style="margin-left: 30px" v-if="showCommentInput==true">
      <p>
        <!-- 表情面板 -->
        <emoji-panel></emoji-panel>
      </p>
      <!--      <el-input v-model="input" placeholder="在此输入评论内容" />-->
      <!--      <el-button type="danger" style="float: right;margin-top: 5px">提交</el-button>-->
      <!--      <comment-list></comment-list>-->
      <!--      <el-button type="warning" style="float: right;margin-top: 5px;margin-right: 5px" @click="commentHistory">评论历史</el-button>-->
      <!--      <div v-if="showCmmentHistoryList==true">-->
      <!--        <el-table>-->
      <!--          <el-table-column prop="content" label="内容" />-->
      <!--          <el-table-column prop="date" label="时间" />-->
      <!--          <el-table-column prop="user" label="评论人" />-->
      <!--          <el-table-column prop="user" label="操作" />-->
      <!--        </el-table>-->
      <!--      </div>-->
    </div>
  </el-row>
  <!-- 评论历史 -->
  <el-divider></el-divider>
  <el-row>
    <el-button type="primary" style="float: right;margin-top: 5px" @click="commentHistory">评论历史</el-button>
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
      </el-table>
    </div>
  </el-row>
  <!-- 实体链接 -->
  <el-divider></el-divider>
  <el-row style="margin-top: 10px">
    <el-button type="primary" @click="goToEntityLink">实体链接</el-button>
  </el-row>
  </el-dialog>
</template>

<!--js-->
<script>
import CommentList from "@/views/CommentList.vue";
import EmojiPanel from "@/views/emoji/EmojiPanel.vue";
import {getCommentHistoryList} from "@/apis/get";
import {ElMessage} from "element-plus";

export default {
  name:"KeyWordsOption",
  components: {EmojiPanel, CommentList},
  /*属性*/
  data(){
    return{
      //控制切换用户页面是否弹出
      dialogVisible:true,
      keyWords:'',
      fileId:'',
      nodeName:'',
      labelName:'',
      showCommentInput:false,
      docName:'',
      showCmmentHistoryList:false,
      commentHistoryTab:[
        {
          content: '2016-05-03',
          userName: 'Tom',
          createTime: 'No. 189, Grove St, Los Angeles',
        },
        {
          content: '2016-05-03',
          userName: 'Tom',
          createTime: 'No. 189, Grove St, Los Angeles',
        }
      ],
      entityLinkWhiteList:[
          'operation',
          'disease',
          'symptom',
          'sign',
          'medicine',
          'check',
          'indicator'
      ]
    }
  },
  /*方法*/
  methods:{
    goToEntityLink(){
      let _this = this;
      let keyWords = _this.$route.params.keyWords;
      let fileId = _this.$route.params.fileId;
      let nodeName = _this.$route.params.nodeName;
      let labelName = _this.$route.params.labelName;
      //EntityLinkJump/:entityName/:labelName
      // _this.$router.push('/EntityLinkJump/'+keyWords+'/'+labelName);
      if (_this.entityLinkWhiteList.includes(labelName)){
        window.location.href="http://10.128.1.114:8019/graph/view/entity?s="+keyWords+"&knowledgeId=ee12d7ee74a7484e90f736c5eba65e5b";
      }else {
        //提示错误
        ElMessage({
          showClose: true,
          message: labelName+'不在白名单内,无法跳转到实体链接',
          type: 'warning',
          duration: 3 * 1000
        })
      }
    },
    comment(){
      let _this = this;
      _this.showCommentInput = !_this.showCommentInput;
      // _this.dialogVisible = false
    },
    commentHistory(){
      let _this = this;
      _this.showCmmentHistoryList = !_this.showCmmentHistoryList;
    },
    //查询评论历史列表
    queryCommentHistoryList(keyWords, fileId, nodeName, labelName) {
      let _this = this;
      getCommentHistoryList(keyWords, fileId, nodeName, labelName).then(function (response){
        _this.commentHistoryTab = response.data.data;
      });
    }
  },
  /*初始化加载*/
  mounted(){
    let _this = this;
    _this.keyWords = _this.$route.params.keyWords;
    _this.fileId = _this.$route.params.fileId;
    _this.nodeName = _this.$route.params.nodeName;
    _this.labelName = _this.$route.params.labelName;
    _this.docName = _this.$route.params.docName;
    // alert(keyWords+":"+fileId+":"+nodeName+":"+labelName);
    //查询评论历史
    _this.queryCommentHistoryList(_this.keyWords,_this.fileId,_this.nodeName,_this.labelName);
    // _this.commentHistoryTab =
  }
}
</script>

<!--样式-->
<style>

</style>