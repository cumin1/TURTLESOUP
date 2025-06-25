package com.turtle.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turtle.common.result.Result;
import com.turtle.common.utils.RegexUtils;
import com.turtle.mapper.UserMapper;
import com.turtle.pojo.dto.UserDTO;
import com.turtle.pojo.entity.User;
import com.turtle.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.turtle.common.constant.MessageConstant.*;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * 用户注册
     * @param userDTO
     * @return
     */
    public Result register(UserDTO userDTO) {
        // 判断邮箱是否合法
        if(!RegexUtils.isValidEmail(userDTO.getEmail())){return Result.error(EMAIL_ERROR);}
        // 判断数据库是否存在相同的用户名和邮箱
        // todo 存在线程安全问题 后期需要加锁
        Long count1 = query().eq("username", userDTO.getUsername()).count();
        if (count1 > 0){return Result.error(USERNAME_EXIST);}
        Long count2 = query().eq("email", userDTO.getEmail()).count();
        if (count2 > 0){return Result.error(EMAIL_EXIST);}
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);
        user.setCreatedAt(LocalDateTime.now());
        save(user);
        return Result.success();
    }
}
