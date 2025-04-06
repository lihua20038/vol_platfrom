import {
  defineStore
} from 'pinia'
import {
  ref
} from 'vue'


export const useTokenStore = defineStore('token', () => {
  const token = ref('')
  const role = ref('')
  const setToken = (newToken) => {
    token.value = newToken
    // localStorage.setItem('token', newToken)
  }

  const clearToken = () => {
    token.value = ''
    // localStorage.removeItem('token')
  }

  const setRole = (r) => {
    role.value = r;
  }
  return {
    token,
    setToken,
    clearToken,
    role,
    setRole
  }
}, {
  persist: {
    paths: ['token', 'role'] // 指定持久化字段
  }
});