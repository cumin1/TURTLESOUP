package com.turtle.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamePageQueryVO {

    // 会话ID
    private Long sessionId;

    // 题目ID
    private Long soupId;

    // 题目标题
    private String title;

    // 题目描述（题干）
    private String description;

    // 难度（1-5）
    private Integer difficulty;

    // 开始时间
    private LocalDateTime startTime;

    // 结束时间
    private LocalDateTime endTime;

    // 玩家游玩状态
    private String status;
}
