package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询一二级修回填参数对象
 *
 * @author 韩旭
 * @since 2021-07-29
 */
@Data
public class QueryCheckListSummary  {
    //日计划ID
    private String dayPlanId;
    //车组ID
    private String trainsetId;
    //修程
    private String mainCyc;
    //班组CODE
    private String deptCode;
    //回填作业人员ID
    private String fillWorkCode;
    //回填状态
    private String backState;
    //派工作业人员ID
    private  String allotWorkCode;
    //单据名称编码
    private String templateTypeCode;
    //运用所编码
    private  String unitCode;
    /**类型
     * OneTwoRepair     一二级修
     * LiveCheck        出所联检
     * Integration      一体化作业申请
     * Custom           自定义
     * ReCheck          复核
     * Equipment        机检一级修
     */
    private String type;

    //项目编码
    private String itemCode;
}
