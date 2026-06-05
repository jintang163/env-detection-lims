package com.lims.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lims.customer.entity.CusCustomerQualification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CusCustomerQualificationMapper extends BaseMapper<CusCustomerQualification> {

    @Select("SELECT * FROM cus_customer_qualification WHERE deleted = 0 AND customer_id = #{customerId} ORDER BY create_time DESC")
    List<CusCustomerQualification> selectByCustomerId(@Param("customerId") Long customerId);

    @Select("SELECT * FROM cus_customer_qualification WHERE deleted = 0 AND customer_id = #{customerId} AND qual_type = #{qualType}")
    List<CusCustomerQualification> selectByCustomerIdAndType(@Param("customerId") Long customerId, @Param("qualType") Integer qualType);
}
