import {createRouter, createWebHashHistory, createWebHistory} from 'vue-router';
import HomePage from "../views/HomePage.vue";
import MedicalDocList from '../views/MedicalDocList.vue';
import MedicalRecordList from "../views/MedicalRecordList.vue";
import MedicalNodeDetail from "../views/MedicalNodeDetail.vue";
import DocQueryList from "../views/DocQueryList.vue";
import DocContentDetail from "../views/DocContentDetail.vue";
import NodeDetail from "../views/NodeDetail.vue";
import EntityLinkJump from "../views/EntityLinkJump.vue";
import EventQueryList from "@/views/EventQueryList.vue";

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage,
    meta: {
      keepAlive: true
    }
  },
  {
    path: '/MedicalDocList/:hospitalId/:admissionId/:stage',
    name: 'MedicalDocList',
    component: MedicalDocList,
    props:true
  },
  {
    path: '/MedicalRecordList/:hospitalId',
    name: 'MedicalRecordList',
    component: MedicalRecordList,
    props:true
  },
  {
    path: '/MedicalNodeDetail/:nodeName/:nodeContent/:entityHightLighted/:spanHightLighted/:eventHightLighted/:entityLabelList/:spanLabelList/:eventList/:entityList/:spanList/:hospitalId/:admissionId/:stage',
    name: 'MedicalNodeDetail',
    component: MedicalNodeDetail,
    props: true
  },
  {
    path: '/NodeDetail/:hospitalId/:admissionId/:stage/:fileId/:nodeName',
    name: 'NodeDetail',
    component: NodeDetail,
    props: true
  },
  {
    path: '/DocQueryList/:hospitalId/:admissionId/:stage',
    name: 'DocQueryList',
    component: DocQueryList,
    props:true
  },
  {
    path: '/EventQueryList/:hospitalId/:admissionId/:stage',
    name: 'EventQueryList',
    component: EventQueryList,
    props:true
  },
  {
    path: '/DocContentDetail/:hospitalId/:admissionId/:stage/:fileId/:showContent',
    name: 'DocContentDetail',
    component: DocContentDetail,
    props:true
  },
  {
    path: '/EntityLinkJump/:entityName/:labelName',
    name: 'EntityLinkJump',
    component: EntityLinkJump,
    meta: {
      keepAlive: true
    }
  }
]

//创建路由
const router = createRouter({
  // history: createWebHistory(),
  history: createWebHashHistory(),
  routes
})

//将路由暴露出去
export default router
