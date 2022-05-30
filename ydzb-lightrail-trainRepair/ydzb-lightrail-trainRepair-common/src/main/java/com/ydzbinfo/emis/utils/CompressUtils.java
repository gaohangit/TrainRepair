package com.ydzbinfo.emis.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.compress.archivers.zip.X000A_NTFS;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 压缩数据工具类
 *
 * @author 张天可
 * @since 2022/2/14
 */
public class CompressUtils {

    protected static final Logger logger = LoggerFactory.getLogger(CompressUtils.class);

    public static class ZipBuilder {
        private boolean finished = false;
        private final List<ZipPart> zipParts = new ArrayList<>();

        @Data
        @AllArgsConstructor
        private static class ZipPart {
            private ZipArchiveEntry zipArchiveEntry;
            private InputStream inputStream;
        }

        private ZipBuilder() {

        }

        public ZipBuilder putFile(String path, InputStream fileInputStream) {
            if (finished) {
                throw new RuntimeException("FATAL: 数据流已关闭");
            }
            zipParts.add(createZipPart(path, fileInputStream));
            return this;
        }

        private ZipPart createZipPart(String path, InputStream fileInputStream) {
            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(path);
            // 兼容winRAR旧版本
            X000A_NTFS zipExtraFieldNTFS = new X000A_NTFS();
            Date now = new Date();
            zipExtraFieldNTFS.setCreateJavaTime(now);
            zipExtraFieldNTFS.setModifyJavaTime(now);
            zipArchiveEntry.addExtraField(zipExtraFieldNTFS);
            return new ZipPart(zipArchiveEntry, fileInputStream);
        }

        public void output(OutputStream outputStream) {
            StopWatch stopWatch = new StopWatch("ZipBuilder.output");
            try {
                try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream)) {
                    String fileNameEncoding = "GBK";
                    zipArchiveOutputStream.setEncoding(fileNameEncoding);
                    zipArchiveOutputStream.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS);
                    zipArchiveOutputStream.setFallbackToUTF8(true);
                    for (ZipPart zipPart : zipParts) {
                        try {
                            stopWatch.start("压缩:" + zipPart.getZipArchiveEntry().getName());
                            zipArchiveOutputStream.putArchiveEntry(zipPart.getZipArchiveEntry());
                            IOUtils.copy(zipPart.getInputStream(), zipArchiveOutputStream);
                            stopWatch.stop();
                        } finally {
                            zipArchiveOutputStream.closeArchiveEntry();
                        }
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(stopWatch.prettyPrint());
                }
            } catch (IOException e) {
                throw new RuntimeException("流写入失败", e);
            } finally {
                this.finished = true;
            }
        }
    }

    public static ZipBuilder zipBuilder() {
        return new ZipBuilder();
    }
}
