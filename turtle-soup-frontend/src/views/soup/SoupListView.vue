<template>
  <div class="soup-list-container">
    <div class="floating-filter-bar">
      <div class="filter-title">筛选</div>
      <div class="filter-group vertical">
        <div class="filter-label">难度</div>
        <div class="card-selector vertical">
          <div
            v-for="item in difficultyOptions"
            :key="item.value"
            :class="['selector-card', {active: filters.difficulty === item.value}]"
            :style="{background: item.gradient}"
            @click="selectDifficulty(item.value)"
          >
            <span class="selector-icon">{{ item.icon }}</span>
            <span class="selector-label">{{ item.label }}</span>
          </div>
        </div>
      </div>
      <div class="filter-group vertical">
        <div class="filter-label">标签</div>
        <div class="card-selector vertical tag-scroll">
          <div
            v-for="tag in tags"
            :key="tag.id"
            :class="['selector-card', {active: selectedTags.includes(tag.id)}]"
            :style="{background: tag.gradient}"
            @click="selectTag(tag.id)"
          >
            <span class="selector-icon">{{ tag.icon }}</span>
            <span class="selector-label">{{ tag.name }}</span>
            <span v-if="selectedTags.includes(tag.id)" class="checkmark">✔</span>
          </div>
        </div>
      </div>
      <span class="filter-clear" @click="clearFilters">重置</span>
    </div>
    <div class="floating-search-bar">
      <div class="search-anim-wrap">
        <div class="search-icon" @click="toggleSearch">
          <img src="/magnifier.svg" alt="search" :class="{active: searchActive}" />
        </div>
        <transition name="slide-input">
          <input
            v-if="searchActive"
            v-model="filters.keyword"
            class="creative-input floating-input"
            placeholder="输入关键词推理吧..."
            @keyup.enter="handleSearch"
          />
        </transition>
        <transition name="slide-go">
          <div
            v-if="searchActive"
            class="search-go"
            @click="handleSearch"
            title="开始侦探"
          >
            <img src="/footprint.svg" alt="go" />
          </div>
        </transition>
      </div>
    </div>
    <div class="main-content">
      <div class="page-header detective-bg">
        <img src="/detective.svg" class="detective-icon" alt="detective" />
        <div class="header-content">
          <h1 class="page-title">推理谜题库</h1>
          <p class="page-subtitle">探索海龟汤的神秘世界，挑战你的逻辑思维</p>
        </div>
        <div class="header-decoration">
          <el-icon class="decoration-icon"><Turtle /></el-icon>
        </div>
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
            <el-button type="primary" size="small" class="play-btn" @click.stop="goToDetail(soup)">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Turtle, ArrowRight } from '@element-plus/icons-vue'
import { getSoupList, startGame } from '@/api/soupApi'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const soupList = ref([])
const tags = ref([
  { id: 5, name: '动物主题' },
  { id: 6, name: '奇幻冒险' },
  { id: 2, name: '恶搞趣味' },
  { id: 4, name: '悬疑烧脑' },
  { id: 7, name: '日常生活' },
  { id: 3, name: '温馨治愈' },
  { id: 1, name: '经典推理' },
  { id: 8, name: '黑暗系' }
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

const searchActive = ref(false)
const toggleSearch = () => { searchActive.value = !searchActive.value }

const difficultyOptions = [
  { label: '简单', value: 1, icon: '🌱', gradient: 'linear-gradient(90deg,#a8ff78,#78ffd6)' },
  { label: '中等', value: 2, icon: '🌿', gradient: 'linear-gradient(90deg,#43cea2,#185a9d)' },
  { label: '较难', value: 3, icon: '🍂', gradient: 'linear-gradient(90deg,#f7971e,#ffd200)' },
  { label: '困难', value: 4, icon: '🌑', gradient: 'linear-gradient(90deg,#232526,#414345)' }
]
const selectDifficulty = (val) => {
  filters.difficulty = filters.difficulty === val ? null : val
  handleSearch()
}
const selectedTags = ref([])
const selectTag = (id) => {
  const idx = selectedTags.value.indexOf(id)
  if (idx === -1) {
    selectedTags.value.push(id)
  } else {
    selectedTags.value.splice(idx, 1)
  }
  filters.tag = selectedTags.value.length ? [...selectedTags.value] : null
  handleSearch()
}
const clearFilters = () => {
  filters.difficulty = null
  filters.tag = null
  filters.keyword = ''
  selectedTags.value = []
  handleSearch()
}

const getDifficultyType = (difficulty) => {
  const types = {
    1: 'success',  // 简单
    2: 'warning',  // 中等
    3: 'info',     // 较难
    4: 'danger'    // 困难
  }
  return types[difficulty] || 'info'
}

const getDifficultyText = (difficulty) => {
  const texts = {
    1: '简单',
    2: '中等',
    3: '较难',
    4: '困难'
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
  // 只传第一个选中的tagId，兼容后端单选
  if (Array.isArray(selectedTags.value) && selectedTags.value.length > 0) {
    filters.tag = selectedTags.value[0]
  } else {
    filters.tag = null
  }
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
  if (!userStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    return
  }
  router.push(`/soup/${soup.id}`)
}

const goToDetail = (soup) => {
  router.push(`/soup/${soup.id}`)
}

tags.value = tags.value.map((tag, idx) => ({
  ...tag,
  icon: ['🐾','🦄','😂','🕵️','🌞','💖','🧩','🌑'][idx],
  gradient: [
    'linear-gradient(90deg,#f7971e,#ffd200)',
    'linear-gradient(90deg,#43cea2,#185a9d)',
    'linear-gradient(90deg,#f7971e,#ffd200)',
    'linear-gradient(90deg,#4f8cff,#6ed0ff)',
    'linear-gradient(90deg,#a8ff78,#78ffd6)',
    'linear-gradient(90deg,#ffecd2,#fcb69f)',
    'linear-gradient(90deg,#667eea,#764ba2)',
    'linear-gradient(90deg,#232526,#414345)'
  ][idx]
}))
</script>

<style scoped>
.soup-list-container {
  display: flex;
  flex-direction: row;
  background: linear-gradient(135deg, #f7f8fa 0%, #e0eafc 100%);
  min-height: 100vh;
  padding-bottom: 40px;
}

.floating-filter-bar {
  position: fixed;
  top: 90px;
  left: 24px;
  width: 140px;
  z-index: 20;
  background: linear-gradient(135deg,rgba(255,255,255,0.85) 60%,rgba(200,220,255,0.7) 100%);
  border-radius: 16px;
  box-shadow: 0 8px 32px 0 rgba(44,62,80,0.18), 0 0 16px 2px #4f8cff44;
  padding: 14px 7px 14px 7px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  animation: fadeInLeft 0.5s cubic-bezier(.68,-0.55,.27,1.55);
  backdrop-filter: blur(8px);
  border: 2.5px solid #e0eafc;
}

@keyframes fadeInLeft {
  from { opacity: 0; transform: translateX(-40px); }
  to { opacity: 1; transform: translateX(0); }
}

.floating-filter-bar .filter-title {
  font-size: 20px;
  font-weight: bold;
  color: #4f8cff;
  margin-bottom: 12px;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px #e0eafc;
}

.filter-group.vertical {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 7px;
  margin-bottom: 4px;
}

.card-selector.vertical {
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.card-selector.vertical.tag-scroll {
  max-height: 180px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #e0eafc #fff;
  padding-right: 2px;
}
.card-selector.vertical.tag-scroll::-webkit-scrollbar {
  width: 6px;
  background: #fff;
}
.card-selector.vertical.tag-scroll::-webkit-scrollbar-thumb {
  background: #e0eafc;
  border-radius: 6px;
}
.selector-card {
  width: 92%;
  min-width: 0;
  margin: 0 auto;
  border-radius: 10px;
  box-shadow: 0 1px 4px #4f8cff18, 0 1px 2px #ffd70018;
  border: 2px solid transparent;
  background-clip: padding-box;
  transition: box-shadow 0.15s, transform 0.15s, border 0.15s;
  cursor: pointer;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 5px;
  padding: 5px 5px 5px 8px;
  font-size: 13px;
  user-select: none;
  opacity: 0.93;
  min-height: 28px;
  height: 28px;
}
.selector-card .selector-icon {
  width: 18px;
  height: 18px;
  font-size: 13px;
  margin-right: 3px;
}
.selector-card .selector-label {
  font-size: 12px;
  font-weight: bold;
  color: #2d3e50;
  letter-spacing: 0.3px;
}
.selector-card.active, .selector-card:hover {
  border: 2px solid #ffd700;
  box-shadow: 0 2px 8px #ffd70033, 0 1px 2px #23252622;
  transform: scale(1.03) translateX(1px);
  opacity: 1;
  z-index: 2;
  background: linear-gradient(90deg,#fffbe6 60%,#ffe066 100%);
}
.selector-card.active .selector-label, .selector-card:hover .selector-label {
  color: #232526;
}
.selector-card .checkmark {
  margin-left: auto;
  color: #ffd700;
  font-size: 13px;
  font-weight: bold;
  background: #fffbe6;
  border-radius: 50%;
  width: 14px;
  height: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 2px #ffd70033;
}

.filter-clear {
  margin-top: 10px;
  color: #ff6f61;
  cursor: pointer;
  font-size: 15px;
  font-weight: bold;
  border-bottom: 1px dashed #ff6f61;
  transition: color 0.2s, box-shadow 0.2s;
  padding: 2px 8px;
  border-radius: 8px;
  background: rgba(255,255,255,0.7);
  box-shadow: 0 1px 4px #ff6f6133;
}

.filter-clear:hover {
  color: #d7263d;
  background: #fff0f0;
  box-shadow: 0 2px 8px #ff6f6133;
}

.floating-search-bar {
  position: fixed;
  top: 90px;
  right: 32px;
  z-index: 20;
  background: rgba(255,255,255,0.98);
  border-radius: 18px;
  box-shadow: 0 8px 32px rgba(44,62,80,0.10);
  padding: 18px 18px 18px 18px;
  display: flex;
  align-items: center;
  gap: 10px;
  animation: fadeInRight 0.5s cubic-bezier(.68,-0.55,.27,1.55);
}

.search-anim-wrap {
  display: flex;
  align-items: center;
  position: relative;
}

.search-icon {
  width: 40px;
  height: 40px;
  cursor: pointer;
  border-radius: 50%;
  background: rgba(230, 236, 255, 0.7);
  box-shadow: 0 2px 8px rgba(44,62,80,0.04);
  opacity: 0.85;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: margin-right 0.4s cubic-bezier(.4,2,.6,1), transform 0.4s cubic-bezier(.4,2,.6,1);
  margin-right: 0;
}

.search-icon img {
  width: 60%;
  height: 60%;
  transition: transform 0.3s, filter 0.3s;
}

.search-icon img.active {
  transform: rotate(-20deg) scale(1.1);
  filter: drop-shadow(0 0 8px #ffd700);
}

.search-anim-wrap {
  min-width: 40px;
}

.search-anim-wrap .search-icon {
  z-index: 2;
}

.slide-input-enter-active, .slide-input-leave-active {
  transition: width 0.4s cubic-bezier(.4,2,.6,1), opacity 0.35s cubic-bezier(.4,2,.6,1), margin-left 0.4s cubic-bezier(.4,2,.6,1);
}

.slide-input-enter-from, .slide-input-leave-to {
  width: 0;
  opacity: 0;
  margin-left: 0;
}

.slide-input-enter-to, .slide-input-leave-from {
  width: 180px;
  opacity: 1;
  margin-left: 12px;
}

.creative-input {
  width: 180px;
  padding: 8px 14px;
  border: none;
  border-radius: 24px;
  background: #fff;
  font-size: 16px;
  color: #2d3e50;
  box-shadow: 0 2px 8px rgba(44,62,80,0.06);
  outline: none;
  margin-left: 12px;
  transition: box-shadow 0.2s;
}

.slide-go-enter-active, .slide-go-leave-active {
  transition: opacity 0.35s cubic-bezier(.4,2,.6,1), transform 0.4s cubic-bezier(.4,2,.6,1);
}

.slide-go-enter-from, .slide-go-leave-to {
  opacity: 0;
  transform: translateX(-24px) scale(0.8);
}

.slide-go-enter-to, .slide-go-leave-from {
  opacity: 1;
  transform: translateX(0) scale(1);
}

.search-go {
  margin-left: 4px;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  transition: filter 0.2s;
}

.search-go img {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 0 6px #ffd700);
}

.search-anim-wrap .search-icon {
  transition: margin-right 0.4s cubic-bezier(.4,2,.6,1), transform 0.4s cubic-bezier(.4,2,.6,1);
}

.search-anim-wrap .search-icon:has(+ .v-enter-active),
.search-anim-wrap .search-icon:has(+ .creative-input) {
  margin-right: 12px;
  transform: translateX(-8px);
}

.main-content {
  flex: 1;
  margin-left: 180px;
  margin-right: 180px;
  min-width: 0;
}

@media (max-width: 900px) {
  .main-content {
    margin-left: 0;
    margin-right: 0;
  }
  .floating-filter-bar, .floating-search-bar {
    display: none;
  }
}

.page-header.detective-bg {
  background: linear-gradient(135deg, #2d3e50 0%, #4f8cff 100%);
  color: #fff;
  border-bottom: 3px solid #ffd700;
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
  font-size: 44px;
  font-weight: bold;
  color: #fff;
  letter-spacing: 2px;
  text-shadow: 2px 2px 8px #2d3e50, 0 0 2px #ffd700;
  -webkit-text-stroke: 1px #2d3e50;
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
</style>