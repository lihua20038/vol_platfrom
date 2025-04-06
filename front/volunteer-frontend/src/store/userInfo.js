import {
    defineStore
} from 'pinia'
import {
    ref
} from 'vue'

export const useUserInfoStore = defineStore('userInfo', () => {
    const info = ref({})

    const setInfo = (newInfo) => {
        info.value = newInfo
    }

    const removeInfo = () => {
        info.value = {}
    }


    const logoutAction = async () => {
        try {
            // await logout(token.value)
            // clearToken()
        } catch (error) {
            console.error('Logout failed:', error)
            // clearToken()
        }
    }

    return {
        info,
        setInfo,
        removeInfo,
        logoutAction
    }

}, {
    persist: true
})

// export default useUserInfoStore;