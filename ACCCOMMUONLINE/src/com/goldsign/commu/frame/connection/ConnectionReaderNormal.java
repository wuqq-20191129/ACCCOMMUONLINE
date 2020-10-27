/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.connection;

import com.goldsign.commu.app.dao.ExceptionLogDao;
import com.goldsign.commu.app.message.ConstructEceptionLog;
import com.goldsign.commu.frame.exception.MessageException;

import java.io.*;
import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * @author hejj
 */
public class ConnectionReaderNormal {

    private BufferedInputStream in;
    private int serialNoShouldBe;
    private int serialNo;
    private boolean stopReader = false;
    private String resultCode = "";
    private int fromClient = -1;
    private final byte STX_B = (byte) 0xEB;
    private final byte ETX = 0x03;
    private final byte QRY = 0x01;
    private final byte NDT = 0x02;
    private final byte DTA = 0x03;
    private static Logger logger = Logger
            .getLogger(ConnectionReaderNormal.class.getName());
    private static final String CLASS_NAME = ConnectionReaderNormal.class
            .getName();
    private String ip;

    // private static Logger logger =
    // Logger.getLogger(ConnectionReader2.class.getName());

    /**
     * 01xx message 0100 Received DATA message. 0101 Received NODATA message.
     * <p>
     * 10xx Socket,IO 1001 Socket error! 1002 IO error!
     * <p>
     * 110x message（receive） 1101 Message start flag error! 1102 Message type
     * error! 1103 Message serial NO error! 1104 Message end flag error! 1105
     * Message read timeout!
     * <p>
     * 115x message（send） 1151 Message length longer than 65536 bytes. 1152
     * Message length is 0. 1153 Message is null.
     */
    public ConnectionReaderNormal(BufferedInputStream bin, int aSerialNoShouldBe) {
        in = bin;
        serialNoShouldBe = aSerialNoShouldBe;
    }

    public Vector read() {
        Vector readerResult = new Vector();
        int dataLength;
        byte[] data = null;

        try {
            // read STX
            readOneByte();
            if ((byte) fromClient == -1) {
                logger.warn("读取消息失败!");
                resultCode = "1002";
                throw new MessageException(resultCode);
            }
            if ((byte) fromClient != STX_B) {
                String mess = " - 消息头开始标识不正确，传入的是：[" + (char) fromClient + "]";
                logger.warn(mess);
                ExceptionLogDao.insert(ConstructEceptionLog.constructLog(ip,
                        CLASS_NAME, mess));
                resultCode = "1101";
                throw new MessageException(resultCode);
            }

            // read message type
            readOneByte();
            if (fromClient != NDT && fromClient != DTA && fromClient != QRY) {
                String mess = "数据类型错误，传入的是：" + fromClient;
                logger.warn(mess);
                ExceptionLogDao.insert(ConstructEceptionLog.constructLog(ip,
                        CLASS_NAME, mess));
                resultCode = "1102";
                throw new MessageException(resultCode);
            }

            // read serial NO
            readOneByte();

            if (serialNoShouldBe != -1) { // for client test,serialNoShouldBe =
                // -1 means ignore serial no check
                if (fromClient != serialNoShouldBe) {
                    String mess = " 序列号错误,传入的序列号：[" + fromClient + "],服务端序列号：["
                            + serialNoShouldBe + "]";
                    logger.warn(mess);
                    ExceptionLogDao.insert(ConstructEceptionLog.constructLog(
                            ip, CLASS_NAME, mess));
                    resultCode = "1103";
                    throw new MessageException(resultCode);
                }
            }
            serialNo = fromClient;

            // read data length
            readOneByte();
            dataLength = fromClient + in.read() * 256;

            // read data
            data = new byte[dataLength];
            this.readBytes(data, 0, data.length);

            // read ETX
            readOneByte();
            if (fromClient != ETX) {
                String mess = "-消息结束标识错误";
                logger.warn(mess);
                ExceptionLogDao.insert(ConstructEceptionLog.constructLog(ip,
                        CLASS_NAME, mess));
                resultCode = "1104";
                throw new MessageException(resultCode);
            } else {
                //modify by zhongzq 20190731
//                logger.debug("正确读取信息");
                logger.trace("正确读取信息");
                if (dataLength == 0) {
                    resultCode = "0101";
                } else {
                    logger.info("读取到的终端消息长度:" + data.length);
                    logger.info("读取到的终端消息：" + Arrays.toString(data));
                    resultCode = "0100";
                }
            }
            // 统计正常接受的消息包
            // this.addReadedCount();

        } catch (IOException e) {
            resultCode = "1002";
            String mess = "IO 异常:" + e.getMessage();
            logger.error(mess);
            ExceptionLogDao.insert(ConstructEceptionLog.constructLog(ip,
                    CLASS_NAME, mess));
        } catch (MessageException e) {
            String mess = "接收的数据格式有误,错误代码为:" + e.getMessage();
            logger.error(mess);
            ExceptionLogDao.insert(ConstructEceptionLog.constructLog(ip,
                    CLASS_NAME, mess));
        } finally {
            readerResult.add(resultCode);
            readerResult.add(data);
            readerResult.add(new Integer(serialNo));
            if (fromClient == 100) {
                logger.debug("读取数据包正常");
            }
        }
        return readerResult;
    }

    public void stopReader() {
        stopReader = true;
    }

    private void readOneByte() throws IOException, MessageException {
        if (!stopReader) {
            fromClient = in.read();
            //modify by zhongzq 20190731
//            logger.debug("读取到的字节值是:" + fromClient);
            logger.trace("读取到的字节值是:" + fromClient);
        } else {
            logger.debug("读取消息被终止");
            resultCode = "1105";
            throw new MessageException(resultCode);
        }
    }

    private void readBytes(byte[] buff, int off, int len) throws IOException,
            MessageException {
        if (!stopReader) {
            //modify by zhongzq 20190826 解决半包问题 待优化
            int readTotal = in.read(buff, off, len);
            if (readTotal != len) {
                String mess = "读取的实际数据区长度不对，传入的数据区长度:[" + len + "],实际读取的数据区长度["
                        + readTotal + "]";
                logger.error(mess);
                ExceptionLogDao.insert(ConstructEceptionLog.constructLog(ip,
                        CLASS_NAME, mess));
                resultCode = "1201";
                throw new MessageException(resultCode);
            }
//            int readTotal = in.read(buff, off, len);
//            if (readTotal < len) {
//                String mess = "读取的实际数据区长度不对，传入的数据区长度:[" + len + "],实际读取的数据区长度["
//                        + readTotal + "]";
//                logger.error(mess + "buff:[" + Arrays.toString(buff) + "]");
//                ExceptionLogDao.insert(ConstructEceptionLog.constructLog(ip,
//                        CLASS_NAME, mess));
//                int tryTime = 10;
//                int reTryReadTotal = 0;
//                while ((reTryReadTotal == 0||reTryReadTotal==-1) && tryTime > 0) {
//                    reTryReadTotal = in.read(buff, readTotal, len - readTotal);
//                    readTotal = readTotal + reTryReadTotal;
//                    logger.info("reTryReadTotal:"+reTryReadTotal+",readTotal:"+readTotal+",tryTime:"+tryTime);
//                    tryTime--;
//                }
//                if(readTotal==len) {
//                    logger.info("半包处理：Total:" + readTotal + "buff:[" + Arrays.toString(buff) + "]");
//                }else {
//                    logger.error("半包处理异常：Total:" + readTotal + "buff:[" + Arrays.toString(buff) + "]");
//                    resultCode = "1201";
//                    throw new MessageException(resultCode);
//                }
//            }
        } else {
            logger.warn("读取消息被终止");
            resultCode = "1105";
            throw new MessageException(resultCode);
        }
    }

    public Vector read(String id) {
        this.ip = id;
        return read();
    }
}
