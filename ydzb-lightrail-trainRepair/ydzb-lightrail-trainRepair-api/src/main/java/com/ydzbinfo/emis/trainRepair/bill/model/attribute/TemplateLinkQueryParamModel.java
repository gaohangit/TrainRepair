package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TemplateLinkQueryParamModel implements Serializable {

    /**
     * 主键
     */
    private String id;

    /**
     * 查询条件编码
     */
    private String queryCode;

    /**
     * 单据类型编码
     */
    private String templateTypeCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人编码
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 排序
     */
    private String sort;

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
