package com.ydzbinfo.emis.common.bill.basetemplate.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.bill.basetemplate.dao.BaseTemplateConfigMapper;
import com.ydzbinfo.emis.common.bill.basetemplate.service.IBaseTemplateConfigService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplateConfig;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 基础单据模板配置表 服务实现类
 * </p>
 *
 * @author 张天可
 * @since 2021-12-24
 */
@Service
public class BaseTemplateConfigServiceImpl extends ServiceImpl<BaseTemplateConfigMapper, BaseTemplateConfig> implements IBaseTemplateConfigService {

}
