package com.turtle.controller;


import com.turtle.common.result.PageResult;
import com.turtle.common.result.Result;
import com.turtle.pojo.dto.SoupDTO;
import com.turtle.pojo.dto.SoupPageQueryDTO;
import com.turtle.pojo.entity.Soup;
import com.turtle.service.SoupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/soup")
@Tag(name = "题目相关接口")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SoupController {

    @Autowired
    private SoupService soupService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @Operation(summary = "上传题目")
    public Result uploadSoup(@RequestBody SoupDTO soupDTO) {
        log.info("上传题目: {}", soupDTO.toString());
        Result result = soupService.uploadSoup(soupDTO);
        // 删除缓存
        Set<String> keys = redisTemplate.keys("soup:page:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return result;
    }

    @PostMapping("/list")
    @Operation(summary = "查看题目列表")
    public Result<PageResult> getSoupList(@RequestBody SoupPageQueryDTO soupPageQueryDTO) {
        log.info("查看题目列表: {}", soupPageQueryDTO.toString());
        // 加缓存 key soup:page:{page}:{pageSize}:{difficulty}:{tag}:{keyword} 其中参数为 null 时用空字符串或“”占位。
        String key = buildCacheKey(soupPageQueryDTO);
        // 1. 先查缓存
        PageResult pageResult = (PageResult) redisTemplate.opsForValue().get(key);
        if (pageResult != null) {
            return Result.success(pageResult);
        }
        // 2. 查数据库
        pageResult = soupService.getSoupListByPage(soupPageQueryDTO);
        // 3. 放入缓存
        redisTemplate.opsForValue().set(key, pageResult, 5, TimeUnit.MINUTES);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询题目")
    public Result<Soup> getSoupById(@PathVariable("id") Long id) {
        log.info("查询id为:{}的题目",id);
        return soupService.getSoupById(id);
    }

    // 拼接redis的key
    private String buildCacheKey(SoupPageQueryDTO dto) {
        return String.format(
                "soup:page:%d:%d:%s:%s:%s",
                dto.getPage(),
                dto.getPageSize(),
                dto.getDifficulty() == null ? "" : dto.getDifficulty(),
                dto.getTagId() == null ? "" : dto.getTagId(),
                dto.getTitle() == null ? "" : dto.getTitle()
        );
    }
}
