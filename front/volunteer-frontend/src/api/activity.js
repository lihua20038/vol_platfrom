import request from '@/utils/request'

export const searchActivities = (params) => request.post('/activity/search', params)
export const getActivityById = (activityId) => request.get(`/activity/${activityId}`)
export const orgSearchActivities = (params) => request.post('/activity/org/list', params)
export const updateActivityState = (params) => request.put('/activity/updateState', params)
export const deleteActivity = (activityId) => request.delete(`/activity/delete/${activityId}`)
export const addActivity = (params) => request.post('/activity/add', params)
export const updateActivity = (params) => request.put('/activity/update', params);
export const fetchRecruitInfo = (params) => request.post('/activity/org/recruitInfo', params);
export const fetchRecruitDetail = (activityId) => request.get(`/activity/recruitDetail/${activityId}`);

export const registerActivity = (data) => request.post('/activity-registration', data)
export const getActivityRecords = (params) => request.post('/activity-registration/list', params)


// 活动详情相关
export const getActivityDetail = (activityId) => request.get(`/activity-detail/${activityId}`)
export const participateActivity = (data) => request.post('/activity-registration/participate', data)
export const cancelRegistration = (id) => request.put(`/activity-registration/cancel/${id}`)
export const reRegister = (id) => request.put(`/activity-registration/reregister/${id}`)
export const updateRegState = (params) => request.put('/activity-registration/updateState', params)