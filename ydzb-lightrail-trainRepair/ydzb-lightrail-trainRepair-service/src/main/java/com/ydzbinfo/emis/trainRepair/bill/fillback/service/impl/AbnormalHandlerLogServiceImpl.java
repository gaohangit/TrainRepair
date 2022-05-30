package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.AbnormalHandlerLogMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IAbnormalHandlerLogService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.AbnormalHandlerLog;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 异常派工回填记录表 服务实现类
 * </p>
 *
 * @author 韩旭
 * @since 2021-11-22
 */
@Service
public class AbnormalHandlerLogServiceImpl extends ServiceImpl<AbnormalHandlerLogMapper, AbnormalHandlerLog> implements IAbnormalHandlerLogService {

}
