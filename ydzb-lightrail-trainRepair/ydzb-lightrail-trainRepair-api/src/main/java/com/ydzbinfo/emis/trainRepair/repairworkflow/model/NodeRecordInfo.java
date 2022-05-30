package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.enums.UploadFileType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecordExtraInfo;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfoWithPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 完整节点记录信息
 *
 * @author 张天可
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeRecordInfo extends NodeRecord {

    /**
     * 是否为跳过记录
     */
    private Boolean skip;

    /**
     * 是否为补卡记录
     */
    private Boolean disposeAfterSkip;

    /**
     * 刷卡上传图片地址
     */
    private List<UploadedFileInfoWithPayload<UploadFileType>> pictureUrls;

    /**
     * 刷卡上传视频地址
     */
    private List<UploadedFileInfoWithPayload<UploadFileType>> videoUrls;

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}

