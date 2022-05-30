package com.ydzbinfo.emis.trainRepair.trainMonitor.service;

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
public interface PrWerDictService  {

    List<PowerDict> getPrWerDicts();

    PowerDict getPrWerDict(PowerDict prWerDict);

    int addPrWerDict(PowerDict prWerDict);

    int delPrWerDict(PowerDict prWerDict);

    int updPrWerDict(PowerDict prWerDict);

}
