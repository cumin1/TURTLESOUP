package com.turtle.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话历史实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("question_log")
public class QuestionLog {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("game_session_id")
    private Long gameSessionId;

    @TableField("question")
    private String question;

    @TableField("ai_answer")
    private String aiAnswer;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
