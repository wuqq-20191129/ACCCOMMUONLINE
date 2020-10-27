package com.goldsign.commu.frame.constant;

import com.goldsign.lib.db.util.DbHelper;
import com.goldsign.lib.db.util.DbcpHelper;

/**
 *
 * @author hejj
 */
public class FrameDBConstant {

    public static DbcpHelper OL_DBCPHELPER;
    public static DbcpHelper ST_DBCPHELPER;
    public static int CPListenerThreadSleepTime = 1000;
    public static String TestSql = "";
    public static int SqlMsgHandleSleepTime = 100;
    public static int FLAG_ST = 0;
    public static int FLAG_OL = 1;

    public static String TABLE_PREFIX = "w_";
    public static String COM_OL_P = "w_acc_ol." + TABLE_PREFIX;
    public static String COM_ST_P = "w_acc_st." + TABLE_PREFIX;
    public static String COM_TK_P = "w_acc_tk." + TABLE_PREFIX;
    public static String COM_TK = "w_acc_tk.";
    public static String COM_OL = "w_acc_ol.";
    public static String COM_ST = "w_acc_st.";
}
