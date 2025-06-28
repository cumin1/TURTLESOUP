package com.turtle.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoupPageQueryVO implements Serializable {
    // 题目ID
    private Long id;

    // 题目标题
    private String title;

    // 题目描述（题干）
    private String description;

    // 标准答案
    private String answer;

    // 难度（1-5）
    private Integer difficulty;

    // 题目分类
    private List<Long> tagIds;
}
