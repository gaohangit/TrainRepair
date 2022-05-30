package com.ydzbinfo.emis.utils;



import javax.crypto.Cipher;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;

/**
 * @author ChenChuan
 * @since 20200414
 */
public class RSACryption {
    /**
     * 生成RSA密钥对
     *
     * @return
     */
    public static KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 把私钥转换为Xml格式
     *
     * @param key
     * @return
     */
    public static String encodePrivateKeyToXml(PrivateKey key) {
        if (!RSAPrivateCrtKey.class.isInstance(key)) {
            return null;
        }
        RSAPrivateCrtKey priKey = (RSAPrivateCrtKey) key;
        StringBuilder sb = new StringBuilder();

        sb.append("<RSAKeyValue>");
        sb.append("<Modulus>");
        sb.append(base64Encode(priKey.getModulus().toByteArray()));
        sb.append("</Modulus>");
        sb.append("<Exponent>");
        sb.append(base64Encode(priKey.getPublicExponent().toByteArray()));
        sb.append("</Exponent>");
        sb.append("<P>");
        sb.append(base64Encode(priKey.getPrimeP().toByteArray()));
        sb.append("</P>");
        sb.append("<Q>");
        sb.append(base64Encode(priKey.getPrimeQ().toByteArray()));
        sb.append("</Q>");
        sb.append("<DP>");
        sb.append(base64Encode(priKey.getPrimeExponentP().toByteArray()));
        sb.append("</DP>");
        sb.append("<DQ>");
        sb.append(base64Encode(priKey.getPrimeExponentQ().toByteArray()));
        sb.append("</DQ>");
        sb.append("<InverseQ>");
        sb.append(base64Encode(priKey.getCrtCoefficient().toByteArray()));
        sb.append("</InverseQ>");
        sb.append("<D>");
        sb.append(base64Encode(priKey.getPrivateExponent().toByteArray()));
        sb.append("</D>");
        sb.append("</RSAKeyValue>");
        return sb.toString();
    }

    /**
     * 把公钥转换为Xml格式
     *
     * @param key
     * @return
     */
    public static String encodePublicKeyToXml(PublicKey key) {
        if (!RSAPublicKey.class.isInstance(key)) {
            return null;
        }
        RSAPublicKey pubKey = (RSAPublicKey) key;
        StringBuilder sb = new StringBuilder();
        sb.append("<RSAKeyValue>");
        sb.append("<Modulus>");
        sb.append(base64Encode(pubKey.getModulus().toByteArray()));
        sb.append("</Modulus>");
        sb.append("<Exponent>");
        sb.append(base64Encode(pubKey.getPublicExponent().toByteArray()));
        sb.append("</Exponent>");
        sb.append("</RSAKeyValue>");

        return sb.toString();
    }

    /**
     * 导入Xml格式的私钥
     *
     * @param xmlPriKey
     * @return
     */
    public static PrivateKey decodePrivateKeyFromXml(String xmlPriKey) {
        xmlPriKey = xmlPriKey.replaceAll("\r", "").replaceAll("\n", "");
        BigInteger modulus = new BigInteger(1,
                base64Decode(getMiddleString(xmlPriKey, "<Modulus>", "</Modulus>")));

        BigInteger publicExponent = new BigInteger(1,
                base64Decode(getMiddleString(xmlPriKey, "<Exponent>", "</Exponent>")));

        BigInteger privateExponent = new BigInteger(1,
                base64Decode(getMiddleString(xmlPriKey, "<D>", "</D>")));

        BigInteger primeP = new BigInteger(1,
                base64Decode(getMiddleString(xmlPriKey, "<P>", "</P>")));

        BigInteger primeQ = new BigInteger(1,
                base64Decode(getMiddleString(xmlPriKey, "<Q>", "</Q>")));

        BigInteger primeExponentP = new BigInteger(1,
                base64Decode(getMiddleString(xmlPriKey, "<DP>", "</DP>")));

        BigInteger primeExponentQ = new BigInteger(1,
                base64Decode(getMiddleString(xmlPriKey, "<DQ>", "</DQ>")));

        BigInteger crtCoefficient = new BigInteger(1,
                base64Decode(getMiddleString(xmlPriKey, "<InverseQ>", "</InverseQ>")));

        RSAPrivateCrtKeySpec rsaPriKey = new RSAPrivateCrtKeySpec(modulus,
                publicExponent, privateExponent, primeP, primeQ,
                primeExponentP, primeExponentQ, crtCoefficient);

        KeyFactory keyf;
        try {
            keyf = KeyFactory.getInstance("RSA");
            return keyf.generatePrivate(rsaPriKey);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 导入Xml格式的公钥
     *
     * @param xmlPubKey
     * @return
     */
    public static PublicKey decodePublicKeyFromXml(String xmlPubKey) {
        xmlPubKey = xmlPubKey.replaceAll("\r", "").replaceAll("\n", "");
        BigInteger modulus = new BigInteger(1,
                base64Decode(getMiddleString(xmlPubKey, "<Modulus>", "</Modulus>")));

        BigInteger publicExponent = new BigInteger(1,
                base64Decode(getMiddleString(xmlPubKey, "<Exponent>", "</Exponent>")));

        RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, publicExponent);

        KeyFactory keyf;
        try {
            keyf = KeyFactory.getInstance("RSA");
            return keyf.generatePublic(rsaPubKey);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 用公钥加密
     *
     * @param data
     * @param xmlPubKey
     * @return
     */
    public static String encryptData(String data, String xmlPubKey) {
        try {
            byte[] dataByte = data.getBytes("utf-8");
            PublicKey pubKey = decodePublicKeyFromXml(xmlPubKey);
            byte[] encryptedDataByte = encryptData(dataByte, pubKey);
            return base64Encode(encryptedDataByte).replace("\n","").replace("\r","");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 用公钥加密
     *
     * @param data
     * @param pubKey
     * @return
     */
    public static byte[] encryptData(byte[] data, PublicKey pubKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥解密
     *
     * @param encryptedData
     * @param xmlPriKey
     * @return
     */
    public static String decryptData(String encryptedData, String xmlPriKey) {
        try {
            byte[] encryptedDataByte = base64Decode(encryptedData);
            PrivateKey priKey = decodePrivateKeyFromXml(xmlPriKey);
            byte[] dataByte = decryptData(encryptedDataByte, priKey);
            return new String(dataByte, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥解密
     *
     * @param encryptedData
     * @param priKey
     * @return
     */
    public static byte[] decryptData(byte[] encryptedData, PrivateKey priKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 根据指定私钥对数据进行签名(默认签名算法为"SHA1withRSA")
     *
     * @param data 要签名的数据
     * @param xmlPriKey 私钥
     * @return
     */
    public static String signData(String data, String xmlPriKey) {
        try {
            byte[] dataByte = data.getBytes("utf-8");
            PrivateKey priKey = decodePrivateKeyFromXml(xmlPriKey);
            byte[] signByte = signData(dataByte, priKey);
            return base64Encode(signByte);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 根据指定私钥和算法对数据进行签名(默认签名算法为"SHA1withRSA")
     *
     * @param data 要签名的数据
     * @param priKey 私钥
     * @return
     */
    public static byte[] signData(byte[] data, PrivateKey priKey) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(priKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 用指定的公钥进行签名验证(默认签名算法为"SHA1withRSA")
     *
     * @param data 数据
     * @param sign 签名结果
     * @param xmlPubKey 公钥
     * @return
     */
    public static boolean verifySign(String data, String sign, String xmlPubKey) {
        try {
            byte[] dataByte = data.getBytes("utf-8");
            byte[] signByte = base64Decode(sign);
            PublicKey pubKey = decodePublicKeyFromXml(xmlPubKey);
            return verifySign(dataByte, signByte, pubKey);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 用指定的公钥进行签名验证(默认签名算法为"SHA1withRSA")
     *
     * @param data   数据
     * @param sign   签名结果
     * @param pubKey 公钥
     * @return
     */
    public static boolean verifySign(byte[] data, byte[] sign, PublicKey pubKey) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(pubKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 把字节码转换为base64编码字符串
     *
     * @param byteArray
     * @return
     */
    public static String base64Encode(byte[] byteArray) {
        sun.misc.BASE64Encoder base64Encoder = new sun.misc.BASE64Encoder();
        return base64Encoder.encode(byteArray);
    }

    /**
     * 把base64编码字符串转换为字节码
     *
     * @param base64EncodedString
     * @return
     * @throws Exception
     */
    public static byte[] base64Decode(String base64EncodedString) {
        sun.misc.BASE64Decoder base64Decoder = new sun.misc.BASE64Decoder();
        try {
            return base64Decoder.decodeBuffer(base64EncodedString);
        } catch (IOException e) {
            return null;
        }
    }

    public static String getMiddleString(String source, String strHead, String strTail) {
        try {
            int indexHead, indexTail;

            if (strHead == null || strHead.isEmpty()) {
                indexHead = 0;
            } else {
                indexHead = source.indexOf(strHead);
            }

            if (strTail == null || strTail.isEmpty()) {
                indexTail = source.length();
            } else {
                indexTail = source.indexOf(strTail, indexHead + strHead.length());
            }
            if (indexTail < 0) {
                indexTail = source.length();
            }

            String rtnStr = "";
            if ((indexHead >= 0) && (indexTail >= 0)) {
                rtnStr = source.substring(indexHead + strHead.length(), indexTail);
            }
            return rtnStr;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
