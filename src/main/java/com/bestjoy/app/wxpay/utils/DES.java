package com.bestjoy.app.wxpay.utils;

import com.bestjoy.app.alipay.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 对称加密算法
 * Created by bestjoy on 15/8/10.
 */
public class DES {

    private static final byte[] DESIV = {0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};// 设置向量，略去

        /**
         * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
         * @param datasource
         * @param password
         * @return
         */
        public static String enCrypto(byte[] datasource, String password) {
            try{
                SecureRandom random = new SecureRandom();
                DESKeySpec desKey = new DESKeySpec(password.getBytes());
                //创建一个密匙工厂，然后用它把DESKeySpec转换成
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey securekey = keyFactory.generateSecret(desKey);
                //Cipher对象实际完成加密操作
                Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                IvParameterSpec iv = new IvParameterSpec(DESIV);// 设置向量
                //用密匙初始化Cipher对象
                cipher.init(Cipher.ENCRYPT_MODE, securekey, iv);
                //现在，获取数据并加密
                //正式执行加密操作
                byte[] encodedByte = cipher.doFinal(datasource);
                return Base64.encode(encodedByte);

            } catch(Throwable e){
                e.printStackTrace();
            }
            return null;
        }

        /**
         * DES解密
         * @param src
         * @param password
         * @return
         * @throws Exception
         */
        public static String deCrypto(String src, String password) throws Exception {
            // DES算法要求有一个可信任的随机数源
//            SecureRandom random = new SecureRandom();
            byte[] data = Base64.decode(src);
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(DESIV);// 设置向量
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, iv);
            // 真正开始解密操作
            byte[] decodedByte = cipher.doFinal(data);
            return new String(decodedByte);
        }

}
