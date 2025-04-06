import request from '@/utils/request'

export const register = (data) => {
    return request.post('/user/register', data)
}

export const login = (data) => {
    return request.post('/user/login', data)
}
export const updateUserInfo = (data) => request.put('/user/update', data)
export const changePassword = (data) => request.put('/user/updatepw', data)
export const logout = () => request.post('/user/logout')
export const fetchUserInfo = () => {
    return request.get('/user/info')
}