<template>
  <div class="about-page card-container">
    <h1>🥚 关于「AI海龟汤」</h1>
    <section class="about-section">
      <h2>项目简介</h2>
      <p>「AI海龟汤」是一款结合了推理、AI互动与趣味谜题的在线解谜平台。你可以与AI一起玩烧脑的海龟汤游戏，体验神秘与可爱的推理世界！</p>
    </section>
    <section class="about-section">
      <h2>主要功能</h2>
      <ul>
        <li>🧩 丰富的谜题库，支持标签、难度筛选</li>
        <li>🤖 AI智能问答，陪你推理每一步</li>
        <li>📜 历史游戏记录，随时回顾你的推理之旅</li>
        <li>🎯 邮箱注册与验证码安全机制</li>
        <li>🌈 响应式美观界面，支持移动端</li>
      </ul>
    </section>
    <section class="about-section">
      <h2>技术栈</h2>
      <ul>
        <li>前端：Vue3 + Pinia + Element Plus + Axios</li>
        <li>后端：Spring Boot + MyBatis Plus + Redis + 邮件服务</li>
        <li>AI服务：DeepSeek API</li>
        <li>数据库：MySQL</li>
      </ul>
    </section>
    <section class="about-section">
      <h2>作者信息</h2>
      <ul>
        <li>👨‍💻 开发者：你可以在此处填写你的昵称或联系方式</li>
        <li>📧 3144941746@qq.com</li>
        <li>🗓️ 项目时间：2025年7月</li>
      </ul>
    </section>
    <section class="about-section">
      <h2>开源声明</h2>
      <p>本项目仅供学习与交流，部分谜题内容来源于网络，如有侵权请联系删除。</p>
    </section>
    <div class="about-footer">
      <p>🌟 感谢你的访问，祝你推理愉快！</p>
    </div>
  </div>
</template>

<style scoped>
.about-page {
  max-width: 700px;
  margin: 40px auto;
  padding: 32px 24px;
  background: linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(44,62,80,0.10);
}
.about-page h1 {
  text-align: center;
  font-size: 2.2rem;
  margin-bottom: 24px;
  color: #6e6bc4;
  letter-spacing: 2px;
}
.about-section {
  margin-bottom: 24px;
}
.about-section h2 {
  color: #232946;
  font-size: 1.2rem;
  margin-bottom: 8px;
}
.about-section ul {
  padding-left: 1.2em;
  margin: 0;
}
.about-section li {
  margin-bottom: 6px;
  font-size: 1rem;
}
.about-footer {
  text-align: center;
  margin-top: 32px;
  color: #6e6bc4;
  font-size: 1.1rem;
}
</style>
