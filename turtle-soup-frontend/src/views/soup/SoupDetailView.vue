<template>
  <div class="soup-detail-container">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="soup" class="soup-content">
      <div class="soup-header card-container detective-bg">
        <img src="/detective.svg" class="detective-icon" alt="detective" />
        <div>
          <h1 class="soup-title">{{ soup.title }}</h1>
          <div class="soup-meta">
            <el-tag :type="getDifficultyType(soup.difficulty)">
              {{ getDifficultyText(soup.difficulty) }}
            </el-tag>
            <div class="tags">
              <el-tag v-for="tag in soup.tags" :key="tag.id" size="small">
                {{ tag.name }}
              </el-tag>
            </div>
          </div>
          <div class="actions">
            <el-button v-if="sessionId && gameStatus === '进行中'" type="primary" @click="continueGame">继续游戏</el-button>
            <el-button v-if="sessionId && gameStatus === '进行中'" type="danger" @click="handleStopGame">结束游戏</el-button>
            <el-button v-else type="primary" @click="startGame">开始游戏</el-button>
            <el-button @click="showAnswer = !showAnswer">
              {{ showAnswer ? '隐藏答案' : '查看答案' }}
            </el-button>
          </div>
        </div>
      </div>

      <div class="soup-body card-container">
        <h2>题目背景</h2>
        <p>{{ soup.description }}</p>
      </div>

      <div v-if="showAnswer" class="soup-answer card-container">
        <h2>答案</h2>
        <p>{{ soup.answer }}</p>
      </div>
    </div>

    <div v-else class="error">
      <el-empty description="题目不存在" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSoupDetail, startGame as apiStartGame, stopGame, getGameStatus } from '@/api/soupApi'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const soup = ref(null)
const loading = ref(true)
const showAnswer = ref(false)
const sessionId = ref(route.query.sessionId)
const gameStatus = ref('')
const statusLoading = ref(false)

const getDifficultyType = (difficulty) => {
  const types = { 2: 'success', 3: 'warning', 4: 'warning', 5: 'danger' }
  return types[difficulty] || 'info'
}

const getDifficultyText = (difficulty) => {
  const texts = { 2: '简单', 3: '中等', 4: '较难', 5: '困难' }
  return texts[difficulty] || '未知'
}

const loadSoupDetail = async () => {
  try {
    const response = await getSoupDetail(route.params.id)
    soup.value = response.data
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const startGame = async () => {
  if (!userStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await apiStartGame(soup.value.id)
    sessionId.value = res.data.id
    await fetchGameStatus()
    ElMessage.success('游戏已开始')
    router.push({ path: `/game/${sessionId.value}`, query: { soupId: res.data.soupId } })
  } catch (e) {
    ElMessage.error('开始游戏失败')
  }
}

const handleStopGame = async () => {
  if (!sessionId.value) {
    ElMessage.warning('无有效游戏会话')
    return
  }
  try {
    await stopGame(sessionId.value)
    ElMessage.success('已结束游戏')
    gameStatus.value = '已结束'
  } catch (e) {
    ElMessage.error('结束游戏失败')
  }
}

const fetchGameStatus = async () => {
  if (!sessionId.value) {
    gameStatus.value = null
    return
  }
  statusLoading.value = true
  try {
    const res = await getGameStatus(sessionId.value)
    gameStatus.value = res.data
  } catch (e) {
    gameStatus.value = null
  } finally {
    statusLoading.value = false
  }
}

const continueGame = () => {
  if (!sessionId.value) {
    ElMessage.warning('无进行中的游戏')
    return
  }
  router.push({ path: `/game/${sessionId.value}`, query: { soupId: soup.value.id } })
}

onMounted(() => {
  loadSoupDetail()
  fetchGameStatus()
})
</script>

<style scoped>
.soup-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.soup-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.soup-header {
  padding: 30px;
}

.soup-title {
  font-size: 28px;
  margin-bottom: 20px;
}

.soup-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.tags {
  display: flex;
  gap: 8px;
}

.actions {
  display: flex;
  gap: 15px;
}

.soup-body,
.soup-answer {
  padding: 30px;
}

.soup-body h2,
.soup-answer h2 {
  margin-bottom: 15px;
}

.loading,
.error {
  padding: 40px;
  text-align: center;
}

.soup-header.detective-bg {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%);
  border-bottom: 2px solid #ffd700;
}

.detective-icon {
  width: 100px;
  height: 100px;
  margin-right: 20px;
}

@media (max-width: 600px) {
  .soup-header.detective-bg {
    flex-direction: column;
    align-items: flex-start;
    padding: 10px !important;
  }
}
</style> 