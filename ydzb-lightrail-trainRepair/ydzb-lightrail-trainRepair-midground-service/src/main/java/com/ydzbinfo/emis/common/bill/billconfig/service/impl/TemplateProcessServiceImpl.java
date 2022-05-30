package com.ydzbinfo.emis.common.bill.billconfig.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.bill.billconfig.dao.*;
import com.ydzbinfo.emis.common.bill.billconfig.service.*;
import com.ydzbinfo.emis.common.bill.utils.BillUtil;
import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSource;
import com.ydzbinfo.emis.handlers.kafka.StreamCloudTransData;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcess;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcessArea;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcessContent;
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
public class TemplateProcessServiceImpl extends ServiceImpl<TemplateProcessMapper, TemplateProcess> implements ITemplateProcessService {

    @Autowired
    TemplateProcessContentMapper templateProcessContentMapper;

    @Autowired
    ITemplateProcessContentService templateProcessContentService;

    @Autowired
    TemplateProcessAreaMapper templateProcessAreaMapper;

    @Autowired
    TemplateProcLinkControlMapper templateProcLinkControlMapper;

    @Autowired
    TemplateProcLinkContentMapper templateProcLinkContentMapper;

    @Autowired
    ITemplateProcLinkContentService templateProcLinkContentService;

    @Autowired
    ITemplateProcLinkControlService templateProcLinkControlService;

    @Autowired
    ITemplateProcessAreaService templateProcessAreaService;

    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.TEMPLATEPROCESS_OUTPUT,
        inputChannel = BillConfigMqSink.TEMPLATEPROCESS_INPUT
    )
    public void insertTemplateProcessInfo(TemplateProcessInfo templateProcess) {
        BillUtil.insertTemplateInfo(
            templateProcess,
            this,
            templateProcessContentService,
            templateProcLinkContentService,
            templateProcLinkControlService,
            templateProcessAreaService,
            t -> t,
            t -> t
        );
    }

    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.TEMPLATEPROCESS_OUTPUT,
        inputChannel = BillConfigMqSink.TEMPLATEPROCESS_INPUT
    )
    public void deleteTemplateProcessByTemplateId(String templateId) {
        // 删除区域
        MybatisPlusUtils.delete(
            templateProcessAreaMapper,
            eqParam(TemplateProcessArea::getTemplateId, templateId)
        );

        // 删除控制信息
        templateProcLinkControlMapper.deleteByTemplateId(templateId);
        // 删除关联单元格信息
        templateProcLinkContentMapper.deleteByTemplateId(templateId);

        // 删除单元格信息
        MybatisPlusUtils.delete(
            templateProcessContentMapper,
            eqParam(TemplateProcessContent::getTemplateId, templateId)
        );

        // 删除单据本身
        this.deleteById(templateId);
    }

    @Override
    public TemplateProcessInfo transToTemplateProcessInfo(TemplateProcess templateProcess) {
        return BillUtil.transToTemplateInfo(
            templateProcess,
            templateProcessContentMapper,
            templateProcLinkContentMapper,
            templateProcLinkControlMapper,
            templateProcessAreaMapper,
            TemplateProcessInfo::new,
            TemplateProcessContentInfo::new
        );
    }

}
