package com.goldsign.commu.frame.util;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Description:
 * 用于所有日志汇总成一行
 *
 * @author: zhongziqi
 * @Date: 2019-08-21
 * @Time: 10:50
 */
public class ThreadLocalUtil {
    public static ThreadLocal<ArrayList> LOG_THREAD_LOCAL = new ThreadLocal<ArrayList>();

    /**
     * @param list
     * @param isEnd 是否后续没有添加输出
     * @return
     */
    public String ArrayToString(ArrayList list, boolean isEnd) {
        int para = 0;
        if (isEnd) {
            para = 1;
        }
        if (list == null || list.size() == 0) {
            return "Nothing ArrayList";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append((String) list.get(i));
            if ((i + para) < list.size()) {
                stringBuilder.append("->");
            }
        }
        LOG_THREAD_LOCAL.remove();
        return stringBuilder.toString();
    }

//    public static synchronized String printBytesToString(byte[] bytes){
//        String result = "";
//        try{
//            result = Arrays.toString(bytes);
//        }catch (Exception e){
////            e.printStackTrace();
//            result ="error:"+e.getMessage();
//        }
//        return result;
//    }

}
