package com.ydzbinfo.emis.utils.upload;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置
 *
 * @author 张天可
 * @since 2021/11/22
 */
@Data
@Component
@ConfigurationProperties("ydzb.upload")
public class UploadProperties {
    /**
     * 实际文件上传基本路径
     */
    private String uploadFilePath;

    /**
     * 文件映射前缀
     */
    private String uploadFileHandlerPath;
}
