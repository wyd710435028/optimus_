<template>
  <router-view></router-view>
  <div class="comment-area">
    <div class="comment-right">
      <div style="float:left;margin-top:50px;">
        <!-- 文本框 -->
        <textarea style="width: 500px;height:150px" ref="textarea" v-model="rootCommentContent" @focus="height80 = true" @blur="doBlur"
                    :placeholder="placeholder" :max-length="{length:800,errorOnly:true}" allow-clear show-word-limit class="edit-area">
        </textarea>
        <!-- 表情面板 -->
        <div class="comment-tips" style="margin-top: 10px;float: left;margin-left: 370px">
          <a-row>
            <svg-icon @click="activeEmojiPanel($event, true)" style="font-size: 35px;" icon-class="biaoqing1 "></svg-icon>
            <a-button type="primary" size="large" @click="createRootComment(rootCommentContent,50)">发表评论</a-button>
<!--            <a-button type="primary" size="large" @click="postComment">评论</a-button>-->
          </a-row>
          <!-- 待选择的表情列表 -->
          <div v-show="emojiPanelActive">
            <div class="emoji-wrapper scaleUp" @click="activeEmojiPanel">
            <span @click="addEmoji(emoji)" class="emoji" v-for="(emoji, idx) in emojiList" :key="idx">
              <img :title="emoji.title" :src="emoji.link" alt="">
            </span>
            </div>
          </div>
          <!-- 三角形 -->
<!--          <div v-show="emojiPanelActive" class="triangle"></div>-->
        </div>
      </div>
    </div>
  </div>

  <!--评论列表-->
  <div style="margin-top: 250px">
    <div style="margin-top: 20px" v-for="comment in commentList" :key="comment">
      <!--根评论-->
      <a-comment
                 :author="comment.userName"
                 :avatar="comment.avatarUrl"
                 :content="comment.content"
                 :datetime="comment.createTime">
          <span @click="replayComment(comment)" class="action"> <IconMessage/> 回复 </span>
        <a-popconfirm content="删除此评论,子评论也会删除,是否继续?" type="warning" @ok="deleteComment(comment)">
          <span class="action"> <IconDelete/> 删除 </span>
        </a-popconfirm>
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
              <a-input v-model="commentContent" placeholder="在此输入你的评论..." />
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
            <a-popconfirm content="删除此评论,子评论也会删除,是否继续?" type="warning" @ok="deleteComment(subComment)">
              <span class="action"> <IconDelete/> 删除 </span>
            </a-popconfirm>
            <div style="margin-top: 20px" v-if="subComment.ifShowReplayInput==true">
              <a-comment
                  align="right"
                  avatar="https://img0.baidu.com/it/u=2947341353,265385944&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500"
              >
                <template #content>
                  <a-input v-model="subCommentContent" placeholder="在此输入你的评论..." />
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
  </div>
</template>

<script>
import {IconDelete, IconHeart, IconMessage, IconStar,IconFaceSmileFill} from '@arco-design/web-vue/es/icon';
import {getCommentLst} from "@/apis/get";
import {saveComment,deleteCommentById,createNewRootComment} from "@/apis/post";
import { ref } from 'vue';

/* 表情配置数据 转为 数组 */
import emojiConfig from '@/components/EmojiText/emoji.json'
import SvgIcon from "@/components/SvgIcon.vue";
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
    IconFaceSmileFill
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
      ],

      /* 文本框中有文字 或 无文字但是处于焦点状态时 为true */
      height80: false,

      /* 表情配置数据 */
      emojiList,

      /* 是否打开表情面板 */
      emojiPanelActive: false,

      /* 文本框的内容 */
      textareaContent: ''
    }
  },

  //方法
  methods:{
    //回复输入框显示/不显示
    replayComment(comment){
      let _this = this;
      if (_this.ifCanShowReplayInput) {
          comment.ifShowReplayInput = !comment.ifShowReplayInput;
          _this.ifCanShowReplayInput = !comment.ifShowReplayInput;
      }else {
        _this.$message.warning({content:'有未关闭的评论框,请先关闭再评论!',closable: true});
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
        _this.$message.warning({content:'请输入评论内容',closable: true});
      }else {
        //输入评论内容才保存
        saveComment(parentId,userId,rootParentId,content).then(function (response){
          if (response.status=='200'){
            _this.$message.success({content:'评论成功',closable: true});
            //关闭输入框
            _this.closeReplayInput(comment);
            _this.queryCommentList();
          }else {
            _this.$message.error({content:'评论失败',closable:true});
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
          _this.$message.success({content:'删除成功',closable: true,duration:3000});
          _this.queryCommentList();
        }else {
          _this.$message.error({content:'删除失败',closable:true,duration:3000});
        }
      });
      // alert(comment.id);
    },

    //创建新评论(根评论)
    createRootComment(rootCommentContent,userId){
      // alert(rootCommentContent);
      let  _this = this;
      if (rootCommentContent == null || rootCommentContent.trim().length == 0){
        _this.$message.warning({content:'请输入评论内容',closable: true});
      }else {
        createNewRootComment(rootCommentContent,userId).then(function (response){
          if (response.status=='200'){
            _this.$message.success({content:'发表成功',closable: true,duration:3000});
            _this.rootCommentContent = null;
            _this.queryCommentList();
          }else {
            _this.$message.error({content:'发表失败',closable:true,duration:3000});
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
    }
  },

  //挂载
  mounted() {
    let _this = this;
    _this.queryCommentList();

    document.addEventListener('click', function (e) { /* 点击其它地方, 关闭表情面板;点击表情面板时,需要阻止事件冒泡 */
      _this.emojiPanelActive = false
    })
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

.img:hover{

}

textarea {
  outline: none;
  border: none;
  background: #f1f2f3;
  resize: none;
  border-radius: 8px;
  padding: 10px 10px;
  font-size: 16px;
  color: #333333;
  border: 1px solid transparent;
}
img {
  -webkit-user-drag: none;
}

.avatar {
  width: 40px;
  height: 40px;
  object-fit: cover;
}

.height80 {
  //height: 80px !important;
}

.height80 textarea {
  border: 1px solid #49b1f5;
}

@keyframes scaleUp {
  0% {
    opacity: 0;
    transform: scale(0)
  }
  100% {
    opacity: 1;
    transform: scale(1)
  }
}

.scaleUp {
  animation: scaleUp 0.3s;
  transform-origin: 0 0;
}

.comment-area {
  display: flex;
  align-items: flex-start;
  color: #90949e;
  .comment-right {
    flex: 1;
    display: flex;
    height: 60px;
    transition: height 0.5s;

    position: relative;

    .edit-area {
      flex: 1;
    }

    .comment-btn {
      background-color: #49b1f5;
      cursor: pointer;
      width: 64px;
      border-radius: 8px;
      margin-left: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }

    .comment-tips {
      //position: absolute;
      bottom: 0px;
      height: 24px;
      width: calc(100% - 72px);
      margin-right: 72px;
      display: flex;
      align-items: center;

      &>span:first-child {
        width: 20px;
        height: 20px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;

        &.active {
          color: #49b1f5;
        }

      }


      .emoji-wrapper {
        user-select: none;
        position: relative;
        bottom: 0;
        top: 100px;
        left: -120px;
        display: flex;
        flex-wrap: wrap;
        width: 294px;
        height: 146px;
        overflow-y: auto;
        background-color: #eeeeee;
        padding: 5px;
        border-radius: 6px;
        border-radius: 6px;
        box-shadow: 0 3px 6px 0 rgb(0 0 0 / 12%);
        border: 1px solid rgba(0, 0, 0, .06);

        &::before {
          content: '';
          position: absolute;
        }

        span.emoji {
          width: 30px;
          height: 30px;
          display: block;
          margin: 2px;
          cursor: pointer;
          padding: 3px;
          border-radius: 6px;

          img {
            width: 100%;
            height: 100%;
          }

          transition: all 0.28s;

          &:hover {
            background-color: #dddddd;
          }
        }
      }

      .triangle {
        content: '';
        position: absolute;
        width: 8px;
        height: 8px;
        top: 200px;
        left: 381px;
        background-color: white;
        border: 1px solid #f0f0f0;
        transform: rotate(45deg);
        border-right-color: transparent;
        border-bottom-color: transparent;
      }
    }
  }

}
</style>
