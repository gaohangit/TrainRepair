package com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TreePartNodeModel {
    @ApiModelProperty("对应SNodeCode")
    private String code;
    @ApiModelProperty("对应SNodeName")
    private String name;
    /**
     *  根据cKeyFlag=‘1’判断“是否为关键部件”，如果是，用sNodeCode去筛选履历结构的parttemplateid，获取{partidentid（部件ID）和serialnum（序列号）}
     */
    private String keyFlag;
    private String partsTypeId;
    private String partsTypeName;
    @ApiModelProperty("位数")
    private String locationNum;
    @ApiModelProperty("功能分类编码")
    private String functionClassCode;
    private String level;
    private List<TreePartNodeModel> children;
}
