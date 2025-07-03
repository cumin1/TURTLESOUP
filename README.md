# AI海龟汤推理游戏平台

## 项目简介
「AI海龟汤」是一款结合了推理、AI互动与趣味谜题的在线解谜平台。支持用户与AI对话推理、题库筛选、历史记录、邮箱注册等功能，适合聚会、破冰、提升逻辑思维能力。

---

## 功能亮点
- 🧩 丰富题库，支持标签/难度筛选
- 🤖 AI智能问答，推理互动
- 📜 历史游戏记录与继续游戏
- 🎯 邮箱注册与验证码安全机制
- 🌈 响应式美观界面，支持移动端

---

## 技术栈
- 前端：Vue3 + Pinia + Element Plus + Axios
- 后端：Spring Boot + MyBatis Plus + Redis + 邮件服务
- AI服务：DeepSeek API
- 数据库：MySQL

---

## 环境依赖
- Node.js >= 16.x
- MySQL >= 8.0
- Redis >= 5.x
- JDK >= 17
- Maven >= 3.6
- python >= 3.8
- 邮箱SMTP服务（如QQ邮箱、163邮箱等）

---

## 数据库和中间件准备
1. **新建数据库**（如`turtle_soup`）
2. **导入SQL文件**
   ```bash
   # 进入MySQL命令行
   mysql -u root -p
   CREATE DATABASE turtle_soup DEFAULT CHARACTER SET utf8mb4;
   USE turtle_soup;
   SOURCE ./turtle-soup-backend/src/main/resources/sql/store.sql;
   ```
3. **启动Redis服务**（默认端口6379）

---

## 后端配置（application-dev.yml 示例）
请在 `turtle-soup-backend/src/main/resources/` 下新建 `application-dev.yml`，内容如下：

```yaml
turtle:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/turtle_soup?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_mysql_password
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: ''
      database: 0
  mail:
    host: smtp.qq.com
    username: your_email@qq.com
    password: your_email_smtp_auth_code
    properties:
      from: your_email@qq.com
  apikey: sk-your_deepseek_api_key
```

---

## 后端启动
```bash
cd turtle-soup-backend
# 推荐用IDEA导入为Maven项目，或命令行：
mvn clean package
# 运行主类
java -jar target/turtle-soup-backend-1.0-SNAPSHOT.jar
# 或用IDEA直接运行 TurtleSoupBackendApplication.java
```

## 导入海龟汤数据
```bash
pip install -r requirement.txt
python import_soup_data.py
```

## 前端启动
```bash
cd turtle-soup-frontend
npm install
npm run serve
# 默认端口 http://localhost:8080
```

---

## 常见问题
- **验证码/注册失败？**
  - 请确保前后端端口一致配置CORS，前端请求带cookie。
  - 邮箱配置需用SMTP授权码。
- **AI问答不可用？**
  - 检查DeepSeek API Key是否正确，网络是否可访问。
- **数据库连接失败？**
  - 检查MySQL配置、端口、账号密码。
- **Redis未启动？**
  - 请先启动本地Redis服务。

---

## 其它
- 如需自定义题库、标签，可直接在数据库中增删改表数据。
- 如遇Bug或有新需求，欢迎提Issue或PR。

---

> 🌟 感谢使用「AI海龟汤」！祝你推理愉快！ 