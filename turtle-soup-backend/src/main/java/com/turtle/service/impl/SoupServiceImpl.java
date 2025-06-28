package com.turtle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.turtle.common.context.BaseContext;
import com.turtle.common.result.PageResult;
import com.turtle.common.result.Result;
import com.turtle.mapper.SoupMapper;
import com.turtle.mapper.SoupTagMapper;
import com.turtle.pojo.dto.SoupDTO;
import com.turtle.pojo.dto.SoupPageQueryDTO;
import com.turtle.pojo.entity.Soup;
import com.turtle.pojo.entity.SoupTag;
import com.turtle.pojo.vo.SoupPageQueryVO;
import com.turtle.service.SoupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SoupServiceImpl extends ServiceImpl<SoupMapper, Soup> implements SoupService {

    @Autowired
    private SoupTagMapper soupTagMapper;
    @Autowired
    private SoupMapper soupMapper;

    /**
     * 上传题目
     * @param soupDTO
     * @return
     */
    @Transactional
    public Result uploadSoup(SoupDTO soupDTO) {
        // 操作soup表
        Soup soup = new Soup();
        BeanUtils.copyProperties(soupDTO, soup);
        soup.setCreatorId(BaseContext.getCurrentId());
        soup.setCreatedAt(LocalDateTime.now());
        soup.setUpdatedAt(LocalDateTime.now());
        save(soup);
        // 操作soup_tag表
        Long soupId = soup.getId();
        log.info("soupId:{}", soupId);
        List<Long> tagIds = soupDTO.getTagIds();
        for (Long tagId : tagIds) {
            SoupTag soupTag = new SoupTag();
            soupTag.setSoupId(soupId);
            soupTag.setTagId(tagId);
            soupTagMapper.insert(soupTag);
        }
        return Result.success("插入题目成功！");
    }

    /**
     * 查找题目列表
     * @param spqDTO
     * @return
     */
    public PageResult getSoupListByPage(SoupPageQueryDTO spqDTO) {
        PageHelper.startPage(spqDTO.getPage(),spqDTO.getPageSize());
        Page<SoupPageQueryVO> page = soupMapper.getListByPage(spqDTO);
        for (SoupPageQueryVO soupPageQueryVO : page) {
            Long soupId = soupPageQueryVO.getId();
            List<SoupTag> soupTagList = soupTagMapper.selectListById(soupId);
            List<Long> tagIds = new ArrayList<>();
            for (SoupTag soupTag : soupTagList) {
                tagIds.add(soupTag.getTagId());
            }
            soupPageQueryVO.setTagIds(tagIds);
        }
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }
}
