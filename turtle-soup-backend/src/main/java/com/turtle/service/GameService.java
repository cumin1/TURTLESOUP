package com.turtle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turtle.pojo.entity.GameSession;

public interface GameService extends IService<GameSession> {
    GameSession startGame(Long userId, Long soupId);

    String askAi(Long gameSessionId, String question);

    void stopGame(Long sessionId);

    String getGameStatus(Long userId, Long sessionId);
}