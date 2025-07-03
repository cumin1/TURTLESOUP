package com.turtle.controller;

import com.turtle.common.context.BaseContext;
import com.turtle.common.result.Result;
import com.turtle.pojo.dto.QuestionDTO;
import com.turtle.pojo.entity.GameSession;
import com.turtle.pojo.entity.QuestionLog;
import com.turtle.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "游戏相关接口")
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * 用户进入游戏，创建game_session
     */
    @PostMapping("/start")
    @Operation(summary = "开始游戏")
    public Result<GameSession> startGame(@RequestParam Long soupId) {
        log.info("用户: {},开始了题目: {}", BaseContext.getCurrentId(), soupId);
        GameSession gameSession = gameService.startGame(BaseContext.getCurrentId(), soupId);
        return Result.success(gameSession);
    }

    /**
     * 用户停止游戏
     * @return
     */
    @GetMapping("/stop")
    @Operation(summary = "结束游戏")
    public Result stopGame(@RequestParam Long sessionId) {
        log.info("用户: {},停止了游戏: {}", BaseContext.getCurrentId(), sessionId);
        gameService.stopGame(sessionId);
        return Result.success();
    }

    /**
     * 用户通关了游戏
     * @return
     */
    @GetMapping("/success")
    @Operation(summary = "通关游戏")
    public Result winGame(@RequestParam Long sessionId) {
        log.info("用户: {},通关了游戏: {}", BaseContext.getCurrentId(), sessionId);
        gameService.winGame(sessionId);
        return Result.success();
    }

    @GetMapping("/status")
    @Operation(summary = "查看该用户游戏状态")
    public Result getGameStatus(@RequestParam Long sessionId) {
        log.info("查看用户{}的游戏状态:{}", BaseContext.getCurrentId(), sessionId);
        String status = gameService.getGameStatus(BaseContext.getCurrentId(), sessionId);
        return Result.success(status);
    }

    /**
     * AI 问答接口
     */
    @PostMapping("/ask")
    @Operation(summary = "AI问答接口")
    public Result askAi(@RequestBody QuestionDTO question) {
        log.info("用户提问: {}",question.toString());
        String resp = gameService.askAi(question);
        return Result.success(resp);
    }

    /**
     * 查看历史会话
     */
    @GetMapping("/sessionDetail/{sessionId}")
    @Operation(summary = "查看历史会话")
    public Result getSessionDetail(@PathVariable Long sessionId) {
        log.info("查看历史会话: {}",sessionId);
        List<QuestionLog> questionLogList = gameService.getSessionDetail(sessionId);
        return Result.success(questionLogList);
    }

    /**
     * 查看题目答案
     */
    @GetMapping("/answer/{soupId}")
    @Operation(summary = "查看题目答案")
    public Result getAnswer(@PathVariable Long soupId) {
        log.info("查看题目答案: {}",soupId);
        String answer = gameService.getAnswer(soupId);
        return Result.success(answer);
    }


} 