package com.turtle.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("soup_tag")
public class SoupTag implements Serializable {
    @TableField("soup_id")
    private Long soupId;
    @TableField("tag_id")
    private Long tagId;
}
