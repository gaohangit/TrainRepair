package com.ydzbinfo.emis.common.bill.typeconfig.service.impl;


import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.common.bill.typeconfig.dao.TemplateAttributeMapper;
import com.ydzbinfo.emis.common.bill.typeconfig.service.ITemplateAttributeService;
import com.ydzbinfo.emis.common.bill.typeconfig.service.ITemplateValueService;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttribute;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeMode;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateValue;
import com.ydzbinfo.emis.utils.MybatisOgnlUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaoxl
 * @since 2021-03-23
 */

@Service
public class TemplateAttributeServiceImpl extends ServiceImpl<TemplateAttributeMapper, TemplateAttribute> implements ITemplateAttributeService {
    @Autowired
    private TemplateAttributeMapper templateAttributeMapper;
    @Autowired
    private ITemplateValueService templateValueService;

    /**
     * 获取属性类型下拉框数据
     */
    @Override
    public List<TemplateAttributeType> getAttributeTypeList() {
        return templateAttributeMapper.getAttributeTypeList();
    }

    /**
     * 获取属性模式下拉框数据
     */
    @Override
    public List<TemplateAttributeMode> getAttributeModeList() {
        return templateAttributeMapper.getAttributeModeList();
    }

    /**
     * 获取属性列表数据
     */
    @Override
    public List<TemplateAttributeForShow> getTemplateAttributeList(Page<TemplateAttributeForShow> page, String attributeTypeCode, String attributeName, String attributeCode,String sysType) {
        return templateAttributeMapper.getTemplateAttributeList(page, attributeTypeCode, MybatisOgnlUtils.replaceWildcardChars(attributeName), attributeCode,sysType);
    }

    /**
     * 删除一个属性
     */
    @Override
    public void delTemplateAttribute(String id) {
        if(ObjectUtils.isEmpty(id)){
            throw new RuntimeException("删除失败，前台错误！");
        }
        //1.去数据库中查一下该属性有没有被单据类型那里配置为关联属性，如果配置了则不能删除
        List<TemplateAttribute> attributeList = MybatisPlusUtils.selectList(templateAttributeMapper, eqParam(TemplateAttribute::getId, id));
        if(!CollectionUtils.isEmpty(attributeList)){
            TemplateAttribute templateAttribute = attributeList.stream().findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(templateAttribute)&&!ObjectUtils.isEmpty(templateAttribute.getAttributeCode())){
                //取XZY_C_TEMPLATELINKATTR表中查询是否存在数据
                Integer linkAttrCount = templateAttributeMapper.getLinkAttrCountByAttributeCode(templateAttribute.getAttributeCode());
                if(!ObjectUtils.isEmpty(linkAttrCount)&&linkAttrCount>0){
                    throw new RuntimeException("该属性在单据类型配置中被使用，无法删除！");
                }
            }
        }
        //2.组织数据
        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        //获取当前时间
        Date currentDate = new Date();
        TemplateAttribute delModel = new TemplateAttribute();
        delModel.setDelTime(currentDate);
        delModel.setDelUserCode(currentUser.getStaffId());
        delModel.setDelUserName(currentUser.getName());
        delModel.setFlag("0");
        delModel.setId(id);
        //3.调用方法进行删除
        this.delTemplateAttributeModel(delModel);
        // TODO 往kafka推送数据
        // if(SpringCloudStreamUtil.enableSendCloudData()){
        //
        // }

        // templateAttributeMapper.delTemplateAttribute(id);
    }

    @Override
    public boolean delTemplateAttributeModel(TemplateAttribute templateAttribute) {
        return MybatisPlusUtils.update(
            templateAttributeMapper,
            templateAttribute,
            eqParam(TemplateAttribute::getId, templateAttribute.getId())
        ) > 0;
    }

    /**
     * 根据属性编码获取取值范围
     */
    @Override
    public List<TemplateValue> getTemplateValueList(String attributeCode) {
        return templateAttributeMapper.getTemplateValueList(attributeCode);
    }


    /**
     * 新增属性
     *
     * @param templateAttribute
     */
    @Override
    @Transactional
    public Integer addTemplateAttribute(TemplateAttributeForSave templateAttribute) {
        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        //获取当前时间
        Date currentDate = new Date();
        //1.如果该属性编码在数据库中已经存在，则不进行添加
        if (MybatisPlusUtils.selectExist(
            templateAttributeMapper,
            eqParam(TemplateAttribute::getAttributeCode, templateAttribute.getAttributeCode()),
            eqParam(TemplateAttribute::getFlag, "1")
        )) {
            throw new RuntimeException("属性编码为" + templateAttribute.getAttributeCode() + "的属性已存在");
        }
        //2.组织属性数据
        templateAttribute.setCreateUserCode(currentUser.getStaffId());
        templateAttribute.setCreateUserName(currentUser.getName());
        templateAttribute.setCreateTime(currentDate);
        templateAttribute.setId(IdWorker.get32UUID());
        templateAttribute.setFlag("1");
        //3.组织属性取值范围数据
        List<TemplateValue> templateValueList = templateAttribute.getTemplateValueList();

        templateValueList.forEach(v -> {
            v.setCreateUserCode(currentUser.getStaffId());
            v.setCreateUserName(currentUser.getName());
            v.setCreateTime(currentDate);
            v.setId(IdWorker.get32UUID());
            v.setAttributeCode(templateAttribute.getAttributeCode());
            v.setFlag("1");
            v.setSortId(String.valueOf(templateValueList.indexOf(v)));
        });
        //4.调用添加model方法
        int i = this.addTemplateAttributeModel(templateAttribute);
        // TODO 往kafka推送数据
        // if(SpringCloudStreamUtil.enableSendCloudData()){
        //
        // }

        // int i = templateAttributeMapper.insert(templateAttribute);
        // if (templateValueList.size() > 0) {
        //     this.setTemplateValues(templateValueList);
        // }
        return i;
    }

    @Override
    public Integer addTemplateAttributeModel(TemplateAttributeForSave templateAttribute) {
        int i = templateAttributeMapper.insert(templateAttribute);
        this.setTemplateValues(templateAttribute.getTemplateValueList(), templateAttribute.getAttributeCode());
        return i;
    }

    /**
     * 新增模板取值范围（先删后插）
     */
    @Override
    public void setTemplateValues(List<TemplateValue> templateValueList,String attributeCode) {
        MybatisPlusUtils.delete(
            templateValueService,
            eqParam(TemplateValue::getAttributeCode, attributeCode)
        );
        if (templateValueList.size() > 0) {
            templateValueService.insertBatch(templateValueList);
        }
    }

    /**
     * 更新属性
     *
     * @param templateAttribute
     */
    @Override
    @Transactional
    public Integer updateTemplateAttribute(TemplateAttributeForSave templateAttribute) {
        // templateAttribute.setFlag(null);
        //组织数据
        List<TemplateValue> templateValueList = templateAttribute.getTemplateValueList();
        templateValueList.forEach(v -> {
            v.setId(IdWorker.get32UUID());
            v.setAttributeCode(templateAttribute.getAttributeCode());
            v.setFlag("1");
            v.setSortId(String.valueOf(templateValueList.indexOf(v)));
        });
        //更新数据
        int i = this.updateTemplateAttributeModel(templateAttribute);
        // TODO 往kafka推送数据
        // if (SpringCloudStreamUtil.enableSendCloudData()) {
        //
        // }

        // int i = templateAttributeMapper.updateTemplateAttribute(templateAttribute);
        // if (templateValueList.size() > 0) {
        //     this.setTemplateValues(templateValueList);
        // }
        return i;
    }

    @Override
    @Transactional
    public Integer updateTemplateAttributeModel(TemplateAttributeForSave templateAttribute) {
        //更新属性表
        TemplateAttribute updateModel = new TemplateAttribute();
        BeanUtils.copyProperties(templateAttribute, updateModel);
        Integer i = templateAttributeMapper.updateById(updateModel);

        //更新取值范围表（先删后插）
        this.setTemplateValues(templateAttribute.getTemplateValueList(), templateAttribute.getAttributeCode());
        return i;
    }


    @Override
    public TemplateAttributeForSave selectTemplateAttributeWithDetail(String id) {
        return templateAttributeMapper.selectTemplateAttributeWithDetail(id);
    }
}
