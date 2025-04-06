import axios from 'axios'
import {
  useTokenStore
} from '@/store/token.js'
import router from '@/router';
import {
  ElMessage
} from 'element-plus'

const service = axios.create({
  baseURL: '/api'
  // timeout: 5000 * 1000
})


// 请求拦截器
service.interceptors.request.use(
  config => {
    const tokenStore = useTokenStore();
    if (tokenStore.token) {
      config.headers.Authorization = tokenStore.token;
    }
    return config
  },
  error => {
    Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 业务执行成功
    if (response.data?.code === 1)
      return response.data;
    // 操作失败
    ElMessage.error(response.data?.msg ? response.data?.msg : "服务异常");
    return Promise.reject(response.data);
  },
  error => {
    if (error.response?.status === 401) {
      // 未授权，跳转到登录页面
      // localStorage.removeItem('token')
      // console.log(error);
      
      ElMessage.error(error.response?.data?.msg || '您未登录')
      const tokenStore = useTokenStore();
      tokenStore.clearToken();
      router.push('/login')
    } else {
      ElMessage.error('服务异常')
    }
    // return Promise.reject(error)
  }
)

export default service