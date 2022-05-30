package com.ydzbinfo.emis.trainRepair.repairworkflow.service;


import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkType;

import java.util.List;

/**
 * <p>
 * 关键作业类型表 服务类
 * </p>
 *
 * @author 高晗
 * @since 2021-06-18
 */
public interface IKeyWorkTypeService extends IService<KeyWorkType> {
    void addKeyWorkType(KeyWorkType keyWorkType);

    void delKeyWorkTypeById(KeyWorkType keyWorkType);

    void updKeyWorkType(KeyWorkType keyWorkType);

    void setKeyWorkType(List<KeyWorkType> keyWorkTypes);

    void sendSetKeyWorkType(List<KeyWorkType> keyWorkTypes);

    List<KeyWorkType> getKeyWorkTypeList(String unitCode,boolean showDelete);

    KeyWorkType getKeyWorkType(String name);

}
