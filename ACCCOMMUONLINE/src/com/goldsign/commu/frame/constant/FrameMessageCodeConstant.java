/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.constant;

import com.goldsign.commu.app.vo.TicketAttributeVo;

import java.util.HashMap;
import java.util.Map;

/**
 * @datetime 2018-1-4 16:42:16
 * @author lind
 */
public class FrameMessageCodeConstant {
    
    public final static String CITY_CODE = "8300";//城市代码
    public final static String BUSINESS_CODE = "0003";//行业代码
    public static String IS_TEST_FLAG = "0";//测试标志0:正式,1:测试
    
    public final static String HCE_LOGCAL_CITY_CODE = "83";//逻辑卡号的城市代码
    public final static String HCE_LOGCAL_BUSINESS_CODE = "03";//逻辑卡号的行业代码
    public final static String HCE_LOGCAL_CARD_TYPE = "80";//逻辑卡号的手机票票卡类型（票库）
    //add by zhongzq 20190910 票卡子类型逻辑卡号对照关系
    public static  Map<String,String> HCE_CARD_LOGIC_NO_MAPPING = new HashMap<String,String>();

    public static  Map<String, TicketAttributeVo> TICKET_ATTRIBUTE = new HashMap<String,TicketAttributeVo>();
    //HCE厂商与逻辑卡号第8位对照关系
    public static Map<String,String> HCE_LOGCAL_NO_INDEX = new HashMap<String,String>(){{
        put("80","1");put("81","9");
    }};
    
    public final static String HCE_PRODUCT_CODE = "0991";//发卡方代码
    public final static String HCE_DEAL_DEV_CODE = "15802";//发行设备代码
    public final static String HCE_CAR_VER = "10";//卡版本号
    public final static String HCE_CARD_APP_VER = "10";//卡应用版本号
    public final static String HCE_SALE_ACT_FLAG = "1";//发售激活标志0:不激活,1:激活
    public static int HCE_QRCODE_VALID_TIME = 1;//手机二维码有效天数

    public final static String MESS_RETURN_CODE = "00";//响应码
    public final static String MESS_ERR_CODE = "00";//错误码
    
    /****加密机参数****/
    public static String ENCRY_TRAN_TYPE = "02";//交易类型标识
    public static String ENCRY_HEADER_MAC = "UBX11109K0173";//命令代码UB/算法标识X/模式标识1（计算MAC）/方案ID 1/根密钥类型109/根密钥K017（K+圈存密钥索引）/离散次数3
    public static String ENCRY_HEADER_TAC = "UBX12109K01F3";//命令代码UB/算法标识X/模式标识1（计算MAC）/方案ID 2/根密钥类型109/根密钥K01F（K+TAC密钥索引）/离散次数3
    public static String ENCRY_HEADER_PSAM = "U1X001109K0DF3";//命令代码U1/算法标识X/加密模式标识0/方案ID 01/根密钥类型109/根密钥K0DF(K+PSAM外部认证密钥)/离散次数3
    public static String ENCRY_HEADER_KEY = "U1X001109KZZZ2";//命令代码U1/算法标识X/加密模式标识0/方案ID 01/根密钥类型109/根密钥KZZZ(ZZZ为替换字符)/离散次数2
    //- 认证码: 邮政编码和电话区号 8300 0991
    //- 根分散: "UrumqiMT"的 ASCII码  (5572756D71694D54)
    //- 区分散: 乌鲁木齐地铁的城市代码和行业代码  8810 0001 （见交通部实施指南定义）
    public static String ENCRY_DISPERSE_DATA = "5572756D71694D5488100001FFFFFFFF";//轨道交通离散数据（16H）||地区代码（16H）
    //- 卡片主控密钥"001"//- 卡片维护密钥"002"//- 外部认证密钥"006"//- 应用主控密钥"009"//- 应用维护密钥"00A"//- 消费密钥"01D"
    //- 圈存密钥"017"//- 交易认证TAC 密钥"01F"//- 应用维护密钥01"010"//- 应用维护密钥02"011"//- 应用锁定密钥"00B"//- 应用解锁密钥"00C"
    //- PIN 解锁密钥"00E"//- PIN 重装密钥"00D"//- 修改透支限额密钥"01B" //- 圈提密钥密钥"019"//- 外部认证密钥"020"
    public static Map<Integer,String> ENCRY_KEY_INDEX = new HashMap<Integer,String>(){{
        put(0,"001");put(1,"002");put(2,"006");put(3,"009");put(4,"00A");put(5,"01D");
        put(6,"017");put(7,"01F");put(8,"010");put(9,"011");put(10,"00B");put(11,"00C");
        put(12,"00E");put(13,"00D");put(14,"01B");put(15,"019");put(16,"020");
    }};
    
    /*
    根据加密机版本号更新加密机参数索引  
    新索引=版本号*256+原索引
    */
    public static void updateVersionEncryParam(){
        String ver = Integer.toString(FrameCodeConstant.KeyVersion);
        ENCRY_HEADER_MAC = "UBX11109K"+ver+"173";//命令代码UB/算法标识X/模式标识1（计算MAC）/方案ID 1/根密钥类型109/根密钥K017（K+(版本号*256+圈存密钥索引)）/离散次数3
        ENCRY_HEADER_TAC = "UBX12109K"+ver+"1F3";//命令代码UB/算法标识X/模式标识1（计算MAC）/方案ID 2/根密钥类型109/根密钥K01F（K+(版本号*256+TAC密钥索引)）/离散次数3
        ENCRY_HEADER_PSAM = "U1X001109K"+ver+"DF3";//命令代码U1/算法标识X/加密模式标识0/方案ID 01/根密钥类型109/根密钥K0DF(K+(版本号*256+PSAM外部认证密钥))/离散次数3
        ENCRY_KEY_INDEX = new HashMap<Integer,String>(){{
            put(0,ver+"01");put(1,ver+"02");put(2,ver+"06");put(3,ver+"09");put(4,ver+"0A");put(5,ver+"1D");
            put(6,ver+"17");put(7,ver+"1F");put(8,ver+"10");put(9,ver+"11");put(10,ver+"0B");put(11,ver+"0C");
            put(12,ver+"0E");put(13,ver+"0D");put(14,ver+"1B");put(15,ver+"19");put(16,ver+"20");
        }};
    }
    
}
