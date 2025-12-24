import request from './index'

// 获取所有职业路径
export const getAllCareerPaths = async () => {
  const response = await request.get('/career-paths')
  return response.data || response // 处理 {data: [...]} 格式
}

// 获取当前用户的职业路径
export const getMyCareerPaths = async () => {
  const response = await request.get('/career-paths/my')
  return response.data || response // 处理 {data: [...]} 格式
}

// 获取单个职业路径详情
export const getCareerPathById = (id) => {
  return request.get(`/career-paths/${id}`)
}
