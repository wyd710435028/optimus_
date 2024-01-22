<template>
  <router-view></router-view>
  <el-header style="background-color: #009688;">
    <el-text style="cursor: pointer;color: #ffffff;font-size: 20px" @click="returnIndex()">Optimus可视化系统</el-text>
  </el-header>
    <el-card class="box-card" style="margin-top: 5px">
      <div class="card-header">
        <span style="font-weight: bold;font-size: 25px; color: #c45656; margin-top: 12px; margin-bottom: 4px;">实体百科</span>
<!--        <el-button class="button" text>返回</el-button>-->
      </div>
      <el-divider style="background-color: #c45656"></el-divider>
      <div v-for="(value, key) in encyclopedia" :key="key" class="text item">
        <div style="font-weight: bold;font-size: 18px; margin-top: 12px; margin-bottom: 4px;">
          {{key}}
        </div>
        <div style="white-space: pre-wrap;">
          {{value}}
        </div>
<!--        <el-divider style="background-color: #c45656"></el-divider>-->
      </div>
    </el-card>
</template>

<!--js-->
<script>
import axios from "axios";
import {ElMessage} from "element-plus";
export default {
  name:"entitylinkDialog",
  /*属性*/
  data(){
    return{
      entityName:'',
      labelName:'',
      encyclopedia:null
    }
  },
  /*方法*/
  methods:{
    init(data){
      this.detailVisible=true;
      //data是父组件弹窗传递过来的值，我们可以打印看看
      console.log(data);
    },
    returnIndex(){
      this.$router.push('/');
    }
  },
  /*初始化加载*/
  mounted(){
    //todo 加载初始化操作
    let _this = this;
    _this.entityName = this.$route.params.entityName;
    _this.labelName = this.$route.params.labelName;
    // alert(_this.entityName);
    // alert(_this.labelName);
    axios.get('entityLink/api/getEntityReferences', {params:{
        returnContent:1,
        termType:_this.labelName,
        term:_this.entityName
    }}).then(function (response){
      // console.log(response);
      // console.log(response.data);
      console.log(response.data.references);
      if (response.data.references!=null){
        _this.encyclopedia = response.data.references[0];
      }else {
        ElMessage({
          showClose: true,
          message: '暂无结果',
          type: 'error',
          duration: 3 * 1000
        })
      }
      console.log(_this.encyclopedia);
    });
  }
}
</script>

<!--样式-->
<style></style>