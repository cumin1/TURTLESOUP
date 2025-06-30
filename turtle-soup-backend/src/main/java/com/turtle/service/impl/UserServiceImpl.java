package com.turtle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turtle.common.result.Result;
import com.turtle.common.utils.JwtUtils;
import com.turtle.common.utils.RegexUtils;
import com.turtle.mapper.UserMapper;
import com.turtle.pojo.dto.UserDTO;
import com.turtle.pojo.entity.User;
import com.turtle.pojo.vo.UserLoginVO;
import com.turtle.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        save(user);
        return Result.success();
    }

    /**
     * 用户登录
     * @param userDTO
     * @return
     */
    public Result<UserLoginVO> login(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String email = userDTO.getEmail();
        String password = DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes());
        // 校验登录参数
        if ((username == null || username.isEmpty()) && (email == null || email.isEmpty())
                || password == null
                || password.isEmpty()
                || username == ""
                || email == ""
                || password == ""
        ) {
            return Result.error(LOGIN_ERROR);
        }
        User user = query().eq("username", username).one();
        if (user == null) {
            user = query().eq("email", email).one();
            if (user == null){
                return Result.error(USER_NOT_FOUND);
            }
        }
        String passwordInDataBase = user.getPassword();
        if (!password.equals(passwordInDataBase)){
            return Result.error(PASSWORD_ERROR);
        }
        user.setLastLogin(LocalDateTime.now());
        saveOrUpdate(user); // 更新上次登录时间
        Long userId = user.getId();
        username = user.getUsername();
        String token = JwtUtils.generateToken(userId, username);
        UserLoginVO loginVo = UserLoginVO.builder()
                .id(userId)
                .username(username)
                .token(token)
                .avatar(user.getAvatarUrl())
                .build();
        return Result.success(loginVo);
    }
}
