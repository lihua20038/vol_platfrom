import request from '@/utils/request'

export const api = { 
  user: {
    register: (data) => request.post('/user/register', data),
    login: (data) => request.post('/user/login', data),
    update: (data) => request.put('/user/update', data),
    updatePassword: (data) => request.put('/user/updatepw', data),
    logout: (token) => request.post('/user/logout', {}, { headers: { Authorization: `Bearer ${token}` } }),
    getInfo: (token) => request.get('/user/info', { headers: { Authorization: token } })
  },
  organization: {
    create: (data) => request.post('/organization/create', data),
    login: (data) => request.post('/organization/login', data),
    updatePassword: (data) => request.put('/organization/updatepw', data),
    logout: (token) => request.post('/organization/logout', {}, { headers: { Authorization: `Bearer ${token}` } })
  },
  activity: {
    create: (data) => request.post('/activity', data),
    update: (data) => request.put('/activity', data),
    delete: (activityId) => request.delete(`/activity/${activityId}`),
    list: (params) => request.get('/activity/list', { params }),
    search: (params) => request.get('/activity/search', { params }),
    getDetail: (activityId) => request.get(`/activity/${activityId}`)
  },
  registration: {
    create: (data) => request.post('/activity-registration', data),
    cancel: (registrationId) => request.put(`/registration/cancel/${registrationId}`),
    update: (data, registrationId) => request.put(`/registration/${registrationId}`, data),
    list: (params) => request.get('/registration/list', { params })
  },
  task: {
    create: (data) => request.post('/task/create', data),
    update: (taskId, data) => request.put(`/task/${taskId}`, data)
  },
  team: {
    create: (data) => request.post('/team/create', data),
    update: (teamId, data) => request.put(`/team/${teamId}`, data)
  }
}