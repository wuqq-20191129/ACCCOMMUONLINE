/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.QrCodeHceDao;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @datetime 2018-3-13
 * @author lind
 * 二维码订单取消
 */
public class Message38 extends MessageValidate{
    
    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message38.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_cancel";

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 59;
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
        QrCodeVo qrCodeVo = new QrCodeVo();
        try {
            //汇总日志为1条输出 20190821 zhongzq
            ArrayList logList = new ArrayList();
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            logList.add("二维码订单取消请求38消息");
//            logger.info("-- 解析二维码订单取消请求消息38 --");
            // 校验数据区长度
            validateDataLen();
            
            //从消息中获取信息
            getQrCodeVo(qrCodeVo);
            
            // 入库
            QrCodeHceDao qrCodeDao = new QrCodeHceDao();
            qrCodeDao.insertQrCancel(qrCodeVo, getOLDbHelper());
            
            //订单查询
            QrCodeOrderVo qrCodeOrderVo = new QrCodeOrderVo();
            qrCodeOrderVo = qrCodeDao.qrCodeOrder(qrCodeVo, getOLDbHelper());
            
            //整合返回消息
            constructMessageVo(qrCodeVo, qrCodeOrderVo, nowDate);
            //入库和发送响应消息48
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(qrCodeVo);
            qrCodeDao.insertQrCancelRsp(qrCodeVo, getOLDbHelper());
            // 更新统计表w_ol_sts_acc
            if("00".equals(qrCodeVo.getReturnCode())){
                updateStsAcc(qrCodeVo);
            }
            writeBackMsg(qrCodeVo);
//            logger.info("--二维码订单取消请求结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
            // 出现异常返回响应的错误信息给终端
            dealException(qrCodeVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), true)+"处理38消息出现异常", e);
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
        if(qrCodeVo.getAccSeq()==null){
            qrCodeVo.setAccSeq(0L);
        }
        //消息生成时间
        qrCodeVo.setMsgGenTime(DateHelper.currentTodToString());
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("异常返回数据类信息:[" +qrCodeVo+"]");
        writeBackMsg(qrCodeVo);
    }

    private void getQrCodeVo(QrCodeVo qrCodeVo) throws Exception  {
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
        //取消时间
        String dealTime = getBcdString(52, 7);
        
        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());
        
        qrCodeVo.setWaterNo(waterNo);
        qrCodeVo.setMessageId(messageId);
        qrCodeVo.setMsgGenTime(msgGenTime);
        qrCodeVo.setTerminalNo(terminalNo);
        qrCodeVo.setSamLogicalId(samLogicalId);
        qrCodeVo.setTerminaSeq(terminaSeq);
        qrCodeVo.setOrderNo(orderNo);
        qrCodeVo.setDealTime(dealTime);
       //modify by zhongzq 20190111 中心流水号纠正
        //中心流水号
//        qrCodeVo.setAccSeq(waterNo);
        Long accSeq = new QrCodeHceDao().getQrCodeyAccSeq(qrCodeVo, getOLDbHelper());
        qrCodeVo.setAccSeq(accSeq);
        
//        logger.debug(qrCodeVo.toString());
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(qrCodeVo.toString());
    }

    //响应码:00：正常；01:不符合业务逻辑或系统处理过程中出现异常，详细原因见错误描述
    private void constructMessageVo(QrCodeVo qrCodeVo, QrCodeOrderVo qrCodeOrderVo, String nowDate) throws Exception {
        //消息生成时间
        Date dataTime = new Date();
        try {
            dataTime = DateHelper.dateStrToSqlTimestamp(qrCodeVo.getDealTime());//订单取消时间
        } catch (ParseException ex) {
            logger.error(ex.getMessage());
        }
        //检验订单状态,未取票
        if(qrCodeOrderVo==null){
            qrCodeVo.setReturnCode("01");
            qrCodeVo.setErrCode("80");//80:未购票或不存在
            //add by zhongzq 20190111 纠正返回订单状态
            qrCodeVo.setTkStatus("80");
            return;
        }
        //二维码可取消，过有效期后自动退款
//        if(qrCodeOrderVo.getValidTime().getTime()<dataTime.getTime()){//退票时间大于二维码有效时间
//            qrCodeVo.setReturnCode("01");
//            qrCodeVo.setErrCode("82");//82:二维码已过有效期
//            return;
//        }
        if(!qrCodeOrderVo.getTicketStatus().equals("00")){
            qrCodeVo.setReturnCode("01");
            qrCodeVo.setErrCode(qrCodeOrderVo.getTicketStatus());
            //add by zhongzq 20190111 纠正返回订单状态
            qrCodeVo.setTkStatus(qrCodeOrderVo.getTicketStatus());
            return;
        }
        //modify by zhongzq 20190111 无用代码
//        if(qrCodeOrderVo.getTicketStatus().equals("84")){
//            qrCodeVo.setReturnCode("01");
//            qrCodeVo.setErrCode(qrCodeOrderVo.getTicketStatus());
//            return;
//        }
        
        //更新订单
        int n = 0;
        QrCodeHceDao qrCodeDao = new QrCodeHceDao();
        n = qrCodeDao.updateQrOrder(qrCodeVo, getOLDbHelper());
        if(n==1){
            qrCodeVo.setReturnCode("00");
            qrCodeVo.setErrCode("00");//00:订单未执行
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("订单"+qrCodeVo.getOrderNo()+"已经取消");
        }
        //响应消息生成时间
        qrCodeVo.setMsgGenTime(nowDate);
        qrCodeVo.setTkStatus(qrCodeOrderVo.getTicketStatus());
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
        byte[] msgVo = new ConstructMessage48().constructMessage(qrCodeVo);
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
        Map<String,Object> valueMap = new HashMap<>();
        Map<String,Object> whereMap = new HashMap<>();
        valueMap.put("qrcode_hce_c_fee", qrCodeVo.getDealFee()/100);
        valueMap.put("qrcode_hce_c_num", qrCodeVo.getSaleTimes());
        valueMap.put("qrcode_hce_c_times", 1);
        whereMap.put("squad_day", qrCodeVo.getMsgGenTime().substring(0, 8));
        whereMap.put("line_id", qrCodeVo.getTerminalNo().substring(0, 2));
        whereMap.put("station_id", qrCodeVo.getTerminalNo().substring(2, 4));
        PubDao.olStsAcc(valueMap, whereMap);
    }
}
