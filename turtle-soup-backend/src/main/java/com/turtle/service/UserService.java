package com.turtle.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.turtle.common.result.Result;
import com.turtle.pojo.dto.UserDTO;
import com.turtle.pojo.entity.User;

public interface UserService extends IService<User> {
    // 用户注册
    Result register(UserDTO userDTO);
}
