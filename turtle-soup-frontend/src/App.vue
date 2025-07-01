<template>
  <div id="app">
    <!-- 现代化导航栏 -->
    <nav class="modern-navbar">
      <div class="nav-container">
        <!-- Logo区域 -->
        <div class="nav-logo">
          <router-link to="/" class="logo-link">
            <div class="logo-icon">
              <el-icon><Turtle /></el-icon>
            </div>
            <span class="logo-text">海龟汤</span>
          </router-link>
        </div>
        
        <!-- 导航菜单 -->
        <div class="nav-menu">
          <router-link 
            to="/" 
            class="nav-item"
            :class="{ active: $route.path === '/' }"
          >
            <el-icon><House /></el-icon>
            <span>首页</span>
          </router-link>
          
          <router-link 
            to="/soup" 
            class="nav-item"
            :class="{ active: $route.path.startsWith('/soup') }"
          >
            <el-icon><Collection /></el-icon>
            <span>谜题库</span>
          </router-link>
          
          <router-link 
            to="/about" 
            class="nav-item"
            :class="{ active: $route.path === '/about' }"
          >
            <el-icon><InfoFilled /></el-icon>
            <span>关于</span>
          </router-link>
        </div>
        
        <!-- 用户区域 -->
        <div class="nav-user">
          <template v-if="userStore.isAuthenticated && userStore.userInfo && userStore.userInfo.username">
            <el-dropdown @command="handleUserCommand" trigger="click">
              <div class="user-avatar">
                <div class="avatar-container">
                  <el-avatar :size="40" :src="userStore.userInfo?.avatar" class="user-avatar-img">
                    {{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
                  </el-avatar>
                  <div class="avatar-status"></div>
                </div>
                <div class="user-info">
                  <span class="username">{{ userStore.userInfo?.username }}</span>
                  <span class="user-role">{{ userStore.isGuest ? '游客' : '用户' }}</span>
                </div>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="user-dropdown">
                  <div class="dropdown-header">
                    <div class="dropdown-avatar">
                      <el-avatar :size="48" :src="userStore.userInfo?.avatar">
                        {{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
                      </el-avatar>
                    </div>
                    <div class="dropdown-user-info">
                      <div class="dropdown-username">{{ userStore.userInfo?.username }}</div>
                      <div class="dropdown-email">{{ userStore.userInfo?.email || '未设置邮箱' }}</div>
                    </div>
                  </div>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    设置
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <div class="auth-buttons">
              <el-button 
                type="primary" 
                @click="$router.push('/login')"
                class="login-btn"
              >
                登录
              </el-button>
              <el-button 
                @click="$router.push('/register')"
                class="register-btn"
              >
                注册
              </el-button>
            </div>
          </template>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Turtle, 
  ArrowDown, 
  House, 
  Collection, 
  InfoFilled, 
  User, 
  SwitchButton,
  Setting
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  userStore.initUserState()
})

// 处理用户下拉菜单命令
const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      ElMessage.info('设置功能开发中...')
      break
    case 'logout':
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
      break
  }
}
</script>

<style scoped>
.modern-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  height: 70px;
}

/* Logo样式 */
.nav-logo {
  flex-shrink: 0;
}

.logo-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #2c3e50;
  font-weight: bold;
  font-size: 24px;
  transition: all 0.3s ease;
}

.logo-link:hover {
  transform: scale(1.05);
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 15px;
  margin-right: 12px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.logo-icon .el-icon {
  font-size: 28px;
  color: white;
}

.logo-link:hover .logo-icon {
  transform: rotate(5deg);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.logo-text {
  font-size: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 导航菜单 */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  justify-content: center;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  text-decoration: none;
  color: #7f8c8d;
  font-weight: 500;
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.1), transparent);
  transition: left 0.5s;
}

.nav-item:hover::before {
  left: 100%;
}

.nav-item:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.nav-item.active {
  color: #667eea;
  background: rgba(102, 126, 234, 0.15);
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.2);
}

.nav-item .el-icon {
  font-size: 18px;
}

.nav-item span {
  font-size: 16px;
}

/* 用户区域 */
.nav-user {
  flex-shrink: 0;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 25px;
  background: rgba(102, 126, 234, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.user-avatar::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.1), transparent);
  transition: left 0.6s;
}

.user-avatar:hover::before {
  left: 100%;
}

.user-avatar:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
}

.avatar-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar-img {
  border: 3px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.user-avatar:hover .user-avatar-img {
  transform: scale(1.1);
  border-color: rgba(102, 126, 234, 0.5);
}

.avatar-status {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #52c41a;
  border: 2px solid white;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(82, 196, 26, 0.7);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(82, 196, 26, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(82, 196, 26, 0);
  }
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.username {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 600;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-role {
  font-size: 11px;
  color: #7f8c8d;
  background: rgba(102, 126, 234, 0.1);
  padding: 2px 6px;
  border-radius: 8px;
  font-weight: 500;
}

.dropdown-icon {
  font-size: 12px;
  color: #7f8c8d;
  transition: transform 0.3s ease;
}

.user-avatar:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 用户下拉菜单 */
.user-dropdown {
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.95);
  padding: 8px;
  min-width: 220px;
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  margin-bottom: 8px;
}

.dropdown-avatar {
  position: relative;
}

.dropdown-avatar .el-avatar {
  border: 3px solid rgba(102, 126, 234, 0.2);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.dropdown-user-info {
  flex: 1;
  min-width: 0;
}

.dropdown-username {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-email {
  font-size: 12px;
  color: #7f8c8d;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-dropdown .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  margin: 2px 0;
  transition: all 0.3s ease;
  font-size: 14px;
}

.user-dropdown .el-dropdown-menu__item:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  transform: translateX(4px);
}

.user-dropdown .el-dropdown-menu__item .el-icon {
  font-size: 16px;
}

/* 认证按钮 */
.auth-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
}

.login-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  padding: 10px 20px;
  border-radius: 25px;
  font-weight: 500;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.register-btn {
  background: rgba(102, 126, 234, 0.1);
  border: 2px solid rgba(102, 126, 234, 0.3);
  color: #667eea;
  padding: 10px 20px;
  border-radius: 25px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.register-btn:hover {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.5);
  transform: translateY(-2px);
}

/* 主内容区域 */
.app-main {
  margin-top: 70px;
  min-height: calc(100vh - 70px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-container {
    padding: 0 20px;
  }
  
  .nav-menu {
    display: none;
  }
  
  .logo-text {
    display: none;
  }
  
  .logo-icon {
    width: 40px;
    height: 40px;
    margin-right: 0;
  }
  
  .logo-icon .el-icon {
    font-size: 24px;
  }
  
  .user-info {
    display: none;
  }
  
  .user-avatar {
    padding: 6px 12px;
    gap: 8px;
  }
  
  .user-avatar-img {
    width: 32px !important;
    height: 32px !important;
  }
  
  .avatar-status {
    width: 10px;
    height: 10px;
    bottom: 1px;
    right: 1px;
  }
  
  .auth-buttons {
    gap: 8px;
  }
  
  .login-btn,
  .register-btn {
    padding: 8px 16px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .nav-container {
    padding: 0 15px;
  }
  
  .user-avatar {
    padding: 4px 8px;
  }
  
  .user-avatar-img {
    width: 28px !important;
    height: 28px !important;
  }
  
  .avatar-status {
    width: 8px;
    height: 8px;
  }
  
  .auth-buttons {
    flex-direction: column;
    gap: 6px;
  }
  
  .login-btn,
  .register-btn {
    padding: 6px 12px;
    font-size: 12px;
  }
}

/* 滚动效果 */
.modern-navbar.scrolled {
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}
</style>
