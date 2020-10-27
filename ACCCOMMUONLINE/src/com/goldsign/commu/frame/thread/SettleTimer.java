/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.thread;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @datetime 2018-7-4 21:45:45
 * @author lind
 * 多元支付清算定时任务
 */
public class SettleTimer {
    
    // 安排指定的任务task在指定的时间firstTime开始进行重复的固定速率period执行．
    public static void timer() {
        //查询清算时间
        
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2); // 控制时
        calendar.set(Calendar.MINUTE, 0);       // 控制分
        calendar.set(Calendar.SECOND, 0);       // 控制秒
 
        Date time = calendar.getTime();         // 得出执行任务的时间,此处为运营日时间02：00：00
 
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println("-------设定要指定任务--------");
            }
        }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行
    }

}
