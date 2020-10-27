/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.test;

import com.goldsign.commu.app.vo.Message55Vo;
import com.goldsign.commu.frame.exception.MessageException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @datetime 2017-11-16 10:54:38
 * @author lind
 */
public class TestEncryptor52 extends TestEncryptor{
  
    /**
     * 计算MAC1
     */
    public static void toJMJ(Message55Vo msg55Vo) throws IOException,
            MessageException {
        Socket clientSocket = null;
        OutputStream out = null;
        BufferedInputStream in = null;
        byte[] datas;
        try {
            clientSocket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress( "172.20.16.21", 8);
            clientSocket.connect(socketAddress, 3000);
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = clientSocket.getOutputStream();

            // 组装第一个消息
            StringBuilder sbuffer = new StringBuilder();
            sbuffer.append("UBX10109K0071");
            sbuffer.append("4840801000000010");
            sbuffer.append("0000000100018000");
            sbuffer.append("10000000000000000015");
            sbuffer.append("000000110000001102112233445566");//钱包余额+ 交易金额+ 交易类型标识(02)+ 终端机编号
            sbuffer.append("04");
            String chargeMsg = sbuffer.toString();
            System.out.println("第一步，准备发送给加密机的消息:" + chargeMsg.length() + chargeMsg);
            sendDataToJMJ(chargeMsg, out);
            System.out.println("第一步,消息发送成功");
            int prifexCode = in.read();// 00
            System.out.println("prifexCode:" + prifexCode);
            int rspMsgLen = in.read(); // 长度
            System.out.println("加密机返回的消息长度：" + rspMsgLen);
//            if (16 > rspMsgLen) {
//                System.out.println("发送给加密机的第一步信息后，加密机返回结果数据长度不正确,接收到的数据长度为["
//                        + rspMsgLen + "]");
//                throw new IOException("加密机返回信息有误");
//            }
            datas = new byte[rspMsgLen];
            in.read(datas);
            String commond = "" + (char) datas[12] + (char) datas[13];
            String rtnCode = "" + (char) datas[14] + (char) datas[15];
            if (!"UC".equals(commond) || !"00".equals(rtnCode)) {
                System.out.println("发送给加密机的第一步信息后，加密机返回结果有误:COMMOND[" + commond
                        + "],返回码[" + rtnCode + "]");
                throw new IOException("加密机返回信息有误");
            }
            System.out.println("加密机返回：" + commond + rtnCode);
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
    
    public static void main(String[] args) {
        Message55Vo msg55Vo = new Message55Vo();
        try {
            toJMJ(msg55Vo);
        } catch (IOException ex) {
            Logger.getLogger(TestEncryptor52.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessageException ex) {
            Logger.getLogger(TestEncryptor52.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
