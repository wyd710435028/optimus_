<template>
  <router-view></router-view>
  <div v-for="comment in commentList" :key="comment">
    <!--根评论-->
    <a-comment
               :author="comment.userName"
               :avatar="comment.avatarUrl"
               :content="comment.content"
               :datetime="comment.createTime">
        <span @click="replayComment(comment)" class="action"> <IconMessage /> 回复 </span>
        <span @click="deleteComment(comment)" class="action"> <IconDelete/> 删除 </span>
        <div style="margin-top: 20px" v-if="comment.ifShowReplayInput==true">
          <!--todo 目前写死,有登陆页面时写成获取当前用户信息-->
          <a-comment
              align="right"
              avatar="https://img0.baidu.com/it/u=2947341353,265385944&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500"
          >
            <template #actions>
              <a-button key="0" type="secondary" @click="closeReplayInput(comment)"> 取消 </a-button>
              <a-button key="1" type="primary" @click="submitComment(comment,comment.id,50,comment.rootParentId,commentContent)"> 提交 </a-button>
            </template>
            <template #content>
              <a-input v-model="commentContent" placeholder="在此输入你的评论。" />
            </template>
          </a-comment>
        </div>
        <!--子评论-->
        <div v-for="subComment in (comment.child)" :key="subComment" style="margin-top: 20px">
          <a-comment
              :author="subComment.userName+' 回复 '+subComment.parentName"
              :avatar="subComment.avatarUrl"
              :content="subComment.content"
              :datetime="subComment.createTime"
          >
          <span @click="replayComment(subComment)" class="action"> <IconMessage /> 回复 </span>
          <span @click="deleteComment(subComment)" class="action"> <IconDelete/> 删除 </span>
          <div style="margin-top: 20px" v-if="subComment.ifShowReplayInput==true">
            <a-comment
                align="right"
                avatar="https://img0.baidu.com/it/u=2947341353,265385944&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500"
            >
              <template #content>
                <a-input v-model="subCommentContent" placeholder="在此输入你的评论。" />
              </template>
              <template #actions>
                <a-button key="0" type="secondary" @click="closeReplayInput(subComment)"> 取消 </a-button>
                <a-button key="1" type="primary" @click="submitComment(subComment,subComment.id,50,subComment.rootParentId,subCommentContent)"> 提交 </a-button>
              </template>
            </a-comment>
          </div>
          </a-comment>
        </div>
    </a-comment>
  </div>
</template>

<script>
import {IconDelete, IconHeart, IconMessage, IconStar} from '@arco-design/web-vue/es/icon';
import {getCommentLst} from "@/apis/get";
import {saveComment,deleteCommentById} from "@/apis/post";

export default {
  components: {
    IconHeart,
    IconMessage,
    IconStar,
    IconDelete
  },
  data(){
    return{
      commentList:[
        {
          "id": 1,
          "content": "管理员发送第一条评论",
          "userId": 1,
          "userName": "admin",
          "createTime": "2024-01-25 06:10:50",
          "isDelete": 0,
          "belongsId": 1,
          "parentId": null,
          "rootParentId": 1,
          "child": [
            {
              "id": 8,
              "content": "用户孙七回复了管理员的第一条评论",
              "userId": 6,
              "userName": "sunqi",
              "createTime": "2024-01-25 06:30:12",
              "isDelete": 0,
              "belongsId": 1,
              "parentId": 1,
              "rootParentId": 1,
              "child": null,
              "ifShowReplayInput": 0
            }
          ],
          "ifShowReplayInput": 0
        },
        {
          "id": 2,
          "content": "用户张三发表的评论1",
          "userId": 2,
          "userName": "zhangsan",
          "createTime": "2024-01-25 06:19:40",
          "isDelete": 0,
          "belongsId": 1,
          "parentId": null,
          "rootParentId": 2,
          "child": [
            {
              "id": 4,
              "content": "管理员回复张三的评论1",
              "userId": 1,
              "userName": "admin",
              "createTime": "2024-01-25 06:21:37",
              "isDelete": 0,
              "belongsId": 1,
              "parentId": 2,
              "rootParentId": 2,
              "child": [
                {
                  "id": 5,
                  "content": "张三回复管理员回复的张三的评论1",
                  "userId": 2,
                  "userName": "admin",
                  "createTime": "2024-01-25 06:23:06",
                  "isDelete": 0,
                  "belongsId": 1,
                  "parentId": 4,
                  "rootParentId": 2,
                  "child": [
                    {
                      "id": 6,
                      "content": "王五回复张三回复管理员回复的张三的评论1",
                      "userId": 4,
                      "userName": "wangwu",
                      "createTime": "2024-01-25 06:25:21",
                      "isDelete": 0,
                      "belongsId": 1,
                      "parentId": 5,
                      "rootParentId": 2,
                      "child": null,
                      "ifShowReplayInput": 0
                    }
                  ],
                  "ifShowReplayInput": 0
                }
              ],
              "ifShowReplayInput": 0
            }
          ],
          "ifShowReplayInput": 0
        },
        {
          "id": 3,
          "content": "用户李四发表的评论1",
          "userId": 3,
          "userName": "lisi",
          "createTime": "2024-01-25 06:20:33",
          "isDelete": 0,
          "belongsId": 1,
          "parentId": null,
          "rootParentId": 3,
          "child": [
            {
              "id": 7,
              "content": "用户赵六回复了李四发表的评论1",
              "userId": 5,
              "userName": "zhaoliu",
              "createTime": "2024-01-25 06:27:31",
              "isDelete": 0,
              "belongsId": 1,
              "parentId": 3,
              "rootParentId": 3,
              "child": null,
              "ifShowReplayInput": 0
            }
          ],
          "ifShowReplayInput": 0
        }
      ]
    }
  },
  methods:{
    //回复输入框显示/不显示
    replayComment(comment){
        comment.ifShowReplayInput=!comment.ifShowReplayInput;
    },
    //关闭回复输入框
    closeReplayInput(comment){
      // alert(comment.ifShowReplayInput);
      comment.ifShowReplayInput = false;
    },
    //查询回复列表
    queryCommentList(){
      let _this = this;
      getCommentLst().then(function (response){
        _this.commentList = response.data.data;
      });
    },
    //提交评论
    submitComment(comment,parentId,userId,rootParentId,content){
      // alert('父评论是:'+parentId+',当前用户id是:'+userId+',根评论id是:'+rootParentId+',评论的内容是:'+content);
      let _this = this;
      saveComment(parentId,userId,rootParentId,content).then(function (response){
        if (response.status=='200'){
          _this.$message.success({content:'评论成功',closable: true});
          //关闭输入框
          _this.closeReplayInput(comment);
        }else {
          _this.$message.error({content:'评论失败',closable:true});
        }
      });
    },
    //删除评论
    deleteComment(comment){
      let _this = this;
      let id = comment.id;
      deleteCommentById(id).then(function (response){
        if (response.status=='200'){
          _this.$message.success({content:'删除成功',closable: true,duration:3000});
          _this.queryCommentList();
        }else {
          _this.$message.error({content:'删除失败',closable:true,duration:3000});
        }
      });
      // alert(comment.id);
    }
  },
  mounted() {
    let _this = this;
    _this.queryCommentList();
  }
};
</script>

<style scoped>
.action {
  display: inline-block;
  padding: 0 4px;
  color: #333333;
  line-height: 24px;
  background: transparent;
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.1s ease;
}
.action:hover {
  background: #c0c4cc;
}
</style>
