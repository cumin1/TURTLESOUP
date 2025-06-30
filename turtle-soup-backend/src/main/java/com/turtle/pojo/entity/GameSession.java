package com.turtle.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 游戏会话实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("game_session")
public class GameSession implements Serializable {
    // 会话ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 用户ID
    @TableField("user_id")
    private Long userId;

    // 题目ID
    @TableField("soup_id")
    private Long soupId;

    // 开始时间
    @TableField("start_time")
    private LocalDateTime startTime;

    // 状态（进行中/已结束）
    @TableField("status")
    private String status;

    // 已用提示数
    @TableField("used_hint_cnt")
    private Integer usedHintCnt;

    // 结束时间
    @TableField("end_time")
    private LocalDateTime endTime;
} 