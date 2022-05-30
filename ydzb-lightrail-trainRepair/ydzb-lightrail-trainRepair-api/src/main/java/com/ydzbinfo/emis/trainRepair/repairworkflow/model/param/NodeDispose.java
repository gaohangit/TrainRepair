package com.ydzbinfo.emis.trainRepair.repairworkflow.model.param;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.enums.UploadFileType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRun;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfoWithPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author gaohan
 * @description 节点处理
 * @createDate 2021/5/18 18:14
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class NodeDispose extends WorkerInfo {

    /**
     * 流程id
     */
    private String flowId;

    /**
     * 流程运行id
     */
    private String flowRunId;

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 流程运行基本信息
     */
    private FlowRun flowRun;

    /**
     * 已上传文件信息
     */
    private List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfos;

    /**
     * 是否确认要跳过节点
     */
    private Boolean skipFlag;

    /**
     * 是否确认忽视最小间隔
     */
    private Boolean confirmIgnoreMinIntervalRestrict;

    /**
     * 是否确认覆盖刷卡
     */
    private Boolean confirmCoverNodeRecord;

    public NodeDispose() {
        this.skipFlag = false;
        this.confirmIgnoreMinIntervalRestrict = false;
        this.confirmCoverNodeRecord = false;
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }

}
