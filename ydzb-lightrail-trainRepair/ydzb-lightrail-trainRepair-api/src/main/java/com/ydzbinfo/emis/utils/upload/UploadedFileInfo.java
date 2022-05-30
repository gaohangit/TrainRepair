package com.ydzbinfo.emis.utils.upload;

import lombok.Data;

/**
 * @author 张天可
 * @since 2021/7/5
 */
@Data
public class UploadedFileInfo {
    /**
     * 相对url路径
     */
    private String relativeUrl;

    /**
     * 相对文件存储路径
     */
    private String relativePath;

    /**
     * 原始文件名（不可靠）
     */
    private String oldName;

    /**
     * 文件对应参数名
     */
    private String paramName;

    /**
     * 是否多个文件使用相同的paramName
     */
    private Boolean isOfList;

    /**
     * 相同paramName文件组别下的数组下标
     */
    private Integer indexOfList;
}
