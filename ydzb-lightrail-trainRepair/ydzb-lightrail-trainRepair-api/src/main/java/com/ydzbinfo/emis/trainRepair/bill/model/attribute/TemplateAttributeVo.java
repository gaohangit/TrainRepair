package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TemplateAttributeVo implements Serializable {
    private static final long serialVersionUID = 3978187184944349665L;
    @ApiModelProperty("属性编码")
    private String attributeCode;
    @ApiModelProperty("属性类型")
    private String attributeTypeName;
    @ApiModelProperty("属性名称")
    private String attributeName;
    @ApiModelProperty("属性模式")
    private String attributeModeName;
    @ApiModelProperty("属性模式说明")
    private String note;
    @ApiModelProperty("属性取值范围")
    private String attributeRangeValue;
    @ApiModelProperty("属性说明")
    private String attributeNote;
    @ApiModelProperty("是否关联属性")
    private String linkAttr;
    @ApiModelProperty("是否回填验证")
    private String backFillVerify;

}
