/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.util.HashMap;
import java.util.Map;

/**
 * @author lind
 * 支付二维码订单app支付结果请求
 * @datetime 2018-5-13
 */
public class Message72 extends MessageValidate {

    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message72.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_app";
    //支付成功
    private static final String PAY_STATUS_SUCCESS = "00";
    //余额不足
    private static final String PAY_STATUS_NON_BALANCE = "01";
    //黑名单账户
    private static final String PAY_STATUS_BLACKLIST = "02";
    //退款
    private static final String PAY_STATUS_REFUND = "03";
    //支付通道通讯异常
    private static final String PAY_STATUS_PAY_CHANNEL_ABNORMAL = "10";
    //其他异常
    private static final String PAY_STATUS_OTHER_ABNORMAL = "99";
    //响应码
    //正常
    private static final String RETURN_CODE_NOMAL = "00";
    //系统处理过程出现异常
    private static final String RETURN_CODE_ABNOMAL = "01";
    //不允许退款，已出票
    private static final String RETURN_CODE_REFUSED_REFUND = "02";
    //订单更新失败
    private static final String RETURN_CODE_ORDER_UPDATE_FAILURE = "03";
    //订单未支付
    private static final String RETURN_CODE_ORDER_NON_PAY = "04";
    //订单已退款
    private static final String RETURN_CODE_ORDER_REFUNDED = "05";
    //订单已取消
    private static final String RETURN_CODE_ORDER_CANCELED = "06";
    //订单重复支付
    private static final String RETURN_CODE_ORDER_PAID_AGAIN = "07";

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 124;
        process();
    }

    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    private void processMessage() throws Exception {
        // 交易时间
        String nowDate = DateHelper.dateToString(new Date());
        QrPayOrderVo constructVo = new QrPayOrderVo();
        try {
            ArrayList logList = new ArrayList();
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            logList.add("支付二维码订单app支付结果请求72消息");
//            logger.info("-- 解析支付二维码订单app支付结果请求消息72 --");
            // 校验数据区长度
            validateDataLen();

            //从消息中获取信息
            getQrPayOrderVo(constructVo);
            QrPayDao qrPayDao = new QrPayDao();
            // 入库
            qrPayDao.insertQrPayApp(constructVo, getOLDbHelper());

            //订单查询
            QrPayOrderVo queryVo = new QrPayOrderVo();
            //优化冗余设置 modify by zhongzq 20190108
//            queryVo.setQrpayId(qrPayOrderVo.getQrpayId());
            queryVo = qrPayDao.qrPayOrder(constructVo, getOLDbHelper());
//            qrPayOrderVo.setAccSeq(queryVo.getAccSeq());
//            qrPayOrderVo.setOrderIp(queryVo.getOrderIp());
//            qrPayOrderVo.setOrderNo(queryVo.getOrderNo());

            //整合返回消息
            constructMessageVo(constructVo, queryVo, nowDate);
            //入库和发送响应消息82
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(constructVo);
            qrPayDao.insertQrPayAppRsp(constructVo, getOLDbHelper());
            writeBackMsg(constructVo);
//            logger.info("--支付二维码订单app支付结果请求结束--");
            //modify by zhongzq 修改下发规则 纠正终端流水号
            //返回码成功时才下发73消息
//            if ("00".equals(qrPayOrderVo.getReturnCode())) {

            if(constructVo.isConstruct73Flag()){
                // 更新统计表w_ol_sts_acc
                Long terminalNo = qrPayDao.getQrPayTerminalNo(constructVo, getOLDbHelper());
                constructVo.setTerminaSeq(terminalNo);
                updateStsAcc(constructVo);
                //入库和发送消息73 下发app支付结果给产生订单终端
                sendMsgQPD(constructVo);
                qrPayDao.insertQrPayDown(constructVo, getOLDbHelper());

//                logList.add("下发73消息-");
            }

            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
            // 出现异常返回响应的错误信息给终端
            dealException(constructVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) +"处理72消息出现异常", e);

            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }

    private void dealException(QrPayOrderVo qrPayOrderVo, Exception e) {
        String errCode = doException(e);
//        qrPayOrderVo.setReturnCode("01");
        qrPayOrderVo.setReturnCode(RETURN_CODE_ABNOMAL);
        qrPayOrderVo.setErrCode(errCode);
        //消息生成时间
        qrPayOrderVo.setMsgGenTime(DateHelper.currentTodToString());

        writeBackMsg(qrPayOrderVo);
    }

    private void getQrPayOrderVo(QrPayOrderVo qrPayOrderVo) throws Exception {
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
        // 支付时间
        String payDate = getBcdString(115, 7);
        // 支付结果
        String payStatus = getCharString(122, 2);

        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());

        qrPayOrderVo.setWaterNo(waterNo);
        qrPayOrderVo.setMessageId(messageId);
        qrPayOrderVo.setMsgGenTime(msgGenTime);
        qrPayOrderVo.setTerminalNo(terminalNo);
        qrPayOrderVo.setPhoneNo(phoneNo);
        qrPayOrderVo.setTerminaSeq(terminaSeq);
        qrPayOrderVo.setImei(imei);
        qrPayOrderVo.setImsi(imsi);
        qrPayOrderVo.setAppCode(appCode);
        qrPayOrderVo.setQrpayId(qrPayID);
        qrPayOrderVo.setQrpayData(qrPayData);
        qrPayOrderVo.setPayChannelType(payChannelType);
        qrPayOrderVo.setPayChannelCode(payChannelCode);
        qrPayOrderVo.setPayDate(payDate);
        qrPayOrderVo.setPayStatus(payStatus);

        //add by zhongzq 20190108
        Long accSeq = new QrPayDao().getQrPayAccSeq(qrPayOrderVo, getOLDbHelper());
        qrPayOrderVo.setAccSeq(accSeq);
        logger.debug(qrPayOrderVo.toString());
    }

    //订单状态0:未支付1:已支付2:订单取消
    //响应码00:正常;01:系统处理过程中出现异常02:不允许退款,已出票;03:订单更新失败
    //modify by zhongzq 20181229 业务分离 退款业务改用74消息 修改业务错误 重构
//    private void constructMessageVo(QrPayOrderVo qrPayOrderVo, QrPayOrderVo tmpVo, String nowDate) throws Exception {
//        //响应消息生成时间
//        qrPayOrderVo.setMsgGenTime(nowDate);
//        qrPayOrderVo.setStatus("0");//默认为未支付
//        int n=0;
//        QrPayDao qrPayDao = new QrPayDao();
//
//        //更新订单为已支付
//        if("00".equals(qrPayOrderVo.getPayStatus())){
//            //检验订单状态
//            if(!"0".equals(tmpVo.getStatus())){
//                qrPayOrderVo.setReturnCode("01");//系统处理过程中出现异常
//                qrPayOrderVo.setPayStatus("99");//订单异常
//            }else{
//                qrPayOrderVo.setStatus("1");//支付成功时,订单状态为"1:已支付"
//                n = qrPayDao.updateQrPayOrder(qrPayOrderVo, getOLDbHelper());
//            }
//        }
//
//        //更新订单为订单取消
//        if("03".equals(qrPayOrderVo.getPayStatus())){
//            //是否已经取票
//            if(tmpVo.getSaleFee()>0 || tmpVo.getSaleTimes()>0 || tmpVo.getDealFee()>0){
//                qrPayOrderVo.setReturnCode("02");//不允许退款，已出票
//                n=1;
//            }
//            if(n==0 && "0".equals(tmpVo.getStatus())){
//                qrPayOrderVo.setReturnCode("04");//未支付
//                n=1;
//            }
//            if(n==0 && "2".equals(tmpVo.getStatus())){
//                qrPayOrderVo.setReturnCode("05");//已退款
//                n=1;
//            }
//            if(n==0){
//                qrPayOrderVo.setStatus("2");//支付成功时,订单状态为"2:订单取消"
//                n = qrPayDao.cancelQrPayOrder(qrPayOrderVo, getOLDbHelper());
//            }
//        }
//        if(n==0){
//            qrPayOrderVo.setReturnCode("03");//更新订单失败
//        }
//    }
    //rebuild by zhongzq 20190102
    private void constructMessageVo(QrPayOrderVo constructVo, QrPayOrderVo queryVo, String nowDate) throws Exception {
        //响应消息生成时间
        constructVo.setMsgGenTime(nowDate);
        constructVo.setLastStatus(constructVo.getStatus());
        constructVo.setStatus(FrameCodeConstant.ORDER_STATUS_NON_PAYMENT);//默认为未支付
        constructVo.setOrderIp(queryVo.getOrderIp());
        constructVo.setOrderNo(queryVo.getOrderNo());
        int n = 0;
        QrPayDao qrPayDao = new QrPayDao();

        //更新订单为已支付
        if (FrameCodeConstant.ORDER_STATUS_NON_PAYMENT.equals(queryVo.getStatus())||FrameCodeConstant.ORDER_STATUS_PAY_FAILURE.equals(queryVo.getStatus())) {
            if (PAY_STATUS_SUCCESS.equals(constructVo.getPayStatus())) {
                constructVo.setStatus(FrameCodeConstant.ORDER_STATUS_PAID);//支付成功时,订单状态为"1:已支付"
            } else {
                //01 02 10 99 均归为支付失败 03业务分离到74 理论上不会收到
                constructVo.setStatus(FrameCodeConstant.ORDER_STATUS_PAY_FAILURE);
            }
            n = qrPayDao.updateQrPayOrder(constructVo, getOLDbHelper());
            if(n ==1) {
                constructVo.setReturnCode(RETURN_CODE_NOMAL);
                if(!PAY_STATUS_REFUND.equals(constructVo.getPayStatus())) {
                    //退款情况不下发
                    constructVo.setConstruct73Flag(true);
                }
            }else if( n==0) {
                constructVo.setReturnCode(RETURN_CODE_ORDER_UPDATE_FAILURE);
            }
        }else if(FrameCodeConstant.ORDER_STATUS_PAID.equals(queryVo.getStatus())){
            //已支付的再收到消息 均回复07 且不更新订单 不下发73
            constructVo.setReturnCode(RETURN_CODE_ORDER_PAID_AGAIN);
        }else  if(FrameCodeConstant.ORDER_STATUS_CANCEL.equals(queryVo.getStatus())){
            //已取消订单再收到 均回复06 且不更新订单 不下发73
            constructVo.setReturnCode(RETURN_CODE_ORDER_CANCELED);
            if (PAY_STATUS_SUCCESS.equals(constructVo.getPayStatus())) {
                n = qrPayDao.updateQrPayOrderByCancelScene(constructVo, getOLDbHelper());
                if( n==0) {
                    constructVo.setReturnCode(RETURN_CODE_ORDER_UPDATE_FAILURE);
                }
            }

        }else if(FrameCodeConstant.ORDER_STATUS_REFUNDED.equals(queryVo.getStatus())){
            //已退款订单再收到 均回复05 且不更新订单 不下发73
            constructVo.setReturnCode(RETURN_CODE_ORDER_REFUNDED);
        }

    }

    private void writeBackMsg(QrPayOrderVo qrPayOrderVo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(qrPayOrderVo);
        sendMsgToConnectionQueue(backMesg);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",//0:请求消息、1：响应消息
//                    qrPayOrderVo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());//0：成功1：失败
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：", ex);
//        }
    }

    private MessageQueue getMessageQueue(QrPayOrderVo qrPayOrderVo) {
        byte[] msgVo = new ConstructMessage82().constructMessage(qrPayOrderVo);
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
        messageQueue.setIpAddress(this.messageFrom);
        messageQueue.setMessage(msgVo);
        messageQueue.setIsParaInformMsg("0");
        messageQueue.setParaInformWaterNo(0);
        messageQueue.setLineId("");
        messageQueue.setStationId("");
        messageQueue.setMessageSequ(messageSequ);
        return messageQueue;
    }

    private void sendMsgToConnectionQueue(MessageQueue mq) {
        CommuConnection con = this.bridge.getConnection();
        con.setMessageInConnectionQueue(mq);
    }
    //下发app支付结果给产生订单终端
    private void sendMsgQPD(QrPayOrderVo qrPayOrderVo) {
        new ConstructMessage73().constructAndSend(qrPayOrderVo);
    }


    /*
    更新日统计表
    */
    private void updateStsAcc(QrPayOrderVo qrPayOrderVo) {
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> whereMap = new HashMap<>();
        String[] fee = qrPayOrderVo.getQrpayData().split("-");
        long feeLong = Long.parseLong(fee[2]) / 100;
        if ("00".equals(qrPayOrderVo.getPayStatus())) {
            valueMap.put("qrpay_hce_fee", feeLong);
            valueMap.put("qrpay_hce_times", 1);
        }
        if ("03".equals(qrPayOrderVo.getPayStatus())) {
            valueMap.put("qrpay_hce_c_fee", feeLong);
            valueMap.put("qrpay_hce_c_times", 1);
        }
        whereMap.put("squad_day", qrPayOrderVo.getPayDate().substring(0, 8));
        whereMap.put("line_id", qrPayOrderVo.getTerminalNo().substring(0, 2));
        whereMap.put("station_id", qrPayOrderVo.getTerminalNo().substring(2, 4));
        PubDao.olStsAcc(valueMap, whereMap);
    }
}
