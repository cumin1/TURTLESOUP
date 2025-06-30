package com.turtle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turtle.common.exception.GameSessionException;
import com.turtle.mapper.GameSessionMapper;
import com.turtle.pojo.entity.GameSession;
import com.turtle.service.GameService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.turtle.common.constant.MessageConstant.SESSION_NOT_FOUND;
import static com.turtle.common.constant.StatusConstant.BEGINNING;
import static com.turtle.common.constant.StatusConstant.STOPPED;

@Service
public class GameServiceImpl extends ServiceImpl<GameSessionMapper, GameSession>  implements GameService {
    /**
     * 用户开始游戏
     * @param userId
     * @param soupId
     * @return
     */
    public GameSession startGame(Long userId, Long soupId) {
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

    @Override
    public String askAi(Long gameSessionId, String question) {
        // TODO: 调用AI问答逻辑
        return "AI回答: " + question;
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
}