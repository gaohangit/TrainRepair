package com.ydzbinfo.emis.common.bill.billconfig.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.common.bill.utils.BillUtil;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcLinkControl;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 单据模板配置内容卡控表 Mapper 接口
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
public interface TemplateProcLinkControlMapper extends BaseMapper<TemplateProcLinkControl> {

    /**
     * 根据模板id删除
     *
     * @param templateId
     * @return
     */
    @DeleteProvider(type = SqlProvider.class, method = "deleteByTemplateId")
    int deleteByTemplateId(@Param("templateId") String templateId);

    class SqlProvider {
        public String deleteByTemplateId(String templateId){
            return BillUtil.generateDeleteContentControlTableByTemplateIdSql(
                TemplateProcessContentInfo.class,
                TemplateProcLinkControl.class
            );
        }
    }
}
