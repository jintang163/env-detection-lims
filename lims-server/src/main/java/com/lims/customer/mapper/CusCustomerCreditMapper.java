package com.lims.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lims.customer.entity.CusCustomerCredit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CusCustomerCreditMapper extends BaseMapper<CusCustomerCredit> {

    @Select("SELECT * FROM cus_customer_credit WHERE deleted = 0 AND customer_id = #{customerId} ORDER BY operate_time DESC")
    List<CusCustomerCredit> selectByCustomerId(@Param("customerId") Long customerId);

    @Select("SELECT COALESCE(SUM(CASE WHEN change_type = 1 THEN change_score ELSE -change_score END), 0) " +
            "FROM cus_customer_credit WHERE deleted = 0 AND customer_id = #{customerId}")
    Integer selectTotalScoreByCustomerId(@Param("customerId") Long customerId);
}
