/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.QrPayDao;
import com.goldsign.commu.app.vo.QrPayOrderVo;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.dao.PubDao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @datetime 2018-5-13
 * @author lind
 * 支付二维码订单支付结果下发响应
 */
public class Message83 extends MessageValidate{
    
    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message83.class.getName());

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 98;
        process();
    }
    
    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    private void processMessage() throws Exception {
        QrPayOrderVo qrPayOrderVo = new QrPayOrderVo();
        try {
//            logger.info("-- 解析支付二维码订单支付结果下发响应消息83 --");
            ArrayList logList = new ArrayList();
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            logList.add("支付二维码订单支付结果下发响应消息83消息");
            // 校验数据区长度
            validateDataLen();
            
            //从消息中获取信息
            getQrPayOrderVo(qrPayOrderVo);
            QrPayDao qrPayDao = new QrPayDao();
            
            // 入库
            qrPayDao.insertQrPayDownRsp(qrPayOrderVo, getOLDbHelper());
            // 更新订单信息
            if("00".equals(qrPayOrderVo.getReturnCode())||"01".equals(qrPayOrderVo.getReturnCode())){
                QrPayOrderVo queryVo = qrPayDao.qrPayOrder(qrPayOrderVo, getOLDbHelper());
//                logger.debug("订单"+queryVo.getOrderNo()+"当前状态:"+queryVo.getStatus());
                qrPayDao.updateQrPayOrderDown(qrPayOrderVo, getOLDbHelper());
                // 更新统计表w_ol_sts_acc
                updateStsAcc(qrPayOrderVo);
                if (!FrameCodeConstant.ORDER_STATUS_PAID.equals(queryVo.getStatus())){
                    logger.error("异常出票响应：,订单状态："+queryVo.getStatus());
                }
            }
//            logger.info("--支付二维码订单支付结果下发响应结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
//            logger.error("处理83消息出现异常", e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) +"处理83消息出现异常", e);
            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }
    
    private void getQrPayOrderVo(QrPayOrderVo qrPayOrderVo) throws Exception  {
        // 消息类型
        String messageId = getCharString(0, 2);
        // 消息生成时间
        String msgGenTime = getBcdString(2, 7);
        // 终端处理流水号
        long terminaSeq = getLong2(9);
        // ACC处理流水号
        long AccSeq = getLong2(13);
        // 订单号
        String orderNo = getCharString(17, 14);
        // 支付标识
        String qrPayID = getCharString(31, 10);
        // 支付二维码信息
        String qrPayData = getCharString(41, 34);
        // 终端处理时间
        String dealTime = getBcdString(75, 7);
        // 实际出票票卡类型 modify by zhongziqi 20190108 cardType 为bcd类型
//        String cardType = getCharString(82, 2);
        String cardType = getBcdString(82, 2);
        // 实际出单程票单价
        long saleFee = getLong2(84);
        // 实际出单程票数量
        long saleTimes = getLong2(88);
        // 实际出单程票总价
        long dealFee = getLong2(92);
        // 响应码
        String returnCode = getCharString(96, 2);
        
        qrPayOrderVo.setMessageId(messageId);
        qrPayOrderVo.setMsgGenTime(msgGenTime);
        qrPayOrderVo.setTerminaSeq(terminaSeq);
        qrPayOrderVo.setAccSeq(AccSeq);
        qrPayOrderVo.setOrderNo(orderNo);
        qrPayOrderVo.setQrpayId(qrPayID);
        qrPayOrderVo.setQrpayData(qrPayData);
        qrPayOrderVo.setDealTime(dealTime);
        qrPayOrderVo.setDealFee(dealFee);
        qrPayOrderVo.setSaleFee(saleFee);
        qrPayOrderVo.setSaleTimes(saleTimes);
        qrPayOrderVo.setReturnCode(returnCode);
        qrPayOrderVo.setCardType(cardType);
        
        logger.debug(qrPayOrderVo.toString());
    }
    
    /*
    更新日统计表
    */
    private void updateStsAcc(QrPayOrderVo qrPayOrderVo) {
        Map<String,Object> valueMap = new HashMap<>();
        Map<String,Object> whereMap = new HashMap<>();
        valueMap.put("qrpay_lc_num", qrPayOrderVo.getSaleTimes());
        valueMap.put("qrpay_lc_times", 1);
        valueMap.put("qrpay_lc_fee", qrPayOrderVo.getDealFee()/100);
        whereMap.put("squad_day", qrPayOrderVo.getDealTime().substring(0, 8));
        whereMap.put("line_id", qrPayOrderVo.getTerminalNo().substring(0, 2));
        whereMap.put("station_id", qrPayOrderVo.getTerminalNo().substring(2, 4));
        PubDao.olStsAcc(valueMap, whereMap);
    }

}
