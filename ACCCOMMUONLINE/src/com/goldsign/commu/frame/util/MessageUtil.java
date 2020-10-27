/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class MessageUtil {

    private static Logger logger = Logger
            .getLogger(MessageUtil.class.getName());

    // 进站客流缓存
    public static boolean decreaseBufferKeepCurrent(TreeMap buffer) {
        synchronized (buffer) {
            if (buffer.isEmpty()) {
                return false;
            }
            boolean isClear = false;

            String current = DateHelper.dateOnlyToString(new Date());
            String before = DateHelper.getDateBefore(current, 3600000 * 24);
            String after = DateHelper.getDateAfter(current, 3600000 * 24);
            String[] keeps = {before, current, after};
            Set keys = buffer.keySet();
            Iterator it = keys.iterator();
            String key;
            Vector v = new Vector();
            while (it.hasNext()) {
                key = (String) it.next();
                if (!isKeep(keeps, key)) {
                    v.add(key);

                }
            }
            for (int i = 0; i < v.size(); i++) {
                key = (String) v.get(i);
                buffer.remove(key);
                isClear = true;
                logger.error(" 缓存删除 key=" + key + "的内容");
            }

            return isClear;

        }
    }

    private static boolean isKeep(String[] keeps, String key) {
        String tmp;
        for (int i = 0; i < keeps.length; i++) {
            tmp = keeps[i];
            if (tmp.equals(key)) {
                return true;
            }
        }
        return false;

    }
}
