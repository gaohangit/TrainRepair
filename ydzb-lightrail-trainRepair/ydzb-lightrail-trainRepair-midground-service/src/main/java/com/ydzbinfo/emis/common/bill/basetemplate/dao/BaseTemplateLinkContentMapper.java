package com.ydzbinfo.emis.common.bill.basetemplate.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.common.bill.utils.BillUtil;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplateLinkContent;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 基础单据模板关联内容表 Mapper 接口
 * </p>
 *
 * @author 张天可
 * @since 2021-12-24
 */
public interface BaseTemplateLinkContentMapper extends BaseMapper<BaseTemplateLinkContent> {

    /**
     * 根据模板id删除
     */
    @DeleteProvider(type = SqlProvider.class, method = "deleteByTemplateId")
    int deleteByTemplateId(@Param("templateId") String templateId);

    class SqlProvider {
        public String deleteByTemplateId(String templateId) {
            return BillUtil.generateDeleteContentLinkTableByTemplateIdSql(
                BaseTemplateContentInfo.class,
                BaseTemplateLinkContent.class
            );
        }
    }
}
