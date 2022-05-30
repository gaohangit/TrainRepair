package com.ydzbinfo.emis.common.bill.basetemplate.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.bill.basetemplate.dao.*;
import com.ydzbinfo.emis.common.bill.basetemplate.service.*;
import com.ydzbinfo.emis.common.bill.utils.BillUtil;
import com.ydzbinfo.emis.common.bill.utils.SsjsonFileUtils;
import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSource;
import com.ydzbinfo.emis.handlers.kafka.StreamCloudTransData;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.BaseTemplateUniqueKey;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.*;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;


/**
 * <p>
 * 基础单据模板主表 服务实现类
 * </p>
 *
 * @author 张天可
 * @since 2021-12-24
 */
@Service
public class BaseTemplateServiceImpl extends ServiceImpl<BaseTemplateMapper, BaseTemplate> implements IBaseTemplateService {

    @Autowired
    private BaseTemplateContentMapper baseTemplateContentMapper;
    @Autowired
    private BaseTemplateLinkContentMapper baseTemplateLinkContentMapper;
    @Autowired
    private BaseTemplateLinkControlMapper baseTemplateLinkControlMapper;
    @Autowired
    private BaseTemplateAreaMapper baseTemplateAreaMapper;

    @Autowired
    private IBaseTemplateContentService baseTemplateContentService;
    @Autowired
    private IBaseTemplateLinkContentService baseTemplateLinkContentService;
    @Autowired
    private IBaseTemplateLinkControlService baseTemplateLinkControlService;
    @Autowired
    private IBaseTemplateAreaService baseTemplateAreaService;

    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.BASETEMPLATE_OUTPUT,
        inputChannel = BillConfigMqSink.BASETEMPLATE_INPUT
    )
    public void insertBaseTemplateInfo(BaseTemplateInfo baseTemplateInfo) {
        BillUtil.insertTemplateInfo(
            baseTemplateInfo,
            this,
            baseTemplateContentService,
            baseTemplateLinkContentService,
            baseTemplateLinkControlService,
            baseTemplateAreaService,
            t -> t,
            t -> t
        );
    }

    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.BASETEMPLATE_OUTPUT,
        inputChannel = BillConfigMqSink.BASETEMPLATE_INPUT
    )
    public void deleteByTemplateIds(Set<String> templateIds) {
        for (String templateId : templateIds) {
            this.deleteByTemplateId(templateId);
        }
    }

    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.BASETEMPLATE_OUTPUT,
        inputChannel = BillConfigMqSink.BASETEMPLATE_INPUT
    )
    public void deleteByTemplateId(String templateId) {
        // 删除区域
        MybatisPlusUtils.delete(
            baseTemplateAreaMapper,
            eqParam(BaseTemplateArea::getTemplateId, templateId)
        );

        // 删除控制信息
        baseTemplateLinkControlMapper.deleteByTemplateId(templateId);
        // 删除关联单元格信息
        baseTemplateLinkContentMapper.deleteByTemplateId(templateId);

        // 删除单元格信息
        MybatisPlusUtils.delete(
            baseTemplateContentMapper,
            eqParam(BaseTemplateContent::getTemplateId, templateId)
        );

        // 删除单据本身
        this.deleteById(templateId);
    }

    @Override
    public BaseTemplateInfo transToBaseTemplateInfo(BaseTemplate baseTemplate) {
        return BillUtil.transToTemplateInfo(
            baseTemplate,
            baseTemplateContentMapper,
            baseTemplateLinkContentMapper,
            baseTemplateLinkControlMapper,
            baseTemplateAreaMapper,
            BaseTemplateInfo::new,
            BaseTemplateContentInfo::new
        );
    }


    @Override
    @Transactional
    synchronized public List<String> saveBaseTemplates(List<BaseTemplateForSave> baseTemplates) {
        if (baseTemplates.size() == 0) {
            throw RestRequestException.normalFail("无保存数据");
        }

        baseTemplates.forEach(BillUtil::validTemplateBasicInfo);

        for (BaseTemplateForSave baseTemplate : baseTemplates) {
            baseTemplate.setTemplatePath(SsjsonFileUtils.saveTemplateSsjsonFile(baseTemplate));
        }
        return BillUtil.saveTemplateInfoGroup(
            new ArrayList<BaseTemplateInfo>(baseTemplates),
            BaseTemplateUniqueKey.class,
            BaseTemplateUniqueKey::new,
            this,
            BaseTemplate::getTemplateId,
            v -> v,
            v -> BillUtil.getIdResetCopyNew(
                v,
                BaseTemplateInfo::new,
                BaseTemplateContentInfo::new,
                BaseTemplateLinkContent::new,
                BaseTemplateLinkControl::new,
                BaseTemplateArea::new
            ),
            null,
            (deleteTemplateIds, newTemplates) ->
                CommonUtils.getBeanByThis(this)
                    .doSaveBaseTemplateByData(deleteTemplateIds, newTemplates)
        );
    }

    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.BASETEMPLATE_OUTPUT,
        inputChannel = BillConfigMqSink.BASETEMPLATE_INPUT
    )
    public void doSaveBaseTemplateByData(List<String> deleteTemplateIds, List<BaseTemplateInfo> insertBaseTemplates) {
        for (String deleteTemplateId : deleteTemplateIds) {
            this.deleteByTemplateId(deleteTemplateId);
        }
        for (BaseTemplateInfo insertBaseTemplate : insertBaseTemplates) {
            this.insertBaseTemplateInfo(insertBaseTemplate);
        }
    }


}
