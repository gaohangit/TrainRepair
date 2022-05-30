package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import lombok.Data;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/22 18:07
 **/
@Data
public class PostModel {

    /**
     * 岗位ID
     */
    private String postId;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 小组编码
     */
    private String branchCode;

    /**
     * 职工id
     */
    private String staffId;

    /**
     * 职工姓名
     */
    private String staffName;

    /**
     * 运用所编码
     */
    private String unitCode;
}
