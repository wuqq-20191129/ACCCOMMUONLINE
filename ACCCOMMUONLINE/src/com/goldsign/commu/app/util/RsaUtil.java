/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.util;

import com.goldsign.commu.frame.util.ByteAndHex;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.util.DateHelper;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * @datetime 2017-9-8 14:51:14
 * @author lind
 */
public class RsaUtil {
    
    public static String data = "12345678901234567890123456789012";
    public static final String publicKeyString="MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAJ4+PbVpFTMCGtkvnzgc87zf5dYX24Qn5aslsusulZ9bAgMBAAE=";
    public static final String privateKeyString="MIHDAgEAMA0GCSqGSIb3DQEBAQUABIGuMIGrAgEAAiEAnj49tWkVMwIa2S+fOBzzvN/l1hfbhCflqyWy6y6Vn1sCAwEAAQIgNdV32dkDgMRlDOESGh1jX6Nunfj6p5zelpSfnjzmZJUCEQDTwU+ZiW2//hJBLksF1ZP1AhEAv06Y1cXcYXAb49I0zoWkDwIRALOhuk/J+FM1SfEHP1dMrf0CEQC9vs0kKs18718aReIOfMrNAhBsaH4103zyTm4WIOFeQGJo";
    
    public static void main(String[] args) throws Exception {
//        KeyPair x = genKeyPair(256);
//        System.out.println("getPrivate:"+Base64.getEncoder().encodeToString(x.getPrivate().getEncoded()));
//        System.out.println("getPublic:"+Base64.getEncoder().encodeToString(x.getPublic().getEncoded()));
        
        //加密私钥
//        String encryptedStr = encrypt(data.getBytes(), publicKeyString);
//        String encryptedStr = "819F7B7C5A383FE2A62D2EC7D6E30D05036C839034593938AC274D2D4229DA06";
        String encryptedStr = "�K�p���j����=�!�*\u0015�^�\"#�]5b��Z�";

//        String aa = DateHelper.dateToString(new Date());
//        System.out.println(aa);
//        String orderDate = "2018111501103720181115173639";
//        String longStr = CharUtil.addCharsBefore(String.valueOf(1000111111111111111L),4,"0");
//        String encryptedStr = RsaUtil.encrypt((orderDate+longStr).getBytes(), RsaUtil.publicKeyString);


        ;
        System.out.println("加密后：" + encryptedStr+"，长度="+encryptedStr.length());
//        System.out.println("加密后：" + new String(encryptedBytes));
        encryptedStr = "https://u.wechat.com/gMBEeRwXHwbPtuXdkCjyTq8";
        //公钥解密
        String decryptedStr = decrypt(encryptedStr, privateKeyString);
        System.out.println("解密后：" + decryptedStr+",长度"+decryptedStr.getBytes().length+",utf-8="+decryptedStr.getBytes("UTF-8").length);
        System.out.println("长度Unicode："+decryptedStr.getBytes("Unicode").length+",GBK="+decryptedStr.getBytes("GBK").length);
    }

    //生成密钥对
    public static KeyPair genKeyPair(int keyLength) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keyLength);
        return keyPairGenerator.generateKeyPair();
    }

    //私钥解密
    public static String decrypt(String content, String privateKeyString) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //获取私钥 
        PrivateKey privateKey=getPrivateKey(privateKeyString);	
        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");//java默认"RSA"="RSA/ECB/PKCS1Padding"
//        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return new String(cipher.doFinal(ByteAndHex.hexStr2BinArr(content)));
    }

    //公钥加密
    public static String encrypt(byte[] content, String publicKeyString) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //获取公钥 
        PublicKey publicKey=getPublicKey(publicKeyString);
//        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return DatatypeConverter.printHexBinary(cipher.doFinal(content));
    }
    
    //将base64编码后的公钥字符串转成PublicKey实例
    public static PublicKey getPublicKey(String publicKey) throws Exception{
            byte[ ] keyBytes=Base64.getDecoder().decode(publicKey.getBytes());
            X509EncodedKeySpec keySpec=new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory=KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);	
    }

    //将base64编码后的私钥字符串转成PrivateKey实例
    public static PrivateKey getPrivateKey(String privateKey) throws Exception{
            byte[ ] keyBytes=Base64.getDecoder().decode(privateKey.getBytes());
            PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory=KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
    }

}
