package com.lims.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.customer.entity.CusCustomer;
import com.lims.customer.vo.CustomerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CusCustomerMapper extends BaseMapper<CusCustomer> {

    @Select("SELECT c.*, d.dept_name as deptName " +
            "FROM cus_customer c " +
            "LEFT JOIN sys_dept d ON c.dept_id = d.id " +
            "WHERE c.deleted = 0 " +
            "AND (#{customerName} IS NULL OR c.customer_name LIKE CONCAT('%', #{customerName}, '%')) " +
            "AND (#{customerType} IS NULL OR c.customer_type = #{customerType}) " +
            "AND (#{customerLevel} IS NULL OR c.customer_level = #{customerLevel}) " +
            "AND (#{isPublic} IS NULL OR c.is_public = #{isPublic}) " +
            "AND (#{customerStatus} IS NULL OR c.customer_status = #{customerStatus}) " +
            "AND (#{ownerId} IS NULL OR c.owner_id = #{ownerId}) " +
            "AND (#{deptId} IS NULL OR c.dept_id = #{deptId}) " +
            "ORDER BY c.create_time DESC")
    IPage<CustomerVO> selectCustomerPage(Page<CustomerVO> page,
                                         @Param("customerName") String customerName,
                                         @Param("customerType") Integer customerType,
                                         @Param("customerLevel") Integer customerLevel,
                                         @Param("isPublic") Integer isPublic,
                                         @Param("customerStatus") Integer customerStatus,
                                         @Param("ownerId") Long ownerId,
                                         @Param("deptId") Long deptId);

    @Select("SELECT DISTINCT c.customer_name FROM cus_customer c WHERE c.deleted = 0 AND c.customer_name LIKE CONCAT('%', #{keyword}, '%') LIMIT 20")
    List<String> selectCustomerNames(@Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM cus_customer WHERE deleted = 0 AND customer_no = #{customerNo} AND id != #{excludeId}")
    Long countByCustomerNo(@Param("customerNo") String customerNo, @Param("excludeId") Long excludeId);
}
