/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.buffer.ThreadMonitorBuffer;
import com.goldsign.commu.frame.manager.CommuThreadManager;
import com.goldsign.commu.frame.vo.TMMonitorResult;
import java.util.Vector;

/**
 *
 * @author hejj
 */
public class CommuThreadPoolUtil {

    public static TMMonitorResult getMonitorResult() {
        return ThreadMonitorBuffer.getMonitorResult();
    }

    public static void replaceHandleThreads(TMMonitorResult mr,
            Vector newThreads, Vector oldThreads) {
        CommuThreadManager.replaceHandleThreads(mr, newThreads, oldThreads);

    }

    public static void repForSetBufferThreadHandups(TMMonitorResult mr) {
        CommuThreadManager.repForSetBufferThreadHandups(mr);
    }

}
