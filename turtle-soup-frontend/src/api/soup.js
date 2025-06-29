import request from './index'

// 获取题目列表（旧版本，已废弃）
export const getSoupListOld = (params) => {
  return request.get('/soup/list', {
    params: {
      page: params.page || 1,
      pageSize: params.size || 10,  // 匹配DTO的pageSize字段
      difficulty: params.difficulty,
      tagId: params.tag,  // 对应后端的tagId参数
      title: params.keyword  // DTO中使用title字段接收关键词
    }
  })
}

// 获取题目详情
export function getSoupDetail(id) {
  return request({
    url: `/soup/${id}`,
    method: 'get'
  })
}

// 创建题目
export function createSoup(data) {
  return request({
    url: '/soup',
    method: 'post',
    data
  })
}

// 更新题目
export function updateSoup(id, data) {
  return request({
    url: `/soup/${id}`,
    method: 'put',
    data
  })
}

// 删除题目
export function deleteSoup(id) {
  return request({
    url: `/soup/${id}`,
    method: 'delete'
  })
}

// AI问答
export function askQuestion(data) {
  return request({
    url: '/soup/ask',
    method: 'post',
    data
  })
} 