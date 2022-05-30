package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TreeModel {
    private String id;
    private String code;
    private String name;
    private Integer orderNum;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeModel> children;
    private boolean disabled;
    @ApiModelProperty("配件类型编码 结构分类的地方需要保存这个值用于查询序列号用")
    private String sNodeobjectid;

    public String getId() {
        return id;
    }
    @JSONField(name = "id")
    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }
    @JSONField(name = "nodeCode")
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    @JSONField(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderNum() {
        return orderNum;
    }
    @JSONField(name = "nodeLevel")
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public List<TreeModel> getChildren() {
        return children;
    }

    public void setChildren(List<TreeModel> children) {
        this.children = children;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getsNodeobjectid() {
        return sNodeobjectid;
    }
    @JSONField(name = "pId")
    public void setsNodeobjectid(String sNodeobjectid) {
        this.sNodeobjectid = sNodeobjectid;
    }
}
