const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave:false,
  outputDir:'dist',
  publicPath:'./',
  devServer: {
    //创建代理
    proxy:{
      '/reUnderstand':{
        target:'http://10.128.3.237:8851', //将 /reUnderstand开头的请求转发到 http://10.128.3.237:8851
        changeOrigin: true, //允许跨域
        pathRewrite:{
          '^/reUnderstand':''
        }
      }
    }
  }
})
