import request from '@/utils/request'

export const orgLogin = (data) => request.post('/org/login', data)
export const getOrgInfo = () => request.get('/org/info')
export const orgRegister = (data) => request.post('/org/register', data)
export const orgChangePassword = (data) => request.put('/org/updatepw', data)
export const updateOrgInfo = (data) => request.put('/org/update', data)
