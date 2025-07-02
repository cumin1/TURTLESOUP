<template>
  <div class="user-games-container">
    <div class="user-games-header card-container">
      <h1>历史游戏记录</h1>
      <div class="game-filter-bar">
        <el-select v-model="gameFilter.status" placeholder="全部状态" style="width: 140px; margin-right: 12px;">
          <el-option label="全部" value="" />
          <el-option label="进行中" value="进行中" />
          <el-option label="已通关" value="已通关" />
          <el-option label="已结束" value="已停止" />
        </el-select>
      </div>
    </div>
    <div class="user-games-body card-container">
      <el-table :data="gameList" style="width: 100%; margin-top: 16px;">
        <el-table-column prop="title" label="谜题标题" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '进行中'" type="warning">进行中</el-tag>
            <el-tag v-else-if="scope.row.status === '已通关'" type="success">已通关</el-tag>
            <el-tag v-else type="info">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" min-width="120">
          <template #default="scope">
            {{ formatTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" min-width="120">
          <template #default="scope">
            {{ formatTime(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100">
          <template #default="scope">
            <el-button v-if="scope.row.status === '进行中'" size="small" type="danger" @click="handleStopGame(scope.row)">
              结束游戏
            </el-button>
            <span v-else>--</span>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          v-model:current-page="gamePagination.page"
          v-model:page-size="gamePagination.pageSize"
          :total="gamePagination.total"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchGameList"
          @current-change="fetchGameList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { stopGame } from '@/api/soupApi'
import request from '@/api/index'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const gameList = ref([])
const gamePagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})
const gameFilter = reactive({
  status: ''
})

const fetchGameList = async () => {
  if (!userStore.isAuthenticated) {
    router.push('/login')
    return
  }
  try {
    const res = await request({
      url: '/user/games',
      method: 'post',
      data: {
        page: gamePagination.page,
        pageSize: gamePagination.pageSize,
        status: gameFilter.status
      }
    })
    gameList.value = res.data.records
    gamePagination.total = res.data.total
  } catch (e) {
    ElMessage.error('加载历史游戏失败')
  }
}

const handleStopGame = async (row) => {
  try {
    await stopGame(row.sessionId)
    ElMessage.success('游戏已结束')
    fetchGameList()
  } catch (e) {
    ElMessage.error('结束游戏失败')
  }
}

const formatTime = (t) => {
  if (!t) return '--'
  return new Date(t).toLocaleString()
}

onMounted(() => {
  fetchGameList()
})
</script>

<style scoped>
.user-games-container {
  max-width: 900px;
  margin: 40px auto;
}
.user-games-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}
.user-games-body {
  margin-top: 0;
}
.game-filter-bar {
  margin-bottom: 0;
  display: flex;
  align-items: center;
}
</style> 