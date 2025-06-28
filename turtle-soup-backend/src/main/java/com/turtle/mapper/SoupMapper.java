package com.turtle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.turtle.pojo.dto.SoupPageQueryDTO;
import com.turtle.pojo.entity.Soup;
import com.turtle.pojo.vo.SoupPageQueryVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SoupMapper extends BaseMapper<Soup> {

    Page<SoupPageQueryVO> getListByPage(SoupPageQueryDTO spqDTO);
}
