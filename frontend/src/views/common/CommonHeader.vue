<template>
  <router-view></router-view>
  <el-header style="background-color: #009688;">
    <el-text style="cursor: pointer;color: #ffffff;font-size: 20px" @click="returnIndex()">
      <span>Optimus可视化系统</span>
    </el-text>
    <el-dropdown style="float: right;margin-right: 10px">
      <el-avatar src="https://p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/9eeb1800d9b78349b24682c3518ac4a3.png~tplv-uwbnlip3yd-webp.webp" class="avatar" shape="circle"> wyd </el-avatar>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="toUserInformation()">个人信息</el-dropdown-item>
          <el-dropdown-item @click="dialogVisible = true">切换用户</el-dropdown-item>
          <el-dropdown-item @click="quit()">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <!-- 切换用户弹出框 -->
    <el-dialog
        v-model="dialogVisible"
        title="切换用户"
        width="250"
        :before-close="handleClose"
    >
      <el-select placeholder="请选择用户">
        <el-option label="wyd" value="wyd" />
        <el-option label="zs" value="wyd" />
      </el-select>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogVisible = false">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </el-header>
</template>
<script>
  export default {
    data(){
      return {
        //控制切换用户页面是否弹出
        dialogVisible:false
      }
    },
    methods:{
      //跳转到个人信息页面
      toUserInformation(){
        // alert("ToUserInformation");
        this.$router.push('/ToUserInformation');
      },
      //返回首页
      returnIndex(){
        this.$router.push('/');
      }
    }
  }
</script>
<style>

</style>