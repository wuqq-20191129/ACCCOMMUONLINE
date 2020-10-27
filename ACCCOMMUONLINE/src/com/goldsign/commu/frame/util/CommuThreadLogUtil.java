/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.vo.CommuHandledMessage;

/**
 *
 * @author hejj
 */
public class CommuThreadLogUtil {

    public static final String unHandleName = "unHandleMsg.log";
    public static final String errName = "ErrHandleMsg.log";
    private static Logger logger = Logger.getLogger(CommuThreadLogUtil.class);

    public synchronized static void writeUnHandleMsgToFile(String threadName,
            Vector msgs) {
        // if(msgs == null || msgs.isEmpty() )
        // return;

        String fileName = FrameCodeConstant.UnHanledMsgLogDir + "/"
                + unHandleName;
        File f = null;
        FileOutputStream fos = null;
        byte[] data = null;
        String msgID = "";
        String head = "";
        int value = 0;
        String biValue = "";
        try {
            f = new File(fileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            fos = new FileOutputStream(f, true);
            for (int i = 0; i < msgs.size(); i++) {
                data = (byte[]) ((Vector) ((CommuHandledMessage) msgs.get(i))
                        .getReadResult()).get(1);
                msgID = "" + (char) data[0] + (char) data[1];
                head = "\r\n"
                        + DateHelper.datetimeToString(new Date(System
                                .currentTimeMillis())) + "   线程" + threadName
                        + "未处理消息 " + msgID + "\r\n";
                fos.write(head.getBytes("GBK"));
                for (int j = 0; j < data.length; j++) {
                    // value =(byte)(0x30+data[j]);
                    biValue = Integer.toHexString((int) data[j]);
                    fos.write(biValue.getBytes());
                }

                // fos.write(data);
            }
            if (msgs.isEmpty()) {
                head = DateHelper.datetimeToString(new Date(System
                        .currentTimeMillis()))
                        + "   线程"
                        + threadName
                        + "未处理消息 " + msgID + "\r\n";
                fos.write(head.getBytes("GBK"));
                fos.write(head.getBytes("GBK"));

            }

            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("写未处理消息到文件失败" + e.getMessage());

        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }

        }

    }

    public synchronized static void writeErrHandleMsgToFile(String threadName,
            Vector msg) {
        // if(msgs == null || msgs.isEmpty() )
        // return;

        String fileName = FrameCodeConstant.UnHanledMsgLogDir + "/" + errName;
        File f = null;
        FileOutputStream fos = null;
        String msgID = "";
        String head = "";
        byte[] data = (byte[]) msg.get(1);
        String biValue = "";
        try {
            f = new File(fileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            fos = new FileOutputStream(f, true);

            msgID = "" + (char) data[0] + (char) data[1];
            head = "\r\n"
                    + DateHelper.datetimeToString(new Date(System
                            .currentTimeMillis())) + "   线程" + threadName
                    + "未处理消息 " + msgID + "\r\n";
            fos.write(head.getBytes("GBK"));
            for (int j = 0; j < data.length; j++) {
                // value =(byte)(0x30+data[j]);
                biValue = Integer.toHexString((int) data[j]);
                fos.write(biValue.getBytes());
            }

            // fos.write(data);
            fos.flush();

        } catch (IOException e) {
            logger.error("写未处理消息到文件失败");

        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }

        }

    }

    public CommuThreadLogUtil() {
    }
}
