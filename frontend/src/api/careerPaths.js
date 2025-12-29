import request from './index'

// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// ⚠️ interceptor returns response.data already
// Backend returns {data: [...]} → after interceptor → {data: [...]}

// 获取所有职业路径
export const getAllCareerPaths = () => {
  return request.get('/career-paths')
}

// 获取当前用户的职业路径
export const getMyCareerPaths = () => {
  return request.get('/career-paths/my')
}

// 获取单个职业路径详情
export const getCareerPathById = (id) => {
  return request.get(`/career-paths/${id}`)
}

// 创建职业路径
export const createCareerPath = (data) => {
  return request.post('/career-paths', data)
}

// 更新职业路径
export const updateCareerPath = (id, data) => {
  return request.put(`/career-paths/${id}`, data)
}

// 删除职业路径
export const deleteCareerPath = (id) => {
  return request.delete(`/career-paths/${id}`)
}
