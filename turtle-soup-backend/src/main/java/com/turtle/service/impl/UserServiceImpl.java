package com.turtle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.turtle.common.context.BaseContext;
import com.turtle.common.exception.EmailException;
import com.turtle.common.exception.UserException;
import com.turtle.common.result.PageResult;
import com.turtle.common.result.Result;
import com.turtle.common.utils.JwtUtils;
import com.turtle.common.utils.RegexUtils;
import com.turtle.mapper.GameSessionMapper;
import com.turtle.mapper.UserMapper;
import com.turtle.pojo.dto.UserDTO;
import com.turtle.pojo.dto.UserGamesPageDTO;
import com.turtle.pojo.entity.GameSession;
import com.turtle.pojo.entity.User;
import com.turtle.pojo.vo.GamePageQueryVO;
import com.turtle.pojo.vo.UserLoginVO;
import com.turtle.service.UserService;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.turtle.common.constant.MessageConstant.*;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private GameSessionMapper gameSessionMapper;

    @Value("${spring.mail.username}")
    String From;

    @Autowired
    JavaMailSender MailSender;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码
     * @param email
     */
    public void sendEmail(String email,String sessionId) {
        String key = "email:" + sessionId + ":" + email;
        // 如果一分钟内已经发送过验证码 则不允许再次发送
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Long expire = Optional.ofNullable(redisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);   // 获取该key剩余时间
            if (expire > 120) {
                throw new EmailException(EMAIL_SEND_TOO_MUCH);
            }
        }
        // 验证邮箱
        if(!RegexUtils.isValidEmail(email)){
            throw new EmailException(EMAIL_ERROR);
        }
        // 生成验证码
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;

        // 发送验证码
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("欢迎游玩AI海龟汤！");
        message.setText("验证码为：" + code);
        message.setTo(email);  // 设置目标邮件地址
        message.setFrom(From);  //设置邮件发送地址
        try {
            MailSender.send(message);   //发送！
            // 将sessionid和邮件地址存入Redis  设置过期时间 1分钟
            redisTemplate.opsForValue().set(key, String.valueOf(code), 2, TimeUnit.MINUTES);
        } catch (MailException e) {
            throw new EmailException(EMAIL_SEND_ERROR);
        }
    }

    /**
     * 用户注册
     * @param userDTO
     * @return
     */
    public Result register(UserDTO userDTO,String sessionId) {
        // 判断邮箱是否合法
        if(!RegexUtils.isValidEmail(userDTO.getEmail())){return Result.error(EMAIL_ERROR);}

        // 判断数据库是否存在相同的用户名和邮箱
        // todo 存在线程安全问题 后期需要加锁
        Long count1 = query().eq("username", userDTO.getUsername()).count();
        if (count1 > 0){return Result.error(USERNAME_EXIST);}
        Long count2 = query().eq("email", userDTO.getEmail()).count();
        if (count2 > 0){return Result.error(EMAIL_EXIST);}

        // 验证验证码是否正确
        String key = "email:" + sessionId + ":" + userDTO.getEmail();
        String codeInRedis = (String)redisTemplate.opsForValue().get(key);
        log.info("code in redis: {}",codeInRedis);
        if (codeInRedis == null){
            throw new UserException(CODE_DELAY);
        }
        if (!codeInRedis.equals(userDTO.getCode())){
            throw new UserException(CODE_ERROR);
        }

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

    /**
     * 查看用户信息
     * @param userId
     * @return
     */
    public UserLoginVO info(Long userId) {
        User user = getById(userId);
        if (user == null){
            throw new UserException(USER_NOT_FOUND);
        }
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(user,userLoginVO);
        return userLoginVO;
    }


    /**
     * 查看用户游玩过的游戏
     * @param userGamesPageDTO
     * @return
     */
    public PageResult games(UserGamesPageDTO userGamesPageDTO) {
        PageHelper.startPage(userGamesPageDTO.getPage(),userGamesPageDTO.getPageSize());
        Page<GamePageQueryVO> games = gameSessionMapper.selectGamesList(BaseContext.getCurrentId(),userGamesPageDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(games.getTotal());
        pageResult.setRecords(games.getResult());
        return pageResult;
    }
}
