package com.lims.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lims.customer.entity.CusCustomerFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CusCustomerFollowMapper extends BaseMapper<CusCustomerFollow> {

    @Select("SELECT * FROM cus_customer_follow WHERE deleted = 0 AND customer_id = #{customerId} ORDER BY follow_time DESC")
    List<CusCustomerFollow> selectByCustomerId(@Param("customerId") Long customerId);

    @Select("SELECT * FROM cus_customer_follow WHERE deleted = 0 AND follow_by = #{followBy} ORDER BY follow_time DESC LIMIT 10")
    List<CusCustomerFollow> selectRecentByFollowBy(@Param("followBy") Long followBy);
}
