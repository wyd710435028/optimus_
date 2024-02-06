<!--模板-->
<template>
  <router-view></router-view>
  <!-- 表情面板 -->
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
</template>

<!--js代码-->
<script>
import CommentList from "@/views/CommentList.vue";
import { inject } from 'vue';
  export default {
    name:'emoji-panel',
    data(){
      return{
        historyList: [10,23,1,33,22]
      }
    },
    methods:{
      //表情点击事件
      onEmojiClick(i) {
        //如果输入框没有焦点，那么让它获取焦点
        const parentRef = inject('msgInputContainer');
        if (parentRef !== document.activeElement) {
          parentRef.focus();
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
      }
    }
  }
</script>

<!--样式-->
<style>
  .emoji-list {
    width: 400px;
    height: 300px;
    padding: 10px;
    overflow-y: scroll;
    overflow-x: hidden;
  }
</style>