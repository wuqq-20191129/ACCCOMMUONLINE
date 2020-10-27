package com.goldsign.commu.app.dao;

import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;

/**
 * 查询序列号
 *
 *
 * @author zhangjh
 */
public class SeqDao {

    // 初始化
    private static final SeqDao SEQ_DAO = new SeqDao();

    /**
     * 私有构造方法
     *
     */
    private SeqDao() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static SeqDao getInstance() {
        return SEQ_DAO;
    }

    /**
     * 根据序列标签查询出序列号
     *
     * @param seqLab 序列标签
     * @return 序列号
     *
     * @throws SQLException SQLException
     */
    public long selectNextSeq(String seqLab, DbHelper helper)
            throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append(" select ").append(seqLab).append(".NEXTVAL seqno")
                .append(" from dual");
        long seqNo = -1;
        boolean result = helper.getFirstDocument(sb.toString());
        if (result) {
            seqNo = helper.getItemLongValue("seqno");
        }
        return seqNo;

    }
}
