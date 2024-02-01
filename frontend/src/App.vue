<template>
  <div id="app">
<!--    <router-view/>-->
    <keep-alive>
      <router-view v-if="$route.meta.keepAlive"></router-view>
    </keep-alive>
    <router-view v-if="!$route.meta.keepAlive" />
  </div>
</template>
<script>
export default ({
  name: 'app'
});

</script>

<style lang="scss">
@import url(//at.alicdn.com/t/c/font_4004562_b46lfqtm52u.css);
body {
  margin: 0;
  overflow-y: scroll;
  overflow-x: hidden;
  /* 背景渐变 */
  //background: linear-gradient(90deg, rgba(247, 149, 51, .1), rgba(243, 112, 85, .1) 15%, rgba(239, 78, 123, .1) 30%, rgba(161, 102, 171, .1) 44%, rgba(80, 115, 184, .1) 58%, rgba(16, 152, 173, .1) 72%, rgba(7, 179, 155, .1) 86%, rgba(109, 186, 130, .1));
}
.shadow {
  box-shadow: 0 4px 8px 6px rgba(7, 17, 27, .06);
}

/* 整个滚动条 */
::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

/* 滚动条上的滚动滑块,参考： 滚动条样式修改->https://blog.csdn.net/coder_jxd/article/details/124213962 */
::-webkit-scrollbar-thumb {
  background-color: #49b1f5;
  /* 关键代码 */
  background-image: -webkit-linear-gradient(45deg,
  rgba(255, 255, 255, 0.4) 25%,
  transparent 25%,
  transparent 50%,
  rgba(255, 255, 255, 0.4) 50%,
  rgba(255, 255, 255, 0.4) 75%,
  transparent 75%,
  transparent);
  border-radius: 32px;
}

/* 滚动条样式，参考： */
/* 滚动条轨道 */
::-webkit-scrollbar-track {
  background-color: #dbeffd;
  border-radius: 32px;
}

* {
  box-sizing: border-box;
}

a {
  text-decoration: none;
  color: inherit;
}
</style>
