/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.test;

import com.goldsign.commu.app.vo.QrPayOrderVo;
import com.goldsign.commu.frame.util.DateHelper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author goldsign
 */
public class TestSocket83 extends Thread{
    
    public static final int port = 6001;
//    public static final String ipAddress = "172.20.19.14";
//    public static final String ipAddress = "127.0.0.1";
    public static final String ipAddress = "210.21.94.85";
    
    public static final byte STX_B = (byte) 0xEB;
    public static final byte ETX = 0x03;
    public static final byte QRY = 0x01;
    public static final byte DTA = 0x03;
    public static final byte[] HEADER = { STX_B, DTA, 0 };
    public static final byte[] QUERY = { STX_B, QRY, 0, 0, 0, ETX };////开始标识 数据类型 序列号 数据长度（两位） 结尾
    
    public static void main(String[] args) {
        TestSocket83 myThread;
        myThread = new TestSocket83();
        myThread.run();
    }
    
    public void run(){
        Socket socket = null;

        try {
            socket = new Socket(ipAddress, port);

            System.out.println("socket.hashCode:"+socket.hashCode());
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            OutputStream out = socket.getOutputStream();
            
            for (int i=0; i<5; i++) {
                StringBuffer sb = new StringBuffer();
                sb.append("socket.i="+i+":");
                
                if(i==0){
                    QrPayOrderVo vo = new QrPayOrderVo();
                    vo.setTerminaSeq(1L);
                    vo.setAccSeq(43L);
                    vo.setOrderNo("20181130093022");
                    vo.setQrpayId("0000000002");
                    vo.setQrpayData("20181130093022-0000000002-00000400");
                    vo.setDealTime("20181130093022");
                    vo.setCardTypeTotal("0100");
                    vo.setSaleFee(200L);
                    vo.setSaleTimes(2L);
                    vo.setDealFee(400L);
                    vo.setReturnCode("00");
                    sendData(data(vo), out, i);
                }else{
                    sendQuery(out, i);
                }
                
                try {
                    sleep(2000);
                } catch (Exception e) {
                }
                
                sb.append(Integer.toHexString(in.read()&0xff));//开关标识
                int dataType = in.read();
                sb.append(","+dataType);//数据类型
                sb.append(","+in.read());//序列号
                int dataLength = in.read()+256*in.read();//数据长度两位
                sb.append(","+dataLength);
                if(dataType==3){
                    byte[] data = new byte[dataLength];
                    in.read(data, 0 , dataLength);
                    sb.append(","+getCharString(data,0,2));//消息类型
                    sb.append(","+getBcdString(data,2,7));//消息生成时间
                    sb.append(","+getLong(data, 9));//终端流水号
                    sb.append(","+getLong(data, 13));//acc流水号
                    sb.append(","+getCharString(data, 17, 10));//支付标识
                    sb.append(","+getCharString(data, 27, 34));//支付二维码
                    sb.append(","+getBcdString(data,61,7));//acc时间
                    sb.append(","+getCharString(data, 68, 2));//响应码
                }
                sb.append(","+in.read());
                System.out.println(sb.toString());
            }
            
//                in.close();
//                out.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
//                    socket.close();
                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public static void sendQuery(OutputStream out, int serialNo) throws IOException{
        QUERY[2] = (byte) serialNo;
        out.write(QUERY);

    }
    
    private void sendData(byte[] b, OutputStream out, int serialNo) throws IOException {
        HEADER[2] = (byte) serialNo;
        out.write(HEADER);
        out.write((byte) ((b.length) % 256));
        out.write((byte) ((b.length) / 256));
        out.write(b);
        out.write(ETX);
    }

    private byte[] data(QrPayOrderVo vo){
        byte[] b = new byte[98];
        System.arraycopy("83".getBytes(), 0, b, 0, 2);//消息类型
        System.arraycopy(bcdStringToByteArray(DateHelper.curentDateToYYYYMMDDHHMMSS()), 0, b, 2, 7);//消息生成时间
        System.arraycopy(longStringToByteArray(vo.getTerminaSeq(),4), 0, b, 9, 4);//HCE平台处理流水
        System.arraycopy(longStringToByteArray(vo.getAccSeq(),4), 0, b, 13, 4);//acc处理流水
        System.arraycopy(vo.getOrderNo().getBytes(), 0, b, 17, 14);//手机号
        System.arraycopy(vo.getQrpayId().getBytes(), 0, b, 31, 10);//
        System.arraycopy(vo.getQrpayData().getBytes(), 0, b, 41, 34);//
        System.arraycopy(bcdStringToByteArray(vo.getDealTime()), 0, b, 75, 7);//时间
        System.arraycopy(bcdStringToByteArray(vo.getCardTypeTotal()), 0, b, 82, 2);//
        System.arraycopy(longStringToByteArray(vo.getSaleFee(),4), 0, b, 84, 4);//
        System.arraycopy(longStringToByteArray(vo.getSaleTimes(),4), 0, b, 88, 4);//
        System.arraycopy(longStringToByteArray(vo.getDealFee(),4), 0, b, 92, 4);//
        System.arraycopy(vo.getReturnCode().getBytes(), 0, b, 96, 2);//
        return b;
    }
    
    
    
    private byte[] longStringToByteArray(long input, int len) {
        String des = Integer.toHexString((int) input);
        if (des.length() >= 8) {
            des = des.substring(0, 7);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len * 2 - des.length(); i++) {
                sb.append("0");
            }
            sb.append(des);
            des = sb.toString();
        }
        
        byte[] rst = new byte[4];
        rst[0] = (byte) (Integer.valueOf(des.substring(6), 16).intValue());
        rst[1] = (byte) (Integer.valueOf(des.substring(4, 6), 16).intValue());
        rst[2] = (byte) (Integer.valueOf(des.substring(2, 4), 16).intValue());
        rst[3] = (byte) (Integer.valueOf(des.substring(0, 2), 16).intValue());
        return rst;
    }
    
    // when transform 2 decimal number for example 98,run this method to get
    // 0x98
    private byte bcd2ToByte1(int i) {
        return (byte) (i / 10 * 16 + i % 10);
    }

    // run this method example : transform "123456789" to
    // {0x01,0x23,0x45,0x67,0x89}
    private byte[] bcdStringToByteArray(String str) {
        try {
            int len = str.length();
            if (str.length() == 0) {
                return null;
            }
            if (len % 2 == 1) {
                str = "0" + str;
                len = len + 1;
            }
            if (len / 2 > 65536) {
                throw new Exception("Transform string to BCD length > "
                        + 65536);
            }
            byte[] tmp = new byte[65536];
            int p = 0;
            for (int i = 0; i < len; i = i + 2) {
                int value = Integer.parseInt(str.substring(i, i + 2));
                byte[] b = {bcd2ToByte1(value)};
                System.arraycopy(b, 0, tmp, p, 1);
                p = p + 1;
            }

            byte[] bb = new byte[p];
            System.arraycopy(tmp, 0, bb, 0, p);
            return bb;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // when transform one byte for example (byte)0x98,run this method to get
    // "98";
    public String byte1ToBcd2(byte b) {
            int i = 0;
            if (b < 0) {
                    i = 256 + b;
            } else {
                    i = b;
            }
            return (new Integer(i / 16)).toString()
                            + (new Integer(i % 16)).toString();
    }

    public String getBcdString(byte[] data, int offset, int length){
            StringBuilder sb = new StringBuilder();
            try {
                    for (int i = 0; i < length; i++) {
                            // logger.info("bcd之前："+data[offset + i]);
                            sb.append(byte1ToBcd2(data[offset + i]));
                            // logger.info("bcd之后："+byte1ToBcd2(data[offset + i]));
                    }
            } catch (Exception e) {
                    e.printStackTrace();
            }
            return sb.toString();
    }

    public char byteToChar(byte b) {
            return (char) b;
    }

    public String getCharString(byte[] data, int offset, int length) {
            StringBuilder sb = new StringBuilder();
            try {
                    for (int i = 0; i < length; i++) {
                            sb.append(byteToChar(data[offset + i]));
                    }
            } catch (Exception e) {
                    e.printStackTrace();
            }
            return sb.toString();
    }

    public String getCharString(char[] data, int offset, int length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                    sb.append(data[offset + i]);
            }
            return sb.toString();
    }

    // when transform one byte for example (byte)0x98,run this method to get
    // 104;
    public int byteToInt(byte b) {
            int i = 0;
            if (b < 0) {
                    i = 256 + b;
            } else {
                    i = b;
            }
            return i;
    }

    public int getInt(byte[] data, int offset) {
            return byteToInt(data[offset]);
    }

    // when transform one short(two bytes) for example 0x12(low),0x34(high),run
    // this method to get 13330
    public int getShort(byte[] data, int offset) {
            int low = byteToInt(data[offset]);
            int high = byteToInt(data[offset + 1]);
            return high * 16 * 16 + low;
    }

    // when transform one long(two shorts) for example 0x12,0x34,0x56,0x78
    public int getLong(byte[] data, int offset) {
            int low = getShort(data, offset);
            int high = getShort(data, offset + 2);
            return high * 16 * 16 * 16 * 16 + low;
    }


}
