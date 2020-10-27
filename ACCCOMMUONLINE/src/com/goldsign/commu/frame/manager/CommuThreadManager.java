/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.manager;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameSynchronizeConstant;
import com.goldsign.commu.frame.thread.CommuMessageHandleThread;
import com.goldsign.commu.frame.util.CommuThreadLogUtil;
import com.goldsign.commu.frame.util.ThreadUtil;
import com.goldsign.commu.frame.vo.CommuHandledMessage;
import com.goldsign.commu.frame.vo.TMMonitorResult;
import com.goldsign.commu.frame.vo.TMThreadMsg;
import com.goldsign.commu.frame.vo.TMThreadStatusVo;

/**
 *
 * @author hejj
 */
public class CommuThreadManager {

    public static Vector<CommuMessageHandleThread> handleThreads = new Vector<CommuMessageHandleThread>();
    private static int nextThread = 0;
    private static int nextThread_seq = 0;
    private static Logger logger = Logger.getLogger(CommuThreadManager.class
            .getName());

    public CommuThreadManager() {
    }

    public int startHandleThreads() {
        CommuMessageHandleThread t = null;
        for (int i = 0; i < FrameCodeConstant.MaxThreadNumber; i++) {
            t = new CommuMessageHandleThread();
            t.setPriority(FrameCodeConstant.ThreadPriority);
            t.setName(new Integer(i).toString());
            CommuThreadManager.handleThreads.add(t);
            t.start();
        }
        logger.info(handleThreads.size() + "个处理线程已启动");
        return handleThreads.size();
    }

    public synchronized CommuMessageHandleThread getDegradeMsgHandleThread() {
        return (CommuMessageHandleThread) CommuThreadManager.handleThreads
                .get(0);
    }

    public synchronized CommuMessageHandleThread getNextMsgHandleThread() {
        CommuMessageHandleThread t = null;
        // 如果要获取的线程号超过最大,复0
        if (nextThread >= FrameCodeConstant.MaxThreadNumber) {
            nextThread = 0;
        }
        // search twice at most
        // 查找当前可用线程从上次取出的线程开始,如果线程正在处理,跳过.最多循环查找次数由配置文件设置
        for (int j = 0; j < FrameCodeConstant.MaxSearchNum; j++) {
            for (int i = nextThread; i < FrameCodeConstant.MaxThreadNumber; i++) {
                t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                        .get(i);
                if (!t.isHandlingMsg()) {
                    nextThread++;
                    return t;

                }
            }
            nextThread = 0;// search from start
        }

        // 使用上面方法找不到,返回第一个线程
        if (nextThread_seq >= FrameCodeConstant.MaxThreadNumber) {
            nextThread_seq = 0;
        }

        t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                .get(nextThread_seq);
        nextThread_seq++; // next one

        // logger.info("min remain msg="+minRemainMsgs + " "+ mht.getName());
        return t;

    }

    public static void stopHandleThread() {
        CommuMessageHandleThread t = null;
        Vector msgs = null;
        CommuHandledMessage msg = null;
        for (int i = 0; i < handleThreads.size(); i++) {
            t = (CommuMessageHandleThread) handleThreads.get(i);
            if (t == null) {
                logger.info("第" + i + "个线程已是NULL值");
                continue;
            }
            // 由于数据库连接异常关闭时,将线程未处理的消息放入代处理缓存
            /*
			 * if(CommuConnectionPoolListener.isSqlConExceptionClose.booleanValue
			 * ()){ msgs = t.getAllMsgs(); for(int j=0;j<msgs.size();j++){ msg =
			 * (CommuHandledMessage)msgs.get(j);
			 * CommuExceptionBuffer.setSqlUnHandleMsgs(msg.getIp(), (byte[])
			 * msg.getReadResult(). get(1)); } continue; }
             */
            // 由于强制关闭时,将消息写入文件

            msgs = t.getAllMsgs();
            msg = t.getMsg();
            if (msgs != null) {
                CommuThreadLogUtil.writeUnHandleMsgToFile(t.getName(), msgs);
            }
            if (msg != null) {
                CommuThreadLogUtil.writeErrHandleMsgToFile(t.getName(),
                        msg.getReadResult());
            }

        }

    }

    private static boolean isThreadByName(CommuMessageHandleThread t,
            String threadName) {
        if (threadName == null || threadName.length() == 0) {
            return false;
        }
        return threadName.equals(t.getName());
    }

    private static void setThreadAttribute(CommuMessageHandleThread t,
            String threadName) {
        t.setPriority(FrameCodeConstant.ThreadPriority);
        t.setName(threadName);
        // logger.error("设置替换线程属性，优先级="+ApplicationConstant.ThreadPriority+"，线程名称="+threadName);
    }

    private static void setThreadMsgs(CommuMessageHandleThread tnew,
            CommuMessageHandleThread t) {
        tnew.setPriorityMsgs(t.getPriorityMsgs());
        tnew.setMsgs(t.getMsgs());
        // 清理就线程消息缓存 2012-06-08
        t.getPriorityMsgs().clear();
        t.getMsgs().clear();
        // logger.error("被替换线程消息移至新线程，优先级消息数量="+t.getPriorityMsgs().size()+"，普通消息数量=="+t.getMsgs().size());
    }

    // 替换线程组
    public static void replaceHandleThreads(TMMonitorResult mr,
            Vector newThreads, Vector oldThreads) {

        TMThreadStatusVo threadStatus;

        synchronized (FrameSynchronizeConstant.CONTROL_TM) {
            for (int i = 0; i < mr.getVos().size(); i++) {
                threadStatus = (TMThreadStatusVo) mr.getVos().get(i);
                CommuThreadManager.replaceHandleThread(threadStatus,
                        newThreads, oldThreads);
            }

        }

    }

    public static void repForSetBufferThreadHandups(TMMonitorResult mr) {

        TMThreadStatusVo threadStatus;

        synchronized (FrameSynchronizeConstant.CONTROL_TM) {
            for (int i = 0; i < mr.getVos().size(); i++) {
                threadStatus = (TMThreadStatusVo) mr.getVos().get(i);
                CommuThreadManager.repForSetBufferThreadHandup(threadStatus);
            }

        }

    }

    public static void repForSetBufferThreadHandup(TMThreadStatusVo threadStatus) {
        String threadName = threadStatus.getThreadName();
        CommuMessageHandleThread t;
        for (int i = 0; i < CommuThreadManager.handleThreads.size(); i++) {
            t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                    .get(i);

            if (CommuThreadManager.isThreadByName(t, threadName)) {
                // 记录挂起线程的消息，作为分析是否需要将同类消息在所有消息队列中清理掉，已防止

                // 在新线程中同样造成堵塞挂起,统计消息的挂起次数
                ThreadUtil.setBufferThreadHandup(t.getHandlingMsg());
            }

        }
    }

    public static void synAllThreads() {
        CommuMessageHandleThread t;
        for (int i = 0; i < CommuThreadManager.handleThreads.size(); i++) {
            t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                    .get(i);
            t.setIsSynForAll(true);
        }
    }

    public static void synReleaseAllThreads() {
        CommuMessageHandleThread t;
        for (int i = 0; i < CommuThreadManager.handleThreads.size(); i++) {
            t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                    .get(i);
            t.setIsSynForAll(false);
        }
    }

    public static boolean getSynStatusForAll(TMThreadStatusVo threadStatus) {
        CommuMessageHandleThread t;
        CommuMessageHandleThread tnew;
        boolean isSynStatusForAll = true;
        boolean isSynStatus;
        String threadName = threadStatus.getThreadName();
        for (int i = 0; i < CommuThreadManager.handleThreads.size(); i++) {
            t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                    .get(i);
            if (!CommuThreadManager.isThreadByName(t, threadName)) {
                isSynStatus = t.getIsSyningForAll();
                isSynStatusForAll = isSynStatusForAll & isSynStatus;
            }
        }
        return isSynStatusForAll;
    }

    public static boolean waitSynForAllThreads() {
        return false;

    }

    public static void replaceHandleThread(TMThreadStatusVo threadStatus,
            Vector newThreads, Vector oldThreads) {
        CommuMessageHandleThread t;
        CommuMessageHandleThread tnew;
        String threadName = threadStatus.getThreadName();

        for (int i = 0; i < CommuThreadManager.handleThreads.size(); i++) {
            t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                    .get(i);

            if (CommuThreadManager.isThreadByName(t, threadName)) {
                // 记录挂起线程的消息，作为分析是否需要将同类消息在所有消息队列中清理掉，已防止

                // 在新线程中同样造成堵塞挂起,统计消息的挂起次数
                // ThreadUtil.setBufferThreadHandup(t.getHandlingMsg());
                tnew = new CommuMessageHandleThread();
                CommuThreadManager.setThreadAttribute(tnew, threadName);// 设置新线程属性

                CommuThreadManager.setThreadMsgs(tnew, t);// 旧线程消息导入新线程

                // 记录线程替换日志
                ThreadUtil.writeInfoForReplaceThread(tnew, t, threadStatus);
                // logger.error("记录线程替换日志");
                // 更新线程状态缓存

                ThreadUtil.updateThreadStatus(tnew.getName());
                // logger.error("更新新线程"+tnew.getName()+"状态缓存为初始化");

                CommuThreadManager.handleThreads.remove(i);// 旧线程从线程池缓存中删除
                CommuThreadManager.handleThreads.add(i, tnew);// 新线程取代旧线程在线程池中的位置
                // logger.error("旧线程从缓存中删除,新线程取代旧线程在线程池中的位置");

                releaseResource(t);
                logger.error("旧线程" + t.getName() + "释放资源,替换的新线程"
                        + tnew.getName());
                newThreads.add(tnew);
                oldThreads.add(t);
                // t.interrupt();//旧线程中断

                // 改为统一启动
                // tnew.start();//新线程启动
                // logger.error("旧线程"+t.getName()+"停止,新线程"+tnew.getName()+"启动");
            }

        }

    }

    private static void releaseResource(CommuMessageHandleThread t) {
        try {
            // t.join(1);
            // 杀死线程前，先释放线程占用的数据库连接资源 2012-04-16 hejj
            t.releaseResource();

            // t.interrupt();//2012-06-08 hejj
            // t.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Vector delMsgFromThreadMsgQueue(TMThreadMsg vo) {
        CommuMessageHandleThread t;
        Vector v = new Vector();
        for (int i = 0; i < CommuThreadManager.handleThreads.size(); i++) {
            t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                    .get(i);
            v.addAll(t.delHandupMsgs(vo));
        }
        return v;

    }

    public static void startNewThread(Vector newThreads) {
        CommuMessageHandleThread t;
        for (int i = 0; i < newThreads.size(); i++) {
            t = (CommuMessageHandleThread) newThreads.get(i);
            t.start();
        }
    }

    public static void killOldThread(Vector oldThreads) {
        CommuMessageHandleThread t;
        for (int i = 0; i < oldThreads.size(); i++) {
            t = (CommuMessageHandleThread) oldThreads.get(i);
            t.stop();
        }
    }
}
