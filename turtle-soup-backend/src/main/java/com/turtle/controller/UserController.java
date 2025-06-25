package com.turtle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@Tag(name = "用户相关接口")
public class UserController {

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public String login(){
        return "yes";
    }
}
