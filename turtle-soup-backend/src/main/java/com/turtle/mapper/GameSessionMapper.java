package com.turtle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.turtle.pojo.dto.UserGamesPageDTO;
import com.turtle.pojo.entity.GameSession;
import com.turtle.pojo.vo.GamePageQueryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GameSessionMapper extends BaseMapper<GameSession> {
    Page<GamePageQueryVO> selectGamesList(Long userId,UserGamesPageDTO userGamesPageDTO);
} 