const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave:false,
  outputDir:'dist',
  publicPath:'./',
  devServer: {
    client: {
      overlay: false
    },
    proxy:{
      "/reUnderstand":{
        target:"http://10.128.3.237:8851",
        pathRewrite:{
          "^/reUnderstand":""
        }
      }
    }
  }
})
