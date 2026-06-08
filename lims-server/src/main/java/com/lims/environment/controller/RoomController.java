package com.lims.environment.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.environment.dto.EnvRoomQuery;
import com.lims.environment.dto.EnvRoomSaveDTO;
import com.lims.environment.service.RoomService;
import com.lims.environment.vo.EnvRoomVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "实验室房间管理")
@RestController
@RequestMapping("/environment/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @ApiOperation("分页查询房间列表")
    @GetMapping("/page")
    public Result<PageResult<EnvRoomVO>> selectPage(EnvRoomQuery query) {
        return Result.success(roomService.selectPage(query));
    }

    @ApiOperation("获取所有正常房间列表")
    @GetMapping("/list")
    public Result<List<EnvRoomVO>> getNormalRoomList() {
        return Result.success(roomService.getNormalRoomList());
    }

    @ApiOperation("获取房间详情")
    @GetMapping("/{id}")
    public Result<EnvRoomVO> getRoomDetail(@PathVariable Long id) {
        return Result.success(roomService.getRoomDetail(id));
    }

    @ApiOperation("新增房间")
    @PostMapping
    public Result<Void> addRoom(@RequestBody @Validated EnvRoomSaveDTO dto) {
        roomService.addRoom(dto);
        return Result.success();
    }

    @ApiOperation("修改房间")
    @PutMapping
    public Result<Void> updateRoom(@RequestBody @Validated EnvRoomSaveDTO dto) {
        roomService.updateRoom(dto);
        return Result.success();
    }

    @ApiOperation("删除房间")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return Result.success();
    }
}
