package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ApiModel("人员查询条件")
public class PersonnelQuery {

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("员工编号")
    private String employeeNo;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页条数")
    private Integer pageSize;
}
