<template>
  <div class="soup-list-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">推理谜题库</h1>
        <p class="page-subtitle">探索海龟汤的神秘世界，挑战你的逻辑思维</p>
      </div>
      <div class="header-decoration">
        <el-icon class="decoration-icon"><Turtle /></el-icon>
      </div>
    </div>

    <div class="filter-section card-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <!-- 难度等级：2简单，3中等，4较难，5困难 -->
          <el-select v-model="filters.difficulty" placeholder="难度" clearable>
            <el-option label="简单" :value="2" />
            <el-option label="中等" :value="3" />
            <el-option label="较难" :value="4" />
            <el-option label="困难" :value="5" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filters.tag" placeholder="标签" clearable>
            <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索题目"
            clearable
            prefix-icon="Search"
          />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch" icon="Search">
            搜索
          </el-button>
        </el-col>
      </el-row>
    </div>

    <div v-if="loading" class="loading-section">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else class="soup-grid">
      <div
        v-for="(soup, index) in soupList"
        :key="soup.id"
        class="soup-card card-container"
        @click="handleSoupClick(soup)"
        :style="{ animationDelay: `${index * 0.1}s` }"
      >
        <div class="soup-header">
          <h3 class="soup-title">{{ soup.title }}</h3>
          <el-tag :type="getDifficultyType(soup.difficulty)" size="small" class="difficulty-tag">
            {{ getDifficultyText(soup.difficulty) }}
          </el-tag>
        </div>
        <p class="soup-question">{{ soup.description || soup.question }}</p>
        <div class="soup-tags">
          <el-tag
            v-for="tagId in soup.tagIds"
            :key="tagId"
            size="small"
            class="tag-item"
          >
            {{ getTagName(tagId) }}
          </el-tag>
        </div>
        <div class="soup-footer">
          <el-button type="primary" size="small" class="play-btn">
            开始推理
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <div v-if="!loading && soupList.length === 0" class="empty-section">
      <el-empty description="暂无符合条件的题目" />
    </div>

    <div class="pagination-section">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[8, 12, 16]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Turtle, ArrowRight } from '@element-plus/icons-vue'
import { getSoupList } from '@/api/soupApi'

const router = useRouter()

const soupList = ref([])
const tags = ref([
  { id: 1, name: '经典' },
  { id: 2, name: '悬疑' },
  { id: 3, name: '搞笑' },
  { id: 4, name: '烧脑' }
])
const loading = ref(false)

const filters = reactive({
  difficulty: null,
  tag: null,
  keyword: ''
})

const pagination = reactive({
  page: 1,
  size: 8,
  total: 0
})

const getDifficultyType = (difficulty) => {
  const types = {
    2: 'success',  // 简单
    3: 'warning',  // 中等
    4: 'warning',  // 较难
    5: 'danger'    // 困难
  }
  return types[difficulty] || 'info'
}

const getDifficultyText = (difficulty) => {
  const texts = {
    2: '简单',
    3: '中等',
    4: '较难',
    5: '困难'
  }
  return texts[difficulty] || '未知'
}

const getTagName = (tagId) => {
  const tag = tags.value.find(t => t.id === tagId)
  return tag ? tag.name : '未知'
}

onMounted(() => {
  loadSoupList()
})

const loadSoupList = async () => {
  try {
    loading.value = true
    
    // 构建请求参数
    const requestParams = {
      page: pagination.page,
      size: pagination.size,
      tagId: filters.tag,
      keyword: filters.keyword,
      difficulty: filters.difficulty
    }
    
    console.log('开始加载题目列表，参数:', requestParams)
    console.log('参数类型检查:', {
      page: typeof requestParams.page,
      size: typeof requestParams.size,
      tagId: typeof requestParams.tagId,
      keyword: typeof requestParams.keyword,
      difficulty: typeof requestParams.difficulty
    })
    
    const response = await getSoupList(requestParams)
    
    console.log('API响应:', response)
    console.log('响应数据类型:', typeof response)
    console.log('响应数据结构:', {
      hasData: !!response.data,
      dataType: typeof response.data,
      hasRecords: !!(response.data && response.data.records),
      recordsType: response.data ? typeof response.data.records : 'undefined'
    })
    
    // 检查响应数据结构
    if (response && response.data) {
      soupList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      console.warn('响应数据格式异常:', response)
      soupList.value = []
      pagination.total = 0
    }
    
    console.log('处理后的数据:', {
      soupList: soupList.value,
      total: pagination.total
    })
  } catch (error) {
    console.error('加载题目列表失败:', error)
    console.error('错误详情:', {
      message: error.message,
      response: error.response,
      request: error.request,
      config: error.config
    })
    ElMessage.error('加载题目列表失败')
    soupList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadSoupList()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadSoupList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadSoupList()
}

const handleSoupClick = (soup) => {
  router.push(`/soup/${soup.id}`)
}
</script>

<style scoped>
.soup-list-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40px;
  padding: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  color: white;
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
  pointer-events: none;
}

.header-content {
  flex: 1;
  z-index: 1;
}

.page-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  letter-spacing: 2px;
}

.page-subtitle {
  font-size: 18px;
  opacity: 0.9;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}

.header-decoration {
  z-index: 1;
}

.decoration-icon {
  font-size: 60px;
  opacity: 0.8;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.filter-section {
  padding: 30px;
  margin-bottom: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.loading-section {
  padding: 40px;
  text-align: center;
}

.soup-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

.soup-card {
  padding: 25px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  border: 1px solid rgba(255,255,255,0.2);
  position: relative;
  overflow: hidden;
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
  transform: translateY(30px);
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.soup-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(102,126,234,0.1), transparent);
  transition: left 0.6s;
}

.soup-card:hover::before {
  left: 100%;
}

.soup-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0,0,0,0.15);
}

.soup-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.soup-title {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0;
  flex: 1;
  margin-right: 15px;
  line-height: 1.4;
}

.difficulty-tag {
  flex-shrink: 0;
  font-weight: bold;
}

.soup-question {
  color: #7f8c8d;
  line-height: 1.6;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 14px;
}

.soup-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.tag-item {
  margin: 0;
  font-size: 12px;
}

.soup-footer {
  display: flex;
  justify-content: flex-end;
}

.play-btn {
  background: linear-gradient(45deg, #667eea, #764ba2);
  border: none;
  transition: all 0.3s ease;
}

.play-btn:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 15px rgba(102,126,234,0.4);
}

.empty-section {
  padding: 60px 20px;
  text-align: center;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .page-title {
    font-size: 36px;
  }
  
  .soup-grid {
    grid-template-columns: 1fr;
  }
  
  .header-decoration {
    display: none;
  }
}

@media (max-width: 480px) {
  .soup-list-container {
    padding: 10px;
  }
  
  .page-header {
    padding: 30px 20px;
  }
  
  .page-title {
    font-size: 28px;
  }
  
  .filter-section {
    padding: 20px;
  }
}
</style>