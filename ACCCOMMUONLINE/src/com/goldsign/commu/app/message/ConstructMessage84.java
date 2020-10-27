package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.QrPayOrderVo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Description:
 *支付二维码app退款通知响应（82）
 * @author: zhongziqi
 * @Date: 2019-01-03
 * @Time: 15:42
 */
public class ConstructMessage84 extends ConstructMessageBase {
    private static Logger logger = Logger.getLogger(ConstructMessage84.class
            .getName());

    public ConstructMessage84() {
    }

    byte[] constructMessage(QrPayOrderVo qrPayOrderVo) {
        qrPayOrderVo.setMessageId("84");
        try {
            initMessage();
            AddStringToMessage(qrPayOrderVo.getMessageId(), 2);
            AddBcdToMessage(qrPayOrderVo.getMsgGenTime(), 7);
            AddLongToMessage(qrPayOrderVo.getTerminaSeq(), 4);//HCE处理流水号
            AddLongToMessage(qrPayOrderVo.getAccSeq(), 4);//中心处理流水号
            AddStringToMessage(qrPayOrderVo.getQrpayId(), 10);//支付标识
            AddStringToMessage(qrPayOrderVo.getQrpayData(), 34);//支付二维码信息
            AddBcdToMessage(qrPayOrderVo.getAccHandleTime(), 7);//中心处理时间
            AddStringToMessage(qrPayOrderVo.getReturnCode(), 2);//返回响应码

            byte[] msg = trimMessage();
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("84["+ Arrays.toString(msg)+"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 84 error! " + e);
            return new byte[0];
        }
    }
}
