package com.lims.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lims.dict.entity.DictTestStandard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DictTestStandardMapper extends BaseMapper<DictTestStandard> {

    @Select("SELECT * FROM dict_test_standard WHERE deleted = 0 AND valid = 1 AND standard_type = #{type} ORDER BY id")
    List<DictTestStandard> selectByType(@Param("type") String type);

    @Select("SELECT s.* FROM dict_test_standard s " +
            "INNER JOIN dict_item_standard is_ ON s.id = is_.standard_id " +
            "WHERE is_.item_id = #{itemId} AND s.deleted = 0 AND s.valid = 1")
    List<DictTestStandard> selectByItemId(@Param("itemId") Long itemId);
}
