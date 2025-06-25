package com.turtle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turtle.common.result.Result;
import com.turtle.mapper.UserMapper;
import com.turtle.pojo.dto.UserDTO;
import com.turtle.pojo.entity.User;
import com.turtle.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * 用户注册
     * @param userDTO
     * @return
     */
    public Result register(UserDTO userDTO) {
        User user1 = getById(1);
        System.out.println(user1);
        return null;
    }
}
