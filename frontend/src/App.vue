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

<style>

</style>
