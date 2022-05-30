package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.enums.UploadFileType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRun;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunRecord;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfoWithPayload;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 高晗
 * @description 关键作业处理
 * @createDate 2021/6/24 15:28
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("关键作业流程运行信息实体")
public class KeyWorkFlowRunInfo extends FlowRun implements IKeyWorkBase {

    /**
     * 作业内容
     */
    private String content;

    /**
     * 功能分类
     */
    private String functionClass;

    /**
     * 部件(构型)节点编码
     */
    private String batchBomNodeCode;

    /**
     * 类型
     */
    private String keyWorkType;

    /**
     * 辆序
     */
    private List<String> carNoList;

    /**
     * 位置
     */
    private String position;

    /**
     * 作业条件
     */
    private String workEnv;

    /**
     * 备注
     */
    private String remark;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 班组code
     */
    private String teamCode;
    /**
     * 班组名称
     */
    private String teamName;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 故障id
     */
    private String faultId;

    /**
     * 图片路径
     */
    private List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFilePathInfos;


    private List<NodeWithRecord> nodeWithRecords;

    /**
     * 已上传文件信息
     */
    private List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfos;

    /**
     * 驳回信息
     */
    private FlowRunRecordInfo flowRunRecordInfo;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 是否可撤销
     */
    private boolean revoke = false;
}
