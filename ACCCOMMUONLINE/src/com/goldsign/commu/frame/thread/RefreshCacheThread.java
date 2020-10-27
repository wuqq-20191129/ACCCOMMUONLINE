package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameMessageCodeConstant;
import com.goldsign.commu.frame.dao.DeviceDao;
import com.goldsign.commu.frame.dao.PubDao;
import com.goldsign.commu.frame.dao.TicketDataSelectDao;
import com.goldsign.commu.frame.util.DateHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;

/**
 * 初始化设备信息，并定时刷新缓存信息
 *
 *
 * @author zhangjh
 *
 */
public class RefreshCacheThread implements Runnable {

    Logger logger = Logger.getLogger(RefreshCacheThread.class);

    @Override
    public void run() {
        while (true) {
            try {
                String onowDate = DateHelper.dateToHHMM(new Date());
                if (FrameCodeConstant.UpdateDevInfoTime.equals(onowDate)) {
                    DeviceDao.update();
                    logger.info("更新设备信息...");
                    //HCE二维码有效天数
                    FrameMessageCodeConstant.HCE_QRCODE_VALID_TIME = PubDao.getOlPubFlagInt("2");
                    //HCE发票测试标志 0正常,1测试
                    FrameMessageCodeConstant.IS_TEST_FLAG = PubDao.getOlPubFlagString("3");
                    //add by zhongzq 20190114 ITM二维码有效期 小时
                    FrameCodeConstant.PARA_QR_CODE_PAY_VALID_TIME =PubDao.getOlPubFlagInt("4")*FrameCodeConstant.MINUTE_UNIT;
                    //add by zhongzq 20190827 迁移时间
                    FrameCodeConstant.PARA_QR_BUFFER_MOVE_DAY = Math.abs(PubDao.getOlPubFlagInt("5")) * -1;
                    FrameCodeConstant.PARA_QR_BUFFER_MOVE_CLOCK = PubDao.getOlPubFlagInt("6");
                    FrameCodeConstant.PARA_QR_BUFFER_MOVE_CONTROL = PubDao.getOlPubFlagInt("7");
                    FrameCodeConstant.PARA_CHANGE_ALLOW_TICKET_TYPE = PubDao.getOlPubFlgList("8");
                    FrameCodeConstant.PARA_QR_BUFFER_MOVE_TIME_NO_LIMIT = PubDao.getOlPubFlagInt("9");
                    FrameCodeConstant.PARA_QR_BUFFER_MOVE_THREAD_SLEEP_TIME = PubDao.getOlPubFlagInt("10")*1000;
                    //add by zhongzq 20190910 票卡子类型逻辑卡号对照关系
                    FrameMessageCodeConstant.HCE_CARD_LOGIC_NO_MAPPING = TicketDataSelectDao.getTkCardTypeLogicNoMapping("3");
                    //add by zhongzq 20191008
                    FrameMessageCodeConstant.TICKET_ATTRIBUTE =TicketDataSelectDao.getTicketAttribute();
                    logger.info("更新缓存信息...");
                }
                Thread.sleep(35000);
            } catch (Exception e) {
                logger.error("定时任务更新缓存信息出现异常", e);

            }

        }

    }

}
