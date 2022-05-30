package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseNode;
import lombok.Data;

import java.util.List;

/**
 * @author 张天可
 * @since 2021/9/30
 */
@Data
public class NodeDisposeCheckResult {
    /**
     * 是否需要确认跳过节点
     */
    private Boolean needConfirmSkippedNodes;
    /**
     * 跳过的节点基本信息
     */
    private List<BaseNode> needSkippedNodes;
    /**
     * 是否需要确认忽视最小间隔
     */
    private Boolean needConfirmIgnoreMinIntervalRestrict;
    /**
     * 确认忽视最小间隔展示信息
     */
    private String confirmIgnoreMinIntervalRestrictMessage;
    /**
     * 是否需要确认覆盖刷卡
     */
    private Boolean needConfirmCoverNodeRecord;
    /**
     * 此次刷卡是否为补卡
     */
    private Boolean disposeAfterSkip;

    /**
     * 检查是否通过
     */
    public boolean checkDisposePasted() {
        return !this.needConfirmCoverNodeRecord && !this.needConfirmIgnoreMinIntervalRestrict && !this.needConfirmSkippedNodes;
    }
}
