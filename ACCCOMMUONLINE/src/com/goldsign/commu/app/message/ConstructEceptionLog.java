/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.EceptionLog;

/**
 *
 * @author hejj
 */
public class ConstructEceptionLog {

    private final static String EMPTY = "";

    /**
     *
     * @param className
     * @param desc
     * @return
     */
    public static EceptionLog constructLog(String className, String desc) {
        return constructLog(EMPTY, EMPTY, EMPTY, className, desc);
    }

    /**
     *
     * @param className
     * @param desc
     * @return
     */
    public static EceptionLog constructLog(String ip, String className,
            String desc) {
        return constructLog(ip, EMPTY, EMPTY, className, desc);
    }

    /**
     *
     * @param excpId
     * @param excpType
     * @param className
     * @param desc
     * @return
     */
    public static EceptionLog constructLog(String excpId, String excpType,
            String className, String desc) {
        return constructLog(EMPTY, excpId, excpType, className, desc);
    }

    /**
     *
     * @param ip
     * @param excpId
     * @param excpType
     * @param className
     * @param desc
     * @return
     */
    public static EceptionLog constructLog(String ip, String excpId,
            String excpType, String className, String desc) {
        EceptionLog log = new EceptionLog();
        log.setExcpId(excpId);
        log.setExcpType(excpType);
        log.setClassName(className);
        log.setExcpDesc(desc);
        log.setIp(ip);
        return log;
    }
}
