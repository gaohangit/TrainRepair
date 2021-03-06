package com.ydzbinfo.emis.common.bill.typeconfig.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.common.bill.typeconfig.dao.TemplateLinkAttrMapper;
import com.ydzbinfo.emis.common.bill.typeconfig.dao.TemplateLinkQueryMapper;
import com.ydzbinfo.emis.common.bill.typeconfig.dao.TemplateTypeMapper;
import com.ydzbinfo.emis.common.bill.typeconfig.service.ITemplateTypeService;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateLinkAttr;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateLinkQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateTypeBase;
import com.ydzbinfo.emis.utils.Assert;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.MybatisOgnlUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.emis.utils.mybatisplus.param.Criteria;
import com.ydzbinfo.emis.utils.mybatisplus.param.LogicalLinkable;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author zhaoxl
 * @since 2021-03-25
 */
@Service
public class TemplateTypeServiceImpl extends ServiceImpl<TemplateTypeMapper, TemplateType> implements ITemplateTypeService {


    @Resource
    private TemplateTypeMapper templateTypeMapper;

    @Autowired
    private TemplateLinkQueryMapper templateLinkQueryMapper;

    @Autowired
    private TemplateLinkAttrMapper templateLinkAttrMapper;

    /**
     * @author: fengshuai
     * @Date: 2021/4/7
     * @Description: ???????????????????????????????????????????????????????????????????????????
     */
    @Override
    public List<TemplateType> getTemplateType(String fatherCode, String type,String sysType,String cFlag) {
        return templateTypeMapper.getTemplateType(fatherCode, type,sysType,cFlag);
    }

    /**
     * @param page
     * @param paramModel
     * @return
     * @author: fengshuai
     * @Date: 2021/4/7
     * @Description: ??????????????????????????????
     */
    @Override
    public List<TemplateTypeForShow> getTemplateTypeList(Page<TemplateType> page, TemplateTypeQueryParamModel paramModel) {
        /**??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //?????????????????????????????????????????????????????????????????????????????????sysType??????0???????????????
        if("0".equals(paramModel.getSysType())){
            paramModel.setSysType("");
        }**/
        return templateTypeMapper.getTemplateTypeList(page, MybatisOgnlUtils.getNewEntityReplacedWildcardChars(paramModel, TemplateTypeQueryParamModel::getTemplateTypeName));
    }

    /**
     * @author: ??????
     * @Date: 2022/3/24
     * @Description: ??????????????????,????????????????????????????????????????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delTemplateTypeAndAttrAndQuery(List<String> delTypeCodes) {
        if(!CollectionUtils.isEmpty(delTypeCodes)){
            Map<String,String> delTypeCodeMap = new HashMap<>();
            for(String delTypeCode:delTypeCodes){
                delTypeCodeMap.put(delTypeCode,delTypeCode+"_"+UUID.randomUUID().toString());
            }
            this.delTemplateTypeList(delTypeCodeMap);
            this.delTemplateLinkQueryList(delTypeCodeMap);
            this.delTemplateLinkAttrList(delTypeCodeMap);
        }
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: ????????????????????????
     */
    @Override
    public List<TemplateQuery> getQueryList() {
        return templateTypeMapper.getQueryList();
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: ??????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer delQuery(List<String> delTypeCodes, String delUserCode, String delUserName) {
        return templateTypeMapper.delQuery(delTypeCodes, delUserCode, delUserName);
    }

    /**
     * @author: ??????
     * @Date: 2022/3/24
     * @Description: ??????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delTemplateLinkQueryList(Map<String,String> delTypeCodeMap) {
        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        String staffId = currentUser.getStaffId();
        String staffName = currentUser.getName();
        Date currentDate = new Date();
        List<TemplateLinkQuery> delTemplateLinkQueryList = new ArrayList<>();
        delTypeCodeMap.forEach((typeCode,afterTypeCode)->{
            TemplateLinkQuery delModel = new TemplateLinkQuery();
            delModel.setTemplateTypeCode(afterTypeCode);
            delModel.setDelUserCode(staffId);
            delModel.setDelUserName(staffName);
            delModel.setDelTime(currentDate);
            delModel.setFlag("0");
            MybatisPlusUtils.update(templateLinkQueryMapper,delModel,eqParam(TemplateLinkQuery::getTemplateTypeCode,typeCode));
        });
    }

    /**
     * @param templateTypeList
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: ??????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer addTemplateTypes(List<TemplateTypeForSave> templateTypeList) {
        //???????????????????????????????????????????????????????????????????????????
        List<String> templateTypeCodes = templateTypeList.stream().filter(t->"1".equals(t.getOperationType())).map(t -> t.getTemplateTypeCode()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(templateTypeCodes)){
            List<TemplateType> exitTemplateTypeList = MybatisPlusUtils.selectList(templateTypeMapper, inParam(TemplateType::getTemplateTypeCode, templateTypeCodes));
            if(!CollectionUtils.isEmpty(exitTemplateTypeList)){
                List<String> exitTemplateTypeCodes = exitTemplateTypeList.stream().map(t -> t.getTemplateTypeCode()).collect(Collectors.toList());
                throw new RuntimeException("??????????????????"+exitTemplateTypeCodes.toString()+"?????????????????????????????????!");
            }
        }
        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        //1.???????????????????????????
        List<String> delTypeCodes = templateTypeList.stream().filter((t) -> t.getOperationType().equals("0")).map(t -> t.getTemplateTypeCode()).collect(Collectors.toList());
        //??????????????????????????????????????????????????????????????????????????????
        if (!CollectionUtils.isEmpty(delTypeCodes)) {
            List<String> delChildrenCodes = templateTypeMapper.getTypeCodesByFatherCodes(delTypeCodes).stream().map(t -> t.getTemplateTypeCode()).collect(Collectors.toList());
            delTypeCodes.addAll(delChildrenCodes);
        }
        if (!CollectionUtils.isEmpty(delTypeCodes)) {
            //????????????????????????????????????
            templateTypeMapper.delTemplateType(delTypeCodes, currentUser.getStaffId(), currentUser.getName());
            //??????????????????????????????
            templateTypeMapper.delTemplateLinkAttrs(delTypeCodes, currentUser.getStaffId(), currentUser.getName());
            //??????????????????????????????
            templateTypeMapper.delQuery(delTypeCodes, currentUser.getStaffId(), currentUser.getName());
        }
        //2.???????????????????????????
        List<TemplateType> addList = templateTypeList.stream().filter((t) -> t.getOperationType().equals("1")).collect(Collectors.toList());
        for (TemplateType item : addList) {
            if (item.getTemplateTypeCode() == null || item.getTemplateTypeCode().equals("")) {
                item.setTemplateTypeCode(UUID.randomUUID().toString().replace("-", ""));
            }
            item.setCreateUserCode(currentUser.getStaffId());
            item.setCreateUserName(currentUser.getName());
        }
        if (addList.size() > 0) {
            return templateTypeMapper.addTemplateTypes(addList);
        } else {
            return 0;
        }
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: ??????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer delTemplateType(List<String> delTypeCodes, String delUserCode, String delUserName) {
        return templateTypeMapper.delTemplateType(delTypeCodes, delUserCode, delUserName);
    }

    /**
     * @author: ??????
     * @Date: 2022/3/24
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delTemplateTypeList(Map<String,String> delTypeCodeMap){
        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        String staffId = currentUser.getStaffId();
        String staffName = currentUser.getName();
        Date currentDate = new Date();
        List<TemplateType> delTemplateTypeList = new ArrayList<>();
        delTypeCodeMap.forEach((typeCode,afterTypeCode)->{
            TemplateType delModel = new TemplateType();
            delModel.setTemplateTypeCode(afterTypeCode);
            delModel.setDelUserCode(staffId);
            delModel.setDelUserName(staffName);
            delModel.setDelTime(currentDate);
            delModel.setFlag("0");
            MybatisPlusUtils.update(templateTypeMapper,delModel,eqParam(TemplateType::getTemplateTypeCode,typeCode));
        });
    }

    /**
     * @param templateType
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: ??????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer updateTemplateType(TemplateTypeForSave templateType) {
        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        List<String> delTypeCodes = new ArrayList<>();
        delTypeCodes.add(templateType.getTemplateTypeCode());

        //1.?????????????????????????????????--???????????????
//        templateTypeMapper.delQuery(delTypeCodes);
        templateTypeMapper.delQueryPhysics(delTypeCodes);
        List<TemplateLinkQueryParamModel> templateLinkQueryParamModelList = new ArrayList<>();
        String[] queryCodes = Arrays.stream(templateType.getQueryCodes().split(",")).filter(StringUtils::hasText).toArray(String[]::new);
        for (String item : queryCodes) {
            TemplateLinkQueryParamModel templateLinkQueryParamModel = new TemplateLinkQueryParamModel();
            templateLinkQueryParamModel.setQueryCode(item);
            templateLinkQueryParamModel.setTemplateTypeCode(templateType.getTemplateTypeCode());
            templateLinkQueryParamModel.setCreateUser(currentUser.getStaffId());
            templateLinkQueryParamModelList.add(templateLinkQueryParamModel);
        }
        if (templateLinkQueryParamModelList.size() > 0) {
            templateTypeMapper.addQuerys(templateLinkQueryParamModelList);
        }
        //2.?????????????????????????????????--???????????????
        // templateTypeMapper.delTemplateLinkAttrs(delTypeCodes);
        templateTypeMapper.delTemplateLinkAttrsPhysics(delTypeCodes);
        List<TemplateLinkAttrModel> templateLinkAttrModelList = new ArrayList<>();
        String[] linkAttrs = templateType.getTemplateLinkAttrs().split(",");
        for (String item : linkAttrs) {
            TemplateLinkAttrModel templateLinkAttrModel = new TemplateLinkAttrModel();
            templateLinkAttrModel.setAttributeCode(item);
            templateLinkAttrModel.setTemplateTypeCode(templateType.getTemplateTypeCode());
            templateLinkAttrModel.setCreateUserCode(currentUser.getStaffId());
            templateLinkAttrModel.setCreateUserName(currentUser.getName());
            templateLinkAttrModelList.add(templateLinkAttrModel);
        }
        if (templateLinkAttrModelList.size() > 0) {
            templateTypeMapper.addTemplateLinkAttrs(templateLinkAttrModelList);
        }
        return MybatisPlusUtils.update(
            templateTypeMapper,
            templateType,
            eqParam(TemplateType::getTemplateTypeCode, templateType.getTemplateTypeCode()),
            eqParam(TemplateType::getFlag, "1")
        );
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/9
     * @Description: ??????????????????????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer delTemplateLinkAttrs(List<String> delTypeCodes, String delUserCode, String delUserName) {
        return templateTypeMapper.delTemplateLinkAttrs(delTypeCodes, delUserCode, delUserName);
    }

    /**
     * @author: ??????
     * @Date: 2022/3/24
     * @Description: ???????????????????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delTemplateLinkAttrList(Map<String,String> delTypeCodeMap){
        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        String staffId = currentUser.getStaffId();
        String staffName = currentUser.getName();
        Date currentDate = new Date();
        List<TemplateLinkAttr> delTemplateLinkAttrList = new ArrayList<>();
        delTypeCodeMap.forEach((typeCode,afterTypeCode)->{
            TemplateLinkAttr delModel = new TemplateLinkAttr();
            delModel.setTemplateTypeCode(afterTypeCode);
            delModel.setDelUserCode(staffId);
            delModel.setDelUserName(staffName);
            delModel.setDelTime(currentDate);
            delModel.setFlag("0");
            MybatisPlusUtils.update(templateLinkAttrMapper,delModel,eqParam(TemplateLinkAttr::getTemplateTypeCode,typeCode));
        });
    }

    @Override
    public TemplateTypeInfo getTemplateTypeInfo(String templateTypeCode) {
        return templateTypeMapper.getTemplateTypeInfo(templateTypeCode);
    }

    private Criteria<TemplateType> getEditableConfigCriteria(boolean showSystemTemplate, boolean showCustomTemplate) {
        ColumnParam<TemplateType>[] columnParams = ColumnParamUtil.filterBlankParams(
            !showSystemTemplate ? neParam(TemplateType::getSysTemplate, "0") : null,// ?????????????????????????????????????????????????????????
            !showCustomTemplate ? neParam(TemplateType::getSysTemplate, "1") : null// ?????????????????????????????????????????????????????????
        );
        if (columnParams.length > 0) {
            return anyMatch(
                neParam(TemplateType::getType, "3"),// ??????
                allMatch(columnParams)
            );
        } else {
            return null;
        }
    }

    /**
     * ???????????????????????????
     */
    private boolean isLeafNode(TemplateTypeTreeNode node) {
        return Objects.equals(node.getType(), "3");
    }

    private List<TemplateTypeTreeNode> getAllChildTemplateTypes(List<String> templateTypeCodes, boolean showSystemTemplate, boolean showCustomTemplate) {
        Assert.notEmptyInnerFatal(templateTypeCodes, "templateTypeCodes??????????????????");
        List<TemplateTypeTreeNode> childTypes = MybatisPlusUtils.selectList(
            this,
            allMatch(
                inParam(TemplateType::getFatherTypeCode, templateTypeCodes),
                eqParam(TemplateType::getFlag, "1")
            ),
            getEditableConfigCriteria(showSystemTemplate, showCustomTemplate)
        ).stream().map(v -> {
            TemplateTypeTreeNode templateTypeTreeNode = new TemplateTypeTreeNode();
            CommonUtils.copyPropertiesToChild(v, TemplateType.class, templateTypeTreeNode, TemplateTypeTreeNode.class);
            templateTypeTreeNode.setChildren(new ArrayList<>());
            return templateTypeTreeNode;
        }).collect(Collectors.toList());
        Map<String, TemplateTypeTreeNode> tempMap = CommonUtils.collectionToMap(childTypes, TemplateTypeBase::getTemplateTypeCode);
        if (childTypes.size() > 0) {
            List<TemplateTypeTreeNode> subChildTypes = getAllChildTemplateTypes(
                childTypes.stream().map(TemplateTypeBase::getTemplateTypeCode).collect(Collectors.toList()),
                showSystemTemplate,
                showCustomTemplate
            );
            for (TemplateTypeTreeNode subChildType : subChildTypes) {
                if (tempMap.containsKey(subChildType.getFatherTypeCode())) {
                    tempMap.get(subChildType.getFatherTypeCode()).getChildren().add(subChildType);
                }
            }
        }
        childTypes.removeIf(templateTypeTreeNode -> !isLeafNode(templateTypeTreeNode) && templateTypeTreeNode.getChildren().size() == 0);
        return childTypes;
    }

    @Override
    public List<TemplateType> getAllTemplateTypesIncludeSelf(String templateTypeCode, boolean showSystemTemplate, boolean showCustomTemplate) {
        Assert.notEmptyInnerFatal(templateTypeCode, "templateTypeCode??????????????????");
        TemplateType currentType = this.selectById(templateTypeCode);
        List<TemplateType> allTypes = new ArrayList<>();
        if (currentType != null) {
            allTypes.add(currentType);
        }
        allTypes.addAll(getAllChildTemplateTypes(templateTypeCode, showSystemTemplate, showCustomTemplate));
        return allTypes;
    }

    @Override
    public List<TemplateType> getAllChildTemplateTypes(String templateTypeCode, boolean showSystemTemplate, boolean showCustomTemplate) {
        List<TemplateTypeTreeNode> allChildTemplateTypes = getAllChildTemplateTypes(Collections.singletonList(templateTypeCode), showSystemTemplate, showCustomTemplate);
        return flatTemplateTypeTreeNodeList(allChildTemplateTypes);
    }

    @Override
    public List<TemplateType> getAllTemplateTypes(boolean showSystemTemplate, boolean showCustomTemplate) {
        List<TemplateType> rootTemplateTypes = MybatisPlusUtils.selectList(
            this,
            eqParam(TemplateType::getFatherTypeCode, null),
            eqParam(TemplateType::getFlag, "1")// ??????????????????????????????
        );

        List<TemplateTypeTreeNode> allChildTemplateTypes = getAllChildTemplateTypes(
            rootTemplateTypes.stream().map(TemplateTypeBase::getTemplateTypeCode).collect(Collectors.toList()),
            showSystemTemplate,
            showCustomTemplate
        );

        List<TemplateType> allTypes = new ArrayList<>();
        allTypes.addAll(rootTemplateTypes);
        allTypes.addAll(flatTemplateTypeTreeNodeList(allChildTemplateTypes));
        return allTypes;
    }

    /**
     * ????????????????????????
     */
    private List<TemplateType> flatTemplateTypeTreeNodeList(List<TemplateTypeTreeNode> allChildTemplateTypes) {
        return CommonUtils.flatTree(
            allChildTemplateTypes,
            TemplateTypeTreeNode::getChildren,
            v -> {
                TemplateType templateType = new TemplateType();
                CommonUtils.copyPropertiesToParent(v, TemplateTypeTreeNode.class, templateType, TemplateType.class);
                return templateType;
            }
        );
    }
}
