package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.QrPayDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.vo.QrPayOrderVo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-01-02
 * @Time: 17:01
 */
public class Message74 extends MessageValidate {
    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message74.class.getName());
    private static final String SEQ_NAME = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_refund";
    //响应码
    //正常
    private static final String RETURN_CODE_NOMAL = "00";
    //系统处理过程出现异常
    private static final String RETURN_CODE_ABNOMAL = "01";
    //已支付没出票
    private static final String UPDATE_SCENE_PAID = "1";
    //取消未出票
    private static final String UPDATE_SCENE_CANCEL = "2";

    @Override
    public void run() throws Exception {
        fix_recv_data_length = 128;
        process();
    }

    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    private void processMessage() throws Exception {
        String nowDate = DateHelper.dateToString(new Date());
        QrPayOrderVo constructVo = new QrPayOrderVo();
        try {
//            logger.info("-- 响应支付二维码app退款通知请求74开始 --");
            ArrayList logList = new ArrayList();
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            logList.add("支付二维码app退款通知请求74消息");
            validateDataLen();
            getConstructVo(constructVo);
            QrPayDao qrPayDao = new QrPayDao();
            qrPayDao.insertQrPayRefund(constructVo, getOLDbHelper());
            QrPayOrderVo queryVo = new QrPayOrderVo();
            queryVo = qrPayDao.qrPayOrder(constructVo, getOLDbHelper());
            constructVo.setAccHandleTime(nowDate);
            constructMessageVo(constructVo, queryVo, qrPayDao);
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(constructVo);
            qrPayDao.insertQrPayRefundRsp(constructVo, getOLDbHelper());
            writeBackMsg(constructVo);
//            logger.info("-- 响应支付二维码app退款通知请求74结束 --");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
            // 出现异常返回响应的错误信息给终端
            dealException(constructVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) + "处理74消息出现异常", e);

            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }
    }

    private void writeBackMsg(QrPayOrderVo constructVo) {
        MessageQueue backMesg = getMessageQueue(constructVo);
        sendMsgToConnectionQueue(backMesg);
    }

    private void sendMsgToConnectionQueue(MessageQueue backMesg) {
        CommuConnection con = this.bridge.getConnection();
        con.setMessageInConnectionQueue(backMesg);
    }

    private MessageQueue getMessageQueue(QrPayOrderVo constructVo) {
        Date masGenTime = new Date(System.currentTimeMillis());
        constructVo.setMsgGenTime(DateHelper.dateToString(masGenTime));
        byte[] msgVo = new ConstructMessage84().constructMessage(constructVo);
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(masGenTime);
        messageQueue.setIpAddress(this.messageFrom);
        messageQueue.setMessage(msgVo);
        messageQueue.setIsParaInformMsg("0");
        messageQueue.setParaInformWaterNo(0);
        messageQueue.setLineId("");
        messageQueue.setStationId("");
        messageQueue.setMessageSequ(messageSequ);
        return messageQueue;
    }

    private void constructMessageVo(QrPayOrderVo constructVo, QrPayOrderVo queryVo, QrPayDao qrPayDao) throws SQLException {
        int n = 0;
        constructVo.setLastStatus(queryVo.getStatus());
        if (FrameCodeConstant.ORDER_STATUS_PAID.equals(queryVo.getStatus())) {
            constructVo.setStatus(FrameCodeConstant.ORDER_STATUS_REFUNDED);
            constructVo.setUpdateFlag(UPDATE_SCENE_PAID);
            n = qrPayDao.updateRefundQrPayOrder(constructVo, getOLDbHelper());
            if (n == 0) {
                logger.info("订单更新失败==QrpayId=" + constructVo.getQrpayId() + ",QrpayData=" + constructVo.getQrpayData() + ",status=" + queryVo.getStatus());
            }
        } else if (FrameCodeConstant.ORDER_STATUS_CANCEL.equals(queryVo.getStatus())) {
            constructVo.setStatus(FrameCodeConstant.ORDER_STATUS_REFUNDED);
            constructVo.setUpdateFlag(UPDATE_SCENE_CANCEL);
            n = qrPayDao.updateRefundQrPayOrderByCancel(constructVo, getOLDbHelper());
            if (n == 0) {
                logger.info("订单更新失败==QrpayId=" + constructVo.getQrpayId() + ",QrpayData=" + constructVo.getQrpayData() + ",status=" + queryVo.getStatus());
            }
        } else {
            logger.info("无需更新订单" + queryVo.getOrderNo() + "退款状态，订单状态" + queryVo.getStatus());
            constructVo.setReturnCode(RETURN_CODE_NOMAL);
        }

    }

    private void getConstructVo(QrPayOrderVo constructVo) throws Exception {
        // 消息类型
        String messageId = getCharString(0, 2);
        // 消息生成时间
        String msgGenTime = getBcdString(2, 7);
        // 终端编号
        String terminalNo = getCharString(9, 9);
        // HCE处理流水号
        long terminaSeq = getLong2(18);
        // 手机号
        String phoneNo = getCharString(22, 11);
        // 手机用户标识
        String imsi = getCharString(33, 15);
        // 手机设备标识
        String imei = getCharString(48, 15);
        // app渠道
        String appCode = getCharString(63, 2);
        // 支付标识
        String qrPayID = getCharString(65, 10);
        // 支付二维码信息
        String qrPayData = getCharString(75, 34);

        // 支付渠道类型
        String payChannelType = getCharString(109, 2);
        // 支付渠道代码
        String payChannelCode = getCharString(111, 4);
        // 退款时间
        String refundDate = getBcdString(115, 7);
        // 退款金额
        long refunFee = getLong2(122);
        //退款原因
        String refundReason = getCharString(126, 2);

        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_NAME, getOLDbHelper());

        constructVo.setWaterNo(waterNo);
        constructVo.setMessageId(messageId);
        constructVo.setMsgGenTime(msgGenTime);
        constructVo.setTerminalNo(terminalNo);
        constructVo.setPhoneNo(phoneNo);
        constructVo.setTerminaSeq(terminaSeq);
        constructVo.setImei(imei);
        constructVo.setImsi(imsi);
        constructVo.setAppCode(appCode);
        constructVo.setQrpayId(qrPayID);
        constructVo.setQrpayData(qrPayData);
        constructVo.setPayChannelType(payChannelType);
        constructVo.setPayChannelCode(payChannelCode);
        constructVo.setRefundDate(refundDate);
        constructVo.setRefundFee(refunFee);
        constructVo.setRefundReason(refundReason);
        Long accSeq = new QrPayDao().getQrPayAccSeq(constructVo, getOLDbHelper());
        constructVo.setAccSeq(accSeq);
        logger.info(constructVo.toString());
    }

    private void dealException(QrPayOrderVo qrPayOrderVo, Exception e) {
        String errCode = doException(e);
        qrPayOrderVo.setReturnCode(RETURN_CODE_ABNOMAL);
        qrPayOrderVo.setErrCode(errCode);
        //消息生成时间
        qrPayOrderVo.setMsgGenTime(DateHelper.currentTodToString());

        writeBackMsg(qrPayOrderVo);
    }

}
