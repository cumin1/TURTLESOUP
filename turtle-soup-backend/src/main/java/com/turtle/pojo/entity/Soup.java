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
 * 海龟汤题目实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("soup")
public class Soup implements Serializable {
    // 题目ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 题目标题
    @TableField("title")
    private String title;

    // 题目描述（题干）
    @TableField("description")
    private String description;

    // 标准答案
    @TableField("answer")
    private String answer;

    // 难度（1-5）
    @TableField("difficulty")
    private Integer difficulty;

    // 创建者ID
    @TableField("creator_id")
    private Long creatorId;

    // 创建时间
    @TableField("created_at")
    private LocalDateTime createdAt;

    // 更新时间
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    // 是否公开
    @TableField("is_public")
    private Boolean isPublic;
} 