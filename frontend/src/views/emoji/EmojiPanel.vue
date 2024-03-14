<template>
  <router-view></router-view>
  <!-- 顶部带表情大的输入框和表情选择面板 -->
  <div class="mo-yu-container">
    <div class="input-part">
      <div class="input-panel" id="msgInputContainer" ref="msgInputContainer"
           placeholder="请在此输入内容"
           contenteditable="true" spellcheck="true">
      </div>
      <div class="action-bar" id="input-action-bar">
        <span class="emoji-btn" @click="handleEmojiPanelVisibility"><IconFaceSmileFill/></span>
        <span class="post-btn" @click="createResultComment(50)">发布</span>
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
    </div>
    <div v-if="showCmmentHistoryList==true">
      <el-table>
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="date" label="时间" />
        <el-table-column prop="user" label="评论人" />
        <el-table-column prop="user" label="操作" />
      </el-table>
    </div>
  </div>
</template>

<script>


import {IconDelete, IconHeart, IconMessage, IconStar,IconFaceSmileFill,IconSend,IconClose} from '@arco-design/web-vue/es/icon';
import {getCommentLst} from "@/apis/get";
import {createNewOrderComment, createNewResultComment, createNewRootComment} from "@/apis/post";

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

import {defineComponent} from "vue";
// export default defineComponent({
//   name:"EmojiPanel",
//   props:{
//     content:String,
//     admissionNo:String,
//     hospitalNo:String,
//     fileId:String,
//     orderContent:String,
//     executeTime:String,
//     executorSign:String
//   }
// });

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
    },
    admissionNo:String,
    hospitalNo:String,
    fileId:String,
    orderContent:String,
    executeTime:String,
    executorSign:String,
    unisoundId:String
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
      isEmojiShowInSubReplay:false,
      showCmmentHistoryList:false
    }
  },

  //方法
  methods:{

    //创建新评论(根评论)
    createResultComment(userId){
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
        // let fileId = _this.$route.params.fileId;
        let fileId = _this.fileId;
        if (fileId.toUpperCase().startsWith('LSYZ')||fileId.toUpperCase().startsWith('CQYZ')){
          //对医嘱的评论
          // alert(_this.orderContent);
          let orderContent = _this.orderContent;
          let executeTime = _this.executeTime;
          let executorSign = _this.executorSign;
          let unisoundId = _this.unisoundId;
          createNewOrderComment(rootCommentContent,userId,fileId,orderContent,executeTime,executorSign,unisoundId).then(function (response){
            if (response.status=='200'){
              // _this.$message.success({content:'发表成功',closable: true,duration:3000});
              ElMessage({
                showClose: true,
                message: '发表成功',
                type: 'success',
              })
              _this.rootCommentContent = null;
              _this.isEmojiShow = false;
              document.getElementById('msgInputContainer').innerHTML = '';
              //用于向父组件传值
              _this.$emit('ifSendSuccess',true);
            }else {
              // _this.$message.error({content:'发表失败',closable:true,duration:3000});
              ElMessage({
                showClose: true,
                message: '发表失败',
                type: 'error',
              })
            }
          });
        }else {
          //其他文书的评论
          let keyWords = _this.$route.params.keyWords;
          let nodeName = _this.$route.params.nodeName;
          let labelName = _this.$route.params.labelName;
          let docName = _this.$route.params.docName;
          // alert(keyWords+"-"+fileId+"-"+nodeName+"-"+labelName+"-"+docName);
          createNewResultComment(rootCommentContent,userId,keyWords,fileId,nodeName,labelName,docName).then(function (response){
            if (response.status=='200'){
              // _this.$message.success({content:'发表成功',closable: true,duration:3000});
              ElMessage({
                showClose: true,
                message: '发表成功',
                type: 'success',
              })
              _this.rootCommentContent = null;
              _this.isEmojiShow = false;
              document.getElementById('msgInputContainer').innerHTML = '';
              //用于向父组件传值
              _this.$emit('ifSendSuccess',true);
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
    commentHistory(){
      let _this = this;
      _this.showCmmentHistoryList = !_this.showCmmentHistoryList;
    }
  },

  //挂载
  mounted() {
    let _this = this;

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
  float: right;
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
  //width: 420px;
  background: #fff;
  position: relative;
  //top: 8%;
  //left: 53%;
}

.emoji-item {
  width: 45px;
  cursor: pointer;
  height: 45px;
  padding: 10px;
}

.emoji-list {
  width: 100%;
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
  width: 100%;
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
