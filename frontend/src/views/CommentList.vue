<template>
  <router-view></router-view>
  <!-- 顶部带表情大的输入框和表情选择面板 -->
  <div class="mo-yu-container">
    <div class="input-part">
      <div class="input-panel" id="msgInputContainer" ref="msgInputContainer"
           placeholder="快来发表你的观点吧~~"
           contenteditable="true" spellcheck="true">
      </div>
      <div class="action-bar" id="input-action-bar">
        <span class="emoji-btn" @click="handleEmojiPanelVisibility"><IconFaceSmileFill/></span>
        <span class="post-btn" @click="createRootComment(50)">发布</span>
      </div>
    </div>
    <!-- 表情面板 -->
    <div class="emoji-container" id="emoji-container" v-show="isEmojiShow">
      <div class="emoji-list">
        <div class="emoji-title-history" v-if="historyList.length!==0">最近使用</div>
        <div class="emoji-history-list" v-if="historyList.length!==0">
          <span v-for="i in historyList" :key="i">
            <img class="emoji-item"  @click="onEmojiClick(i)"
                 :src="'https://cdn.sunofbeaches.com/emoji/'+i+'.png'">
          </span>
        </div>
        <div class="emoji-title-all">全部</div>
        <div class="emoji-all-list">
           <span v-for="i in 130" :key="i">
            <img @click="onEmojiClick(i)" class="emoji-item"
                 :src="'https://cdn.sunofbeaches.com/emoji/'+i+'.png'">
           </span>
        </div>
      </div>
<!--      <emoji-panel></emoji-panel>-->
    </div>
  </div>


  <!--评论列表-->
  <div class="commentTree" @click="handleClickBlank">
    <div v-for="comment in commentList" :key="comment">
      <!--根评论-->
      <div style="margin-top: 20px">
        <!-- 根评论头像 -->
        <span><img :src="comment.avatarUrl" style="width: 50px;height: 50px;border-radius:50%;float: left"></span>
        <!-- 根评论人名称 -->
        <span style="font-size: 16px;color: #86909c;margin-left: 20px;font-weight: bold">{{comment.userName}}</span>
        <!-- 根评论时间 -->
        <span style="font-size: 14px; color: #86909c;margin-left: 10px;font-weight: bold">{{comment.createTime}}</span>
        <!-- 根评论内容 -->
        <span style="font-size: 16px;">
          <div
              style="margin-left: 70px;margin-top: 8px"
              v-html="comment.content"
              contenteditable="false" spellcheck="false">
          </div>
        </span>
        <!-- 根评论回复和删除操作 -->
        <div style="margin-left: 15%;">
          <span style="margin-top: 8px" @click="replayComment(comment)" class="action"> <IconMessage/> 回复 </span>
          <el-popconfirm width="300" title="删除此评论,子评论也会删除,是否继续?" type="warning" @confirm="deleteComment(comment)">
            <template #reference>
              <span class="action"> <IconDelete/> 删除 </span>
            </template>
          </el-popconfirm>
        </div>

        <!-- 回复输入框,点击回复显示 -->
        <div v-if="comment.ifShowReplayInput==true" style="margin-left: 65px;margin-top: 10px">
          <!--todo 待做:目前写死,有登陆页面时写成获取当前用户信息-->
          <!-- 当前用户头像 -->
          <span><img src="https://img0.baidu.com/it/u=2947341353,265385944&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500" style="width: 40px;height: 40px;border-radius:50%;float: left"></span>
          <!-- 输入框 -->
          <div class="input-replay" id="rootMsgInputContainer" ref="rootMsgInputContainer" :placeholder="'回复:'+comment.userName" contenteditable="true" spellcheck="true"></div>
          <div style="width: 400px; margin-left: 37%; margin-top: 10px;">
            <span style="margin-left: 18px;margin-top: 8px" @click="showEmojiInReplay()" class="action"> <IconFaceSmileFill/> 表情 </span>
            <span style="margin-left: 18px;margin-top: 8px" @click="closeReplayInput(comment)" class="action"> <iconClose /> 取消 </span>
            <span style="margin-left: 18px;margin-top: 8px" @click="submitRootMsgComment(comment,comment.id,50,comment.rootParentId,commentContent)" class="action"> <IconSend/> 发送 </span>
<!--            <el-button key="2" type="danger" @click="showEmojiInReplay()">表情</el-button>-->
<!--            <el-button key="0" type="warning" @click="closeReplayInput(comment)"> 取消 </el-button>-->
<!--            <el-button key="1" type="primary" @click="submitRootMsgComment(comment,comment.id,50,comment.rootParentId,commentContent)"> 提交 </el-button>-->
            <div style="position: relative;" class="emoji-container" id="emoji-container" v-show="isEmojiShowInReplay">
              <div class="emoji-list">
                <div class="emoji-title-history" v-if="historyList.length!==0">
                  最近使用
                </div>
                <div class="emoji-history-list" v-if="historyList.length!==0">
                <span v-for="i in historyList" :key="i">
                  <img class="emoji-item"  @click="onEmojiInReplayClick(i)"
                       :src="'https://cdn.sunofbeaches.com/emoji/'+i+'.png'">
                </span>
                </div>
                <div class="emoji-title-all">
                  全部
                </div>
                <div class="emoji-all-list">
                 <span v-for="i in 130" :key="i">
                  <img @click="onEmojiInReplayClick(i)" class="emoji-item"
                       :src="'https://cdn.sunofbeaches.com/emoji/'+i+'.png'">
                 </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!--子评论-->
      <div v-for="subComment in (comment.child)" :key="subComment">
        <div style="margin-top: 20px">
          <!--子评论头像-->
          <span><img :src="subComment.avatarUrl" style="width: 40px;height: 40px;border-radius:50%;float: left;margin-left:55px"></span>
          <!--用户名-->
          <span style="font-size: 16px;color: #86909c;margin-left: 10px;">{{subComment.userName}} 回复 {{subComment.parentName}}</span>
          <!--时间-->
          <span style="font-size: 14px; color: #86909c;margin-left: 20px;">{{subComment.createTime}}</span>
          <!--评论内容,支持表情显示-->
          <span style="font-size: 16px;">
            <div
              style="margin-left: 115px;margin-top: 8px"
              v-html="subComment.content"
              contenteditable="false" spellcheck="false">
            </div>
          </span>
          <!--子评论回复和删除操作-->
          <span style="margin-left:115px;margin-top: 8px" @click="replayComment(subComment)" class="action"> <IconMessage /> 回复 </span>
<!--          <a-popconfirm content="删除此评论,子评论也会删除,是否继续?" type="warning" @ok="deleteComment(subComment)">-->
<!--            <span class="action"> <IconDelete/> 删除 </span>-->
<!--          </a-popconfirm>-->
          <el-popconfirm width="300" title="删除此评论,子评论也会删除,是否继续?" @confirm="deleteComment(subComment)">
            <template #reference>
              <span class="action"> <IconDelete/> 删除 </span>
            </template>
          </el-popconfirm>

          <!-- 回复输入框,点击回复显示 -->
          <div v-if="subComment.ifShowReplayInput==true" style="margin-left: 90px;margin-top: 10px">
            <!--todo 待做:目前写死,有登陆页面时写成获取当前用户信息-->
            <!-- 当前用户头像 -->
            <span><img src="https://img0.baidu.com/it/u=2947341353,265385944&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500" style="width: 30px;height: 30px;border-radius:50%;float: left"></span>
            <!-- 输入框 -->
            <div class="input-replay" id="rootSubMsgInputContainer" ref="rootSubMsgInputContainer" :placeholder="'回复:'+subComment.userName" contenteditable="true" spellcheck="true"></div>
<!--            <div style="margin-top:10px;float: right; margin-right: 17.35%">-->
<!--              -->
<!--            </div>-->
            <div style="width: 400px;margin-left: 35%;margin-top: 10px;">
<!--              <span class="emoji-btn" @click="showEmojiInSubReplay"><IconFaceSmileFill/></span>-->
              <span style="margin-left: 18px;margin-top: 8px" @click="showEmojiInSubReplay()" class="action"> <IconFaceSmileFill/> 表情 </span>
              <span style="margin-left: 18px;margin-top: 8px" @click="closeReplayInput(subComment)" class="action"> <iconClose /> 取消 </span>
              <span style="margin-left: 18px;margin-top: 8px" @click="submitRootSubMsgComment(subComment,subComment.id,50,subComment.rootParentId,commentContent)" class="action"> <IconSend/> 发送 </span>
<!--              <el-button key="2" type="danger" @click="showEmojiInSubReplay()">表情</el-button>-->
<!--              <el-button key="0" type="warning" @click="closeReplayInput(subComment)"> 取消 </el-button>-->
<!--              <el-button key="1" type="primary" @click="submitRootSubMsgComment(subComment,subComment.id,50,subComment.rootParentId,commentContent)"> 提交 </el-button>-->
              <div class="emoji-container" id="emoji-container" style="position: relative;z-index: 1000" v-show="isEmojiShowInSubReplay">
                <div class="emoji-list">
                  <div class="emoji-title-history" v-if="historyList.length!==0">
                    最近使用
                  </div>
                  <div class="emoji-history-list" v-if="historyList.length!==0">
                <span v-for="i in historyList" :key="i">
                  <img class="emoji-item"  @click="onEmojiInSubReplayClick(i)"
                       :src="'https://cdn.sunofbeaches.com/emoji/'+i+'.png'">
                </span>
                  </div>
                  <div class="emoji-title-all">
                    全部
                  </div>
                  <div class="emoji-all-list">
                 <span v-for="i in 130" :key="i">
                  <img @click="onEmojiInSubReplayClick(i)" class="emoji-item"
                       :src="'https://cdn.sunofbeaches.com/emoji/'+i+'.png'">
                 </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-divider></el-divider>
    </div>
  </div>
</template>

<script>


import {IconDelete, IconHeart, IconMessage, IconStar,IconFaceSmileFill,IconSend,IconClose} from '@arco-design/web-vue/es/icon';
import {getCommentLst} from "@/apis/get";
import {saveComment,deleteCommentById,createNewRootComment} from "@/apis/post";
import { ref } from 'vue';

/* 表情配置数据 转为 数组 */
import emojiConfig from '@/components/EmojiText/emoji.json'
import SvgIcon from "@/components/SvgIcon.vue";
import {ElMessage} from "element-plus";
let emojiList = []
for (let key in emojiConfig) {
  emojiList.push({
    title: key,
    link: emojiConfig[key]
  })
}

export default {
  //组件
  components: {
    SvgIcon,
    IconHeart,
    IconMessage,
    IconStar,
    IconDelete,
    IconFaceSmileFill,
    IconSend,
    IconClose
  },
  props: {
    imgPrefix: { /* 图片路径前缀 */
      type:String,
      default:''
    },
    placeholder: { /* 默认占位符 */
      type:String,
      default: '快来发表你的观点吧~~'
    },
    avatarUrl: { /* 头像 */
      type:String
    },
    emojiSize:{
      type:Number,
      default: 10
    },
    afterComment: {  /* 发表评论之后，需要执行的函数 */
      type: Function
    }
  },
  //数据
  data(){
    return{
      commentContent:'',
      subCommentContent:'',
      rootCommentContent:'',
      ifCanShowReplayInput:true,
      commentList:[
        // {
        //   "id": 1,
        //   "content": "管理员发送第一条评论",
        //   "userId": 1,
        //   "userName": "admin",
        //   "createTime": "2024-01-25 06:10:50",
        //   "isDelete": 0,
        //   "belongsId": 1,
        //   "parentId": null,
        //   "rootParentId": 1,
        //   "child": [
        //     {
        //       "id": 8,
        //       "content": "用户孙七回复了管理员的第一条评论",
        //       "userId": 6,
        //       "userName": "sunqi",
        //       "createTime": "2024-01-25 06:30:12",
        //       "isDelete": 0,
        //       "belongsId": 1,
        //       "parentId": 1,
        //       "rootParentId": 1,
        //       "child": null,
        //       "ifShowReplayInput": 0
        //     }
        //   ],
        //   "ifShowReplayInput": 0
        // },
        // {
        //   "id": 2,
        //   "content": "用户张三发表的评论1",
        //   "userId": 2,
        //   "userName": "zhangsan",
        //   "createTime": "2024-01-25 06:19:40",
        //   "isDelete": 0,
        //   "belongsId": 1,
        //   "parentId": null,
        //   "rootParentId": 2,
        //   "child": [
        //     {
        //       "id": 4,
        //       "content": "管理员回复张三的评论1",
        //       "userId": 1,
        //       "userName": "admin",
        //       "createTime": "2024-01-25 06:21:37",
        //       "isDelete": 0,
        //       "belongsId": 1,
        //       "parentId": 2,
        //       "rootParentId": 2,
        //       "child": [
        //         {
        //           "id": 5,
        //           "content": "张三回复管理员回复的张三的评论1",
        //           "userId": 2,
        //           "userName": "admin",
        //           "createTime": "2024-01-25 06:23:06",
        //           "isDelete": 0,
        //           "belongsId": 1,
        //           "parentId": 4,
        //           "rootParentId": 2,
        //           "child": [
        //             {
        //               "id": 6,
        //               "content": "王五回复张三回复管理员回复的张三的评论1",
        //               "userId": 4,
        //               "userName": "wangwu",
        //               "createTime": "2024-01-25 06:25:21",
        //               "isDelete": 0,
        //               "belongsId": 1,
        //               "parentId": 5,
        //               "rootParentId": 2,
        //               "child": null,
        //               "ifShowReplayInput": 0
        //             }
        //           ],
        //           "ifShowReplayInput": 0
        //         }
        //       ],
        //       "ifShowReplayInput": 0
        //     }
        //   ],
        //   "ifShowReplayInput": 0
        // },
        // {
        //   "id": 3,
        //   "content": "用户李四发表的评论1",
        //   "userId": 3,
        //   "userName": "lisi",
        //   "createTime": "2024-01-25 06:20:33",
        //   "isDelete": 0,
        //   "belongsId": 1,
        //   "parentId": null,
        //   "rootParentId": 3,
        //   "child": [
        //     {
        //       "id": 7,
        //       "content": "用户赵六回复了李四发表的评论1",
        //       "userId": 5,
        //       "userName": "zhaoliu",
        //       "createTime": "2024-01-25 06:27:31",
        //       "isDelete": 0,
        //       "belongsId": 1,
        //       "parentId": 3,
        //       "rootParentId": 3,
        //       "child": null,
        //       "ifShowReplayInput": 0
        //     }
        //   ],
        //   "ifShowReplayInput": 0
        // }
      ],
      /* 文本框中有文字 或 无文字但是处于焦点状态时 为true */
      height80: false,
      /* 表情配置数据 */
      emojiList,
      /* 是否打开表情面板 */
      emojiPanelActive: false,
      /* 文本框的内容 */
      textareaContent: '',
      historyList: [10,23,1,33,22],
      isEmojiShow: false,
      isEmojiShowInReplay:false,
      isEmojiShowInSubReplay:false
    }
  },

  //方法
  methods:{
    //回复输入框显示/不显示
    replayComment(comment){
      let _this = this;
      _this.isEmojiShowInReplay = false;
      _this.isEmojiShow = false;
      _this.isEmojiShowInSubReplay = false;
      if (_this.ifCanShowReplayInput) {
          comment.ifShowReplayInput = !comment.ifShowReplayInput;
          _this.ifCanShowReplayInput = !comment.ifShowReplayInput;
      }else {
        // _this.$message.warning({content:'有未关闭的评论框,请先关闭再评论!',closable: true});
        ElMessage({
          showClose: true,
          message: '有未关闭的评论框,请先关闭再评论!',
          type: 'warning',
        })
      }
    },
    //关闭回复输入框
    closeReplayInput(comment){
      // alert(comment.ifShowReplayInput);
      let _this = this;
      _this.commentContent = null;
      _this.subCommentContent = null;
      comment.ifShowReplayInput = false;
      _this.ifCanShowReplayInput = true;
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
      if (content == null || content.trim().length == 0){
        // _this.$message.warning({content:'请输入评论内容',closable: true});
        ElMessage({
          showClose: true,
          message: '请输入评论内容',
          type: 'warning',
        })
      }else {
        //输入评论内容才保存
        saveComment(parentId,userId,rootParentId,content).then(function (response){
          if (response.status=='200'){
            // _this.$message.success({content:'评论成功',closable: true});
            ElMessage({
              showClose: true,
              message: '评论成功',
              type: 'success',
            })
            //关闭输入框
            _this.closeReplayInput(comment);
            _this.queryCommentList();
          }else {
            // _this.$message.error({content:'评论失败',closable:true});
            ElMessage({
              showClose: true,
              message: '评论失败',
              type: 'error',
            })
          }
        });
      }
    },
    submitRootMsgComment(comment,parentId,userId,rootParentId){
      // alert('父评论是:'+parentId+',当前用户id是:'+userId+',根评论id是:'+rootParentId+',评论的内容是:'+content);
      let content = document.getElementById("rootMsgInputContainer").innerHTML;
      let _this = this;
      if (content == null || content.trim().length == 0){
        // _this.$message.warning({content:'请输入评论内容',closable: true});
        ElMessage({
          showClose: true,
          message: '请输入评论内容',
          type: 'warning',
        })
      }else {
        //输入评论内容才保存
        saveComment(parentId,userId,rootParentId,content).then(function (response){
          if (response.status=='200'){
            // _this.$message.success({content:'评论成功',closable: true});
            ElMessage({
              showClose: true,
              message: '评论成功',
              type: 'success',
            })
            //关闭输入框
            _this.closeReplayInput(comment);
            _this.queryCommentList();
          }else {
            // _this.$message.error({content:'评论失败',closable:true});
            ElMessage({
              showClose: true,
              message: '评论失败',
              type: 'error',
            })
          }
        });
      }
    },
    submitRootSubMsgComment(comment,parentId,userId,rootParentId){
      // alert('父评论是:'+parentId+',当前用户id是:'+userId+',根评论id是:'+rootParentId+',评论的内容是:'+content);
      let content = document.getElementById("rootSubMsgInputContainer").innerHTML;
      let _this = this;
      if (content == null || content.trim().length == 0){
        // _this.$message.warning({content:'请输入评论内容',closable: true});
        ElMessage({
          showClose: true,
          message: '请输入评论内容',
          type: 'warning',
        })
      }else {
        //输入评论内容才保存
        saveComment(parentId,userId,rootParentId,content).then(function (response){
          if (response.status=='200'){
            // _this.$message.success({content:'评论成功',closable: true});
            ElMessage({
              showClose: true,
              message: '评论成功',
              type: 'success',
            })
            //关闭输入框
            _this.closeReplayInput(comment);
            _this.queryCommentList();
          }else {
            // _this.$message.error({content:'评论失败',closable:true});
            ElMessage({
              showClose: true,
              message: '评论失败',
              type: 'error',
            })
          }
        });
      }
    },
    //删除评论
    deleteComment(comment){
      let _this = this;
      let id = comment.id;
      deleteCommentById(id).then(function (response){
        if (response.status=='200'){
          // _this.$message.success({content:'删除成功',closable: true,duration:3000});
          ElMessage({
            showClose: true,
            message: '删除成功',
            type: 'success'
          })
          _this.queryCommentList();
        }else {
          // _this.$message.error({content:'删除失败',closable:true,duration:3000});
          ElMessage({
            showClose: true,
            message: '删除失败',
            type: 'error'
          })
        }
      });
      // alert(comment.id);
    },

    //创建新评论(根评论)
    createRootComment(userId){
      // alert(document.getElementById('inputContent').innerHTML);
      let rootCommentContent = document.getElementById('msgInputContainer').innerHTML;
      // alert(document.getElementById('inputContent').textContent);
      // alert(rootCommentContent);
      let  _this = this;
      if (rootCommentContent == null || rootCommentContent.trim().length == 0){
        // _this.$message.warning({content:'请输入评论内容',closable: true});
        ElMessage({
          showClose: true,
          message: '请输入评论内容',
          type: 'warning',
        })
      }else {
        createNewRootComment(rootCommentContent,userId).then(function (response){
          if (response.status=='200'){
            // _this.$message.success({content:'发表成功',closable: true,duration:3000});
            ElMessage({
              showClose: true,
              message: '发表成功',
              type: 'success',
            })
            _this.rootCommentContent = null;
            _this.queryCommentList();
            _this.isEmojiShow = false;
            document.getElementById('msgInputContainer').innerHTML = '';
          }else {
            // _this.$message.error({content:'发表失败',closable:true,duration:3000});
            ElMessage({
              showClose: true,
              message: '发表失败',
              type: 'error',
            })
          }
        });
      }
    },

    /* 添加表情 */
    addEmoji(emoji) {
      let textarea = this.$refs['textarea'];
      console.log(textarea.selectionStart, textarea.selectionEnd, 'start,end');

      // 最开始的位置要记录下，后面要根据它来设置插入文本后，设置光标的位置
      let selectionStart1 = textarea.selectionStart

      let txtArr = this.rootCommentContent.split('')
      txtArr.splice(textarea.selectionStart, textarea.selectionEnd - textarea.selectionStart, emoji.title)
      this.rootCommentContent = txtArr.join('')

      /* 一定要放在$nextTick去执行, 上面修改完值后, 还要等vue把修改的数据渲染出来之后, 再去定位光标 */
      this.$nextTick(() => {
        // 替换文本后, 需要把光标，再次定位到替换后的那个位置，否则，它会回到最前面
        textarea.focus()
        textarea.setSelectionRange(selectionStart1 + emoji.title.length, selectionStart1 + emoji.title.length)
      })
    },

    /* 激活表情面板, 第二个参数: 是否切换 */
    activeEmojiPanel(e, isToggle) {
      if (isToggle) {
        this.emojiPanelActive = !this.emojiPanelActive
      } else {
        this.emojiPanelActive = true
      }
      e.stopPropagation() /* 阻止事件冒泡 */
    },

    /* 文本域失去焦点时 */
    doBlur() {
      if (this.rootCommentContent.length > 0) {
        this.height80 = true
      } else {
        this.height80 = false
      }
    },

    /* 发表评论 */
    postComment() {

      if(!this.rootCommentContent) {
        alert("内容为空!");
        return;
      }

      let _this = this

      /* 处理换行, 虽然解决了, 但是不知道为什么在文本域里面按enter和手动输入\n有啥区别?
               哦懂了, \n在正则里面就是表示的换行这一个字符, 手动输入的\n其实是2个字符, 按enter输入的其实是一个字符（虽然它看上去是2个字符）,
               我们程序员习惯了\n表示换行这个字符(但这只是在开发工具里面支持的写法),
               如果把下面改成 /\\n/ 去替换那就可以匹配到手动输入的\n这2个字符
            */
      // console.log(this.textareaContent,'textareaContent');
      let result = this.rootCommentContent.replace(/\n/g, function (str) {
        console.log('检测到str:' + str);
        return "<br/>"
      })
      // console.log(result,'result');

      /* 处理表情 */
      /* 这个replace函数, 第一个参数是正则表达式, 他回去匹配文本；第二个参数是将匹配的文本传入进行处理的函数，函数的返回值将会替换匹配的文本 */
      result = result.replace(/\[.*?]/g, function (str) {
        if(_this.emojiSize) {
          return `<img src="${_this.imgPrefix}${emojiConfig[str]}"  style="width:${_this.emojiSize}px;height:${_this.emojiSize}"/>`;
        } else {
          return `<img src="${_this.imgPrefix}${emojiConfig[str]}" />`;
        }
      });

      this.$emit('comment',result)

      this.doBlur()
    },

    handleEmojiPanelVisibility() {
      this.isEmojiShow = !this.isEmojiShow;
    },
    removeHistory() {
      //todo 清空历史
    },
    //表情点击事件
    onEmojiClick(i) {
      //如果输入框没有焦点，那么让它获取焦点
      if (this.$refs.msgInputContainer !== document.activeElement) {
        this.$refs.msgInputContainer.focus();
      }
      //往焦点出插入内容
      let targetUrl = "https://cdn.sunofbeaches.com/emoji/" + i + ".png";
      console.log("targetUrl==> " + targetUrl);
      let imageTag = `<img src="${targetUrl}" width="20" height="20">`;
      document.execCommand("insertHtml", false, imageTag);
      //保存历史记录
      //先要获取出来，然后进行拼接
      let targetStr = i.toString();
      let items = window.localStorage.getItem("emoji-history");
      if (items) {
        //进行切割
        let currentIndex = 0;
        let itemArray = items.split(",");
        for (let j = 0; j < itemArray.length; j++) {
          //从头开始加,targetStr一定是有的了
          //所以，先添加逗号
          //判断i是否已经包含了
          let item = itemArray[j];
          if (targetStr.indexOf(item) === -1) {
            targetStr += ",";
            targetStr += item;
            currentIndex++;
          }
          //1+1,2,3,4,5==>6个元素
          if (currentIndex > 4) {
            break;
          }
        }
      }
      //console.log("targetStr ==> " + targetStr);
      //最多保存6个，也就是最近使用
      window.localStorage.setItem("emoji-history", targetStr);
      this.updateHistory();
    },

    //表情点击事件
    onEmojiInReplayClick(i) {
      //如果输入框没有焦点，那么让它获取焦点
      if (this.$refs.rootMsgInputContainer !== document.activeElement) {
        document.getElementById("rootMsgInputContainer").focus();
      }
      //往焦点出插入内容
      let targetUrl = "https://cdn.sunofbeaches.com/emoji/" + i + ".png";
      console.log("targetUrl==> " + targetUrl);
      let imageTag = `<img src="${targetUrl}" width="20" height="20">`;
      document.execCommand("insertHtml", false, imageTag);
      //保存历史记录
      //先要获取出来，然后进行拼接
      let targetStr = i.toString();
      let items = window.localStorage.getItem("emoji-history");
      if (items) {
        //进行切割
        let currentIndex = 0;
        let itemArray = items.split(",");
        for (let j = 0; j < itemArray.length; j++) {
          //从头开始加,targetStr一定是有的了
          //所以，先添加逗号
          //判断i是否已经包含了
          let item = itemArray[j];
          if (targetStr.indexOf(item) === -1) {
            targetStr += ",";
            targetStr += item;
            currentIndex++;
          }
          //1+1,2,3,4,5==>6个元素
          if (currentIndex > 4) {
            break;
          }
        }
      }
      //console.log("targetStr ==> " + targetStr);
      //最多保存6个，也就是最近使用
      window.localStorage.setItem("emoji-history", targetStr);
      this.updateHistory();
    },
    onEmojiInSubReplayClick(i) {
      //如果输入框没有焦点，那么让它获取焦点
      if (this.$refs.rootMsgInputContainer !== document.activeElement) {
        document.getElementById("rootSubMsgInputContainer").focus();
      }
      //往焦点出插入内容
      let targetUrl = "https://cdn.sunofbeaches.com/emoji/" + i + ".png";
      console.log("targetUrl==> " + targetUrl);
      let imageTag = `<img src="${targetUrl}" width="20" height="20">`;
      document.execCommand("insertHtml", false, imageTag);
      //保存历史记录
      //先要获取出来，然后进行拼接
      let targetStr = i.toString();
      let items = window.localStorage.getItem("emoji-history");
      if (items) {
        //进行切割
        let currentIndex = 0;
        let itemArray = items.split(",");
        for (let j = 0; j < itemArray.length; j++) {
          //从头开始加,targetStr一定是有的了
          //所以，先添加逗号
          //判断i是否已经包含了
          let item = itemArray[j];
          if (targetStr.indexOf(item) === -1) {
            targetStr += ",";
            targetStr += item;
            currentIndex++;
          }
          //1+1,2,3,4,5==>6个元素
          if (currentIndex > 4) {
            break;
          }
        }
      }
      //console.log("targetStr ==> " + targetStr);
      //最多保存6个，也就是最近使用
      window.localStorage.setItem("emoji-history", targetStr);
      this.updateHistory();
    },

    updateHistory() {
      let items = window.localStorage.getItem("emoji-history");
      if (items) {
        this.historyList = items.split(",");
      }
    },
    //点击空白处的事件监听
    handleClickBlank(){
      // alert('点击了空白');
      let _this = this;
      _this.isEmojiShow = false;
      // _this.isEmojiShowInReplay = false;
    },
    showEmojiInReplay(){
      // alert("aaa");
      let _this = this;
      _this.isEmojiShow = false;
      _this.isEmojiShowInReplay = !_this.isEmojiShowInReplay;
    },
    showEmojiInSubReplay(){
      // alert("aaa");
      let _this = this;
      _this.isEmojiShow = false;
      _this.isEmojiShowInSubReplay = !_this.isEmojiShowInSubReplay;
    }
  },

  //挂载
  mounted() {
    let _this = this;
    _this.queryCommentList();

    document.addEventListener('click', function (e) { /* 点击其它地方, 关闭表情面板;点击表情面板时,需要阻止事件冒泡 */
      _this.emojiPanelActive = false
    });

    //防止点击表情的时候输入框失去焦点
    let inputPart = document.getElementById("input-action-bar");
    if (inputPart) {
      inputPart.addEventListener("mousedown", function (e) {
        e.preventDefault();
      })
    }
    let emojiContainer = document.getElementById("emoji-container");
    if (emojiContainer) {
      emojiContainer.addEventListener("mousedown", function (e) {
        e.preventDefault();
      })
    }
    //更新历史
    this.updateHistory();
  }
};
</script>

<style lang="scss" scoped>
/*arco相关样式*/
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

/*=========start======*/
.emoji-item {
  cursor: pointer; /* 修改光标形状 */
  //border: 1px solid ; /* 添加红色边框 */
}

.emoji-item:hover {
  border: 1px solid #4682B4; /* 添加红色边框 */
  //border-radius: 4px;
  background-color: #B3C0D1;
}

.emoji-container{
  margin-top: 10px;
  margin-left: 15px;
  //border-top: 1px solid #B3C0D1;
  //border-left: 1px solid #B3C0D1;
  background-color: #faecd8;
  //border: 3px solid #c45656;
  border-radius: 4px;
}

.emoji-btn {
  padding: 5px 0;
  color: dodgerblue;
  cursor: pointer;
}

.post-btn {
  cursor: pointer;
  border-radius: 4px;
  padding: 5px 20px;
  background: dodgerblue;
  color: white;
}

.action-bar {
  display: flex;
  justify-content: space-between;
}


.action-bar {
  padding: 10px 10px 0;
}

.input-part {
  padding: 10px;
}

.mo-yu-container {
  width: 420px;
  background: #fff;
  position: absolute;
  top: 8%;
  left: 53%;
}

.emoji-item {
  width: 45px;
  cursor: pointer;
  height: 45px;
  padding: 10px;
}

.emoji-list {
  width: 400px;
  height: 300px;
  padding: 10px;
  overflow-y: scroll;
  overflow-x: hidden;
}


/*滚动条效果*/
.emoji-list::-webkit-scrollbar {
  width: 10px;
}

.emoji-list::-webkit-scrollbar-track {
  background-color: #F9FAFB;
  -webkit-border-radius: 2em;
  -moz-border-radius: 2em;
  border-radius: 2em;
}

.emoji-list::-webkit-scrollbar-thumb {
  background-color: #E5E6EB;
  -webkit-border-radius: 2em;
  -moz-border-radius: 2em;
  border-radius: 2em;
}

.input-panel:focus {
  border: dodgerblue 2px solid;
}

.input-panel img {
  margin: 0 2px;
  vertical-align: middle;
}

.input-panel {
  margin: 0 auto;
  font-size: 14px;
  line-height: 20px;
  background: #efefef;
  width: 380px;
  height: 100px;
  outline: none;
  border: #F4F5F6 2px solid;
  border-radius: 4px;
  padding: 5px;
  overflow-x: hidden;
  word-break:break-all
}

.input-panel:empty:before {
  content: attr(placeholder);
  position: absolute;
  color: #4d4d4d;
  background-color: transparent;
}

.input-replay:focus {
  border: dodgerblue 2px solid;
}

.input-replay img {
  margin: 0 2px;
  vertical-align: middle;
}

.input-replay {
  margin: 0 auto;
  margin-left: 50px;
  font-size: 14px;
  line-height: 20px;
  background: #ffffff;
  width: 80%;
  height: 40px;
  outline: none;
  border: #86909c 1px solid;
  border-radius: 4px;
  padding: 5px;
  overflow-x: hidden;
  word-break:break-all
}

.input-replay:empty:before {
  content: attr(placeholder);
  position: relative;
  color: #c45656;
  background-color: transparent;
}

.commentTree{
  width: 50%;
  //margin-left: 20%;
  //border: #333333 2px solid;
  //border-radius: 4px;
}
/*=========end======*/
</style>
