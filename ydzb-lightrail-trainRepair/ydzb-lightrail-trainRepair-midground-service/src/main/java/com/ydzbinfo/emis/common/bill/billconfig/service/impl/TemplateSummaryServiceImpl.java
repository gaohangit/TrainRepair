package com.ydzbinfo.emis.common.bill.billconfig.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.bill.billconfig.dao.*;
import com.ydzbinfo.emis.common.bill.billconfig.service.*;
import com.ydzbinfo.emis.common.bill.utils.BillUtil;
import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSource;
import com.ydzbinfo.emis.handlers.kafka.StreamCloudTransData;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 张天可
 * @since 2021-06-18
 */
@Service
public class TemplateSummaryServiceImpl extends ServiceImpl<TemplateSummaryMapper, TemplateSummary> implements ITemplateSummaryService {


    @Autowired
    TemplateSummaryContentMapper templateSummaryContentMapper;
    @Autowired
    TemplateSummLinkContentMapper templateSummLinkContentMapper;
    @Autowired
    TemplateSummLinkControlMapper templateSummLinkControlMapper;
    @Autowired
    TemplateSummaryAreaMapper templateSummaryAreaMapper;

    @Autowired
    ITemplateSummaryContentService templateSummaryContentService;
    @Autowired
    ITemplateSummLinkContentService templateSummLinkContentService;
    @Autowired
    ITemplateSummLinkControlService templateSummLinkControlService;
    @Autowired
    ITemplateSummaryAreaService templateSummaryAreaService;

    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.TEMPLATESUMMARY_OUTPUT,
        inputChannel = BillConfigMqSink.TEMPLATESUMMARY_INPUT
    )
    public void insertTemplateSummaryInfo(TemplateSummaryInfo templateSummaryInfo) {
        BillUtil.insertTemplateInfo(
            templateSummaryInfo,
            this,
            templateSummaryContentService,
            templateSummLinkContentService,
            templateSummLinkControlService,
            templateSummaryAreaService,
            t -> t,
            t -> t
        );
    }

    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.TEMPLATESUMMARY_OUTPUT,
        inputChannel = BillConfigMqSink.TEMPLATESUMMARY_INPUT
    )
    public void deleteTemplateSummaryByTemplateId(String templateId) {
        // 删除区域
        MybatisPlusUtils.delete(
            templateSummaryAreaMapper,
            eqParam(TemplateSummaryArea::getTemplateId, templateId)
        );

        // 删除控制信息
        templateSummLinkControlMapper.deleteByTemplateId(templateId);
        // 删除关联单元格信息
        templateSummLinkContentMapper.deleteByTemplateId(templateId);

        // 删除单元格信息
        MybatisPlusUtils.delete(
            templateSummaryContentMapper,
            eqParam(TemplateSummaryContent::getTemplateId, templateId)
        );

        // 删除单据本身
        this.deleteById(templateId);
    }

    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.TEMPLATESUMMARY_OUTPUT,
        inputChannel = BillConfigMqSink.TEMPLATESUMMARY_INPUT
    )
    public void updateTemplateSummaryStateById(TemplateSummary templateSummary) {
        this.baseMapper.updateTemplateSummaryStateById(templateSummary);
    }

    @Override
    public TemplateSummaryInfo transToTemplateSummaryInfo(TemplateSummary templateSummary) {
        return BillUtil.transToTemplateInfo(
            templateSummary,
            templateSummaryContentMapper,
            templateSummLinkContentMapper,
            templateSummLinkControlMapper,
            templateSummaryAreaMapper,
            TemplateSummaryInfo::new,
            TemplateSummaryContentInfo::new
        );
    }

}
