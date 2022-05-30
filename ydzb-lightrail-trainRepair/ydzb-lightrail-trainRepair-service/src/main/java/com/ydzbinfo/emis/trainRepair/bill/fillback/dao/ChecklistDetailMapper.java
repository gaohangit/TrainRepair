package com.ydzbinfo.emis.trainRepair.bill.fillback.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 记录单详细信息表 Mapper 接口
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
public interface ChecklistDetailMapper extends BaseMapper<ChecklistDetail> {
void deleteList(List<ChecklistDetail> list);
    List<ChecklistDetail>  selectByAttribute(@Param("checklistSummaryId") String checklistSummaryId,@Param("attributeCodeList") List<String> attributeCodeList);
    void deleteByPrimaryKey(ChecklistDetail detail);
    void updatePrimaryKey(ChecklistDetail detail);
}
