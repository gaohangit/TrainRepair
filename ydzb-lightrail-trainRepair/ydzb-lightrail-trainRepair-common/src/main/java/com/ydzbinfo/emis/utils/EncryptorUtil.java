package com.ydzbinfo.emis.utils;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * @author 张天可
 * @since 2021/7/13
 */
public class EncryptorUtil {
    public static void main(String[] args) {
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword("abc123");//配置文件中的秘钥
        System.out.println("加密后账户：" + encryptor.encrypt("workprocess"));
        System.out.println("加密后密码：" + encryptor.encrypt("ictsdcyus"));
    }
}
