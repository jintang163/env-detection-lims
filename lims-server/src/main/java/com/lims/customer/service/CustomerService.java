package com.lims.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.customer.dto.*;
import com.lims.customer.entity.CusCustomer;
import com.lims.customer.entity.CusCustomerCredit;
import com.lims.customer.entity.CusCustomerFollow;
import com.lims.customer.entity.CusCustomerQualification;
import com.lims.customer.vo.CustomerDetailVO;
import com.lims.customer.vo.CustomerVO;

import java.util.List;

public interface CustomerService extends IService<CusCustomer> {

    PageResult<CustomerVO> page(CustomerQuery query);

    void save(CustomerSaveDTO dto);

    void update(CustomerSaveDTO dto);

    void delete(Long id);

    CustomerDetailVO getDetail(Long id);

    void moveToPublic(Long id, String reason);

    void removeFromPublic(Long id, Long ownerId, String ownerName);

    void adjustLevel(Long id, Integer level);

    void adjustCredit(CustomerCreditSaveDTO dto);

    void saveQualification(CustomerQualificationSaveDTO dto);

    void updateQualification(CustomerQualificationSaveDTO dto);

    void deleteQualification(Long id);

    List<CusCustomerQualification> getQualificationList(Long customerId);

    void saveFollow(CustomerFollowSaveDTO dto);

    void updateFollow(CustomerFollowSaveDTO dto);

    void deleteFollow(Long id);

    List<CusCustomerFollow> getFollowList(Long customerId);

    void claimPublicCustomer(Long id);

    void assignCustomer(Long id, Long ownerId, String ownerName);

    List<CusCustomerCredit> getCreditList(Long customerId);
}
