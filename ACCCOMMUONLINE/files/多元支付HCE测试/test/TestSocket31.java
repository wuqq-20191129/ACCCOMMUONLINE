/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.test;

import com.goldsign.commu.app.vo.AirSaleCfmVo;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.util.DateHelper;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author goldsign
 */
public class TestSocket31 extends Thread{
    
    public static final int port = 6001;
//    public static final String ipAddress = "172.20.19.14";
    public static final String ipAddress = "127.0.0.1";
    
    public static final byte STX_B = (byte) 0xEB;
    public static final byte ETX = 0x03;
    public static final byte QRY = 0x01;
    public static final byte DTA = 0x03;
    public static final byte[] HEADER = { STX_B, DTA, 0 };
    public static final byte[] QUERY = { STX_B, QRY, 0, 0, 0, ETX };////开始标识 数据类型 序列号 数据长度（两位） 结尾
    
    public static void main(String[] args) {
        TestSocket31 myThread;
        myThread = new TestSocket31();
        myThread.run();
    }
    
    public void run(){
        Socket socket = null;

        try {
            socket = new Socket(ipAddress, port);

            System.out.println("socket.hashCode:"+socket.hashCode());
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            OutputStream out = socket.getOutputStream();
            
            for (int i=0; i<10; i++) {
                StringBuffer sb = new StringBuffer();
                sb.append("socket.i="+i+":");
                
                if(i==0){
                    AirSaleCfmVo vo = new AirSaleCfmVo();
                    vo.setTerminaSeq(0L);
                    vo.setSamLogicalId("0000000000000002");
                    vo.setTerminaNo("800015802");
                    vo.setBranchesCode("0000000000000002");
                    vo.setCardType("0800");
                    vo.setIssMainCode("0000");
                    vo.setIssSubCode("0000");
                    vo.setPhoneNo("13400000002");
                    vo.setImei("000000000000002");
                    vo.setImsi("000000000000002");
                    vo.setCardLogicalId("8300000300000003");
                    vo.setCardPhyId("00000000000000000000");
                    vo.setIsTestFlag("1");
                    vo.setResultCode("1");
                    vo.setDealTime("20171214102500");
                    vo.setSysRefNo(413L);
                    vo.setAppCode("00");
                    sendData(data31(vo), out, i);
//                }else if(i==1){
//                }else if(i==2){
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
                    sb.append(","+getLong(data, 9));//系统参照编号
                    sb.append(","+getCharString(data, 13, 9));//BOM代码
                    sb.append(","+getCharString(data, 22, 16));//sam逻辑卡号
                    sb.append(","+getLong(data, 38));//终端流水号
                    sb.append(","+getCharString(data, 42, 20));//
                    sb.append(","+getCharString(data, 62, 20));//
                    sb.append(","+getCharString(data, 82, 1));//
                    sb.append(","+getCharString(data, 83, 2));//
                    sb.append(","+getCharString(data, 85, 2));//
                    
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

    private byte[] data31(AirSaleCfmVo vo){
        byte[] b = new byte[160];
        System.arraycopy("31".getBytes(), 0, b, 0, 2);//消息类型
        System.arraycopy(bcdStringToByteArray(DateHelper.curentDateToYYYYMMDDHHMMSS()), 0, b, 2, 7);//消息生成时间
        System.arraycopy(vo.getTerminaNo().getBytes(), 0, b, 9, 9);//当前bom代码
        System.arraycopy(vo.getSamLogicalId().getBytes(), 0, b, 18, 16);//逻辑卡号
        System.arraycopy(longStringToByteArray(vo.getTerminaSeq(),4), 0, b, 34, 4);//终端处理流水号
        System.arraycopy(vo.getBranchesCode().getBytes(), 0, b, 38, 16);//网点编码
        System.arraycopy(vo.getIssMainCode().getBytes(), 0, b, 54, 4);//
        System.arraycopy(vo.getIssSubCode().getBytes(), 0, b, 58, 4);//
        System.arraycopy(vo.getPhoneNo().getBytes(), 0, b, 62, 11);//手机号
        System.arraycopy(vo.getImsi().getBytes(), 0, b, 73, 15);//手机用户标识
        System.arraycopy(vo.getImei().getBytes(), 0, b, 88, 15);//手机设备标识
        System.arraycopy(bcdStringToByteArray(vo.getCardType()), 0, b, 103, 2);//票卡类型
        System.arraycopy(CharUtil.addCharsAfter(vo.getCardLogicalId(),20," ").getBytes(), 0, b, 105, 20);//
        System.arraycopy(vo.getCardPhyId().getBytes(), 0, b, 125, 20);//
        System.arraycopy(vo.getIsTestFlag().getBytes(), 0, b, 145, 1);//
        System.arraycopy(vo.getResultCode().getBytes(), 0, b, 146, 1);//
        System.arraycopy(bcdStringToByteArray(vo.getDealTime()), 0, b, 147, 7);//
        System.arraycopy(longStringToByteArray(vo.getSysRefNo(),4), 0, b, 154, 4);//
        System.arraycopy(vo.getAppCode().getBytes(), 0, b, 158, 2);//APP渠道
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
