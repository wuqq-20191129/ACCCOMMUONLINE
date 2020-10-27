/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.util;

import com.goldsign.commu.app.message.MessageValidate;
import com.goldsign.commu.app.vo.EncryptorVo;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameMessageCodeConstant;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.util.ByteAndHex;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;

import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

/**
 * 加密机通讯类
 * @datetime 2018-1-10 9:04:07
 * @author lind
 */
public class EncryptorJMJUtil {
    private static Logger logger = Logger.getLogger(MessageValidate.class.getName());
    
    /*
    * 计算卡密钥
    */
    public static void getCardKey(EncryptorVo encryptorVo) throws IOException, MessageException{
        constructMsgKEY(encryptorVo);
        encryptDataKey(encryptorVo, FrameMessageCodeConstant.ENCRY_KEY_INDEX);
    }
    
    /*
    * 计算MAC1
    */
    public static void getMac1(EncryptorVo encryptorVo) throws IOException, MessageException{
        constructMsgMAC1(encryptorVo);
        encryptData(encryptorVo);
    }
    
    /*
    * 计算MAC2
    */
    public static void getMac2(EncryptorVo encryptorVo) throws IOException, MessageException{
        constructMsgMAC2(encryptorVo);
        encryptData(encryptorVo);
    }
    
    
    /*
    * 计算充值TAC
    */
    public static void getTac(EncryptorVo encryptorVo) throws IOException, MessageException{
        constructMsgTAC(encryptorVo);
        encryptData(encryptorVo);
    }
    
    /*
    * 计算消费TAC
    */
    public static void getTrxTac(EncryptorVo encryptorVo, String trxType) throws IOException, MessageException{
        constructMsgTrxTAC(encryptorVo,trxType);
        encryptData(encryptorVo);
    }
    
    /*
    * 计算PSAM激活MAC
    */
    public static void getPsamMac(EncryptorVo encryptorVo) throws IOException, MessageException{
        constructMsgPSAM(encryptorVo);
        encryptData(encryptorVo);
    }
    
    /**
     * 将相关的充值信息转换成组装成字符串
     * 构造传输给机密机所需的信息
     * @param encryptorVo 消息
     */
    private static void constructMsgMAC2(EncryptorVo encryptorVo) {
        StringBuilder builder = new StringBuilder();
        //消息头
        builder.append(FrameMessageCodeConstant.ENCRY_HEADER_MAC);
        //轨道交通离散数据（16H）||地区代码（16H）
        builder.append(FrameMessageCodeConstant.ENCRY_DISPERSE_DATA);
        // 分散数据:消费票卡逻辑卡号 
        builder.append(encryptorVo.getCardLogicalId().substring(0,16));//不足20位时左对齐，右补空格。默认值：全为0
        // 过程数据:伪随机数+ 钱包联机交易序号+ 8000
        builder.append(encryptorVo.getTkChgeSeq())
                .append(dealStr(Long.toHexString(encryptorVo.getOnlTranTimes()).toUpperCase(), 4))
                .append("8000");
        builder.append("10000000000000000018");
        
        // 终端机编号(Sam卡逻辑卡号 前4位加后8位 )
        String no = encryptorVo.getSamLogicalId().substring(0, 4)
                + encryptorVo.getSamLogicalId().substring(8);
        // 校验数据:交易金额+ 交易类型标识(02)+ 终端机编号+ 交易时间
        builder.append(dealStr(Long.toHexString(encryptorVo.getChargeFee()).toUpperCase(), 8))
                .append(FrameMessageCodeConstant.ENCRY_TRAN_TYPE)
                .append(no)
                .append(encryptorVo.getDealTime());
        
        builder.append("04");
        encryptorVo.setChargeMsg(builder.toString());
    }
    
    /**
     * 将相关的充值信息转换成组装成字符串
     * 构造传输给机密机所需的信息
     * @param encryptorVo 消息
     */
    private static void constructMsgMAC1(EncryptorVo encryptorVo) {
        StringBuilder builder = new StringBuilder();
        //消息头
        builder.append(FrameMessageCodeConstant.ENCRY_HEADER_MAC);
        //轨道交通离散数据（16H）||地区代码（16H）
        builder.append(FrameMessageCodeConstant.ENCRY_DISPERSE_DATA);
        // 分散数据:消费票卡卡逻辑卡号 
        builder.append(encryptorVo.getCardLogicalId().substring(0,16));
        // 过程数据:伪随机数+ 钱包联机交易序号+ 8000
        builder.append(encryptorVo.getTkChgeSeq())
                .append(dealStr(Long.toHexString(encryptorVo.getOnlTranTimes()).toUpperCase(), 4))
                .append("8000");
        builder.append("10000000000000000015");
        
        // 终端机编号(Sam卡逻辑卡号 前4位加后8位 )
        String no = encryptorVo.getSamLogicalId().substring(0, 4)
                + encryptorVo.getSamLogicalId().substring(8);
        // 校验数据:钱包余额+ 交易金额+ 交易类型标识(02)+ 终端机编号
        builder.append(dealStr(Long.toHexString(encryptorVo.getBalance()).toUpperCase(), 8))
                .append(dealStr(Long.toHexString(encryptorVo.getChargeFee()).toUpperCase(), 8))
                .append(FrameMessageCodeConstant.ENCRY_TRAN_TYPE)
                .append(no);
        
        builder.append("04");
        encryptorVo.setChargeMsg(builder.toString());
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("构建充值信息："+encryptorVo.getChargeMsg());
    }
    
    /**
     * 将相关的充值信息转换成组装成字符串
     * 构造传输给机密机所需的信息
     * @param encryptorVo 消息
     */
    private static void constructMsgTAC(EncryptorVo encryptorVo) {
        StringBuilder builder = new StringBuilder();
        //消息头
        builder.append(FrameMessageCodeConstant.ENCRY_HEADER_TAC);
        //轨道交通离散数据（16H）||地区代码（16H）
        builder.append(FrameMessageCodeConstant.ENCRY_DISPERSE_DATA);
        // 分散数据:消费票卡卡逻辑卡号 
        builder.append(encryptorVo.getCardLogicalId().substring(0,16));
        builder.append("10000000000000000024");
        
        // 终端机编号(Sam卡逻辑卡号 前4位加后8位 )
        String no = encryptorVo.getSamLogicalId().substring(0, 4)
                + encryptorVo.getSamLogicalId().substring(8);
        // 校验数据:钱包余额(交易后)+ 钱包联机交易序号（加一前）+ 交易金额+ 交易类型标识(02)+ 终端机编号 + 交易时间
        builder.append(dealStr(Long.toHexString(encryptorVo.getBalance()).toUpperCase(), 8))
                .append(dealStr(Long.toHexString(encryptorVo.getOnlTranTimes()).toUpperCase(), 4))
                .append(dealStr(Long.toHexString(encryptorVo.getChargeFee()).toUpperCase(), 8))
                .append(FrameMessageCodeConstant.ENCRY_TRAN_TYPE)
                .append(no)
                .append(encryptorVo.getDealTime());
        
        builder.append("04");
        encryptorVo.setChargeMsg(builder.toString());
    }
    
    /**
     * 将相关的消费信息转换成组装成字符串
     * 构造传输给机密机所需的信息
     * @param encryptorVo 消息
     * @param trxType 消费类型 06普通消费 09复合消费
     */
    private static void constructMsgTrxTAC(EncryptorVo encryptorVo, String trxType) {
        StringBuilder builder = new StringBuilder();
        //消息头
        builder.append(FrameMessageCodeConstant.ENCRY_HEADER_TAC);
        //轨道交通离散数据（16H）||地区代码（16H）
        builder.append(FrameMessageCodeConstant.ENCRY_DISPERSE_DATA);
        // 分散数据:消费票卡卡逻辑卡号 
        builder.append(encryptorVo.getCardLogicalId().substring(0,16));
        builder.append("10000000000000000024");
        
        // 终端机编号(Sam卡逻辑卡号 前4位加后8位 )
        String no = encryptorVo.getSamLogicalId().substring(0, 4)
                + encryptorVo.getSamLogicalId().substring(8);
        // 校验数据:交易金额+ 交易类型标识+ 终端机编号 + 交易时间
        builder.append(dealStr(Long.toHexString(encryptorVo.getChargeFee()).toUpperCase(), 8))
                .append(trxType)
                .append(no)
                .append(encryptorVo.getDealTime());
        
        builder.append("04");
        encryptorVo.setChargeMsg(builder.toString());
    }
    
    /**
     * 将相关的发卡信息转换成组装成字符串
     * 构造传输给机密机所需的信息
     * @param encryptorVo 消息
     */
    private static void constructMsgKEY(EncryptorVo encryptorVo) {
        StringBuilder builder = new StringBuilder();
        //消息头
        builder.append(FrameMessageCodeConstant.ENCRY_HEADER_KEY);
        //轨道交通离散数据（16H）||地区代码（16H）
        builder.append(FrameMessageCodeConstant.ENCRY_DISPERSE_DATA);
        builder.append("01016");
        // 分散数据:消费票卡卡逻辑卡号（16H）
        builder.append(encryptorVo.getCardLogicalId().substring(0,16));
        //逻辑卡号的反（16H）
        builder.append(ByteAndHex.hexStr2HexStrTurn(encryptorVo.getCardLogicalId().substring(0,16)));
        encryptorVo.setChargeMsg(builder.toString());
    }
    
    /**
     * 将相关的发卡信息转换成组装成字符串
     * 构造传输给机密机所需的信息
     * @param encryptorVo 消息
     */
    private static void constructMsgPSAM(EncryptorVo encryptorVo) {
        StringBuilder builder = new StringBuilder();
        //消息头
        builder.append(FrameMessageCodeConstant.ENCRY_HEADER_PSAM);
        //轨道交通离散数据（16H）||地区代码（16H）
        builder.append(FrameMessageCodeConstant.ENCRY_DISPERSE_DATA);
        builder.append(encryptorVo.getSamLogicalId());
        builder.append("01008");
        //PSAM获取随机数（16H）
        builder.append(encryptorVo.getRandomNum());
        encryptorVo.setChargeMsg(builder.toString());
    }
    
    private static byte[] HEADER = {(byte)0x88, (byte)0x10, (byte)FrameCodeConstant.KeyVersion, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static void sendDataToJMJ(String chargeMsg, OutputStream out)
            throws IOException {
        byte[] chargeeMsgBytes;
        byte[] sendData;
        chargeeMsgBytes = AsciiUtil.strToByteArr(chargeMsg);
        int messageLength = 12 + chargeMsg.length();
        sendData = new byte[2 + messageLength];
        sendData[0] = (byte) (messageLength/256);
        sendData[1] = (byte) (messageLength%256);
        System.arraycopy(HEADER, 0, sendData, 2,
                HEADER.length);
        // 将消息数据拷贝到待发送的字节数组中
        System.arraycopy(chargeeMsgBytes, 0, sendData, 14,
                chargeeMsgBytes.length);
        // 发送充值请求
        logger.debug("发送给加密机的字节数组：" + DatatypeConverter.printHexBinary(sendData));//debug
        logger.info("发送给加密机的消息：" + new String(sendData));
        out.write(sendData);
    }
    
    /**
     * 调用加密机获取mac
     * @param encryptorVo
     * @throws java.io.IOException
     * @throws com.goldsign.commu.frame.exception.MessageException
     */
    private static void encryptData(EncryptorVo encryptorVo) throws IOException,
            MessageException {
        Socket clientSocket = null;
        OutputStream out = null;
        BufferedInputStream in = null;
        byte[] datas;
        try {
            clientSocket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(FrameCodeConstant.ServerIp, FrameCodeConstant.ServerPort);
            logger.debug("加密机连接测试跟踪1,"+FrameCodeConstant.ServerIp+" "+FrameCodeConstant.ServerPort);
            clientSocket.connect(socketAddress, FrameCodeConstant.ReadOneByteTimeOutFromEncryptor);
            logger.debug("加密机连接测试跟踪2");
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = clientSocket.getOutputStream();

            String chargeMsg = encryptorVo.getChargeMsg();
            logger.debug("准备发送给加密机的消息:" + chargeMsg.length() + chargeMsg);
            sendDataToJMJ(chargeMsg, out);
            logger.info("加密机消息发送成功");
            int prifexCode = in.read();// 00
            logger.debug("prifexCode:" + prifexCode);
            int rspMsgLen = prifexCode*256 + in.read(); // 长度
            logger.debug("加密机返回的消息长度：" + rspMsgLen);
            if (16 > rspMsgLen) {
                logger.error("加密机返回结果数据长度不正确,接收到的数据长度为[" + rspMsgLen + "]");
                encryptorVo.setReturnCode("01");
                encryptorVo.setErrCode("14");//加密机系统异常
//                throw new IOException("加密机返回信息有误");
            } else{
                datas = new byte[rspMsgLen];
                in.read(datas);
                String commond = "" + (char) datas[12] + (char) datas[13];
                String rtnCode = "" + (char) datas[14] + (char) datas[15];
                if (!("U2".equals(commond) || "UC".equals(commond)) || !"00".equals(rtnCode)) {
                    logger.error("加密机返回结果有误:COMMOND[" + commond + "],返回码[" + rtnCode + "]");
                    encryptorVo.setReturnCode("01");
                    encryptorVo.setErrCode("14");//加密机系统异常
//                    throw new IOException("加密机返回信息有误");
                } else {
                    logger.info("加密机返回：COMMOND[" + commond + "],返回码[" + rtnCode + "]");
                    constructEncoderVo(encryptorVo, datas, commond);
                }
            }
        }catch (Exception ex) {
            logger.error("加密机异常："+ex.getMessage());
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("14");//加密机系统异常
        } finally {
            if (null != out) {
                out.close();
            }
            if (null != in) {
                in.close();
            }
            if (null != clientSocket) {
                clientSocket.close();
            }
        }

    }
    
    
    /**
     * 调用加密机获取票卡17组密钥
     * @param encryptorVo
     * @throws java.io.IOException
     * @throws com.goldsign.commu.frame.exception.MessageException
     */
    private static void encryptDataKey(EncryptorVo encryptorVo, Map<Integer,String> map) throws IOException,
            MessageException {
        Socket clientSocket = null;
        OutputStream out = null;
        BufferedInputStream in = null;
        byte[] datas;
        try {
            clientSocket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(FrameCodeConstant.ServerIp, FrameCodeConstant.ServerPort);
            logger.debug("加密机连接测试跟踪1,"+FrameCodeConstant.ServerIp+" "+FrameCodeConstant.ServerPort);
            clientSocket.connect(socketAddress, FrameCodeConstant.ReadOneByteTimeOutFromEncryptor);
            logger.debug("加密机连接测试跟踪2");
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = clientSocket.getOutputStream();

            String chargeMsg = "";
            StringBuilder cardKey = new StringBuilder();
            
            for(int i=0; i<map.size() && "00".equals(encryptorVo.getReturnCode()); i++){
                chargeMsg = encryptorVo.getChargeMsg().replace("ZZZ", map.get(i));
//                logger.debug("准备发送给加密机的消息:" + chargeMsg.length() + chargeMsg);
                ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("准备发送给加密机的消息:" + chargeMsg.length() + chargeMsg);
                sendDataToJMJ(chargeMsg, out);
//                logger.info("消息发送成功");
                int prifexCode = in.read();// 00
//                logger.debug("prifexCode:" + prifexCode);
                ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("prifexCode:" + prifexCode);
                int rspMsgLen = prifexCode*256 + in.read(); // 长度
                logger.debug("加密机返回的消息长度：" + rspMsgLen);
                if (16 > rspMsgLen) {
                    logger.error("加密机返回结果数据长度不正确,接收到的数据长度为[" + rspMsgLen + "]");
                    ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("加密机返回结果数据长度不正确,接收到的数据长度为[" + rspMsgLen + "]");
                    encryptorVo.setReturnCode("01");
                    encryptorVo.setErrCode("14");//加密机系统异常
    //                throw new IOException("加密机返回信息有误");
                    break;
                } else{
                    datas = new byte[rspMsgLen];
                    in.read(datas);
                    String commond = "" + (char) datas[12] + (char) datas[13];
                    String rtnCode = "" + (char) datas[14] + (char) datas[15];
                    if (!("U2".equals(commond) || "UC".equals(commond)) || !"00".equals(rtnCode)) {
                        logger.error("加密机返回结果有误:COMMOND[" + commond + "],返回码[" + rtnCode + "]");
                        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("加密机返回结果有误:COMMOND[" + commond + "],返回码[" + rtnCode + "]");
                        encryptorVo.setReturnCode("01");
                        encryptorVo.setErrCode("14");//加密机系统异常
    //                    throw new IOException("加密机返回信息有误");
                        break;
                    } else {
//                        logger.info("加密机返回：COMMOND[" + commond + "],返回码[" + rtnCode + "]");
                        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("加密机返回：COMMOND[" + commond + "],返回码[" + rtnCode + "]");
                        constructEncoderVo(encryptorVo, datas, commond);
                    }
                }
                cardKey.append(encryptorVo.getMac());
            }
            encryptorVo.setMac(cardKey.toString());
        } finally {
            if (null != out) {
                out.close();
            }
            if (null != in) {
                in.close();
            }
            if (null != clientSocket) {
                clientSocket.close();
            }
        }

    }

    
    /*
    取mac
    */
    private static void constructEncoderVo(EncryptorVo encryptorVo, byte[] datas, String commond) {
        String mac = "00000000";
        encryptorVo.setMac(mac);
        int len = 19;
        if(commond.equals("UC")){
            len = 16;
        }
        try {
            if (null != datas) {
                // 过滤掉前面18个字节
                byte[] macs = new byte[datas.length - len];
                System.arraycopy(datas, len, macs, 0, macs.length);
                mac = new String(macs);
                encryptorVo.setMac(mac);
                if(commond.equals("U2")){
                    logger.debug("mac：长度[" + (char) datas[16] + (char) datas[17] + (char) datas[18] + "],mac[" + mac + "]");
                }else{
                    logger.debug("mac[" + mac + "]");
                }
            }
        } catch (Exception e) {
            logger.error("处理加密机返回的消息出现异常：" + e);
            encryptorVo.setErrCode("70");
            encryptorVo.setReturnCode("01");
        }
    }
    
    
    /**
     * 如果输入的字符串不够长度，就在前面补0
     * @param input
     * @param length
     * @return
     */
    public static String dealStr(String input, int length) {
        // 字符串长度超过要求长度，截取要求长度的字符串
        if (input.length() > length) {
            return input.substring(0, length - 1);
        } else {
            // 前面补0
            StringBuilder stringBuilder = new StringBuilder();
            String zero = "0";
            for (int index = 0; index < length - input.length(); index++) {
                stringBuilder.append(zero);
            }
            stringBuilder.append(input);
            return stringBuilder.toString();
        }
    }

}
