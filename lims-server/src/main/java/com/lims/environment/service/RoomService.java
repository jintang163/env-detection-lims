package com.lims.environment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.environment.dto.EnvRoomQuery;
import com.lims.environment.dto.EnvRoomSaveDTO;
import com.lims.environment.entity.EnvRoom;
import com.lims.environment.vo.EnvRoomVO;

import java.util.List;

public interface RoomService extends IService<EnvRoom> {

    PageResult<EnvRoomVO> selectPage(EnvRoomQuery query);

    void addRoom(EnvRoomSaveDTO dto);

    void updateRoom(EnvRoomSaveDTO dto);

    void deleteRoom(Long id);

    EnvRoomVO getRoomDetail(Long id);

    List<EnvRoomVO> getRoomList();
}
