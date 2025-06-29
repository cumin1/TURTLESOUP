import request from './index'

export const getSoupList = (params) => {
  return request({
    url: '/soup/list',
    method: 'post',
    data: {
      page: params.page || 1,
      pageSize: params.size || 10,
      title: params.keyword || '',
      difficulty: params.difficulty,
      tagId: params.tagId
    }
  })
}

export const getSoupDetail = (id) => {
  return request({
    url: `/soup/${id}`,
    method: 'get'
  })
}

export const askQuestion = (data) => {
  return request({
    url: '/soup/ask',
    method: 'post',
    data
  })
}