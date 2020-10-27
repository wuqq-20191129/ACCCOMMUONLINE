/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.test;

import com.goldsign.commu.app.util.EncryptorJMJUtil;
import com.goldsign.commu.app.vo.EncryptorVo;
import com.goldsign.commu.app.vo.Message55Vo;
import com.goldsign.commu.frame.constant.FrameMessageCodeConstant;
import com.goldsign.commu.frame.exception.MessageException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @datetime 2017-11-16 10:54:38
 * @author lind
 */
public class TestEncryptor55 extends TestEncryptor{
    
    /**
     * 调用加密机
     *
     * @return 消息
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

            // 组装消息
            StringBuilder sbuffer = new StringBuilder();
            //- 根分散: "UrumqiMT"的 ASCII码  (5572756D71694D54)
            //- 区分散: 乌鲁木齐地铁的城市代码和行业代码  8810 0001 （见交通部实施指南定义）
//            sbuffer.append("U1X001109K0012 5572756D71694D5488100001FFFFFFFF 01 016 83000003000010007CFFFFFCFFFFEFFF");//卡密钥计算
            sbuffer.append("U1X001109K00125572756D71694D5488100001FFFFFFFF0101683000003000010007CFFFFFCFFFFEFFF");//卡密钥计算

//            sbuffer.append("U1X001109K0DF3 5572756D71694D5488100001FFFFFFFF 8300000001E00052 01 008 6A45FB97F71852EF");//卡激活
//            sbuffer.append("U1X001109K0DF35572756D71694D5488100001FFFFFFFF8300000001E00052010086A45FB97F71852EF");//卡激活（联调）
            
//            sbuffer.append("UBX11109K0173 5572756D71694D5488100001FFFFFFFF 8300000300000013 1F928695 000A 8000 1 0000000000000000 015 000031BE 00000004 02 830001E00064 04");//计算充值MAC1
//            sbuffer.append("UBX11109K01735572756D71694D5488100001FFFFFFFF83000003000000131F928695000A800010000000000000000015000031BE0000000402830001E0006404");//计算充值MAC1    --MAC1:BD611CD0        

//            sbuffer.append("UBX11109K0173 5572756D71694D5488100001FFFFFFFF 8300000300000013 1F928695 000A 8000 1 0000000000000000 018 00000004 02 830001E00064 20180108174913 04");//充值（联调）MAC2
//            sbuffer.append("UBX11109K01735572756D71694D5488100001FFFFFFFF83000003000000131F928695000A8000100000000000000000180000000402830001E000642018010817491304");//充值（联调）MAC2  --MAC2:71D82FEC

//            sbuffer.append("UBX12109K01F3 5572756D71694D5488100001FFFFFFFF 8300000300000013 1 0000000000000000 024 000031C2 000A 00000004 02 830001E00064 20180108174913 04");//计算充值TAC  
//            sbuffer.append("UBX12109K01F35572756D71694D5488100001FFFFFFFF830000030000001310000000000000000024000031C2000A0000000402830001E000642018010817491304");//计算充值TAC
            
            String chargeMsg = sbuffer.toString();
            System.out.println("准备发送给加密机的消息:" + chargeMsg.length() + chargeMsg);
            sendDataToJMJ(chargeMsg, out);
//            sendDataToJMJByte(sendData, out); 
            System.out.println("消息发送成功");
            int prifexCode = in.read();// 00
            System.out.println("prifexCode:" + prifexCode);
            int rspMsgLen = in.read(); // 长度
            System.out.println("加密机返回的消息长度：" + rspMsgLen);
            if (16 > rspMsgLen) {
                System.out.println("发送给加密机信息后，加密机返回结果数据长度不正确,接收到的数据长度为["
                        + rspMsgLen + "]");
                throw new IOException("加密机返回信息有误");
            }
            datas = new byte[rspMsgLen];
            in.read(datas);
            String commond = "" + (char) datas[12] + (char) datas[13];
            String rtnCode = "" + (char) datas[14] + (char) datas[15];
            System.out.println(new String(datas));
            if (!("U2".equals(commond) || "UC".equals(commond)) || !"00".equals(rtnCode)) {
                System.out.println("发送给加密机信息后，加密机返回结果有误:COMMOND[" + commond
                        + "],返回码[" + rtnCode + "]");
                throw new IOException("加密机返回信息有误");
            }
            System.out.println("加密机返回：COMMOND[" + commond + "],返回码[" + rtnCode + "]");
            
            String mac = null;
            int len = 19;
            if(commond.equals("UC")){
                len = 16;
            }
            try {
                if (null != datas) {
                    // 过滤掉前面4个字节
                    byte[] macs = new byte[datas.length - len];
                    System.arraycopy(datas, len, macs, 0, macs.length);
                    mac = new String(macs);
                }
            } catch (Exception e) {
                throw new IOException("处理加密机返回的消息出现异常：" + e);
            }
            if(commond.equals("U2")){
                System.out.println("mac：长度[" + (char) datas[16] + (char) datas[17] + (char) datas[18] + "],mac[" + mac + "]");
            }else{
                System.out.println("mac[" + mac + "]");
            }
            
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
//        Message55Vo msg55Vo = new Message55Vo();
//        try {
//            toJMJ(msg55Vo);
//        } catch (IOException ex) {
//            Logger.getLogger(TestEncryptor55.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MessageException ex) {
//            Logger.getLogger(TestEncryptor55.class.getName()).log(Level.SEVERE, null, ex);
//        }
        EncryptorVo encryptorVo = new EncryptorVo();
        try {
            FrameMessageCodeConstant.updateVersionEncryParam();
            encryptorVo.setCardLogicalId("0000100000000001");
            EncryptorJMJUtil.getCardKey(encryptorVo);
            System.out.println("17组key:"+encryptorVo.getMac());
        } catch (Exception e) {
        }
        
    }
}
