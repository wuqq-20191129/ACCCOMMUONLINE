/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.QrCodeAfcDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.vo.QrCodeOrderVo;
import com.goldsign.commu.app.vo.QrCodeVo;
import com.goldsign.commu.frame.connection.CommuConnection;
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
 * 二维码支付执行结果请求
 * @datetime 2017-8-29
 */
public class Message57 extends MessageValidate {

//    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message57.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_afc";

    @Override
    public void run() throws Exception {
        //二维码支付执行结果请求的数据区字节长度
        fix_recv_data_length = 81;
        process();
    }

    public void process() throws Exception {
        //modify by zhongzq 20191013
//        synchronized (LOCK) {
            processMessage();
//        }
    }

    private void processMessage() throws Exception {
        // 交易时间
        String nowDate = DateHelper.dateToString(new Date());
        QrCodeVo qrCodeVo = new QrCodeVo();
        try {
//            logger.info("-- 解析二维码支付执行结果请求消息57 --");
            //汇总日志为1条输出 20190821 zhongzq
            ArrayList logList = new ArrayList();
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            logList.add("二维码支付执行结果请求57消息");
            // 校验数据区长度
            validateDataLen();
            //从消息中获取信息
            getQrCodeVo(qrCodeVo);
            // 入库
            QrCodeAfcDao qrCodeDao = new QrCodeAfcDao();
            qrCodeDao.insertQrCodeResult(qrCodeVo, getOLDbHelper());
            //更新订单：订单执行结果显示完整成功执行和部分成功执行时更新订单信息
            int updateN = 0;
            updateN = qrCodeDao.updateMsg(qrCodeVo, getOLDbHelper());

            //整合返回消息
            //响应码:00:成功处理57请求 01:其他：处理不成功
            if (updateN == 1) {
                qrCodeVo.setReturnCode("00");//00:成功处理
            } else {
                qrCodeVo.setReturnCode("01");//00:处理不成功
                //modify by zhongzq 20190321 增加校验 有可能是出了部分票
//                qrCodeVo.setTkStatus("00");
//                qrCodeDao.lockOrder(qrCodeVo, getOLDbHelper());//收到57后解锁订单，订单状态设为未执行}
                QrCodeOrderVo qrCodeOrderVo = qrCodeDao.qrCodeOrder(qrCodeVo, getOLDbHelper());
                if (qrCodeOrderVo != null) {
                    if (qrCodeOrderVo.getSaleTimes() > 0) {
                        qrCodeVo.setTkStatus("01");
                    } else {
                        qrCodeVo.setTkStatus("00");
                    }
                    qrCodeDao.lockOrder(qrCodeVo, getOLDbHelper());//收到57后解锁订单，订单状态设为未执行}
                } else {
                    ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("订单不存在=" + qrCodeVo.getOrderNo());
                }
            }
            constructMessageVo(qrCodeVo, nowDate);
            //入库和发送响应消息67
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(qrCodeVo);
            int n = 0;
            n = qrCodeDao.insertQrCodeResultRsp(qrCodeVo, getOLDbHelper());
            // 更新统计表w_ol_sts_acc
            if (n > 0 && "00".equals(qrCodeVo.getReturnCode())) {
                updateStsAcc(qrCodeVo);
            }
            writeBackMsg(qrCodeVo);
//            logger.info("--二维码支付执行结果请求消息结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));

        } catch (Exception e) {
            // 出现异常返回响应的错误信息给终端
            dealException(qrCodeVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) + "处理57消息出现异常", e);

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
        // 终端处理流水号
        long terminaSeq = getLong2(34);
        // 66中心响应处理流水号
        long accSeq = getLong2(38);
        // 订单执行结果
        String resultCode = getCharString(42, 2);
        // 订单号
        String orderNo = getCharString(44, 14);
        // 手机号
        String phoneNo = getCharString(58, 11);
        // 已发售单程票单价
        long saleFee = getLong2(69);
        // 已发售单程票数量
        long saleTimes = getLong2(73);
        // 已发售单程票总价
        long dealFee = getLong2(77);
        //取票时间
//        String dealTime = getBcdString(81, 7);

        // 响应acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());

        qrCodeVo.setWaterNo(waterNo);
        qrCodeVo.setMessageId(messageId);
        qrCodeVo.setMsgGenTime(msgGenTime);
        qrCodeVo.setTerminalNo(terminalNo);
        qrCodeVo.setSamLogicalId(samLogicalId);
        qrCodeVo.setAccSeq(accSeq);
        qrCodeVo.setTerminaSeq(terminaSeq);

        qrCodeVo.setResultCode(resultCode);
        qrCodeVo.setOrderNo(orderNo);
        qrCodeVo.setPhoneNo(phoneNo);
        qrCodeVo.setSaleFee(saleFee);
        qrCodeVo.setSaleTimes(saleTimes);
        qrCodeVo.setDealFee(dealFee);

        qrCodeVo.setDealTime(msgGenTime);
        logger.debug(qrCodeVo.toString());
    }

    private void constructMessageVo(QrCodeVo qrCodeVo, String nowDate) {
        //响应消息生成时间
        qrCodeVo.setMsgGenTime(nowDate);
        //20181130 zhongzq  应该对应回57消息上传上来的acc_seq而非取该单的water_no
//        qrCodeVo.setAccSeq(qrCodeVo.getWaterNo());
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
        byte[] msgVo = new ConstructMessage67().constructMessage(qrCodeVo);
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
        valueMap.put("qrcode_lc_fee", qrCodeVo.getDealFee() / 100);
        valueMap.put("qrcode_lc_num", qrCodeVo.getSaleTimes());
        valueMap.put("qrcode_lc_times", 1);
        whereMap.put("squad_day", qrCodeVo.getMsgGenTime().substring(0, 8));
        whereMap.put("line_id", qrCodeVo.getTerminalNo().substring(0, 2));
        whereMap.put("station_id", qrCodeVo.getTerminalNo().substring(2, 4));
        PubDao.olStsAcc(valueMap, whereMap);
    }
}
