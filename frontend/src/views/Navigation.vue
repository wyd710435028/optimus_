<template>
  <router-view></router-view>
  <el-container style=" border: 1px solid #eee">
    <el-container>
      <el-header>Header</el-header>
      <el-container>
        <el-aside width="200px" style="border: 1px solid #eee">
          <el-menu default-active="1" @open="handleOpen" @close="handleClose" >
            <el-sub-menu index="1">
              <template v-slot:title>导航一</template>
              <el-menu-item-group>
                <el-menu-item index="1-1"><router-link to="/MedicalDocList">跳转到文书列表</router-link></el-menu-item>
                <el-menu-item index="1-2">选项2</el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>
            <el-sub-menu index="2">
              <template v-slot:title><router-link to="/MedicalRecordList">跳转到病历列表</router-link></template>
            </el-sub-menu>
          </el-menu>
        </el-aside>
        <el-container>
          <el-main style="border: 1px solid #eee">Main</el-main>
          <el-footer style="border: 1px">Footer</el-footer>
        </el-container>
      </el-container>
    </el-container>
  </el-container>
</template>
<script>
export default {
  data() {
    const item = {
      date: '2016-05-02',
      name: '王小虎',
      address: '上海市普陀区金沙江路 1518 弄'
    };
    return {
      tableData: Array(20).fill(item)
    }
  },
  methods: {
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
</style>
