package com.ydzbinfo.emis.utils.upload;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 增加了额外载荷的文件上传信息
 *
 * @author 张天可
 * @since 2021/7/5
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UploadedFileInfoWithPayload<PAYLOAD> extends UploadedFileInfo {
    /**
     * 额外载荷
     */
    private PAYLOAD payload;
}
