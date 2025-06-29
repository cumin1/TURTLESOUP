package com.turtle.controller;


import com.turtle.common.result.PageResult;
import com.turtle.common.result.Result;
import com.turtle.pojo.dto.SoupDTO;
import com.turtle.pojo.dto.SoupPageQueryDTO;
import com.turtle.service.SoupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/soup")
@Tag(name = "题目相关接口")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SoupController {

    @Autowired
    private SoupService soupService;

    @PostMapping
    @Operation(summary = "上传题目")
    public Result uploadSoup(@RequestBody SoupDTO soupDTO) {
        log.info("上传题目: {}", soupDTO.toString());
        Result result = soupService.uploadSoup(soupDTO);
        return result;
    }

    @PostMapping("/list")
    @Operation(summary = "查看题目列表")
    public Result<PageResult> getSoupList(@RequestBody SoupPageQueryDTO soupPageQueryDTO) {
        log.info("查看题目列表: {}", soupPageQueryDTO.toString());
        PageResult pageResult = soupService.getSoupListByPage(soupPageQueryDTO);
        return Result.success(pageResult);
    }
}
