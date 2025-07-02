<template>
  <div class="profile-container">
    <div class="profile-content">
      <div class="profile-header card-container">
        <div class="user-info">
          <el-avatar :size="80" :src="userStore.userInfo?.avatar">
            {{ userStore.userInfo?.username?.charAt(0) }}
          </el-avatar>
          <div class="user-details">
            <h1 class="username">{{ userStore.userInfo?.username }}</h1>
            <p class="user-role">
              注册用户
            </p>
          </div>
        </div>
      </div>

      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="个人信息" name="info">
          <div class="profile-body card-container">
            <h2>个人信息</h2>
            <el-form :model="profileForm" label-width="100px">
              <el-form-item label="用户名">
                <el-input v-model="profileForm.username" disabled />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="updateProfile">
                  更新信息
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        <el-tab-pane label="历史游戏" name="games">
          <div class="profile-body card-container">
            <h2>历史游戏记录</h2>
            <div class="game-filter-bar">
              <el-select v-model="gameFilter.status" placeholder="全部状态" style="width: 140px; margin-right: 12px;">
                <el-option label="全部" value="" />
                <el-option label="进行中" value="IN_PROGRESS" />
                <el-option label="已通关" value="SUCCESS" />
                <el-option label="已结束" value="STOPPED" />
              </el-select>
            </div>
            <el-table :data="gameList" style="width: 100%; margin-top: 16px;">
              <el-table-column prop="title" label="谜题标题" min-width="120" />
              <el-table-column prop="status" label="状态" min-width="80">
                <template #default="scope">
                  <el-tag v-if="scope.row.status === 'IN_PROGRESS'" type="warning">进行中</el-tag>
                  <el-tag v-else-if="scope.row.status === 'SUCCESS'" type="success">已通关</el-tag>
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
                  <el-button v-if="scope.row.status === 'IN_PROGRESS'" size="small" type="danger" @click="handleStopGame(scope.row)">
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
        </el-tab-pane>
      </el-tabs>

      <div class="profile-actions card-container">
        <h2>账户操作</h2>
        <div class="action-buttons">
          <el-button @click="changePassword = true">
            修改密码
          </el-button>
          <el-button type="danger" @click="handleLogout">
            退出登录
          </el-button>
        </div>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="changePassword" title="修改密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePassword = false">取消</el-button>
        <el-button type="primary" @click="updatePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { stopGame } from '@/api/soupApi'
import request from '@/api/index'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const activeTab = ref('info')

const changePassword = ref(false)
const passwordFormRef = ref()

const profileForm = reactive({
  username: userStore.userInfo?.username || '',
  email: userStore.userInfo?.email || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

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
  if (route.query.tab === 'games') {
    activeTab.value = 'games'
    fetchGameList()
  } else if (activeTab.value === 'games') {
    fetchGameList()
  }
})

watch(() => activeTab.value, (val) => {
  if (val === 'games') fetchGameList()
})

const updateProfile = () => {
  ElMessage.success('信息更新成功')
}

const updatePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    ElMessage.success('密码修改成功')
    changePassword.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    ElMessage.error('密码修改失败')
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-header {
  padding: 40px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 10px;
}

.user-role {
  color: #7f8c8d;
  font-size: 16px;
}

.profile-body,
.profile-actions {
  padding: 30px;
}

.profile-body h2,
.profile-actions h2 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #2c3e50;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.profile-tabs {
  margin-top: 24px;
}
.game-filter-bar {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}
</style> 