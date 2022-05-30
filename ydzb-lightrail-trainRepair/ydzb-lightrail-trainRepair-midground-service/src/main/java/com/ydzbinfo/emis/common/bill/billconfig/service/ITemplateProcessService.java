package com.ydzbinfo.emis.common.bill.billconfig.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcess;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcessForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 张天可
 * @since 2021-06-18
 */
public interface ITemplateProcessService extends IService<TemplateProcess> {

    void insertTemplateProcessInfo(TemplateProcessInfo templateProcessInfo);

    void deleteTemplateProcessByTemplateId(String TemplateId);

    TemplateProcessInfo transToTemplateProcessInfo(TemplateProcess templateProcess);
}
