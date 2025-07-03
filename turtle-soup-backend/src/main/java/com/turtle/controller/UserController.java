package com.turtle.controller;

import com.turtle.common.context.BaseContext;
import com.turtle.common.result.PageResult;
import com.turtle.common.result.Result;
import com.turtle.pojo.dto.UserDTO;
import com.turtle.pojo.dto.UserGamesPageDTO;
import com.turtle.pojo.entity.User;
import com.turtle.pojo.vo.UserLoginVO;
import com.turtle.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户相关接口")
@CrossOrigin(origins = "http://localhost:8080",
        allowedHeaders = "*",
        allowCredentials = "true",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<UserLoginVO> login(@RequestBody UserDTO userDTO){
        log.info("用户登录: {}",userDTO.toString());
        Result<UserLoginVO> result = userService.login(userDTO);
        return result;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result register(@RequestBody UserDTO userDTO,HttpSession session){
        log.info("用户注册: {}",userDTO.toString());
        Result result = userService.register(userDTO,session.getId());
        return result;
    }

    @GetMapping("/mail/{email}")
    @Operation(summary = "发送验证码")
    public Result mail(@PathVariable String email, HttpSession session){
        log.info("发送验证码: {}",email);
        userService.sendEmail(email,session.getId());
        return Result.success();
    }

    @GetMapping("/info")
    @Operation(summary = "查看用户信息")
    public Result info(){
        log.info("查看用户信息");
        Long userId = BaseContext.getCurrentId();
        UserLoginVO user = userService.info(userId);
        return Result.success(user);
    }

    @PostMapping("/games")
    @Operation(summary = "查看用户游玩过的游戏")
    public Result<PageResult> games(@RequestBody UserGamesPageDTO userGamesPageDTO){
        log.info("查看用户游玩过的游戏");
        PageResult pageResult = userService.games(userGamesPageDTO);
        return Result.success(pageResult);
    }
}
