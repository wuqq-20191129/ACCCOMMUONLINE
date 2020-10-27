package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.QrPayOrderVo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-01-03
 * @Time: 23:09
 */
public class ConstructMessage85 extends ConstructMessageBase {
    private static Logger logger = Logger.getLogger(ConstructMessage85.class
            .getName());

    public ConstructMessage85() {
    }

    byte[] constructMessage(QrPayOrderVo qrPayOrderVo) {
        qrPayOrderVo.setMessageId("85");
        try {
            initMessage();
            AddStringToMessage(qrPayOrderVo.getMessageId(), 2);
            AddBcdToMessage(qrPayOrderVo.getMsgGenTime(), 7);
            AddStringToMessage(qrPayOrderVo.getTerminalNo(), 9);//终端编号
            AddStringToMessage(qrPayOrderVo.getSamLogicalId(), 16);
            AddLongToMessage(qrPayOrderVo.getTerminaSeq(), 4);//终端处理流水号
            AddStringToMessage(qrPayOrderVo.getOrderNo(), 14);//订单号
            AddBcdToMessage(qrPayOrderVo.getOrderDate(), 7);//订单生成时间
            AddLongToMessage(qrPayOrderVo.getAccSeq(), 4);//中心处理流水号
            AddStringToMessage(qrPayOrderVo.getReturnCode(), 2);//响应码
            byte[] msg = trimMessage();
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("85["+ Arrays.toString(msg)+"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 85 error! " + e);
            return new byte[0];
        }
    }
}
