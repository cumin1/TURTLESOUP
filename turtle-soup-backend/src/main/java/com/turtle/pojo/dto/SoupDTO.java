package com.turtle.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoupDTO {
    private String title;         // 题目标题
    private String description;   // 题目描述（题干）
    private String answer;        // 标准答案
    private Integer difficulty;   // 难度（1-5）
    private List<Long> tagIds;    // 标签ID列表
    private Boolean isPublic;     // 是否公开
}
