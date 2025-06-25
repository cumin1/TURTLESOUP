package com.turtle.controller;

import com.turtle.common.result.Result;
import com.turtle.pojo.dto.UserDTO;
import com.turtle.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@Tag(name = "用户相关接口")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public String login(){
        return "yes";
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public String register(@RequestBody UserDTO userDTO){
        log.info("用户注册: {}",userDTO.toString());
        Result result = userService.register(userDTO);
        return null;
    }
}
