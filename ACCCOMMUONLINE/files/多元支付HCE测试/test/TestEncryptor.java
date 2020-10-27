/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.test;

import com.goldsign.commu.app.util.AsciiUtil;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.bind.DatatypeConverter;

/**
 * @datetime 2017-11-16 10:54:38
 * @author lind
 */
public class TestEncryptor {
    
    public static final byte[] HEADER = {(byte)0x88, (byte)0x10, 0x00, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    
    public static void sendDataToJMJ(String chargeMsg, OutputStream out)
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
        System.out.println(DatatypeConverter.printHexBinary(sendData));
        System.out.println(new String(sendData));
        out.write(sendData);
    }
    
    public static void sendDataToJMJByte(byte[] sendData, OutputStream out)
            throws IOException {
        byte[] message;
        int messageLength = 2 + sendData.length;
        message = new byte[messageLength];
        sendData[0] = (byte) (messageLength/256);
        sendData[1] = (byte) (messageLength%256);
        System.arraycopy(HEADER, 0, message, 2,
                HEADER.length);
        // 将消息数据拷贝到待发送的字节数组中
        System.arraycopy(sendData, 0, message, 2,
                sendData.length);
        // 发送充值请求
        out.write(message);
    }
    
    
    public static void validateResult(byte[] datas, int rspMsgLen)
            throws IOException {
        // 如果读取的长度不为0，而实际读取的信息长度为0，返回异常
        if (rspMsgLen != 0 && datas.length == 0) {
            System.out.println("发送给加密机的第二步信息后，加密机返回结果数据长度不正确,接收到的数据长度为[" + rspMsgLen
                    + "]");
            throw new IOException("加密机返回信息有误");
        }
    }
    
}
