package com.turtle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.turtle.pojo.entity.SoupTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SoupTagMapper extends BaseMapper<SoupTag> {
    List<SoupTag> selectListById(Long soupId);
}
