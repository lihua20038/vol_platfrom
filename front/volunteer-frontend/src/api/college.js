import request from '@/utils/request'

export const getAllColleges = () => {
    return request.get('/college');
}