package com.turtle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turtle.common.exception.BaseException;
import com.turtle.common.exception.GameSessionException;
import com.turtle.common.utils.PromptUtils;
import com.turtle.mapper.GameSessionMapper;
import com.turtle.mapper.QuestionLogMapper;
import com.turtle.mapper.SoupMapper;
import com.turtle.pojo.dto.QuestionDTO;
import com.turtle.pojo.entity.GameSession;
import com.turtle.pojo.entity.Soup;
import com.turtle.pojo.entity.QuestionLog;
import com.turtle.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.turtle.common.constant.MessageConstant.*;
import static com.turtle.common.constant.StatusConstant.*;

@Slf4j
@Service
public class GameServiceImpl extends ServiceImpl<GameSessionMapper, GameSession>  implements GameService {

    @Autowired
    private SoupMapper soupMapper;

    @Autowired
    private QuestionLogMapper questionLogMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    /**
     * 用户开始游戏
     * @param userId
     * @param soupId
     * @return
     */
    public GameSession startGame(Long userId, Long soupId) {
        // 判断用户是否达到最大同时进行游戏的数量
        Long count = query().eq("user_id", userId)
                .eq("status", BEGINNING)
                .count();

        if (count >= 3){
            throw new GameSessionException(TOO_MANY_GAME);
        }

        // 查询是否已有进行中的会话
        GameSession existSession = lambdaQuery()
            .eq(GameSession::getUserId, userId)
            .eq(GameSession::getSoupId, soupId)
            .eq(GameSession::getStatus, BEGINNING)
            .one();
        if (existSession != null) {
            return existSession;
        }
        GameSession gameSession = new GameSession();
        gameSession.setUserId(userId);
        gameSession.setSoupId(soupId);
        gameSession.setStartTime(LocalDateTime.now());
        gameSession.setStatus(BEGINNING);
        save(gameSession);
        return gameSession;
    }


    /**
     * 停止游戏
     * @param sessionId
     */
    public void stopGame(Long sessionId) {
        // 先查询出实体
        GameSession gameSession = getById(sessionId);
        if (gameSession != null) {
            // 修改字段
            gameSession.setStatus(STOPPED);
            gameSession.setEndTime(LocalDateTime.now());
            // 更新到数据库
            updateById(gameSession);
        }
    }

    /**
     * 用户通关了游戏
     * @return
     */
    public void winGame(Long sessionId) {
        // 先查询出实体
        GameSession gameSession = getById(sessionId);
        if (gameSession != null) {
            // 修改字段
            gameSession.setStatus(SUCCESS);
            gameSession.setEndTime(LocalDateTime.now());
            // 更新到数据库
            updateById(gameSession);
        }
    }

    /**
     * 查看游戏运行状态
     * @param userId
     * @param sessionId
     * @return
     */
    public String getGameStatus(Long userId, Long sessionId) {
        GameSession gameSession = getById(sessionId);
        if (gameSession == null) {
            return null;
        }
        return gameSession.getStatus();
    }


    /**
     * 调用AI问答逻辑
     * @param questionDTO
     * @return
     */
    public String askAi(QuestionDTO questionDTO) {
        Long sessionId = questionDTO.getSessionId();
        String userQuestion = questionDTO.getQuestion();
        
        // 1. 获取会话和题目信息
        GameSession session = getById(sessionId);
        if (session == null) return SESSION_NOT_FOUND;
        Soup soup = soupMapper.selectById(session.getSoupId());
        if (soup == null) return SOUP_NOT_FOUND;
        
        // 2. 获取历史对话
        List<Message> conversationHistory = getConversationHistory(sessionId);
        
        // 3. 组装 system prompt
        String systemPrompt = PromptUtils.getPrompt(soup.getDescription(), soup.getAnswer());
        
        // 4. 组装完整对话
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        
        // 添加历史对话
        messages.addAll(conversationHistory);
        
        // 添加当前问题
        messages.add(new UserMessage(userQuestion));
        
        // 5. 调用 AI (使用 RestTemplate)
        String apiUrl = "https://api.deepseek.com/v1/chat/completions";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", convertToOpenAIFormat(messages));
        requestBody.put("max_tokens", 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = null;
        try {
            response = restTemplate.postForEntity(apiUrl, entity, Map.class);
        } catch (RestClientException e) {
            log.error("调用DeepSeek失败", e);
            throw new BaseException("AI服务异常，请稍后再试");
        }

        // 解析响应
        String aiResponse = parseAIResponse(response.getBody());
        
        // 6. 判断是否成功
        if (aiResponse.startsWith("SUCCESS:")) {
            // 游戏成功，更新会话状态
            session.setStatus(SUCCESS);
            session.setEndTime(LocalDateTime.now());
            updateById(session);
            
            // 保存对话历史
            saveQuestionLog(sessionId, userQuestion, aiResponse);
            
            return aiResponse;
        }
        
        // 7. 保存对话历史
        saveQuestionLog(sessionId, userQuestion, aiResponse);

        log.info(aiResponse);
        
        return aiResponse;
    }

    // 获取对话历史
    private List<Message> getConversationHistory(Long sessionId) {
        List<Message> messages = new ArrayList<>();
        
        // 从 question_log 表查询历史对话
        List<QuestionLog> history = questionLogMapper.selectList(
            new QueryWrapper<QuestionLog>()
                .eq("game_session_id", sessionId)
                .orderByAsc("created_at")
        );
        
        // 转换为 Spring AI 的 Message 格式
        for (QuestionLog log : history) {
            messages.add(new UserMessage(log.getQuestion()));
            messages.add(new AssistantMessage(log.getAiAnswer()));
        }
        
        return messages;
    }

    // 保存对话消息
    private void saveQuestionLog(Long sessionId, String question, String aiAnswer) {
        QuestionLog questionLog = QuestionLog.builder()
            .gameSessionId(sessionId)
            .question(question)
            .aiAnswer(aiAnswer)
            .createdAt(LocalDateTime.now())
            .build();
        
        questionLogMapper.insert(questionLog);
    }

    // 转换消息格式
    private List<Map<String, String>> convertToOpenAIFormat(List<Message> messages) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Message message : messages) {
            Map<String, String> msg = new HashMap<>();
            if (message instanceof SystemMessage) {
                msg.put("role", "system");
            } else if (message instanceof UserMessage) {
                msg.put("role", "user");
            } else if (message instanceof AssistantMessage) {
                msg.put("role", "assistant");
            }
            msg.put("content", message.getText());
            result.add(msg);
        }
        return result;
    }

    // 解析 AI 响应
    private String parseAIResponse(Map responseBody) {
        if (responseBody != null && responseBody.containsKey("choices")) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                return (String) message.get("content");
            }
        }
        return "AI 未能给出有效回答";
    }

    /**
     * 查看历史会话
     * @param sessionId
     * @return
     */
    public List<QuestionLog> getSessionDetail(Long sessionId) {
        // 从 question_log 表查询历史对话
        List<QuestionLog> history = questionLogMapper.selectList(
                new QueryWrapper<QuestionLog>()
                        .eq("game_session_id", sessionId)
                        .orderByAsc("created_at")
        );
        return history;
    }

    /**
     * 查看题目答案
     * @param soupId
     * @return
     */
    public String getAnswer(Long soupId) {
        Soup soup = soupMapper.selectById(soupId);
        if (soup == null) {
            throw new BaseException(SOUP_NOT_FOUND);
        }
        return soup.getAnswer();
    }
}