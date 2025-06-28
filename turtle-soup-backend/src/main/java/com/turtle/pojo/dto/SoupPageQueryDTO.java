package com.turtle.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SoupPageQueryDTO implements Serializable {
    private int page;
    private int pageSize;
    private String title;         // 题目标题
    private Integer difficulty;   // 难度（1-5）
    private Long tagId;    // 标签ID
}
