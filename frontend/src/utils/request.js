import axios from 'axios'
import {ElMessage} from 'element-plus'

// 创建axios实例
// eslint-disable-next-line no-unused-vars
const service = axios.create({
    // baseURL: '/', // api的base_Url
    // 后端的请求路径
    // baseURL: 'http://10.128.3.122:8100/optimus_visual', // 远程api的base_Url
    baseURL: 'http://10.200.16.66:8100/optimus_visual', // 本地api的base_Url
    timeout: 500000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
    function (config) {
        // 在发送请求之前做些什么
        return config
    },
    function (error) {
        // 对请求错误做些什么
        return Promise.reject(error)
    }
)

// 响应拦截器
// axios.interceptors.response.use(
//     function (config) {
//         // 对响应数据做点什么
//         return config
//     },
//     function (error) {
//         // 对响应错误做点什么
//         return Promise.reject(error)
//     }
// )

// 响应拦截器
service.interceptors.response.use(
    response => {
        /**
         * code为非200是报错
         */
        if (response.data.code !== '200') {
            ElMessage({
                showClose: true,
                message: response.data.msg,
                type: 'error',
                duration: 3 * 1000
            })
        }
        return response
    }
)

export default service

