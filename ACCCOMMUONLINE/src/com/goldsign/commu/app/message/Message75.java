package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.QrPayDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.vo.QrPayOrderVo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.dao.PubDao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Description:支付二维码购票取消通知请求
 *
 * @author: zhongziqi
 * @Date: 2019-01-03
 * @Time: 15:51
 */
public class Message75 extends MessageValidate {
    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message75.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_cancel";
    //订单取消正常
    private static final String RETURN_CODE_NOMAL = "00";
    //SAM卡不存在
    private static final String RETURN_CODE_SAM_CARD_NOT_EXIST = "01";
    //终端编号不合法
    private static final String RETURN_CODE_TERMINAL_NO_ILLEGAL = "02";
    //订单号不合法
    private static final String RETURN_CODE_ORDER_NO_ILLEGAL = "03";
    //订单号不存在
    private static final String RETRUN_CODE_ORDER_NO_NOT_EXIST = "04";
    //中心系统异常
    private static final String RETRUN_CODE_SYSTEM_ABNOMAL = "10";
    //其他异常
    private static final String RETURN_CODE_OTHER_ABNOMAL = "99";

    @Override
    public void run() throws Exception {
        fix_recv_data_length = 82;
        process();
    }

    private void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    private void processMessage() throws Exception {
        QrPayOrderVo constructVo = new QrPayOrderVo();
        try {
//            logger.info("-- 响应支付二维码购票通知请求75开始 --");
            ArrayList logList = new ArrayList();
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            logList.add("支付二维码购票通知请求75消息");
            validateDataLen();
            getConstructVo(constructVo);
            QrPayDao qrPayDao = new QrPayDao();
            qrPayDao.insertQrPayCancel(constructVo, getOLDbHelper());
            QrPayOrderVo queryVo = qrPayDao.queryByOrderNo(constructVo, getOLDbHelper());
            constructMessageVo(constructVo, queryVo, qrPayDao);
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(constructVo);
            qrPayDao.insertQrPayCancelRsp(constructVo, getOLDbHelper());
            writeBackMsg(constructVo);
//            logger.info("-- 响应支付二维码app退款通知请求75结束 --");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
            // 出现异常返回响应的错误信息给终端
            dealException(constructVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) +"处理75消息出现异常", e);

            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }
    }

    private void dealException(QrPayOrderVo constructVo, Exception e) {
        String errCode = doException(e);
        constructVo.setReturnCode(RETRUN_CODE_SYSTEM_ABNOMAL);
        constructVo.setErrCode(errCode);
        //消息生成时间
        constructVo.setMsgGenTime(DateHelper.currentTodToString());

        writeBackMsg(constructVo);
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
        byte[] msgVo = new ConstructMessage85().constructMessage(constructVo);
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
        constructVo.setAccSeq(queryVo.getAccSeq());
        boolean result = true;
        //03:订单号编码不合法
        String orderNo = constructVo.getOrderNo();
        //订单号长度
        result = orderNo.length() == 14;
        //日期
        if (result) {
            Date d = new Date(System.currentTimeMillis());
            String dateStr = DateHelper.dateOnlyToString(d);
            result = dateStr.equals(orderNo.substring(0, 8));
        }
        //序号
        if (result) {
            String patternReg = "^[0-9]*$";
            result = Pattern.compile(patternReg).matcher(orderNo.substring(8)).find();
        }
        if (result) {
            result = PubDao.checkDevCode(constructVo.getTerminalNo());
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("终端编号：" + constructVo.getTerminalNo());
        } else {
            constructVo.setReturnCode(RETURN_CODE_ORDER_NO_ILLEGAL);//03:订单号编码不合法
            return;
        }

        if (result) {
            result = PubDao.checkPSAM(constructVo.getSamLogicalId());
        } else {
            constructVo.setReturnCode(RETURN_CODE_TERMINAL_NO_ILLEGAL);//02:终端编号不合法
            return;
        }

        if (result) {
            result = qrPayDao.checkOrderNo(constructVo, getOLDbHelper());
        } else {
            constructVo.setReturnCode(RETURN_CODE_SAM_CARD_NOT_EXIST);//01:SAM卡不存在
            return;
        }
        if (result) {
            int n = 0;
            //取消优先于支付
            if (FrameCodeConstant.ORDER_STATUS_NON_PAYMENT.equals(queryVo.getStatus()) ||
                    FrameCodeConstant.ORDER_STATUS_PAY_FAILURE.equals(queryVo.getStatus()) ||
                    FrameCodeConstant.ORDER_STATUS_PAID.equals(queryVo.getStatus())
                    ) {
                constructVo.setReturnCode(RETURN_CODE_NOMAL);
                constructVo.setLastStatus(queryVo.getStatus());
                if ( queryVo.getSaleTimes() == 0) {
                    n = qrPayDao.updateCancelQrPayOrder(constructVo, getOLDbHelper());
                }else {
                    ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("订单"+constructVo.getOrderNo()+"无需更新，已出票");
                }
            } else {
                ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("订单"+constructVo.getOrderNo()+"无需更新，订单状态"+constructVo.getStatus());
                //已取消订单 已退款
                constructVo.setReturnCode(RETURN_CODE_NOMAL);
            }
        } else {
            constructVo.setReturnCode(RETRUN_CODE_ORDER_NO_NOT_EXIST);//04:设备订单号不存在
            return;
        }
    }

    private void getConstructVo(QrPayOrderVo constructVo) throws Exception {
        String messageId = getCharString(0, 2);
        // 消息生成时间
        String msgGenTime = getBcdString(2, 7);
        // 终端编号
        String terminalNo = getCharString(9, 9);
        // Sam卡逻辑卡号
        String samLogicalId = getCharString(18, 16);
        // 终端处理流水号
        long terminaSeq = getLong2(34);
        // 订单号
        String orderNo = getCharString(38, 14);
        // 订单生成时间
        String orderDate = getBcdString(52, 7);
        // 发售票卡类型
        String cardType = getBcdString(59, 2);
        // 发售单程票单价
        long saleFee = getLong2(61);
        // 发售单程票数量
        long saleTimes = getLong2(65);
        // 发售单程票总价
        long dealFee = getLong2(69);
        //订单取消时间
        String orderCancelTime = getBcdString(73, 7);
        //订单取消原因
        String orderCancelReason = getCharString(80, 2);

        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());

        constructVo.setWaterNo(waterNo);
        constructVo.setMessageId(messageId);
        constructVo.setMsgGenTime(msgGenTime);
        constructVo.setTerminalNo(terminalNo);
        constructVo.setSamLogicalId(samLogicalId);
        constructVo.setTerminaSeq(terminaSeq);
        constructVo.setOrderNo(orderNo);
        constructVo.setOrderDate(orderDate);
        constructVo.setCardType(cardType);
        constructVo.setSaleFee(saleFee);
        constructVo.setSaleTimes(saleTimes);
        constructVo.setDealFee(dealFee);
        constructVo.setOrderCancelTime(orderCancelTime);
        constructVo.setGetOrderCancelReason(orderCancelReason);

        constructVo.setOrderIp(this.messageFrom);
        //中心流水号
        Long accSeq = new QrPayDao().getQrPayAccSeq(constructVo, getOLDbHelper());
        constructVo.setAccSeq(accSeq);

        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(constructVo.toString());
    }

}
