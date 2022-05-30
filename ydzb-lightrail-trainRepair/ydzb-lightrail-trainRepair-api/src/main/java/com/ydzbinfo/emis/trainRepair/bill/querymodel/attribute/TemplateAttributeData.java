package com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute;

import lombok.Data;

/***
 * 获取单据属性 数据库结构   fs  2021-04-17迁移
 * 
 **/
@Data
public class TemplateAttributeData {

	// 属性类型编码
	private String attributeTypeCode;
	// 属性类型名称
	private String attributeTypeName;
	// 排序
	private String typeSort;

	// 属性编码
	private String attributeCode;
	// 属性名称
	private String attributeName;

	// 是否关联属性 1--是 0 --否
	private String linkAttr;
	// 排序
	private String sort;

	//属性对应的单元格是否允许修改（1：允许；0：不允许）
	private String isChange;
}
