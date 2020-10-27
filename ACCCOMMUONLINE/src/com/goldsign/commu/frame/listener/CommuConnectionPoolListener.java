/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.listener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.buffer.CommuExceptionBuffer;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameSynchronizeConstant;
import com.goldsign.commu.frame.manager.CommuThreadManager;
import com.goldsign.commu.frame.thread.CommuMessageHandleThread;
import com.goldsign.commu.frame.thread.CommuServerThread;
import com.goldsign.commu.frame.util.SocketUtil;
import com.goldsign.commu.frame.vo.CommuHandledMessage;
import com.goldsign.lib.db.util.DbHelper;
import com.goldsign.lib.db.util.DbcpHelper;

/**
 *
 * @author hejj
 */
public class CommuConnectionPoolListener extends Thread {

    private static Logger logger = Logger
            .getLogger(CommuConnectionPoolListener.class.getName());

    private int count = 0;
    // private static DbcpHelper test_cp = null;
    private Hashtable test_config = null;
    private String msgFlag = "";
    private int flag = -1;
    private String testSql = "";

    public static Boolean isConnectForSTConnection = new Boolean(false);
    public static Boolean isConnectForOLConnection = new Boolean(false);
    public static Boolean isSqlConExceptionClose = new Boolean(false);

    public CommuConnectionPoolListener() {
    }

    public CommuConnectionPoolListener(Hashtable config, String msgFlag,
            int flag) {
        this.test_config = config;
        this.msgFlag = msgFlag;
        this.flag = flag;
        this.testSql = FrameDBConstant.TestSql;
    }

    //
    public void run() {
        logger.info(msgFlag + "连接池监听线程已启动...");
        while (true) {
            try {

                this.testConnectionPools();
                // 如是应急中心数据源，不作处理，其他数据源才作处理
                if (this.isOkForAllConnect()) {
                    if (SocketUtil.isNeedRestartServerSocket()) {
                        // 准备重新启动服务器端口，禁止多次重新启动服务器端口
                        SocketUtil.setRestartedStatus(false);
                        this.restartServerSocket();

                    }
                }

                CommuConnectionPoolListener
                        .sleep(FrameDBConstant.CPListenerThreadSleepTime);
            } catch (InterruptedException e) {
                logger.error("连接池监听线程将终止.........");
            }

        }
    }

    private boolean isOkForAllConnect() {
        synchronized (FrameSynchronizeConstant.CONTROL_CP_ST) {
            synchronized (FrameSynchronizeConstant.CONTROL_CP_OL) {
                if (CommuConnectionPoolListener.isConnectForSTConnection
                        .booleanValue()
                        && CommuConnectionPoolListener.isConnectForOLConnection
                                .booleanValue()) {
                    return true;
                } else {
                    return false;
                }
            }
        }

    }

    private void testConnectionPools() {
        // 测试连接
        if (!this.testConnection()) {
            // 设置连接不可用
            this.setConnectStatus(flag, false);
            synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CLOSE) {
                if (!SocketUtil.isClosedServerSocket.booleanValue()) {
                    SocketUtil.closeServerSocketForCPListener(this.msgFlag);
                    SocketUtil.isClosedServerSocket = new Boolean(true);
                }
            }
             logger.error("尝试重建" + msgFlag + "连接池" + (++count)
             + "次.....");
            return;
        }

        // 连接可用,正常运行测试连接无问题
        if (count == 0) {
            this.setConnectStatus(flag, true);

            return;
        }
        logger.error(msgFlag + "连接中断后已可用.....");

        // 重建连接池
        if (this.resetConnectonPool()) {
            // 设置连接池可用标志
            this.setConnectStatus(this.flag, true);
            // 设置可以重新启动服务器SOCK端口
            SocketUtil.setRestartedStatus(true);
        }

    }

    private void setConnectStatus(int flag, boolean status) {
        switch (this.flag) {
            case 0: {
                synchronized (FrameSynchronizeConstant.CONTROL_CP_ST) {
                    CommuConnectionPoolListener.isConnectForSTConnection = new Boolean(
                            status);
                }
                break;
            }
            case 1: {
                synchronized (FrameSynchronizeConstant.CONTROL_CP_OL) {
                    CommuConnectionPoolListener.isConnectForOLConnection = new Boolean(
                            status);
                }
                break;
            }
        }
    }

    private boolean testConnectionPool(DbcpHelper dbcp) {
        boolean result = false;
        DbHelper dbHelper = null;
        try {

            dbHelper = new DbHelper("data", dbcp.getConnection());
            result = dbHelper.getFirstDocument(testSql);
            return result;

        } catch (SQLException e) {
            logger.error(msgFlag + "数据库异常,错误代码:" + e.getErrorCode() + " 消息:"
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (dbHelper != null) {
                    dbHelper.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    private boolean testConnection() {
        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            DriverManager.setLoginTimeout(10);
            String url = (String) test_config.get("URL");
            String user = (String) test_config.get("User");
            String password = (String) test_config.get("Password");
            con = DriverManager.getConnection(
                    url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(this.testSql);
            if (rs.next()) {
                return true;
            }

        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException e) {
            logger.info(msgFlag + "数据库异常,错误代码:" + e.getErrorCode() + " 消息:", e);
            return false;

        } finally {
            try {
                if (con != null) {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    con.close();
                }

            } catch (SQLException e) {
                return false;

            }
        }
        return false;
    }

    private boolean resetConnectonPool() {
        DbcpHelper cp = null;

        try {
            cp = new DbcpHelper((String) test_config.get("Driver"),
                    (String) test_config.get("URL"),
                    (String) test_config.get("Database"),
                    (String) test_config.get("User"),
                    (String) test_config.get("Password"),
                    Integer.parseInt((String) test_config.get("MaxActive")),
                    Integer.parseInt((String) test_config.get("MaxIdle")),
                    Integer.parseInt((String) test_config.get("MaxWait")));

            if (!this.testConnectionPool(cp)) {
                logger.error("重建的" + msgFlag + "连接池不可用.....");
                return false;
            }

            switch (this.flag) {
                case 0: {
                    synchronized (FrameSynchronizeConstant.CONTROL_CP_ST) {
                        if (FrameDBConstant.ST_DBCPHELPER != null) {
                            FrameDBConstant.ST_DBCPHELPER.close();
                        }
                        FrameDBConstant.ST_DBCPHELPER = cp;
                    }
                    break;
                }
                case 1: {
                    synchronized (FrameSynchronizeConstant.CONTROL_CP_OL) {
                        if (FrameDBConstant.OL_DBCPHELPER != null) {
                            FrameDBConstant.OL_DBCPHELPER.close();
                        }
                        FrameDBConstant.OL_DBCPHELPER = cp;
                    }
                    break;

                }
            }

            logger.error("重设" + msgFlag + "连接池.....");
            this.count = 0;
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException e) {
            return false;
        }

    }

    private boolean restartServerSocket() {
        CommuServerThread server = new CommuServerThread();
        logger.error("数据库连接恢复,重新启动监听服务器");
        try {
            this.handleSqlBufferMsgs();
            server.start();
            // 恢复监听器监听功能

            this.restoreControl();
            return true;
        } catch (InterruptedException e) {
            return false;
        }

    }

    private void restoreControl() {
        logger.error("恢复控制参数");
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_START) {
            SocketUtil.isClosedServerSocket = new Boolean(false);
        }
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_RESTART) {
            SocketUtil.isRestartedServerSocket = new Boolean(false);
        }

        synchronized (FrameSynchronizeConstant.CONTROL_CP_ST) {
            isConnectForSTConnection = new Boolean(false);
        }
        synchronized (FrameSynchronizeConstant.CONTROL_CP_OL) {
            isConnectForOLConnection = new Boolean(false);
        }
    }

    private void handleSqlBufferMsgs() throws InterruptedException {
        Vector unHandleSqlMsgs = CommuExceptionBuffer.getSqlUnHandleMsgs();
        if (unHandleSqlMsgs == null || unHandleSqlMsgs.isEmpty()) {
            return;
        }
        CommuHandledMessage chm = null;
        CommuConnection con = new CommuConnection();
        logger.error("由于Sql连接异常需重新处理的消息记录为" + unHandleSqlMsgs.size() + "条");
        for (int i = 0; i < unHandleSqlMsgs.size(); i++) {
            // logger.error("处理Sql连接异常消息缓存记录第" + i + "条");
            chm = (CommuHandledMessage) unHandleSqlMsgs.get(i);
            con.processReaderResult(chm.getIp(), chm.getReadResult());
            CommuConnectionPoolListener
                    .sleep(FrameDBConstant.SqlMsgHandleSleepTime);

        }
        logger.error("成功重新处理由于Sql连接异常的消息记录为" + unHandleSqlMsgs.size() + "条");
    }

    public static void dumpThreadUnHandledMsgs() {
        CommuMessageHandleThread t = null;
        Vector msgs = null;
        CommuHandledMessage msg = null;
        for (int i = 0; i < CommuThreadManager.handleThreads.size(); i++) {
            t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                    .get(i);
            if (t == null) {
                logger.info("第" + i + "个线程已是NULL值");
                continue;
            }
            // 由于数据库连接异常关闭时,将线程未处理的消息放入待处理缓存

            msgs = t.getAllMsgs();

            logger.error("线程" + t.getName() + "未处理消息" + msgs.size());
            for (int j = 0; j < msgs.size(); j++) {
                // logger.error("class name:"+msgs.get(j).getClass().getName());

                msg = (CommuHandledMessage) msgs.get(j);
                CommuExceptionBuffer.setSqlUnHandleMsgs(msg.getIp(),
                        (byte[]) (msg.getReadResult().get(1)));
            }

        }

    }

}
