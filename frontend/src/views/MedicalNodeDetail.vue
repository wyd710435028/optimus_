<template>
  <router-view></router-view>
  <el-container>
    <!-- 公共头部 -->
<!--    <common-header></common-header>-->
    <el-header style="background-color: #009688;">
      <common-header></common-header>
    </el-header>
    <el-row class="container">
      <el-button color="#356B98" :dark="isDark" style="margin-top: 20px;margin-right: 5px" @click="refresh()">刷新</el-button>
      <el-button color="#359894" :dark="isDark" style="margin-top: 20px;margin-right: 20px" @click="returnMedicalDocList()">返回</el-button>
    </el-row>
    <el-container>
      <el-main>
<!--        <el-row>-->
<!--          <el-col :span="24"><div class="grid-content bg-purple-dark">entity理解结果</div></el-col>-->
<!--        </el-row>-->
        <el-divider>entity理解结果</el-divider>
        <el-row>
          <el-col :span="4"><strong style="color: indianred">{{nodeName}}</strong></el-col>
          <el-col :span="20">
            <div>
              <label v-html="entityHightLighted"></label>
            </div>
          </el-col>
        </el-row>
        <el-divider style="background-color: #c45656"></el-divider>
        <el-row type="flex" justify="end">
          <el-col :span="20" style="margin: 5px">
            <div v-for="label in entityLabelList" :key="label" style="float: left">
              <el-tag v-if="label.labelType=='entity'" :color="label.labelColor" @click="clickEntityTag(label.labelContent,label.labelColor)"><label style="color:#303133">{{label.labelContent}}</label></el-tag>
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
          <el-col :span="4"><strong style="color: indianred">{{nodeName}}</strong></el-col>
          <el-col :span="20">
            <div>
              <label v-html="spanHightLighted"></label>
            </div>
          </el-col>
        </el-row>
        <el-divider style="background-color: #c45656"></el-divider>
        <el-row type="flex" justify="end">
          <el-col :span="20" style="margin: 5px">
<!--            <el-tag checked style="float: left">span标签列表</el-tag>-->
            <div v-for="label in spanLabelList" :key="label" style="float: left;margin-left: 5px">
              <el-tag v-if="label.labelType=='span'" :color="label.labelColor" @click="clickSpanTag(label.labelContent,label.labelColor)"><label style="color:#303133">{{label.labelContent}}</label></el-tag>
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
          <el-col :span="4"><strong style="color: indianred">{{nodeName}}</strong></el-col>
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
        <el-divider style="background-color: #c45656"></el-divider>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
import {hightTextByOneTag} from "@/apis/post";
import CommonHeader from "@/views/common/CommonHeader.vue";

export default {
  components: {CommonHeader},
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
      ],
      entityList:[],
      spanList:[]
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
      this.nodeName=this.$route.params.nodeName;
      this.nodeContent=this.$route.params.nodeContent;
      this.entityHightLighted=this.$route.params.entityHightLighted;
      this.spanHightLighted=this.$route.params.spanHightLighted;
      this.eventHightLighted=this.$route.params.eventHightLighted;
      let entityLabelListStr = this.$route.params.entityLabelList;
      let spanLabelListStr = this.$route.params.spanLabelList;
      let eventList = this.$route.params.eventList;
      let entityList = this.$route.params.entityList;
      let spanList = this.$route.params.spanList;
      // alert("接收到的参数:"+str);
      // this.allLabelList = JSON.parse(str);
      this.entityLabelList = JSON.parse(entityLabelListStr);
      this.spanLabelList = JSON.parse(spanLabelListStr);
      this.eventData = JSON.parse(eventList);
      this.entityList = JSON.parse(entityList);
      this.spanList = JSON.parse(spanList);
      // alert(entityList);
      // alert(spanList);
      // alert(this.nodeContent);
      // alert(this.allLabelList);
      // alert(this.nodeName);
      // alert(this.nodeContent);
      // alert(this.spanHightLighted);
    },
    clickEntityTag(labelContent,labelColor){
      let _this = this;
      //文本内容
      let nodeContent = _this.nodeContent;
      //label内容
      // alert(labelContent);
      //label颜色
      // alert(labelColor);
      //entity集合(字符串形式)
      let entityList = this.$route.params.entityList;
      // alert(entityList);
      hightTextByOneTag(nodeContent,labelContent,labelColor,entityList).then(function (response){
        _this.entityHightLighted =response.data.data.hightedText;
      })
    },
    clickSpanTag(labelContent,labelColor){
      let _this = this;
      //文本内容
      let nodeContent = _this.nodeContent;
      //label内容
      // alert(labelContent);
      //label颜色
      // alert(labelColor);
      //entity集合(字符串形式)
      let spanList = this.$route.params.spanList;
      // alert(entityList);
      hightTextByOneTag(nodeContent,labelContent,labelColor,spanList).then(function (response){
        _this.spanHightLighted =response.data.data.hightedText;
      })
    },
    refresh(){
      window.location.reload()
    },
    returnMedicalDocList(){
      let hospitalId = this.$route.params.hospitalId;
      let admissionId = this.$route.params.admissionId;
      let stage = this.$route.params.stage;
      this.$router.push('/MedicalDocList/'+hospitalId+'/'+admissionId+'/'+stage+'/noFileId/noDocType');
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