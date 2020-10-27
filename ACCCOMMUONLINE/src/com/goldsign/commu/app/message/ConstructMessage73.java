/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.QrPayOrderVo;
import com.goldsign.commu.frame.manager.MessageQueueManager;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

/**
 * 支付二维码订单支付结果下发请求（73）
 * @author lindq
 */
class ConstructMessage73 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage73.class
            .getName());

//    private long hdlStartTime; // 处理的起始时间
//    private long hdlEndTime;// 处理的结束时间
    
    public ConstructMessage73() {
    }

    byte[] constructMessage(QrPayOrderVo qrPayOrderVo) {
        qrPayOrderVo.setMessageId("73");
        try {
            initMessage();
            AddStringToMessage(qrPayOrderVo.getMessageId(), 2);
            AddBcdToMessage(qrPayOrderVo.getMsgGenTime(), 7);
            AddLongToMessage(qrPayOrderVo.getTerminaSeq(), 4);//终端处理流水号
            AddLongToMessage(qrPayOrderVo.getAccSeq(), 4);//中心处理流水号
            AddStringToMessage(qrPayOrderVo.getOrderNo(), 14);//订单号
            AddStringToMessage(qrPayOrderVo.getQrpayId(), 10);//支付标识
            AddStringToMessage(qrPayOrderVo.getQrpayData(), 34);//支付二维码信息
            AddStringToMessage(qrPayOrderVo.getPayChannelType(), 2);//支付渠道类型
            AddStringToMessage(qrPayOrderVo.getPayChannelCode(), 4);//支付渠道代码
            AddBcdToMessage(qrPayOrderVo.getPayDate(), 7);//支付时间
            //modify by zhongzq 20181122 应为支付结果
//            AddStringToMessage(qrPayOrderVo.getReturnCode(), 2);//支付结果
            AddStringToMessage(qrPayOrderVo.getPayStatus(), 2);//支付结果

            byte[] msg = trimMessage();
//            logger.info("Construct message 73 success! ");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 73 error! " + e);
            return new byte[0];
        }
    }
    
    
    public void constructAndSend(QrPayOrderVo qrPayOrderVo) {
            byte[] msg = constructMessage(qrPayOrderVo);
            String ip = qrPayOrderVo.getOrderIp();
            MessageQueue messageQueue = new MessageQueue();
            messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
            messageQueue.setIpAddress(ip);
            messageQueue.setMessage(msg);
            messageQueue.setIsParaInformMsg("0");
            messageQueue.setParaInformWaterNo(0);
            messageQueue.setLineId("");
            messageQueue.setStationId("");
            messageQueue.setMessageType("73");

            if(ip==null){
                return;
            }
//            logger.info("73消息入队列：" + Arrays.toString(msg));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("73消息入队列["+Arrays.toString(msg)+"]");
//            sendToJms(ip, msg, "0");
            
            Vector v = null;
            if(MessageQueueManager.MESSAG_QUEUES.containsKey(ip)){
                v = (Vector) MessageQueueManager.MESSAG_QUEUES.get(ip);
                v.add(messageQueue);
            }else{
                v = new Vector();
                v.add(messageQueue);
                MessageQueueManager.MESSAG_QUEUES.put(ip, v);
            }


//            ComMessageQueueDao dao = null;
//            Vector mqs = null;
//            try {
//                this.hdlStartTime = System.currentTimeMillis();
//                dao = new ComMessageQueueDao();
//                mqs = dao.pullQueuesForThread();
//                if (mqs != null && !mqs.isEmpty()) {
//                    MessageQueueManager.setMessageQueue(mqs);
//                }
//            } catch (Exception e) {
//                logger.error(e);
//                this.hdlEndTime = System.currentTimeMillis();
//                // 记录日志
//                LogDbUtil.logForDbDetail(
//                        FrameLogConstant.MESSAGE_ID_PARAM_COMMU_QUEUE, "",
//                        this.hdlStartTime, this.hdlEndTime,
//                        FrameLogConstant.RESULT_HDL_FAIL, Thread
//                                .currentThread().getName(),
//                        FrameLogConstant.LOG_LEVEL_ERROR, "通讯队列处理");
//            }
            
//            logger.info("成功发送73消息到" + ip);
    }
}
