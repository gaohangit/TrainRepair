package com.ydzbinfo.emis.trainRepair.bill.fillback.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChkDetailLinkContent;

import java.util.List;

/**
 * <p>
 * 单据回填内容关联内容表 Mapper 接口
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
public interface ChkDetailLinkContentMapper extends BaseMapper<ChkDetailLinkContent> {
    void deleteList(List<String> list);
}
