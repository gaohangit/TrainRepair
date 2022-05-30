package com.ydzbinfo.emis.common.workprocess.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.workprocess.dao.ProcessPacketMapper;
import com.ydzbinfo.emis.common.workprocess.service.IProcessPacketService;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPacket;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
@Service
public class ProcessPacketServiceImpl extends ServiceImpl<ProcessPacketMapper, ProcessPacket> implements IProcessPacketService {

}
