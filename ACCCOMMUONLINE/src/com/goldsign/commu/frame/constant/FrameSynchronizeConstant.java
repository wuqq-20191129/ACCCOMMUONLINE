/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.constant;

import com.goldsign.commu.frame.vo.SynchronizedControl;

/**
 *
 * @author hejj
 */
public class FrameSynchronizeConstant {
    // SOCKET 同步控制变量

    public final static SynchronizedControl CONTROL_SOCKET_START = new SynchronizedControl();// 启动同步
    public final static SynchronizedControl CONTROL_SOCKET_RESTART = new SynchronizedControl();// 重新启动同步
    public final static SynchronizedControl CONTROL_SOCKET_CLOSE = new SynchronizedControl();// 关闭同步
    public final static SynchronizedControl CONTROL_SOCKET_CLOSE_EXCEPTION = new SynchronizedControl();// 异常关闭同步
    public final static SynchronizedControl CONTROL_SOCKET_CONNECTION = new SynchronizedControl();// 连接关闭同步
    // 连接池 同步控制变量
    public final static SynchronizedControl CONTROL_CP_OL = new SynchronizedControl();// OL连接池启动同步

    public final static SynchronizedControl CONTROL_CP_ST = new SynchronizedControl();// ST连接池启动同步

    // 线程池 同步控制变量
    public final static SynchronizedControl CONTROL_TM = new SynchronizedControl();// 线程池同步

}
