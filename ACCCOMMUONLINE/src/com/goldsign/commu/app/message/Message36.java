/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.QrCodeHceDao;
import com.goldsign.commu.app.dao.QrPayDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.util.RsaUtil;
import com.goldsign.commu.app.vo.QrCodeOrderVo;
import com.goldsign.commu.app.vo.QrCodeVo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameMessageCodeConstant;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author lind
 * 二维码取票码请求
 * @datetime 2018-3-13
 */
public class Message36 extends MessageValidate {

    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message36.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_tkcode";

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 83;
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
        QrCodeVo constructVo = new QrCodeVo();
        try {
            //汇总日志为1条输出 20190821 zhongzq
            ArrayList logList = new ArrayList();
            logList.add("二维码取票码请求业务36消息");
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
//            logger.info("-- 解析二维码取票码请求消息36 --");
            // 校验数据区长度
            validateDataLen();
            //从消息中获取信息
            getQrCodeVo(constructVo);
            // 入库
            QrCodeHceDao qrCodeDao = new QrCodeHceDao();
            qrCodeDao.insertQrCode(constructVo, getOLDbHelper());
            //订单查询
            QrCodeOrderVo queryVo;
            queryVo = qrCodeDao.qrCodeOrder(constructVo, getOLDbHelper());
            //整合返回消息
            constructMessageVo(constructVo, queryVo, nowDate);
            //入库和发送响应消息46
            // modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(constructVo);
            qrCodeDao.insertQrCodeRsp(constructVo, getOLDbHelper());
            // 更新统计表w_ol_sts_acc
            if ("00".equals(constructVo.getReturnCode())) {
                updateStsAcc(constructVo);
            }
            writeBackMsg(constructVo);
//            logger.info("--二维码取票码请求结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
            // 出现异常返回响应的错误信息给终端
            dealException(constructVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) + "处理36消息出现异常", e);

            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }

    private void dealException(QrCodeVo qrCodeVo, Exception e) {
        String errCode = doException(e);
        qrCodeVo.setReturnCode("01");
        qrCodeVo.setErrCode(errCode);
        //消息生成时间
        qrCodeVo.setMsgGenTime(DateHelper.currentTodToString());

        writeBackMsg(qrCodeVo);
    }

    private void getQrCodeVo(QrCodeVo qrCodeVo) throws Exception {
        // 消息类型
        String messageId = getCharString(0, 2);
        // 消息生成时间
        String msgGenTime = getBcdString(2, 7);
        // 终端编号
        String terminalNo = getCharString(9, 9);
        // Sam卡逻辑卡号
        String samLogicalId = getCharString(18, 16);
        // 订单号
        String orderNo = getCharString(34, 14);
        // 终端处理流水号
        long terminaSeq = getLong2(48);
        // 手机号
        String phoneNo = getCharString(52, 11);
        // 已发售单程票单价
        long saleFee = getLong2(63);
        // 已发售单程票数量
        long saleTimes = getLong2(67);
        // 已发售单程票总价
        long dealFee = getLong2(71);

        String beginStation = getCharString(75, 4);

        String endStation = getCharString(79, 4);

        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());

        qrCodeVo.setWaterNo(waterNo);
        qrCodeVo.setMessageId(messageId);
        qrCodeVo.setMsgGenTime(msgGenTime);
        qrCodeVo.setTerminalNo(terminalNo);
        qrCodeVo.setSamLogicalId(samLogicalId);
        qrCodeVo.setTerminaSeq(terminaSeq);

        qrCodeVo.setOrderNo(orderNo);
        qrCodeVo.setPhoneNo(phoneNo);
        qrCodeVo.setSaleFee(saleFee);
        qrCodeVo.setSaleTimes(saleTimes);
        qrCodeVo.setDealFee(dealFee);

        qrCodeVo.setStartStation(beginStation);
        qrCodeVo.setEndStation(endStation);
        Long accSeq = QrPayDao.getBusinessNo(FrameCodeConstant.BUESINESS_NO_TYPE_QRCODE, getOLDbHelper());
        //中心流水号
        qrCodeVo.setAccSeq(accSeq);
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(qrCodeVo.toString());
//        logger.debug("36 Message："+qrCodeVo.toString());
    }

    //响应码:00：正常；01:不符合业务逻辑或系统处理过程中出现异常，详细原因见错误描述
    private void constructMessageVo(QrCodeVo qrCodeVo, QrCodeOrderVo qrCodeOrderVo, String nowDate) throws Exception {
        //响应消息生成时间
        qrCodeVo.setMsgGenTime(nowDate);
        //订单表不存在订单号时，生成取票码
        if (qrCodeOrderVo == null || qrCodeOrderVo.getQrcode() == null) {
            qrCodeOrderVo = new QrCodeOrderVo();
            //取票码=订单号+生成时间+终端处理流水(4位)
            String tkCode = qrCodeVo.getOrderNo() + qrCodeVo.getMsgGenTime() + CharUtil.addCharsBefore(String.valueOf(qrCodeVo.getTerminaSeq()), 4, "0");
            String qrCode = null;
            qrCode = RsaUtil.encrypt(tkCode.getBytes(), RsaUtil.publicKeyString);
            qrCodeVo.setQrcode(qrCode);
            qrCodeOrderVo.setQrcode(qrCode);
            qrCodeOrderVo.setTkcode(tkCode);
            qrCodeOrderVo.setValidTime(DateHelper.addDay(DateHelper.dateStrToUtilDate(qrCodeVo.getMsgGenTime()), FrameMessageCodeConstant.HCE_QRCODE_VALID_TIME));//有效时间=消息生成时间+1天
            qrCodeVo.setValidTime(DateHelper.dateToString(qrCodeOrderVo.getValidTime()));
            //插入订单信息
            QrCodeHceDao qrCodeDao = new QrCodeHceDao();
            qrCodeVo.setTkStatus("00");//未取票（订单未执行）
            qrCodeDao.insertQrCodeOrder(qrCodeOrderVo, qrCodeVo, OLDbHelper);
        } else {
            qrCodeVo.setPhoneNo(qrCodeOrderVo.getPhoneNo());
        }
        qrCodeVo.setQrcode(qrCodeOrderVo.getQrcode());
        qrCodeVo.setValidTime(DateHelper.dateToString(qrCodeOrderVo.getValidTime()));
    }

    private void writeBackMsg(QrCodeVo qrCodeVo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(qrCodeVo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",//0:请求消息、1：响应消息
//                    qrCodeVo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());//0：成功1：失败
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：", ex);
//        }
        sendMsgToConnectionQueue(backMesg);
    }

    private MessageQueue getMessageQueue(QrCodeVo qrCodeVo) {
        byte[] msgVo = new ConstructMessage46().constructMessage(qrCodeVo);
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
    更新日统计表
    */
    private void updateStsAcc(QrCodeVo qrCodeVo) {
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> whereMap = new HashMap<>();
        valueMap.put("qrcode_hce_fee", qrCodeVo.getDealFee() / 100);
        valueMap.put("qrcode_hce_num", qrCodeVo.getSaleTimes());
        valueMap.put("qrcode_hce_times", 1);
        whereMap.put("squad_day", qrCodeVo.getMsgGenTime().substring(0, 8));
        whereMap.put("line_id", qrCodeVo.getTerminalNo().substring(0, 2));
        whereMap.put("station_id", qrCodeVo.getTerminalNo().substring(2, 4));
        PubDao.olStsAcc(valueMap, whereMap);
    }
}
