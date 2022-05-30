package com.ydzbinfo.emis.utils;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * md5工具类
 *
 * @author 张天可
 */
public class Md5CaculateUtil {

    protected static final Logger logger = LoggerFactory.getLogger(Md5CaculateUtil.class);

    /**
     * 获取一个文件流的md5值(可处理大文件)
     *
     * @return md5 value
     */
    public static String getMD5(InputStream inputStream) {
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            logger.error("获取文件md5值出错", e);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("关闭输入流出错", e);
            }
        }
    }

}
