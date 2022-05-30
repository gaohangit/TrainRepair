package com.ydzbinfo.emis.trainRepair.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.PowerDict;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-03-01
 */
public interface PrWerDictMapper extends BaseMapper<PowerDict> {

    List<PowerDict> getPrWerDicts();

    PowerDict getPrWerDict(PowerDict prWerDict);

    int addPrWerDict(PowerDict prWerDict);

    int delPrWerDict(PowerDict prWerDict);

    int updPrWerDict(PowerDict prWerDict);

}
