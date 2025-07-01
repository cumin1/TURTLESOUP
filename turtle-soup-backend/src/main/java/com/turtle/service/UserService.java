package com.turtle.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.turtle.common.result.Result;
import com.turtle.pojo.dto.UserDTO;
import com.turtle.pojo.entity.User;
import com.turtle.pojo.vo.UserLoginVO;

public interface UserService extends IService<User> {
    // 用户注册
    Result register(UserDTO userDTO);

    // 用户登录
    Result<UserLoginVO> login(UserDTO userDTO);

    // 查看用户信息
    UserLoginVO info(Long userId);
}
