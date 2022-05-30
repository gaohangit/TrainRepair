package com.ydzbinfo.emis.utils;

import com.alibaba.fastjson.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

/**
 * 加密解密工具包
 */
public class DESUtil {

    public static final String Key = "ICT_TSMCIS";
    private static boolean isEncrypt = true;
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static String encode(String key, String data) {
        key = key.substring(0, 8);
        if (data == null)
            return null;
        if (isEncrypt) {
            try {
                DESKeySpec dks = new DESKeySpec(key.getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                // key的长度不能够小于8位字节
                Key secretKey = keyFactory.generateSecret(dks);
                Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
                AlgorithmParameterSpec paramSpec = new IvParameterSpec(key.getBytes());
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
                byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
                return byte2hex(bytes);
            } catch (Exception e) {
                e.printStackTrace();
                return data;
            }
        }
        return data;
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @param key  解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     */
    public static String decode(String key, String data) {
        key = key.substring(0, 8);
        if (data == null)
            return null;
        if (isEncrypt) {
            try {
                DESKeySpec dks = new DESKeySpec(key.getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                // key的长度不能够小于8位字节
                Key secretKey = keyFactory.generateSecret(dks);
                Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
                AlgorithmParameterSpec paramSpec = new IvParameterSpec(key.getBytes());
                cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
                return new String(cipher.doFinal(hex2byte(data.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                return data;
            }
        }
        return data;
    }

    /**
     * 二行制转字符串
     */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("trainsetId", "CR400AF-2125");
        jsonObject.put("sCarNo", "01");
        // jsonObject.put("workType", "1");
        // jsonObject.put("trainsetName", "CR400AF-2125");
        // jsonObject.put("unitCode", "A001");
        // jsonObject.put("workID", "65139a1f3bbc495aacd44e44049aab33");
        // jsonObject.put("trainsetId", "CR400AF-2125");
        // jsonObject.put("itemCode", "CRHZ00000528");
        // jsonObject.put("picType", "1");
        // jsonObject.put("partPosition", "车头");
        // jsonObject.put("carNo", "1");
        // jsonObject.put("partName", "1");
        String encodedData = DESUtil.encode("DCWHict795", "{}");

        System.out.println(encodedData);
        String qwertyuiop = DESUtil.decode("DCWHict795", "52D7F259216FD8FC757A1E6C8B2CB884A01B184743CFA17E9B29573B6DF5F3208B8FC09114B955D61F16ADD8F8BADDE9AF72640E768B8D2496FE84A76A2D4EDEAB7ED1054C6DC089AC99468F6B73EB7766F4CC5EDB7E99C32075BFC0DB66073647E9098372FFF92E49500E00C4A7162A11A8099A1EE0F325CAD1F2DF98EC41E72A351CD57374CA3498B210945046B608F4EF99CAF827F5E983248081848B373013528681273035C5C4D69BFFF5DFCEEA5438D5AD3602E053C79BB612AF28F8817E0389DAC0BEFDBB9B6E5FEF56896EBC8C2E670F69522E3847D5410285CDFEBB2CC655DBDBE91ABB0AA620B3A597F2EA3EBFC48B8B17CB17AADB72B6000ACF5D2381ECC967AD73952DC469A164AC7909E65DD2994235E6B0FE413EBD7B025AEDD4B49605CCECAFBA212716CE27E352A45730C8EE8216885ACBBF21F42857021084B255E5C23F21E1677CD3B1FDE0ED8CF9650BBDDEB8E96E69AEE3E131C272522AB93B938B50639E013001F780E97A1946CA61A84B1DE2A3BB64F309B34E8ECFC6AAFD4663EA6D03520656BE6E6C6EAE7D6B2B4C9D42F75BE4B9BBD30E12B57F3CC3E628156D8E2841558E9D4C64ED4B747809973B38BACF86359F742A4A19821E3CE0B88146C26569C8D5D532ABD9E23CB647D77CDEBEDCEF1C788F3EAE5F3221E60E52481BAB3628267A4DEF89F3A1CE2DAF176BCE29682B13B915E05982D049C70C3650AF0B0FECAFAE61C8AECC5467AB4E7CFD3D25B225A43D9FDD11F33D31514A6D796D84D50ACEE3809E07810EF9CFB8D32E36F148132A6DE823F4B3A1779B675D1AF2E2D8B939C210CE9501A10C36ED24C87C4992C80E789AC4B3EDE696030A0137FD0875C2C2914D972194D313EE4EB5C394EDA46050958C87B842BF4AF15981582236ED9641B769A7B53BCBF4A24F7BFEE66E06B8BD4ACDC71F76488BB51D0E3ECAE5C372F9B932CFD599BF03F064928ADA0E3E40458D1CBB360E3226F30F05FB5E09BAB4A4BE7BEE9D0A1DA9A4BD7857774E212828A84BCFDACF00356C5D7AFADC987A1D323C2D601A6B2966057A938CE36B038F2B4B7B08B5E8DA8D3EDA51BBE4B2F7A04FB3DD7DA09B932FAE72B3BF61BE0180CF7A1DD0F531D850CE42504275E2F4B3D472EA508D418271D8C8EF33FBA74D97AFE285665325EFF0796A5951D9BC9522BC6023C5019EBE3303D145CD4B2B125E901517CBC7FC536B085AA8710766CB04D08E0BE5BA5E374E6BF313C1D58C38B2E8FF8EE4A0D8324503A450B2369E8D9409641293FFB21D6B546F2585A50D1F40E516DB1145DC2A02F0FEC706E2E24013A15414D3F88C12EA1DEEFC9DA432F28ADE2E1EB1DF8732F6E622BC1FBECC0A6193AFC7E9B491988E4B7F60FB57AD0FEE8026916D1E0EF39C8A487E549C182321669D71795A4C72342F52ADAC5FD8E49CDB685A5BC32D1D1844C55A43FBDD931D664F53FFDC472D814FBA295BE8C4A114FFF54C94EA71556856144F3EBDA0422971D9394D3265A5B0E2F693A468A96767905BFBC2ABA1F60D356C8859C4B64BD60133F113856EC5C3A81B94B234459B3AB928029C7D0F1CB8900E1DA6787D74DB6D8346E067B8119202553748382893CD3ACBDF6875F8E9DCD447C2C76BFD8C68F8543A2D60FFF5CEAB408F670AB90326D011C553820A91E3DA6F0A69877DAF35221BEBF6D9D07497ACE149231B8B841BE7333DCB80E3EDFB97282F90AE32DE8A67225A220BFBC52E3CE3019AAAFADA6600AD1BDE9309A13C1F54E1A8C7C5CA32DD7D8AEDD2E875A83430F6AB39CA2B6D040F1D25C4ACD34648BF0F9BFE86CD6175775BBC292EA813BD14B9793655AA72CCF25A40EE53116719A43305C83F18C4D41628D0DCD964704A4995DE6F4F6D012E2647B4727C33F8711DC35375BC2C66306B30CC9F2291DED787CF358A5F00D58F14EA38036B5FF2E02F031A4A6A2510E937BB6AD234EBDF1FE28FF4333CEEB2726ABBA05D5B152D8463CBB2CC8447F226DBFC2922310210A8B6B519ABE1D20678E578993BE7D7EE9A88821E135AE7FA6B36E7B78B45F7559EC7C90151FFB703BC47A758494D14F60E319B5019F1DA999BD775BDE03F16E5506BE3A3FF4660A7768126B12B1DF98F41EC60A36EE96E644CB60FB0317F93AF67A992C6CC2A50A9D29654EF52FCE88229DED84B5E3A5542B8F601D5194866C6F4B71D4A819CA67B26A2D8DCE02515E04C0EB4C8FA21397E8CC8CA45A9D9B251ADB4CE86139A62A1BA0E716B13F76A9B3E6B37779439C8806F4039F58CD25E963C93B34095A9114A10CBBED04D95B1BE506CBC17DBB41638ED3B373C22F33C7D7CC08333EB6A03451DEFA85EBC6EC1374F0E3685ACF240E0B72DD678DF98C6B1B248BDB2D6D2E9");

        System.out.println(qwertyuiop);
        //解密
        // String decryptedData = RSACryption.decryptData("FExQ2s6SzTju17avslEdXvx3Dj23D73UIh7J1cPep/pGm7hrZiQhl35t9S33JQBz1zUvud2nlPzahKGd9f31ZyWO1udZ8llfPeHofW3pChqBrnN+WfgOIuJLSqjB+WtR32RynPQF5ySe0yJBcV/c9ziecogyX/+0wupphUbg1J4=",
        //     Constants.priKeyXml);
        // System.out.println("解密---------:" + decryptedData);
    }

}
