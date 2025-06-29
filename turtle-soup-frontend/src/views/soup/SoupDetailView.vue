<template>
  <div class="soup-detail-container">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="soup" class="soup-content">
      <div class="soup-header card-container">
        <h1 class="soup-title">{{ soup.title }}</h1>
        <div class="soup-meta">
          <el-tag :type="getDifficultyType(soup.difficulty)">
            {{ soup.difficulty }}
          </el-tag>
          <div class="tags">
            <el-tag v-for="tag in soup.tags" :key="tag.id" size="small">
              {{ tag.name }}
            </el-tag>
          </div>
        </div>
        <div class="actions">
          <el-button type="primary" @click="startGame">开始游戏</el-button>
          <el-button @click="showAnswer = !showAnswer">
            {{ showAnswer ? '隐藏答案' : '查看答案' }}
          </el-button>
        </div>
      </div>

      <div class="soup-body card-container">
        <h2>题目描述</h2>
        <p>{{ soup.question }}</p>
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
import { getSoupDetail } from '@/api/soup'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const soup = ref(null)
const loading = ref(true)
const showAnswer = ref(false)

const getDifficultyType = (difficulty) => {
  const types = { '简单': 'success', '中等': 'warning', '困难': 'danger' }
  return types[difficulty] || 'info'
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

const startGame = () => {
  if (!userStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    return
  }
  router.push(`/game/${soup.value.id}`)
}

onMounted(() => {
  loadSoupDetail()
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
</style> 