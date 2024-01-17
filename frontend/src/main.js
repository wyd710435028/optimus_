import { createApp } from 'vue'
import App from './App.vue'
//引入路由
import router from './router'
import store from './store'
//引入element ui
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import zhLocale from 'element-plus/es/locale/lang/zh-cn';
//引入axious
import axios from 'axios';
import service from "@/utils/request";
import Vue from 'vue';

// 解决 ElTable 自动宽度高度导致的「ResizeObserver loop limit exceeded」问题
const debounce = (fn, delay) => {
    let timer = null;
    return function () {
        let context = this;
        let args = arguments;
        clearTimeout(timer);
        timer = setTimeout(function () {
            fn.apply(context, args);
        }, delay);
    }
}

const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver{
    constructor(callback) {
        callback = debounce(callback, 16);
        super(callback);
    }
}

//初始化插件
const  app =createApp(App).use(store).use(router).use(ElementPlus,{
    locale:{
        el:{
            // 整体覆盖
            ...zhLocale.el,
            pagination: {
                pagesize: '条/页',
                total: `共 {total} 条`,
                goto: '前往第',
                pageClassifier: '页'
            }
        }
    }
}).mount('#app')

// app.config.globalProperties.$instance = service;

