package com.turtle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.turtle.pojo.entity.GameSession;
import org.apache.ibatis.annotations.Mapper;
 
@Mapper
public interface GameSessionMapper extends BaseMapper<GameSession> {
    // 可根据需要添加自定义方法
} 