package com.lims.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lims.dict.entity.DictItemStandard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DictItemStandardMapper extends BaseMapper<DictItemStandard> {

    @Select("SELECT * FROM dict_item_standard WHERE deleted = 0 AND item_id = #{itemId}")
    List<DictItemStandard> selectByItemId(@Param("itemId") Long itemId);

    @Select("SELECT * FROM dict_item_standard WHERE deleted = 0 AND standard_id = #{standardId}")
    List<DictItemStandard> selectByStandardId(@Param("standardId") Long standardId);
}
