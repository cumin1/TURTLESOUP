package com.turtle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turtle.pojo.dto.QuestionDTO;
import com.turtle.pojo.entity.GameSession;
import com.turtle.pojo.entity.QuestionLog;

import java.util.List;

public interface GameService extends IService<GameSession> {
    GameSession startGame(Long userId, Long soupId);

    void stopGame(Long sessionId);

    String getGameStatus(Long userId, Long sessionId);

    String askAi(QuestionDTO question);

    void winGame(Long sessionId);

    List<QuestionLog> getSessionDetail(Long sessionId);

    String getAnswer(Long soupId);
}