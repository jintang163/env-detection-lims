package com.lims.environment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.environment.dto.EnvRoomQuery;
import com.lims.environment.dto.EnvRoomSaveDTO;
import com.lims.environment.entity.EnvRoom;
import com.lims.environment.mapper.EnvRoomMapper;
import com.lims.environment.service.RoomService;
import com.lims.environment.vo.EnvRoomVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl extends ServiceImpl<EnvRoomMapper, EnvRoom> implements RoomService {

    @Autowired
    private EnvRoomMapper envRoomMapper;

    private static final int STATUS_DISABLED = 0;
    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_MAINTENANCE = 2;

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "停用";
            case 1: return "正常";
            case 2: return "维护中";
            default: return "";
        }
    }

    private String getRoomTypeName(String roomType) {
        if (roomType == null) return "";
        switch (roomType) {
            case "1": return "理化实验室";
            case "2": return "微生物实验室";
            case "3": return "仪器室";
            case "4": return "试剂室";
            case "5": return "样品室";
            case "6": return "气瓶室";
            case "7": return "办公室";
            default: return "";
        }
    }

    @Override
    public PageResult<EnvRoomVO> selectPage(EnvRoomQuery query) {
        LambdaQueryWrapper<EnvRoom> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getRoomNo())) {
            wrapper.like(EnvRoom::getRoomNo, query.getRoomNo());
        }
        if (StrUtil.isNotBlank(query.getRoomName())) {
            wrapper.like(EnvRoom::getRoomName, query.getRoomName());
        }
        if (StrUtil.isNotBlank(query.getBuilding())) {
            wrapper.like(EnvRoom::getBuilding, query.getBuilding());
        }
        if (StrUtil.isNotBlank(query.getFloor())) {
            wrapper.like(EnvRoom::getFloor, query.getFloor());
        }
        if (StrUtil.isNotBlank(query.getRoomType())) {
            wrapper.eq(EnvRoom::getRoomType, query.getRoomType());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(EnvRoom::getDeptId, query.getDeptId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(EnvRoom::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(EnvRoom::getCreateTime);

        IPage<EnvRoom> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<EnvRoomVO> voList = page.getRecords().stream().map(entity -> {
            EnvRoomVO vo = BeanUtil.copyProperties(entity, EnvRoomVO.class);
            vo.setStatusName(getStatusName(entity.getStatus()));
            vo.setRoomTypeName(getRoomTypeName(entity.getRoomType()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoom(EnvRoomSaveDTO dto) {
        LambdaQueryWrapper<EnvRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnvRoom::getRoomNo, dto.getRoomNo());
        if (this.count(wrapper) > 0) {
            throw new BizException("房间编号已存在");
        }
        EnvRoom room = BeanUtil.copyProperties(dto, EnvRoom.class);
        this.save(room);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoom(EnvRoomSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("房间ID不能为空");
        }
        EnvRoom existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BizException("房间不存在");
        }
        if (!existing.getRoomNo().equals(dto.getRoomNo())) {
            LambdaQueryWrapper<EnvRoom> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EnvRoom::getRoomNo, dto.getRoomNo());
            wrapper.ne(EnvRoom::getId, dto.getId());
            if (this.count(wrapper) > 0) {
                throw new BizException("房间编号已存在");
            }
        }
        EnvRoom room = BeanUtil.copyProperties(dto, EnvRoom.class);
        this.updateById(room);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(Long id) {
        EnvRoom room = this.getById(id);
        if (room == null) {
            throw new BizException("房间不存在");
        }
        this.removeById(id);
    }

    @Override
    public EnvRoomVO getRoomDetail(Long id) {
        EnvRoom room = this.getById(id);
        if (room == null) {
            throw new BizException("房间不存在");
        }
        EnvRoomVO vo = BeanUtil.copyProperties(room, EnvRoomVO.class);
        vo.setStatusName(getStatusName(room.getStatus()));
        vo.setRoomTypeName(getRoomTypeName(room.getRoomType()));
        return vo;
    }

    @Override
    public List<EnvRoomVO> getRoomList() {
        LambdaQueryWrapper<EnvRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnvRoom::getStatus, STATUS_NORMAL);
        wrapper.orderByAsc(EnvRoom::getRoomNo);
        List<EnvRoom> list = this.list(wrapper);
        return list.stream().map(entity -> {
            EnvRoomVO vo = BeanUtil.copyProperties(entity, EnvRoomVO.class);
            vo.setStatusName(getStatusName(entity.getStatus()));
            vo.setRoomTypeName(getRoomTypeName(entity.getRoomType()));
            return vo;
        }).collect(Collectors.toList());
    }
}
