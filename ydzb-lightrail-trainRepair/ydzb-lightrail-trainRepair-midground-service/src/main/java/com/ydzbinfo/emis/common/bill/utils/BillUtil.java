package com.ydzbinfo.emis.common.bill.utils;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.common.bill.constant.TemplateQueryEnum;
import com.ydzbinfo.emis.common.bill.typeconfig.dao.TemplateQueryMapper;
import com.ydzbinfo.emis.common.bill.typeconfig.dao.TemplateTypeMapper;
import com.ydzbinfo.emis.trainRepair.bill.model.*;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.IBaseTemplateUniqueKey;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.ITemplateUniqueKey;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.ITemplateSummary;
import com.ydzbinfo.emis.trainRepair.constant.BillTemplateStateEnum;
import com.ydzbinfo.emis.utils.Assert;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.entity.SerializableFunction;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * 记录单工具类
 *
 * @author 张天可
 * @date 2021/6/22
 */
@Component
public class BillUtil {

    public static final String DELIMITER = "_";

    /**
     * 标记单据生成templateNo与templateName的用到的动态属性枚举类
     * 注：index值不可变更，后续新增属性的index值必须大于之前每一个，防止后续生成的单据templateNo与之前的不兼容
     */
    @Getter
    @AllArgsConstructor
    public enum TemplateDynamicPartEnum {
        TRAINSET_TYPE(
            IBaseTemplateUniqueKey::getTrainsetType,
            IBaseTemplateUniqueKey::getTrainsetType,
            TemplateQueryEnum.TrainsetType,
            0
        ),
        BATCH(
            IBaseTemplateUniqueKey::getBatch,
            IBaseTemplateUniqueKey::getBatch,
            TemplateQueryEnum.Batch,
            1
        ),
        ITEM(
            IBaseTemplateUniqueKey::getItemCode,
            ITemplateBaseExtraMixin::getItemName,
            TemplateQueryEnum.Item,
            2
        ),
        MARSHAL_COUNT(
            IBaseTemplateUniqueKey::getMarshalCount,
            IBaseTemplateUniqueKey::getMarshalCount,
            TemplateQueryEnum.MarshalCount,
            3
        ),
        ;
        private final SerializableFunction<IBaseTemplateUniqueKey, ?> valueGetter;
        private final SerializableFunction<ITemplateBase, ?> valueNameGetter;
        private final TemplateQueryEnum propertyQueryEnum;
        private final Integer index;
    }

    private static List<TemplateDynamicPartEnum> getSortedTemplateDynamicPartEnums() {
        return Arrays.stream(TemplateDynamicPartEnum.values()).sorted(Comparator.comparing(TemplateDynamicPartEnum::getIndex)).collect(Collectors.toList());
    }

    private static void addTemplateNoParts(IBaseTemplateUniqueKey entity, Consumer<String> addValue) {
        List<TemplateDynamicPartEnum> partEnums = getSortedTemplateDynamicPartEnums();
        for (TemplateDynamicPartEnum partEnum : partEnums) {
            SerializableFunction<IBaseTemplateUniqueKey, ?> valueGetter = partEnum.getValueGetter();
            Object value = valueGetter.apply(entity);
            if (!ObjectUtils.isEmpty(value)) {
                addValue.accept("[" + partEnum.getPropertyQueryEnum().toString() + "_" + value + "]");
            }
        }
    }

    private static void addTemplateNameParts(ITemplateBase entity, Consumer<String> addValue) {
        List<TemplateDynamicPartEnum> partEnums = getSortedTemplateDynamicPartEnums();
        for (TemplateDynamicPartEnum partEnum : partEnums) {
            SerializableFunction<ITemplateBase, ?> valueNameGetter = partEnum.getValueNameGetter();
            Object valueName = valueNameGetter.apply(entity);
            if (!ObjectUtils.isEmpty(valueName)) {
                addValue.accept("[" + getPropertyDescription(partEnum.getPropertyQueryEnum()) + "_" + valueName + "]");
            }
        }
    }

    private static TemplateQueryMapper templateQueryMapper;
    private static TemplateTypeMapper templateTypeMapper;

    @Autowired
    public void init(TemplateQueryMapper templateQueryMapper, TemplateTypeMapper templateTypeMapper) {
        BillUtil.templateQueryMapper = templateQueryMapper;
        BillUtil.templateTypeMapper = templateTypeMapper;
    }

    private static String getPropertyDescription(TemplateQueryEnum templateQueryEnum) {
        Map<String, TemplateQuery> templateQueryMap = CacheUtil.getDataUseThreadCache(
            "BillUtil.getPropertyDescription.templateQueryMap",
            () -> {
                List<TemplateQuery> templateQueryList = templateQueryMapper.selectList(null);
                return CommonUtils.collectionToMap(templateQueryList, TemplateQuery::getQueryCode);
            }
        );
        if (templateQueryMap.containsKey(templateQueryEnum.toString())) {
            return templateQueryMap.get(templateQueryEnum.toString()).getQueryName();
        } else {
            throw RestRequestException.fatalFail("XZY_B_TEMPLATEQUERY表数据与程序内部不符，请检查");
        }
    }

    /**
     * 生成模板编号
     */
    public static String generateTemplateNo(IBaseTemplateUniqueKey templateUniqueKey) {
        if (templateUniqueKey instanceof ITemplateUniqueKey) {
            return generateConfigTemplateNo((ITemplateUniqueKey) templateUniqueKey);
        } else {
            return generateBaseTemplateNo(templateUniqueKey);
        }
    }

    private static String generateConfigTemplateNo(ITemplateUniqueKey templateUniqueBase) {
        List<String> properties = new ArrayList<>();
        if (!StringUtils.isEmpty(templateUniqueBase.getUnitCode())) {
            properties.add(templateUniqueBase.getDepotCode() + "/" + templateUniqueBase.getUnitCode());
        } else {
            properties.add(templateUniqueBase.getDepotCode());
        }
        properties.add(templateUniqueBase.getTemplateTypeCode());
        addTemplateNoParts(
            templateUniqueBase,
            properties::add
        );
        return String.join(DELIMITER, properties.toArray(new String[0]));
    }

    private static String generateBaseTemplateNo(IBaseTemplateUniqueKey templateUniqueBase) {
        List<String> properties = new ArrayList<>();
        properties.add(templateUniqueBase.getTemplateTypeCode());
        addTemplateNoParts(
            templateUniqueBase,
            properties::add
        );
        return String.join(DELIMITER, properties.toArray(new String[0]));
    }

    /**
     * 生成模板名称
     */
    public static String generateTemplateName(ITemplateBase templateBase) {
        if (templateBase instanceof IConfigTemplateBase) {
            return generateConfigTemplateName((IConfigTemplateBase) templateBase);
        } else {
            return generateBaseTemplateName(templateBase);
        }
    }

    /**
     * 生成模板名称
     */
    private static String generateConfigTemplateName(IConfigTemplateBase configTemplateBase) {
        // 段/运用所_批次_车组_模板类型名称_项目名称_编组模式
        // 段/运用所_批次_车组_模板类型名称_项目名称_编组模式_版本
        List<String> names = new ArrayList<>();
        if (!StringUtils.isEmpty(configTemplateBase.getUnitCode())) {
            names.add(configTemplateBase.getDepotName() + "/" + configTemplateBase.getUnitName());
        } else {
            names.add(configTemplateBase.getDepotName() + "/全段");
        }
        addBasicNameParts(configTemplateBase, names);
        return String.join(DELIMITER, names.toArray(new String[0]));
    }

    /**
     * 生成基础模板名称
     */
    private static String generateBaseTemplateName(ITemplateBase templateBase) {
        List<String> names = new ArrayList<>();
        addBasicNameParts(templateBase, names);
        return String.join(DELIMITER, names.toArray(new String[0]));
    }

    public static TemplateType queryTemplateTypeUseThreadCache(String templateTypeCode) {
        return CacheUtil.getDataUseThreadCache(
            "BillUtil.generateTemplateName.getTemplateTypeDetail" + templateTypeCode,
            () -> templateTypeMapper.selectById(templateTypeCode)
        );
    }

    private static void addBasicNameParts(ITemplateBase templateBase, List<String> names) {
        TemplateType templateType = queryTemplateTypeUseThreadCache(templateBase.getTemplateTypeCode());
        names.add(templateType.getTemplateTypeName());

        addTemplateNameParts(
            templateBase,
            names::add
        );

        names.add(integerToString(templateBase.getMarshallingType()));
        if (templateBase instanceof ITemplateSummary) {
            ITemplateSummary templateSummary = (ITemplateSummary) templateBase;
            names.add(templateSummary.getVersion());
        }
    }

    public static TemplateAll transToTemplateAll(TemplateSummaryInfo templateSummaryInfo) {
        TemplateAll templateAll = transTemplateTo(
            templateSummaryInfo,
            TemplateAll::new,
            TemplateAllContent::new,
            TemplateAllLinkContent::new,
            TemplateAllControl::new,
            TemplateAllArea::new
        );
        templateAll.setState(BillTemplateStateEnum.RELEASED);
        return templateAll;
    }

    public static TemplateAll transToTemplateAll(TemplateProcessInfo templateProcessInfo) {
        TemplateAll templateAll = transTemplateTo(
            templateProcessInfo,
            TemplateAll::new,
            TemplateAllContent::new,
            TemplateAllLinkContent::new,
            TemplateAllControl::new,
            TemplateAllArea::new
        );
        templateAll.setState(BillTemplateStateEnum.UNRELEASED);
        return templateAll;
    }

    public static String integerToString(Integer integer) {
        return integer == null ? "" : integer.toString();
    }

    public static <TEMPLATE_INFO extends ITemplateInfo<CONTENT_INFO, LINK, CTRL, AREA>,
        CONTENT_INFO extends ITemplateContentInfo<LINK, CTRL>,
        LINK extends ITemplateLinkContentBase,
        CTRL extends ITemplateControlBase,
        AREA extends ITemplateAreaBase>
    TEMPLATE_INFO transTemplateTo(
        ITemplateInfo<?, ?, ?, ?> sourceTemplate,
        Supplier<TEMPLATE_INFO> newTemplateInfoGetter,
        Supplier<CONTENT_INFO> newContentInfoGetter,
        Supplier<LINK> newLinkGetter,
        Supplier<CTRL> newCtrlGetter,
        Supplier<AREA> newAreaGetter
    ) {
        TEMPLATE_INFO targetTemplate = newTemplateInfoGetter.get();
        BeanUtils.copyProperties(sourceTemplate, targetTemplate, "contents", "areas");
        if (sourceTemplate.getContents() != null) {
            List<CONTENT_INFO> targetContents = sourceTemplate.getContents().stream().map(sourceContent -> {
                CONTENT_INFO targetContent = newContentInfoGetter.get();
                BeanUtils.copyProperties(sourceContent, targetContent, "linkContents", "controls");
                if (sourceContent.getLinkContents() != null) {
                    targetContent.setLinkContents(sourceContent.getLinkContents().stream().map(sourceLinkContent -> {
                        LINK targetLinkContent = newLinkGetter.get();
                        BeanUtils.copyProperties(sourceLinkContent, targetLinkContent);
                        return targetLinkContent;
                    }).collect(Collectors.toList()));
                }

                if (sourceContent.getControls() != null) {
                    targetContent.setControls(sourceContent.getControls().stream().map(sourceControl -> {
                        CTRL targetControl = newCtrlGetter.get();
                        BeanUtils.copyProperties(sourceControl, targetControl);
                        return targetControl;
                    }).collect(Collectors.toList()));
                }

                return targetContent;
            }).collect(Collectors.toList());
            targetTemplate.setContents(targetContents);
        }

        if (sourceTemplate.getAreas() != null) {
            List<AREA> targetAreas = sourceTemplate.getAreas().stream().map(sourceArea -> {
                AREA targetArea = newAreaGetter.get();
                BeanUtils.copyProperties(sourceArea, targetArea);
                return targetArea;
            }).collect(Collectors.toList());
            targetTemplate.setAreas(targetAreas);
        }
        return targetTemplate;
    }

    // private static <TEMPLATE_INFO> TEMPLATE_INFO getNewInstance(Class<TEMPLATE_INFO> tClass) {
    //     try {
    //         return tClass.newInstance();
    //     } catch (InstantiationException | IllegalAccessException e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    public static <
        TEMPLATE_INFO extends ITemplateInfo<CONTENT_INFO, LINK, CTRL, AREA>,
        TEMPLATE extends ITemplateBase,
        CONTENT_INFO extends ITemplateContentInfo<LINK, CTRL>,
        CONTENT extends TemplateContentBase,
        LINK extends TemplateLinkContentBase,
        CTRL extends TemplateControlBase,
        AREA extends TemplateAreaBase
        >
    TEMPLATE_INFO transToTemplateInfo(
        TEMPLATE template,
        BaseMapper<CONTENT> contentMapper,
        BaseMapper<LINK> linkMapper,
        BaseMapper<CTRL> ctrlMapper,
        BaseMapper<AREA> areaMapper,
        Supplier<TEMPLATE_INFO> newTemplateInfoGetter,
        Supplier<CONTENT_INFO> newContentInfoGetter
    ) {
        TEMPLATE_INFO templateSummaryInfo = newTemplateInfoGetter.get();
        BeanUtils.copyProperties(template, templateSummaryInfo);
        String templateId = template.getTemplateId();
        List<CONTENT> contents = MybatisPlusUtils.selectList(
            contentMapper,
            eqParam(TemplateContentBase::getTemplateId, templateId)
        );
        List<CONTENT_INFO> contentInfoList = contents.stream().map(v -> {
            CONTENT_INFO contentInfo = newContentInfoGetter.get();
            BeanUtils.copyProperties(v, contentInfo);
            contentInfo.setControls(new ArrayList<>());
            contentInfo.setLinkContents(new ArrayList<>());
            return contentInfo;
        }).collect(Collectors.toList());
        templateSummaryInfo.setContents(contentInfoList);

        List<String> contentIds = contents.stream().map(TemplateContentBase::getId).collect(Collectors.toList());
        Map<String, CONTENT_INFO> contentMap = CommonUtils.collectionToMap(contentInfoList, ITemplateContentBase::getId);

        List<LINK> linkContents = contentIds.size() > 0 ? MybatisPlusUtils.selectList(
            linkMapper,
            inParam(
                TemplateLinkContentBase::getContentId,
                contentIds
            )
        ) : new ArrayList<>();

        for (LINK linkContent : linkContents) {
            if (contentMap.containsKey(linkContent.getContentId())) {
                contentMap.get(linkContent.getContentId()).getLinkContents().add(linkContent);
            }
        }

        List<CTRL> linkControls = contentIds.size() > 0 ? MybatisPlusUtils.selectList(
            ctrlMapper,
            inParam(
                TemplateControlBase::getContentId,
                contentIds
            )
        ) : new ArrayList<>();
        for (CTRL linkControl : linkControls) {
            if (contentMap.containsKey(linkControl.getContentId())) {
                contentMap.get(linkControl.getContentId()).getControls().add(linkControl);
            }
        }

        List<AREA> areas = MybatisPlusUtils.selectList(
            areaMapper,
            eqParam(TemplateAreaBase::getTemplateId, templateId)
        );
        templateSummaryInfo.setAreas(areas);
        return templateSummaryInfo;
    }

    public static <
        TEMPLATE_INFO extends ITemplateInfo<CONTENT_INFO, LINK, CTRL, AREA>,
        TEMPLATE extends ITemplateBase,
        CONTENT_INFO extends ITemplateContentInfo<LINK, CTRL>,
        CONTENT extends ITemplateContentBase,
        LINK extends ITemplateLinkContentBase,
        CTRL extends ITemplateControlBase,
        AREA extends ITemplateAreaBase
        >
    void insertTemplateInfo(
        TEMPLATE_INFO templateInfo,
        IService<TEMPLATE> templateService,
        IService<CONTENT> contentService,
        IService<LINK> linkService,
        IService<CTRL> ctrlService,
        IService<AREA> areaService,
        Function<TEMPLATE_INFO, TEMPLATE> transToTemplate,
        Function<CONTENT_INFO, CONTENT> transToContent
    ) {
        templateService.insert(transToTemplate.apply(templateInfo));
        if (templateInfo.getContents() != null) {
            List<CONTENT> allContents = new ArrayList<>();
            List<LINK> allLinkContents = new ArrayList<>();
            List<CTRL> allControls = new ArrayList<>();
            for (CONTENT_INFO contentInfo : templateInfo.getContents()) {
                allContents.add(transToContent.apply(contentInfo));
                if (contentInfo.getLinkContents() != null) {
                    allLinkContents.addAll(contentInfo.getLinkContents());
                }
                if (contentInfo.getControls() != null) {
                    allControls.addAll(contentInfo.getControls());
                }
            }
            if (allContents.size() > 0) {
                contentService.insertBatch(allContents);
            }
            if (allLinkContents.size() > 0) {
                linkService.insertBatch(allLinkContents);
            }
            if (allControls.size() > 0) {
                ctrlService.insertBatch(allControls);
            }
        }
        if (templateInfo.getAreas() != null && templateInfo.getAreas().size() > 0) {
            areaService.insertBatch(templateInfo.getAreas());
        }
    }

    private static String getNewId() {
        return UUID.randomUUID().toString();
    }

    public static Function<String, String> getIdMapper() {
        Map<String, String> contentIdMap = new LinkedHashMap<>();
        return (oldId) -> {
            if (!contentIdMap.containsKey(oldId)) {
                contentIdMap.put(oldId, getNewId());
            }
            return contentIdMap.get(oldId);
        };
    }

    public static <
        TEMPLATE_INFO extends ITemplateInfo<CONTENT_INFO, LINK, CTRL, AREA>,
        CONTENT_INFO extends ITemplateContentInfo<LINK, CTRL>,
        LINK extends ITemplateLinkContentBase,
        CTRL extends ITemplateControlBase,
        AREA extends ITemplateAreaBase
        >
    TEMPLATE_INFO getIdResetCopyNew(
        TEMPLATE_INFO source,
        Supplier<TEMPLATE_INFO> newTemplateGetter,
        Supplier<CONTENT_INFO> newContentInfoGetter,
        Supplier<LINK> newLinkContentGetter,
        Supplier<CTRL> newControlGetter,
        Supplier<AREA> newAreaGetter
    ) {
        TEMPLATE_INFO target = newTemplateGetter.get();
        BeanUtils.copyProperties(source, target, "contents", "areas");
        String templateId = getNewId();
        target.setTemplateId(templateId);
        if (source.getContents() != null) {
            List<CONTENT_INFO> newContents = new ArrayList<>();
            target.setContents(newContents);
            Function<String, String> getContentId = getIdMapper();
            for (CONTENT_INFO content : source.getContents()) {
                CONTENT_INFO newContent = newContentInfoGetter.get();
                newContents.add(newContent);
                BeanUtils.copyProperties(content, newContent, "linkContents", "controls");
                String id = getContentId.apply(content.getId());
                newContent.setId(id);
                newContent.setTemplateId(templateId);
                if (content.getLinkContents() != null) {
                    List<LINK> newLinkContents = new ArrayList<>();
                    newContent.setLinkContents(newLinkContents);
                    for (LINK linkContent : content.getLinkContents()) {
                        LINK newLinkContent = newLinkContentGetter.get();
                        newLinkContents.add(newLinkContent);
                        BeanUtils.copyProperties(linkContent, newLinkContent);
                        newLinkContent.setId(getNewId());
                        newLinkContent.setLinkContentId(getContentId.apply(linkContent.getLinkContentId()));
                        newLinkContent.setContentId(id);
                    }
                }
                if (content.getControls() != null) {
                    List<CTRL> newControls = new ArrayList<>();
                    newContent.setControls(newControls);
                    for (CTRL control : content.getControls()) {
                        CTRL newControl = newControlGetter.get();
                        newControls.add(newControl);
                        BeanUtils.copyProperties(control, newControl);
                        newControl.setId(getNewId());
                        newControl.setContentId(id);
                    }
                }
            }
        }
        if (source.getAreas() != null) {
            List<AREA> newAreas = new ArrayList<>();
            target.setAreas(newAreas);
            for (AREA area : source.getAreas()) {
                AREA newArea = newAreaGetter.get();
                newAreas.add(newArea);
                BeanUtils.copyProperties(area, newArea);
                String areaId = getNewId();
                newArea.setId(areaId);
                newArea.setTemplateId(templateId);
            }
        }
        return target;
    }

    /**
     * 生成根据单据id删除单据单元格关联信息表的sql
     *
     * @param contentInfoClass 使用泛型类可在编译期间校验父子关系
     */
    public static <
        CONTENT_INFO extends ITemplateContentInfo<LINK, ?>,
        LINK extends ITemplateLinkContentBase
        > String generateDeleteContentLinkTableByTemplateIdSql(
        Class<CONTENT_INFO> contentInfoClass,
        Class<LINK> linkClass
    ) {
        return generateDeleteContentSubTableByTemplateIdSql(contentInfoClass, linkClass);
    }

    /**
     * 生成根据单据id删除单据单元格控制信息表的sql
     *
     * @param contentInfoClass 使用泛型类可在编译期间校验父子关系
     */
    public static <
        CONTENT_INFO extends ITemplateContentInfo<?, CTRL>,
        CTRL extends ITemplateControlBase
        > String generateDeleteContentControlTableByTemplateIdSql(
        Class<CONTENT_INFO> contentInfoClass,
        Class<CTRL> ctrlClass
    ) {
        return generateDeleteContentSubTableByTemplateIdSql(contentInfoClass, ctrlClass);
    }

    private static <
        CONTENT_INFO extends ITemplateContentInfo<?, ?>
        > Class<? extends TemplateContentBase> findContentClass(Class<CONTENT_INFO> contentInfoClass) {
        Class<? super CONTENT_INFO> superclass = contentInfoClass;
        // 找到了或者父类为null时退出循环
        while (!(superclass == null || ReflectUtil.getTableName(superclass) != null)) {
            superclass = superclass.getSuperclass();
        }
        //noinspection unchecked
        return (Class<? extends TemplateContentBase>) superclass;
    }

    private static <
        CONTENT_INFO extends ITemplateContentInfo<?, ?>,
        CONTENT_SUB
        > String generateDeleteContentSubTableByTemplateIdSql(
        Class<CONTENT_INFO> contentInfoClass,
        Class<CONTENT_SUB> contentSubClass
    ) {
        String contentTableName = ReflectUtil.getTableName(findContentClass(contentInfoClass));
        Assert.notEmptyInnerFatal(contentTableName, contentInfoClass + "不对应数据库表");
        String contentSubTableName = ReflectUtil.getTableName(contentSubClass);
        Assert.notEmptyInnerFatal(contentSubTableName, contentSubClass + "不对应数据库表");
        return new SQL()
            .DELETE_FROM(contentSubTableName)
            .WHERE("S_CONTENTID IN (" +
                new SQL()
                    .SELECT("S_ID")
                    .FROM(contentTableName)
                    .WHERE("S_TEMPLATEID = #{templateId}")
                    .toString() +
                ")"
            ).toString();
    }

    /**
     * 保存单据详情（真删除）
     *
     * @param templateInfoList         要保存的单据详情列表
     * @param newKeyGetter             获取新key对象
     * @param templateKeyClass         单据key对应类
     * @param transToTemplate          转换为单据表对象
     * @param templateIdGetter         单据id的get方法，必须直接引用，否则获取不到id字段信息
     * @param templateService          单据表对象的service对象
     * @param newIdResetEntityGetter   获取id重置过的新单据详情对象
     * @param extraPropertySetter      保存前额外的属性设置
     * @param doTemplateInfoGroupSaver 进行最终的数据持久化，参数1为要删除的单据id列表，参数2为要插入的单据详情列表
     * @param <TEMPLATE_INFO>          单据详情类
     * @param <TEMPLATE>               单据表类
     * @param <CONTENT_INFO>           单据单元格详情类
     * @param <LINK>                   单据单元格关联信息表类
     * @param <CTRL>                   单据单元格控制信息表类
     * @param <AREA>                   单据区域表类
     * @param <TEMPLATE_KEY>           单据key类
     * @return 新单据的id列表
     */
    public static <
        TEMPLATE_INFO extends ITemplateInfo<CONTENT_INFO, LINK, CTRL, AREA>,
        TEMPLATE extends TEMPLATE_KEY,
        CONTENT_INFO extends ITemplateContentInfo<LINK, CTRL>,
        LINK extends ITemplateLinkContentBase,
        CTRL extends ITemplateControlBase,
        AREA extends ITemplateAreaBase,
        TEMPLATE_KEY extends IBaseTemplateUniqueKey
        >
    List<String> saveTemplateInfoGroup(
        List<TEMPLATE_INFO> templateInfoList,
        Class<TEMPLATE_KEY> templateKeyClass,
        Supplier<TEMPLATE_KEY> newKeyGetter,
        IService<TEMPLATE> templateService,
        SerializableFunction<TEMPLATE, String> templateIdGetter,
        Function<TEMPLATE_INFO, TEMPLATE> transToTemplate,
        Function<TEMPLATE_INFO, TEMPLATE_INFO> newIdResetEntityGetter,
        Consumer<TEMPLATE_INFO> extraPropertySetter,
        BiConsumer<List<String>, List<TEMPLATE_INFO>> doTemplateInfoGroupSaver
    ) {
        // if (templateInfoList.size() == 0) {
        //     throw RestRequestException.normalFail("无保存数据");
        // }
        // for (TEMPLATE_INFO templateForSave : templateInfoList) {
        //     templateForSave.setTemplatePath(SsjsonFileUtils.saveTemplateSsjsonFile(templateForSave));
        // }
        Map<TEMPLATE_KEY, List<TEMPLATE_INFO>> groups = templateInfoList.stream().collect(Collectors.groupingBy((templateForSave) -> {
            TEMPLATE_KEY key = newKeyGetter.get();
            BeanUtils.copyProperties(templateForSave, key);
            CommonUtils.emptyStringToNull(key);
            return key;
        }));

        if (groups.size() > 1) {
            throw RestRequestException.fatalFail("觉察到条件不一致的单据，无法保存！");
        }

        Map.Entry<TEMPLATE_KEY, List<TEMPLATE_INFO>> group = new ArrayList<>(groups.entrySet()).get(0);

        Function<TEMPLATE_INFO, String> templateForSaveIdGetter = (v) -> templateIdGetter.apply(transToTemplate.apply(v));

        Set<String> existIds = group.getValue().stream().map(templateForSaveIdGetter).filter(StringUtils::hasText).collect(Collectors.toSet());
        for (TEMPLATE_INFO templateForSave : group.getValue()) {
            if (!StringUtils.hasText(templateForSaveIdGetter.apply(templateForSave))) {// 没有id说明是新增，需要根据key检查唯一性
                if (MybatisPlusUtils.selectExist(
                    templateService,
                    MybatisPlusUtils.allMatch(
                        getEqColumnParamsFromEntity(group.getKey(), true, templateKeyClass)
                    ),
                    existIds.size() > 0 ? notInParam(templateIdGetter, existIds) : null
                )) {
                    throw RestRequestException.normalFail("单据新增失败，单据已存在，请刷新");
                }
            } else {// 有id说明是修改，需要检查当前单据是否存在
                if (!selectExist(
                    templateService,
                    eqParam(templateIdGetter, templateForSave.getTemplateId())
                )) {
                    throw RestRequestException.normalFail("单据更新失败，单据不存在，请刷新");
                }
            }
        }

        List<TEMPLATE> existTemplateList = MybatisPlusUtils.selectList(
            templateService,
            getEqColumnParamsFromEntity(group.getKey(), true, templateKeyClass)
        );

        List<String> deleteTemplateIds = existTemplateList.stream().map(templateIdGetter).collect(Collectors.toList());
        List<TEMPLATE_INFO> newTemplates = templateInfoList.stream().map(template -> {
            TEMPLATE_INFO newTemplate = newIdResetEntityGetter.apply(template);
            if (extraPropertySetter != null) {
                extraPropertySetter.accept(newTemplate);
            }
            // 生成templateNo
            newTemplate.setTemplateNo(BillUtil.generateTemplateNo(newTemplate));
            // 生成templateName
            newTemplate.setTemplateName(BillUtil.generateTemplateName(newTemplate));

            return newTemplate;
        }).collect(Collectors.toList());

        doTemplateInfoGroupSaver.accept(deleteTemplateIds, newTemplates);
        return newTemplates.stream().map(templateForSaveIdGetter).collect(Collectors.toList());
    }

    public static void validTemplateBasicInfo(ITemplateBase templateBase) {
        if (StringUtils.hasText(templateBase.getItemCode()) && !StringUtils.hasText(templateBase.getItemName())) {
            throw RestRequestException.fatalFail("项目编码不为空时，项目名称不能为空！");
        }
    }

}
