package com.lims.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lims.dict.entity.DictTestItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DictTestItemMapper extends BaseMapper<DictTestItem> {

    @Select("SELECT * FROM dict_test_item WHERE deleted = 0 AND item_category = #{category} ORDER BY id")
    List<DictTestItem> selectByCategory(@Param("category") String category);

    @Select("SELECT * FROM dict_test_item WHERE deleted = 0 AND (item_name LIKE CONCAT('%', #{keyword}, '%') OR item_code LIKE CONCAT('%', #{keyword}, '%')) LIMIT 20")
    List<DictTestItem> selectByKeyword(@Param("keyword") String keyword);
}
