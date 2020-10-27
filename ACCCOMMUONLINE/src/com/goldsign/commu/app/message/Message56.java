/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.QrCodeAfcDao;
import com.goldsign.commu.app.dao.QrCodeHceDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.util.RsaUtil;
import com.goldsign.commu.app.vo.QrCodeOrderVo;
import com.goldsign.commu.app.vo.QrCodeVo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author lind
 * 二维码认证请求
 * @datetime 2017-8-29
 */
public class Message56 extends MessageValidate {

    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message56.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_afc";

    @Override
    public void run() throws Exception {
        //二维码认证请求的数据区字节长度
        fix_recv_data_length = 102;
        process();
    }

    public void process() throws Exception {
        //modyfy by zhongzq 20191013
//        synchronized (LOCK) {
            processMessage();
//        }
    }

    private void processMessage() throws Exception {
        // 交易时间
        String nowDate = DateHelper.dateToString(new Date());
        QrCodeVo qrCodeVo = new QrCodeVo();
        try {
//            logger.info("-- 解析二维码认证请求消息56 --");
            long startTime = System.currentTimeMillis();
            //汇总日志为1条输出 20190821 zhongzq
            ArrayList logList = new ArrayList();
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            logList.add("二维码认证请求56消息");
            // 校验数据区长度
            validateDataLen();

            //从消息中获取信息
            getQrCodeVo(qrCodeVo);

            // 入库
            QrCodeAfcDao qrCodeDao = new QrCodeAfcDao();
            qrCodeDao.insertQrCode(qrCodeVo, getOLDbHelper());

            //订单查询
            QrCodeOrderVo qrCodeOrderVo = new QrCodeOrderVo();
            //add by zhongzq 20191013 这不是一个好解决方法
            synchronized (LOCK) {
                qrCodeOrderVo = qrCodeDao.qrCodeOrder(qrCodeVo, getOLDbHelper());

                //整合返回消息
                //响应码:00:订单未执行01:订单部分完成;02:订单取消;80:未购票或不存在81:订单已完成(已取票)82:二维码已过有效期83:购票、取票始发站不一致当响应码非00、01时，订单号、手机号、发售单程票价等字段值填默认(0)返回
                constructMessageVo(qrCodeVo, qrCodeOrderVo, nowDate);
                //入库和发送响应消息66
                //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(qrCodeVo);
                qrCodeDao.insertQrCodeRsp(qrCodeVo, getOLDbHelper());
                //可取票时，锁定订单
                if ("00".equals(qrCodeVo.getReturnCode()) || "01".equals(qrCodeVo.getReturnCode())) {
                    qrCodeVo.setTkStatus("84");
                    qrCodeDao.lockOrder(qrCodeVo, getOLDbHelper());
                }
            }

            writeBackMsg(qrCodeVo);
            //modify by zhongzq 201801106
//            logger.info("--充值请求响应结束--");
//            logger.info("--二维码认证请求响应结束--");
            long endTime = System.currentTimeMillis();
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("56业务耗时:" + (endTime - startTime));
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));

        } catch (Exception e) {
           // 出现异常返回响应的错误信息给终端
            dealException(qrCodeVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) + "处理56消息出现异常", e);
            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }

    private void dealException(QrCodeVo qrCodeVo, Exception e) {
        String errCode = doException(e);
        //modify by zhongzq 20181106  由于没有异常对应代码 返回80 订单不存在
//        qrCodeVo.setReturnCode("01");
        qrCodeVo.setReturnCode("80");
        qrCodeVo.setErrCode(errCode);
        if(qrCodeVo.getAccSeq()==null){
            qrCodeVo.setAccSeq(0L);
        }
        //消息生成时间
        qrCodeVo.setMsgGenTime(DateHelper.currentTodToString());
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("异常返回数据类信息:[" +qrCodeVo+"]");
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
        // 二维码密文
        String qrCodeStr = getCharString(38, 64);
        //备注
        String remark = "";
        //modify by zhongzq 20181106
//        logger.debug(qrCodeStr);
        logger.debug("获取到的二维码===" + qrCodeStr);
        String qrCode = null;
        //RSA解密
        try {
            qrCode = RsaUtil.decrypt(qrCodeStr.trim(), RsaUtil.privateKeyString);
            //modify by zhongzq 20181106 修复解密二维码超过64位入库异常问题并记录原始
//            logger.debug(qrCodeStr);
            logger.debug("解密处理后二维码===" + qrCode);
            //modify by zhongzq 20190110 修复异常 优化remark内容
            if (qrCode.getBytes("UTF-8").length > 64) {
                remark = "Error,Upload QRCode==" + qrCodeStr + ",Decrypt QRcode==" + qrCode + ",Decrypt UTF-8 length=" + qrCode.getBytes("UTF-8").length;
                if (remark.length() > 1000) {
                    remark = remark.substring(0, 1000);
                }
                logger.error(remark);
                qrCode = "0";
            }

        } catch (Exception ex) {
            logger.error("Ras解密错误：", ex);
        }
        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());

        qrCodeVo.setWaterNo(waterNo);
        qrCodeVo.setMessageId(messageId);
        qrCodeVo.setMsgGenTime(msgGenTime);
        qrCodeVo.setTerminalNo(terminalNo);
        qrCodeVo.setSamLogicalId(samLogicalId);
        qrCodeVo.setQrcode(qrCode);
        qrCodeVo.setTerminaSeq(terminaSeq);
        //modify by zhongzq 20190111 中心流水号纠正
        //中心流水号
//        qrCodeVo.setAccSeq(waterNo);
        Long accSeq = new QrCodeHceDao().getQrCodeyAccSeq(qrCodeVo, getOLDbHelper());
        qrCodeVo.setAccSeq(accSeq);
        qrCodeVo.setRemark(remark);
//        logger.debug(qrCodeVo.toString());
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(qrCodeVo.toString());
    }

    //响应码:00:订单未执行01:订单部分完成80:未购票或不存在81:订单已完成(已取票)82:二维码已过有效期83:购票、取票始发站不一致
    private void constructMessageVo(QrCodeVo qrCodeVo, QrCodeOrderVo qrCodeOrderVo, String nowDate) {
        //56消息生成时间
        Date dataTime56 = new Date();
        try {
            dataTime56 = DateHelper.dateStrToSqlTimestamp(qrCodeVo.getMsgGenTime());
        } catch (ParseException ex) {
            logger.error(ex.getMessage());
        }
        //响应消息生成时间
        qrCodeVo.setMsgGenTime(nowDate);

        if (qrCodeOrderVo == null) {
            qrCodeVo.setReturnCode("80");//80:未购票或不存在
            return;
        }
        if (qrCodeOrderVo.getValidTime().getTime() < dataTime56.getTime()) {//取票时间大于二维码有效时间
            qrCodeVo.setReturnCode("82");//82:二维码已过有效期
            return;
        }
        if (qrCodeOrderVo.getDealFee() >= qrCodeOrderVo.getDealFeeTotal() || qrCodeOrderVo.getSaleTimes() >= qrCodeOrderVo.getSaleTimesTotal()) {
            qrCodeVo.setReturnCode("81");//81:订单已完成(已取票)
            return;
        }
        //旧订单没有车站信息不校验
        if (qrCodeOrderVo.getStartStation() != null && !"".equals(qrCodeOrderVo.getStartStation())) {
            if (!qrCodeOrderVo.getStartStation().equals(qrCodeVo.getTerminalNo().substring(0, 4))) {//从终端编号取车站线路代码
                qrCodeVo.setReturnCode("83");//83:购票、取票始发站不一致
                return;
            }
        }
        //00:订单未执行;01:订单部分完成;02:订单取消;80:未购票或不存在;81:订单已完成(已取票);82:二维码已过有效期;83:购票、取票始发站不一致
        qrCodeVo.setReturnCode(qrCodeOrderVo.getTicketStatus());
        if (qrCodeOrderVo.getTicketStatus() == null) {
            qrCodeVo.setReturnCode("00");//00:订单未执行
        }
        if (!(qrCodeVo.getReturnCode().equals("00") || qrCodeVo.getReturnCode().equals("01"))) {
            return;
        }
        //当响应码非00、01时，订单号、手机号、发售单程票价等字段值填默认(0)返回
        qrCodeVo.setOrderNo(qrCodeOrderVo.getOrderNo());
        qrCodeVo.setPhoneNo(qrCodeOrderVo.getPhoneNo());
        qrCodeVo.setSaleFee(qrCodeOrderVo.getSaleFee());
        qrCodeVo.setSaleTimes(qrCodeOrderVo.getSaleTimes());
        qrCodeVo.setDealFee(qrCodeOrderVo.getDealFee());

        qrCodeVo.setSaleFee(qrCodeOrderVo.getSaleFeeTotal());//发售单价
        //modify by zhongzqi 20190319
//        qrCodeVo.setSaleTimes(qrCodeOrderVo.getSaleTimesTotal()-qrCodeOrderVo.getSaleTimes());//发售数量-已发售数量
//        qrCodeVo.setDealFee(qrCodeOrderVo.getDealFeeTotal()-qrCodeOrderVo.getDealFee());//发售总价-已发售总价
        qrCodeVo.setSaleTimes(qrCodeOrderVo.getSaleTimesTotal());//发售数量
        qrCodeVo.setDealFee(qrCodeOrderVo.getDealFeeTotal());//发售总价
        qrCodeVo.setHasTakeNum(qrCodeOrderVo.getSaleTimes());//已取数量
        //modify by zhongzq
//        if(qrCodeVo.getDealFee()<-1 || qrCodeVo.getSaleTimes()<-1){
//            qrCodeVo.setReturnCode("81");//81:订单已完成(已取票)
//            return;
//        }
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
        byte[] msgVo = new ConstructMessage66().constructMessage(qrCodeVo);
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
}
