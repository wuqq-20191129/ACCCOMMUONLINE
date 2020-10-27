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
 * @datetime 2018-5-13
 * @author lind
 * 支付二维码订单查询请求
 */
public class Message71 extends MessageValidate{
    
    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message71.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_orstatus";

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 109;
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
//            logger.info("-- 解析支付二维码订单查询请求消息71 --");
            ArrayList logList = new ArrayList();
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            logList.add("支付二维码订单查询请求71消息");
            // 校验数据区长度
            validateDataLen();
            
            //从消息中获取信息
            getQrPayOrderVo(qrPayOrderVo);

            // 入库
            QrPayDao qrPayDao = new QrPayDao();
            qrPayDao.insertQrPayStatus(qrPayOrderVo, getOLDbHelper());
            QrPayOrderVo queryVo = new QrPayOrderVo();
            //订单查询
            queryVo = qrPayDao.qrPayOrder(qrPayOrderVo, getOLDbHelper());
            //整合返回消息
            constructMessageVo(qrPayOrderVo,queryVo, nowDate);
            //入库和发送响应消息81
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(qrPayOrderVo);
            qrPayDao.insertQrPayStatusRsp(qrPayOrderVo, getOLDbHelper());
            writeBackMsg(qrPayOrderVo);
//            logger.info("--支付二维码订单查询请求结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
            // 出现异常返回响应的错误信息给终端
            dealException(qrPayOrderVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) +"处理71消息出现异常", e);

            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }

    private void constructMessageVo(QrPayOrderVo qrPayOrderVo, QrPayOrderVo queryVo, String nowDate) {
        //响应消息生成时间
        qrPayOrderVo.setMsgGenTime(nowDate);
        qrPayOrderVo.setOrderNo(queryVo.getOrderNo());
        qrPayOrderVo.setOrderDate(qrPayOrderVo.getOrderDate());
        qrPayOrderVo.setCardTypeTotal(queryVo.getCardTypeTotal());
        qrPayOrderVo.setSaleFeeTotal(queryVo.getSaleFeeTotal());
        qrPayOrderVo.setSaleTimesTotal(queryVo.getSaleTimesTotal());
        qrPayOrderVo.setDealFeeTotal(queryVo.getDealFeeTotal());
        qrPayOrderVo.setStatus(queryVo.getStatus());
        qrPayOrderVo.setSaleFee(queryVo.getSaleFee());
        qrPayOrderVo.setSaleTimes(queryVo.getSaleTimes());
        qrPayOrderVo.setDealFee(queryVo.getDealFee());
        qrPayOrderVo.setValidTime(queryVo.getValidTime());
        // zhongzq 20181228 优化逻辑 应最先判断
        if("03".equals(qrPayOrderVo.getStatus())){
            return;//订单不存在
        }
        //检验订单状态
        int dealFee = 0;
        String qrPayData = qrPayOrderVo.getQrpayData();
        dealFee = Integer.parseInt(qrPayData.substring(qrPayData.length()-8, qrPayData.length()));
       //modify by zhongzq 20190112 修复逻辑错误
//        if(dealFee!=qrPayOrderVo.getDealFeeTotal()){
        if(dealFee!=qrPayOrderVo.getDealFeeTotal()&&"0".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("02");
            return;
        }

        if("0".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("00");
            return;//订单未支付
        }
        if("1".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("01");
            return;//订单已支付
        }
        if("4".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("04");
            return;//订单已取消
        }
        if("5".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("05");
            return;//订单支付失败
        }
        if("6".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("06");
            return;//订单已退款
        }
    }


    private void dealException(QrPayOrderVo qrPayOrderVo, Exception e) {
        String errCode = doException(e);
        qrPayOrderVo.setReturnCode("01");
        qrPayOrderVo.setErrCode(errCode);
        //消息生成时间
        qrPayOrderVo.setMsgGenTime(DateHelper.currentTodToString());

        writeBackMsg(qrPayOrderVo);
    }

    private void getQrPayOrderVo(QrPayOrderVo qrPayOrderVo) throws Exception  {
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
        //中心流水号 modify by zhongzq 20190108 获取accSeq
//        qrPayOrderVo.setAccSeq(waterNo);
        Long accSeq = new QrPayDao().getQrPayAccSeq(qrPayOrderVo, getOLDbHelper());
        qrPayOrderVo.setAccSeq(accSeq);
        logger.debug(qrPayOrderVo.toString());
    }

    //订单状态00:订单未支付01:订单已支付02:订单支付金额与发售总价不一致03:订单不存在04:订单已取消 05:订单支付失败 06：订单已退款
    private void constructMessageVo(QrPayOrderVo qrPayOrderVo, String nowDate) throws Exception {
        //响应消息生成时间
        qrPayOrderVo.setMsgGenTime(nowDate);
        // zhongzq 20181228 优化逻辑 应最先判断
        if("03".equals(qrPayOrderVo.getStatus())){
            return;//订单不存在
        }
        //检验订单状态
        int dealFee = 0;
        String qrPayData = qrPayOrderVo.getQrpayData();
        dealFee = Integer.parseInt(qrPayData.substring(qrPayData.length()-8, qrPayData.length()));
        if(dealFee!=qrPayOrderVo.getDealFeeTotal()){
            qrPayOrderVo.setStatus("02");
            return;
        }
        // zhongzq 20181228 优化逻辑 应最先判断
//        if("03".equals(qrPayOrderVo.getStatus())){
//            return;//订单不存在
//        }
        if("0".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("00");
            return;//订单已支付
        }
        if("1".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("01");
            return;//订单未支付
        }
        // modify by zhongzq 20181228 业务纠正 订单取消2改为4 对应是规范 新增5 6状态
//        if("2".equals(qrPayOrderVo.getStatus())){
//            qrPayOrderVo.setStatus("04");
//            return;//订单已取消
//        }
        if("4".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("04");
            return;//订单已取消
        }
        if("5".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("05");
            return;//订单支付失败
        }
        if("6".equals(qrPayOrderVo.getStatus())){
            qrPayOrderVo.setStatus("06");
            return;//订单已退款
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
        byte[] msgVo = new ConstructMessage81().constructMessage(qrPayOrderVo);
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
