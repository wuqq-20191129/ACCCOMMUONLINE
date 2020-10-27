package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameSynchronizeConstant;
import com.goldsign.commu.frame.listener.CommuConnectionPoolListener;
import com.goldsign.commu.frame.server.OnlineServer;

import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class SocketUtil {

    public static Boolean isClosedServerSocket = false;
    public static Boolean isRestartedServerSocket = false;
    private static Logger logger = Logger.getLogger(SocketUtil.class);

    public static void closeServerSocketForCPListener(String msgFlag) {

        try {
            if (OnlineServer.serverSocket != null) {
                synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CLOSE_EXCEPTION) {
                    CommuConnectionPoolListener.isSqlConExceptionClose = true;
                }

                OnlineServer.serverSocket.close();
                OnlineServer.serverSocket = null;
                OnlineServer.closeConnectedSockets();

                CommuConnectionPoolListener.dumpThreadUnHandledMsgs();
                logger.info(msgFlag + "数据库连接异常,关闭监听服务器");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    public static void closeServerSocketForThreadMonitor(String threadId) {
        try {
            if (OnlineServer.serverSocket != null) {
                synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CLOSE_EXCEPTION) {
                    CommuConnectionPoolListener.isSqlConExceptionClose = true;
                }

                OnlineServer.serverSocket.close();
                OnlineServer.serverSocket = null;
                OnlineServer.closeConnectedSockets();

                // CommuConnectionPoolListener.dumpThreadUnHandledMsgs();
                logger.info(threadId + "线程异常,关闭监听服务器");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    public static void setRestartedStatus(boolean status) {
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_RESTART) {
            SocketUtil.isRestartedServerSocket = status;
        }
    }

    public static boolean isNeedRestartServerSocket() {
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_RESTART) {
            return SocketUtil.isRestartedServerSocket.booleanValue();
        }
    }
}
