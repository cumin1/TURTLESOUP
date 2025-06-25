package com.turtle.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    // 用户名（唯一）
    private String username;
    // 密码哈希
    private String password;
    // 电子邮箱（唯一）
    private String email;
}
