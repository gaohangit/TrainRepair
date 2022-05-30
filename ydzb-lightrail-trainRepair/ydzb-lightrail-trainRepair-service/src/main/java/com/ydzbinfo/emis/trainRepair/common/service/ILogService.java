package com.ydzbinfo.emis.trainRepair.common.service;


import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.common.querymodel.Log;

/**
 * <p>
 * 日志表 服务类
 * </p>
 *
 * @author 高晗
 * @since 2021-09-17
 */
public interface ILogService extends IService<Log> {
    void addLog(Log log);

}
