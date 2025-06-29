package com.turtle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turtle.common.result.PageResult;
import com.turtle.common.result.Result;
import com.turtle.pojo.dto.SoupDTO;
import com.turtle.pojo.dto.SoupPageQueryDTO;
import com.turtle.pojo.entity.Soup;

public interface SoupService extends IService<Soup> {
    // 上传题目
    Result uploadSoup(SoupDTO soupDTO);

    // 查找题目列表
    PageResult getSoupListByPage(SoupPageQueryDTO soupPageQueryDTO);

    // 根据ID查询题目
    Result<Soup> getSoupById(Long id);
}
