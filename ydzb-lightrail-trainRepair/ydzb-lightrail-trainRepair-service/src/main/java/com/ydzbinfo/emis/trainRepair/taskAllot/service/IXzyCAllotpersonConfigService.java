package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCAllotpersonConfig;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
public interface IXzyCAllotpersonConfigService extends IService<XzyCAllotpersonConfig> {

    List<XzyCAllotpersonConfig> getAllotPerson(String branchCode);

    int setPerson(XzyCAllotpersonConfig allotpersonConfig);

    int deleteAllByDept(String deptCode);

}
