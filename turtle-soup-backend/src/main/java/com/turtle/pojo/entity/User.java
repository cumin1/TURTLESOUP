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
 * 用户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User implements Serializable {

    // 用户ID
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    // 用户名（唯一）
    @TableField("username")
    private String username;

    // 密码
    @TableField("password")
    private String password;

    // 电子邮箱（唯一）
    @TableField("email")
    private String email;

    // 头像URL
    @TableField("avatar_url")
    private String avatarUrl;

    // 创建时间
    @TableField("created_at")
    private LocalDateTime createdAt;

    // 最后登录时间
    @TableField("last_login")
    private LocalDateTime lastLogin;
}
