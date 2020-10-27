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
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author lind
 * 支付二维码订单上传
 * @datetime 2018-5-8
 */
public class Message70 extends MessageValidate {

    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message70.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_create";

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 73;
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
        QrPayOrderVo qrPayOrderVo = new QrPayOrderVo();
        try {
            //汇总日志为1条输出 20190822 zhongzq
            ArrayList logList = new ArrayList();
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            logList.add("支付二维码订单上传请求70消息");
//            logger.info("-- 支付二维码订单上传请求消息70 --");
            // 校验数据区长度
            validateDataLen();
            //从消息中获取信息
            getQrPayOrderVo(qrPayOrderVo);
            // 入库
            QrPayDao qrPayDao = new QrPayDao();
            qrPayDao.insertQrPayCreate(qrPayOrderVo, getOLDbHelper());
            //整合返回消息
            //响应码:00:订单处理正常01:SAM卡不存在02:终端编号不合法03:订单号编码不合法04:设备订单号重复10:中心系统异常99:其他
            constructMessageVo(qrPayOrderVo, nowDate);
            //订单处理正常时，插入订单表
            if ("00".equals(qrPayOrderVo.getReturnCode())) {
                qrPayDao.insertQrPayOrder(qrPayOrderVo, getOLDbHelper());
            }
            //入库和发送响应消息80
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(qrPayOrderVo);
            qrPayDao.insertQrPayCreateRsp(qrPayOrderVo, getOLDbHelper());
            writeBackMsg(qrPayOrderVo);
//            logger.info("--支付二维码订单上传响应结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
            // 出现异常返回响应的错误信息给终端
            dealException(qrPayOrderVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) + "处理70消息出现异常", e);

            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }

    //中心系统异常
    private void dealException(QrPayOrderVo qrPayOrderVo, Exception e) {
        String errCode = doException(e);
        qrPayOrderVo.setReturnCode("10");
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

        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());

        qrPayOrderVo.setWaterNo(waterNo);
        qrPayOrderVo.setMessageId(messageId);
        qrPayOrderVo.setMsgGenTime(msgGenTime);
        qrPayOrderVo.setTerminalNo(terminalNo);
        qrPayOrderVo.setSamLogicalId(samLogicalId);

        qrPayOrderVo.setOrderNo(orderNo);
        qrPayOrderVo.setOrderDate(orderDate);
        qrPayOrderVo.setCardType(cardType);
        qrPayOrderVo.setSaleFee(saleFee);
        qrPayOrderVo.setSaleTimes(saleTimes);
        qrPayOrderVo.setDealFee(dealFee);
        qrPayOrderVo.setTerminaSeq(terminaSeq);
        qrPayOrderVo.setOrderIp(this.messageFrom);
        Long accSeq = QrPayDao.getBusinessNo(FrameCodeConstant.BUESINESS_NO_TYPE_QRPAY, getOLDbHelper());
        //中心流水号
        qrPayOrderVo.setAccSeq(accSeq);

        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(qrPayOrderVo.toString());
    }


    //响应码:00:订单处理正常01:SAM卡不存在02:终端编号不合法03:订单号编码不合法04:设备订单号重复10:中心系统异常99:其他
    private void constructMessageVo(QrPayOrderVo qrPayOrderVo, String nowDate) throws SQLException {
        //响应消息生成时间
        qrPayOrderVo.setMsgGenTime(nowDate);
        QrPayDao qrPayDao = new QrPayDao();

        //校验消息
        boolean result = true;
        //03:订单号编码不合法
        String orderNo = qrPayOrderVo.getOrderNo();
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
            result = matchs(orderNo.substring(8), patternReg);
        }

        if (result) {
            result = PubDao.checkDevCode(qrPayOrderVo.getTerminalNo());
            logger.debug("终端编号：" + qrPayOrderVo.getTerminalNo());
        } else {
            qrPayOrderVo.setReturnCode("03");//03:订单号编码不合法
            return;
        }

        if (result) {
            result = PubDao.checkPSAM(qrPayOrderVo.getSamLogicalId());
        } else {
            qrPayOrderVo.setReturnCode("02");//02:终端编号不合法
            return;
        }

        if (result) {
            result = qrPayDao.checkOrderNo(qrPayOrderVo, getOLDbHelper());
        } else {
            qrPayOrderVo.setReturnCode("01");//01:SAM卡不存在
            return;
        }

        if (!result) {
            //支付标识（10位左补0）00000000000
            int id = qrPayDao.qrPayID(getOLDbHelper());
            String qrPayID = CharUtil.addCharsBefore(id + "", 10, "0");
            qrPayDao.updateQrPayID(id, getOLDbHelper());
            //支付金额 00000100
            String payDee = CharUtil.addCharsBefore(qrPayOrderVo.getDealFee() + "", 8, "0");
            //支付二维码信息 20180101010101-00000000000-0000000100
            String qrPayData = qrPayOrderVo.getOrderDate() + "-" + qrPayID + "-" + payDee;
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("qrPayData:" + qrPayData);

            qrPayOrderVo.setQrpayId(qrPayID);
            qrPayOrderVo.setQrpayData(qrPayData);
            //addby zhongzq 20181228 订单有效时间
            qrPayOrderVo.setValidTime(DateHelper.getDateTimeAfter(qrPayOrderVo.getOrderDate(), FrameCodeConstant.PARA_QR_CODE_PAY_VALID_TIME));
        } else {
            qrPayOrderVo.setReturnCode("04");//04:设备订单号重复
            return;
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
        byte[] msgVo = new ConstructMessage80().constructMessage(qrPayOrderVo);
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

    /*
     * 正则表达式匹配
     */
    public boolean matchs(String str, String reg) {
        return Pattern.compile(reg).matcher(str).find();
    }
}
