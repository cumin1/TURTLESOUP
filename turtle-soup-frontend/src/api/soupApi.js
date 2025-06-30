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

// 开始游戏
export const startGame = (soupId) => {
  return request({
    url: '/game/start',
    method: 'post',
    params: { soupId }
  })
}

// 停止游戏
export const stopGame = (sessionId) => {
  return request({
    url: '/game/stop',
    method: 'get',
    params: { sessionId }
  })
}

// 获取游戏状态
export const getGameStatus = (sessionId) => {
  return request({
    url: '/game/status',
    method: 'get',
    params: { sessionId }
  })
}