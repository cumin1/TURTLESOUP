package com.turtle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turtle.common.result.Result;
import com.turtle.pojo.dto.SoupDTO;
import com.turtle.pojo.entity.Soup;

public interface SoupService extends IService<Soup> {
    // 上传题目
    Result uploadSoup(SoupDTO soupDTO);
}
