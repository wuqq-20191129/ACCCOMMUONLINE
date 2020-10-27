/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.constant;

/**
 *
 * @author hejj
 */
public class FrameLogConstant {

    /**
     * *****系统日志级别*************************************************
     */
    public static String LOG_LEVEL_CURRENT = "1";// 日志级别
    public static String LOG_LEVEL_INFO = "1";// 普通信息级
    public static String LOG_LEVEL_WARN = "2";// 警告信息级
    public static String LOG_LEVEL_ERROR = "3";// 一般错误级
    public static String LOG_LEVEL_ERROR_SYS = "4";// 系统错误级

    public static String LOG_LEVEL_TEXT_CURRENT = "普通信息";
    public static String LOG_LEVEL_INFO_TEXT = "普通信息";// 普通信息级
    public static String LOG_LEVEL_WARN_TEXT = "警告信息";// 警告信息级
    public static String LOG_LEVEL_ERROR_TEXT = "一般错误";// 一般错误级
    public static String LOG_LEVEL_ERROR_SYS_TEXT = "系统错误";// 系统错误级

    /**
     * ************ 消息ID*******************************************
     */
    public static String MESSAGE_ID_NON_RETURN = "16";// 非即时退款申请信息

    public static String MESSAGE_ID_START = "101";// 应用启动
    public static String MESSAGE_ID_CONFIG = "102";// 配置设置
    public static String MESSAGE_ID_PUSH_QUEUE = "104";// 消息入库待发送
    public static String MESSAGE_ID_PARAM_COMMU_QUEUE = "105";// 通讯队列处理
    public static String MESSAGE_ID_CONNECTION = "106";// SOCKET连接处理
    public static String MESSAGE_ID_SOCKET_EXCHAGE = "107";// SOCKET消息交换
    public static String MESSAGE_ID_FTP = "108";// FTP文件
    /**
     * 消息名称*****************************************************************
     */
    public static String MESSAGE_ID_NON_RETURN_NAME = "非即时退款申请信息";// 非即时退款申请信息
    public static String MESSAGE_ID_START_NAME = "应用启动";// 应用启动
    public static String MESSAGE_ID_CONFIG_NAME = "配置设置";// 配置设置
    public static String MESSAGE_ID_PUSH_QUEUE_NAME = "消息入库待发送";// 消息入库待发送
    public static String MESSAGE_ID_PARAM_COMMU_QUEUE_NAME = "通讯队列处理";// 通讯队列处理
    public static String MESSAGE_ID_CONNECTION_NAME = "SOCKET连接";// SOCKET连接处理
    public static String MESSAGE_ID_SOCKET_EXCHAGE_NAME = "SOCKET消息交换";// SOCKET消息交换
    public static String MESSAGE_ID_FTP_NAME = "FTP文件";// FTP文件
    /**
     * 消息的处理结果，记录日志使用************************
     */
    public static String RESULT_HDL_SUCESS = "0";// 成功
    public static String RESULT_HDL_FAIL = "1";// 失败
    public static String RESULT_HDL_WARN = "2";// 警告

    // 日志级别线程时间间隔
    public static long ThreadLogSleepTime = 300000;
}
