package com.ydzbinfo.emis.common.bill.basetemplate.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSource;
import com.ydzbinfo.emis.handlers.kafka.StreamCloudTransData;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 基础单据模板主表 服务类
 * </p>
 *
 * @author 张天可
 * @since 2021-12-24
 */
public interface IBaseTemplateService extends IService<BaseTemplate> {

    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.BASETEMPLATE_OUTPUT,
        inputChannel = BillConfigMqSink.BASETEMPLATE_INPUT
    )
    void insertBaseTemplateInfo(BaseTemplateInfo baseTemplateInfo);


    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.BASETEMPLATE_OUTPUT,
        inputChannel = BillConfigMqSink.BASETEMPLATE_INPUT
    )
    void deleteByTemplateIds(Set<String> templateIds);

    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.BASETEMPLATE_OUTPUT,
        inputChannel = BillConfigMqSink.BASETEMPLATE_INPUT
    )
    void deleteByTemplateId(String templateId);

    /**
     * 将主表数据转换为详情数据
     */
    BaseTemplateInfo transToBaseTemplateInfo(BaseTemplate baseTemplate);

    @Transactional
    List<String> saveBaseTemplates(List<BaseTemplateForSave> baseTemplates);
}
