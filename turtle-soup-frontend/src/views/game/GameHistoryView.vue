<template>
  <div class="game-history-container card-container">
    <h2>历史对话记录</h2>
    <el-timeline>
      <el-timeline-item
        v-for="(item, idx) in chatMessages"
        :key="idx"
        :timestamp="item.time"
        :color="item.type === 'ai' ? '#6e6bc4' : '#ffd700'"
      >
        <div>
          <span v-if="item.type === 'ai'">🤖 <b>AI：</b></span>
          <span v-else>🧑‍💻 <b>你：</b></span>
          {{ item.content }}
        </div>
      </el-timeline-item>
    </el-timeline>
    <el-button style="margin-top: 24px;" @click="$router.back()">返回</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/api/index'

const route = useRoute()
const chatMessages = ref([])

onMounted(async () => {
  const sessionId = route.params.sessionId
  if (sessionId) {
    try {
      const res = await request({
        url: `/game/sessionDetail/${sessionId}`,
        method: 'get'
      })
      if (Array.isArray(res.data)) {
        chatMessages.value = []
        res.data.forEach(item => {
          if (item.question) {
            chatMessages.value.push({
              type: 'user',
              content: item.question,
              time: item.createdAt ? new Date(item.createdAt).toLocaleString() : ''
            })
          }
          if (item.aiAnswer) {
            chatMessages.value.push({
              type: 'ai',
              content: item.aiAnswer,
              time: item.createdAt ? new Date(item.createdAt).toLocaleString() : ''
            })
          }
        })
      }
    } catch (e) {
      chatMessages.value = []
    }
  }
})
</script>

<style scoped>
.game-history-container {
  max-width: 700px;
  margin: 40px auto;
  padding: 32px 24px;
}
</style> 