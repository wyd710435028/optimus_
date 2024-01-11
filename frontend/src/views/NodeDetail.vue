<template>
  <router-view></router-view>
  <el-container>
    <el-header style="background-color: #009688;cursor: pointer" @click="returnIndex()">Optimus可视化系统</el-header>
    <el-container>
      <el-main>
        <el-divider>entity理解结果</el-divider>
        <el-row>
          <el-col :span="4"><strong>{{nodeName}}</strong></el-col>
          <el-col :span="20">
            <div>
              <label v-html="entityHightLighted"></label>
            </div>
          </el-col>
        </el-row>
        <el-divider></el-divider>
        <el-row type="flex" justify="end">
          <el-col :span="20" style="margin: 5px">
            <div v-for="label in entityLabelList" :key="label" style="float: left">
              <el-tag v-if="label.labelType=='entity'" :color="label.labelColor"><label style="color:#303133">{{label.labelContent}}</label></el-tag>
            </div>
            <!--            <el-tag color="#409EFF"><label style="color:#000;">标签一</label></el-tag>-->
            <!--            <el-tag color="green"><label style="color:#000;">标签二</label></el-tag>-->
            <!--            <el-tag color= "pink"><label style="color:#000;">标签三</label></el-tag>-->
            <!--            <el-tag type="danger"><label style="color:#000;">标签五</label></el-tag>-->
          </el-col>
        </el-row>

        <!--        <el-row>-->
        <!--          <el-col :span="24"><div class="grid-content bg-purple-dark">span理解结果</div></el-col>-->
        <!--        </el-row>-->
        <el-divider>span理解结果</el-divider>
        <el-row>
          <el-col :span="4"><strong>{{nodeName}}</strong></el-col>
          <el-col :span="20">
            <div>
              <label v-html="spanHightLighted"></label>
            </div>
          </el-col>
        </el-row>
        <el-divider></el-divider>
        <el-row type="flex" justify="end">
          <el-col :span="20" style="margin: 5px">
            <!--            <el-tag checked style="float: left">span标签列表</el-tag>-->
            <div v-for="label in spanLabelList" :key="label" style="float: left;margin-left: 5px">
              <el-tag v-if="label.labelType=='span'" :color="label.labelColor"><label style="color:#303133">{{label.labelContent}}</label></el-tag>
            </div>
            <!--            <el-tag color="#409EFF"><label style="color:#000;">标签一</label></el-tag>-->
            <!--            <el-tag color="green"><label style="color:#000;">标签二</label></el-tag>-->
            <!--            <el-tag color= "pink"><label style="color:#000;">标签三</label></el-tag>-->
            <!--            <el-tag type="danger"><label style="color:#000;">标签五</label></el-tag>-->
          </el-col>
        </el-row>

        <!--        <el-row>-->
        <!--          <el-col :span="24"><div class="grid-content bg-purple-dark">event理解结果</div></el-col>-->
        <!--        </el-row>-->
        <el-divider>event理解结果</el-divider>
        <el-row>
          <el-col :span="4"><strong>{{nodeName}}</strong></el-col>
          <el-col :span="20">
            <div>
              <label v-html="eventHightLighted"></label>
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="4"></el-col>
          <el-col :span="20">
            <el-table :data="eventData"
                      border
                      style="width: 100%;margin:10px">
              <el-table-column prop="eventName" label="eventName"></el-table-column>
              <el-table-column property="eventIdentity" label="eventIdentity"></el-table-column>
              <el-table-column prop="attributeMap" label="attributeMap"></el-table-column>
            </el-table>
          </el-col>
        </el-row>
        <el-divider></el-divider>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
export default {
  data() {
    return {
      nodeName:'',
      nodeContent:'',
      entityHightLighted:'',
      spanHightLighted:'',
      eventHightLighted:'',
      allLabelList:[],
      entityLabelList:[],
      spanLabelList:[],
      eventData:[
        {
          eventName:'aaa',
          eventIdentity:'bbb',
          attributeMap:'cccc'
        },
        {
          eventName:'bbb',
          eventIdentity:'ccc',
          attributeMap:'dddd'
        }
      ]
    };
  },
  methods: {
    getDocByRecordIdAndDocName(data) {
      if (data.children==null){
        let docName = data.label;
        let recoreId = this.$route.params.recordId;
        //todo 调用后台查询对应文书查询对应文书
      }else {
        return;
      }
    },
    toDocUnderstandDetail(){
      let hospitalId=this.$route.params.hospitalId;
      let admissionId=this.$route.params.admissionId;
      let stage=this.$route.params.stage;
      let fileId=this.$route.params.fileId;
      let nodeName=this.$route.params.nodeName;
      alert(hospitalId+'-'+admissionId+'-'+fileId+'-'+nodeName+'-'+stage);
    },
    returnIndex(){
      this.$router.push('/');
    }
  },
  mounted(){
    //todo 加载初始化操作
    this.toDocUnderstandDetail();
  }
};
</script>
<style>
.el-header{
  background-color: #409EFF;
  color: #ffffff;
  text-align: left;
  line-height: 60px;
}

.bg-purple-dark {
  background: #e6a23c;
}
</style>