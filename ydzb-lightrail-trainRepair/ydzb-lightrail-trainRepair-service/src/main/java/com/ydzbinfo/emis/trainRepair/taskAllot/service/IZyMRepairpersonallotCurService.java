package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyMRepairpersonallotCur;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 史艳涛
 * @since 2022-01-14
 */
public interface IZyMRepairpersonallotCurService extends IService<ZyMRepairpersonallotCur> {
    List<ZyMRepairpersonallotCur> selectByDeptCode(String deptCode);
}
