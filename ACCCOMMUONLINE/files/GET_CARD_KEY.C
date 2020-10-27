#define WIN32_LEAN_AND_MEAN                                                     //- 提高编译效率
#pragma pack(1)

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <Winsock2.h>
#include "..\\file\\file.h"
#include "..\\cvt\\cvt.h"
#include "GET_CARD_KEY.H"
                                                                                //- 系统分散数据，两级，第一级得到根密钥，第二级得到区域（地市）密钥
                                                                                //- 根分散: "UrumqiMT"的 ASCII码
																				//- 区分散: 乌鲁木齐地铁的城市代码和行业代码  8810 0001 （见交通部实施指南定义）
char sys_div[16] = "\x55\x72\x75\x6D\x71\x69\x4D\x54"\
                   "\x88\x10\x00\x01\xFF\xFF\xFF\xFF";
                                                                                //- 认证码: 邮政编码和电话区号 8300 0991
char dll_pin[4] = "\x83\x00\x09\x91";


int g_cm_ver;                                                                   //- 使用版本号
int dll_pin_ok = 1;                                                             //- 动态库 PIN校验是否通过


#define CURRENT_VER                     0x01                                    //- 当前协议版本
#define MAX_DATA_LEN                    0x400                                   //- 数据内容最大长度
struct IN_OUT_DATA
{
  unsigned long ln;
  unsigned char ver;
  unsigned char ass;
  unsigned char card;
  unsigned char cmd;
  unsigned char data[MAX_DATA_LEN];
};
struct IN_OUT_DATA inda, otda;                                                  //- 输入输出缓冲
#define CLR_IN_OUT_DATA\
    memset((void*)&inda, 0, sizeof(struct IN_OUT_DATA));\
    memset((void*)&otda, 0, sizeof(struct IN_OUT_DATA));
                                                                                //- 辅助参数说明
                                                                                //- 第一位：1：记录日志；0：不记录日志
                                                                                //- 第二位：1：详细信息；0：关键信息和错误信息
unsigned char debug_op = 0;
unsigned char log_op = 0;



#define LOG_FILE_NAME                   "get_key.log"                           //- 日志文件名称

#define JUDGE_CLOSE_RETURN(JUDGE, OPTION)\
    if(JUDGE)\
    {\
      OPTION\
      return;\
    }




struct ST_JMJ                                                                   //- 加密机数据结构
{
  char t_ip[4];                                                                 //- 加密机IP地址
  char t_port[2];                                                               //- 加密机端口
} st_jmj;

//--------+---------+---------+---------+---------+---------+---------+---------
//- 说明: 日志信息
//- 参数: info                [I ]日志信息
//- 返回: 无
//--------+---------+---------+---------+---------+---------+---------+---------
void LOG_INFO(char* info)
{
//  LogAdd(LOG_FILE_NAME, info);
}


//--------+---------+---------+---------+---------+---------+---------+---------
//- 说明: TCP/IP数据读取函数
//- 参数: sckt                [I ]读取数据的SOCKET
//-       len                 [I ]需要读出的数据长度
//-       tm                  [I ]规定读出循环次数
//-       itvl                [I ]每次读出的时间间隔( 毫秒 )
//-       cnt                 [ O]读出数据
//- 返回:        0  成功返回
//-       XXXXXXXX  错误代码
//-          10000  连接关闭
//--------+---------+---------+---------+---------+---------+---------+---------
int recv_wait( SOCKET sckt, int len, int tm, int itvl, char* cnt )
{
  int iIdx;
  int iLeft;
  int ret;
  int time;
  int rt_code;

  iIdx = 0;
  time = tm;
  rt_code = 0xFF;
  iLeft = len;

  while( time )
  {
      ret = recv( sckt, cnt + iIdx, iLeft, 0 );
      if( ( ret == 0 )|| ( ret == SOCKET_ERROR ) )
      {
        if( !sckt )
        {
          return 10000;
        }
        time --;
        Sleep( itvl );
      }
      else
      {
        iLeft = iLeft - ret;
        iIdx = iIdx + ret;

        if( iLeft <= 0  )
        {
          time = 0;
          rt_code = 0x0;
          continue;
        }
        time --;
        Sleep( itvl );
      }
  }

  return rt_code;
}

//--------+---------+---------+---------+---------+---------+---------+---------
//- 说明: TCP/IP数据发送函数
//- 参数: sckt                [I ]发送数据的SOCKET
//-       len                 [I ]需要发送的数据长度
//-       tm                  [I ]规定发送循环次数
//-       itvl                [I ]每次发送的时间间隔( 毫秒 )
//-       cnt                 [I ]发送数据
//- 返回:        0  成功返回
//-       XXXXXXXX  错误代码
//--------+---------+---------+---------+---------+---------+---------+---------
int send_wait( SOCKET sckt, int len, int tm, int itvl, char* cnt )
{
  int iIdx;
  int iLeft;
  int ret;
  int time;
  int rt_code;

  iIdx = 0;
  time = tm;
  rt_code = 0xFF;
  iLeft = len;

  while( time )
  {
    ret = send( sckt, cnt + iIdx, iLeft, 0 );
    if( ( ret == 0 ) || ( ret == SOCKET_ERROR ) )
    {
      if( !sckt )
      {
         return 10000;
      }
      time --;
      Sleep( itvl );
    }
    else
    {
      iLeft = iLeft - ret;
      iIdx = iIdx + ret;

      if( iLeft <= 0 )
      {
        time = 0;
        rt_code = 0x0;
        continue;
      }
      time --;
      Sleep( itvl );
    }
  }

  return rt_code;
}



//--------+---------+---------+---------+---------+---------+---------+---------
//- 说明: 通用指令_获得加密机授权
//- 参数: 无
//- 返回:        0  成功
//-            非0  失败
//--------+---------+---------+---------+---------+---------+---------+---------
void cmd_0001(void)
{
  int i ;

  struct ST_CMD_IN_DATA                                                         //- 输入数据结构
  {
    char t_ip[4];                                                               //- 加密机IP地址
    char t_port[2];                                                             //- 加密机端口
    char pin[4];                                                                //- 授权 pin
    char key_ver[1];                                                            //- 加密机密钥版本

    unsigned long ln;                                                           //- 输入数据长度
  } si;

  struct ST_CMD_OT_DATA                                                         //- 输出数据结构
  {
    unsigned long ln;                                                           //- 输入数据长度
  } so;

  si.ln = sizeof(struct ST_CMD_IN_DATA) - 4;
  so.ln = sizeof(struct ST_CMD_OT_DATA) - 4;

  if(inda.ln != si.ln + 4)                                                      //- 指令长度判断
  {
    otda.ass = IN_DATA_ERR;
    return;
  }

  memcpy(&si, inda.data, si.ln);


  for(i = 0; i < 4; i ++)
  {
    if(si.pin[i] != dll_pin[i])
	{
      otda.ass = IN_DATA_ERR;
      return;
	}
  }

  dll_pin_ok = 0;
  g_cm_ver = si.key_ver[0];

  memcpy(st_jmj.t_ip, si.t_ip, 6);

  memcpy(&otda.data[0], &so, so.ln);                                            //- 输出数据
  otda.ln += so.ln;
}







//--------+---------+---------+---------+---------+---------+---------+---------
//- 说明: 获取密钥
//- 参数: ot_d                [IO]密文
//-       key_id              [I ]密钥索引
//-       div_lv              [I ]离散级别
//-       div                 [I ]离散数据
//-       in_d                [I ]明文
//-       in_d_ln             [I ]明文长度(必须是 8的倍数)
//-       sck                 [I ]通讯socket
//- 返回: 0:成功    非0:失败
//--------+---------+---------+---------+---------+---------+---------+---------
int get_key(char* ot_d, int key_id, int div_lv, char* div,
    char* in_d, int in_d_ln, SOCKET sck)
{
  char in[4096] = {0};
  char ot[4096] = {0};
  char ot_ln_c[2];
  int in_ln;
  int ot_ln;
  int rt;

  int p;


  p = 2;
  memcpy(&in[p], JMJ_HAED, JMJ_HAED_LN);                                        //- 12字节头
  p += JMJ_HAED_LN;
  memcpy(&in[p], "U1", 2);                                                      //- 2字节命令代码
  p += 2;
  memcpy(&in[p], "X", 1);                                                       //- 1字节算法标识
  p += 1;
  memcpy(&in[p], "0", 1);                                                       //- 1字节加密模式标识
  p += 1;
  memcpy(&in[p], "01", 2);                                                      //- 2字节方案ID
  p += 2;
  memcpy(&in[p], "109", 3);                                                     //- 3字节根密钥类型
  p += 3;
  sprintf(&in[p], "K%03X", key_id);                                             //- 4字节密钥索引
  p += 4;
  sprintf(&in[p], "%01X", div_lv);                                              //- 1字节离散级别
  p += 1;
  if(div_lv != 0)
  {
    Convert(0, div_lv * 8, div, &in[p]);
	p += div_lv * 8 * 2;
  }
  memcpy(&in[p], "01", 2);                                                      //- 2字节数据填充标识
  p += 2;
  sprintf(&in[p], "%03d", in_d_ln);                                             //- 3字节数据长度
  p += 3;
  Convert(0, in_d_ln, in_d, &in[p]);                                            //- 数据
  p += in_d_ln * 2;

  in_ln = p - 2;

  in[0] = (char)((in_ln & 0xFF00) >> 8);
  in[1] = (char)(in_ln & 0xFF);

  LOG_INFO(&in[2 + 12]);

  rt = send_wait(sck, in_ln + 2, 5, 0, in);                                     //- 发送命令
  if(rt != 0)
  {
    return SEND_ERROR;
  }
  rt = recv_wait(sck, 2, 100, 10, ot_ln_c);                                     //- 接收响应长度
  if(rt != 0)
  {
    return RECV_ERROR;
  }
  ot_ln = (unsigned char)ot_ln_c[0];
  ot_ln = (ot_ln << 8) + ((unsigned char)ot_ln_c[1]);
  rt = recv_wait(sck, ot_ln, 5, 0, ot);                                         //- 接收响应
  if(rt != 0)
  {
    return RECV_ERROR;
  }

  LOG_INFO(&ot[12]);

  if((ot[JMJ_HAED_LN + 2] != '0' ) && (ot[JMJ_HAED_LN + 3] != '0' ))
  {
    return STAT_ERROR;
  }

  Convert(1, in_d_ln,  &ot[JMJ_HAED_LN + 7], ot_d);                             //- 返回数据前有 3字节长度

  return 0;
}











//--------+---------+---------+---------+---------+---------+---------+---------
//- 说明: 通用指令_获得卡密钥
//- 参数: 无
//- 返回:        0  成功
//-            非0  失败
//--------+---------+---------+---------+---------+---------+---------+---------
void cmd_0002(void)
{
  int i;
  int rt ;

  struct ST_CMD_IN_DATA                                                         //- 输入数据结构
  {
    char logic_no[8];                                                           //- 用户卡逻辑卡号

    unsigned long ln;                                                           //- 输入数据长度
  } si;

  struct ST_CMD_OT_DATA                                                         //- 输出数据结构
  {
    char DCCK[16];                                                              //- 卡片主控密钥
    char DCMK[16];                                                              //- 卡片维护密钥
    char DCEAK[16];                                                             //- 外部认证密钥

    char DACK[16];                                                              //- 应用主控密钥（DACK）
    char DAMK[16];                                                              //- 应用维护密钥（DAMK）
    char DPK[16];                                                               //- 消费密钥（DPK）
    char DLK[16];                                                               //- 圈存密钥（DLK）
    char DTK[16];                                                               //- 交易认证TAC 密钥（DTK）
    char DAMK01[16];                                                            //- 应用维护密钥01（DAMK01）
    char DAMK02[16];                                                            //- 应用维护密钥02（DAMK02）
    char DABK[16];                                                              //- 应用锁定密钥（DABK）
    char DAUK[16];                                                              //- 应用解锁密钥（DAUK）
    char DPUK[16];                                                              //- PIN 解锁密钥（DPUK）
    char DPRK[16];                                                              //- PIN 重装密钥（DPRK）
    char DUK[16];                                                               //- 修改透支限额密钥（DUK）
    char DULK[16];                                                              //- 圈提密钥密钥（DULK）
    char DAEAK[16];                                                             //- 外部认证密钥（DAEAK）


    unsigned long ln;                                                           //- 输入数据长度
  } so;

  char div_d[16];                                                               //- 卡号分散


  SOCKET  ssock;                                                                //- client socket
  WSADATA wsadata;                                                              //- WSA data
  struct  protoent *ppe;                                                        //- protoent data
  struct  sockaddr_in sin;                                                      //- sock address data

  int df;                                                                       //- 应用序号

  char Server_IP_Address[16];
  unsigned short Server_Port;


  if(dll_pin_ok != 0)
  {
    otda.ass = IN_DATA_ERR;
    return;
  }

  si.ln = sizeof(struct ST_CMD_IN_DATA) - 4;
  so.ln = sizeof(struct ST_CMD_OT_DATA) - 4;

  if(inda.ln != si.ln + 4)                                                      //- 指令长度判断
  {
    otda.ass = IN_DATA_ERR;
    return;
  }

  memcpy(&si, inda.data, si.ln);

  memcpy(&div_d[0], si.logic_no, 8);
  for(i = 0; i < 8; i ++)
  {
    div_d[i + 8] = ~div_d[i];
  }

                                                                                //- 获得配置信息
  sprintf(Server_IP_Address, "%d.%d.%d.%d", (unsigned char)st_jmj.t_ip[0],
    (unsigned char)st_jmj.t_ip[1], (unsigned char)st_jmj.t_ip[2],
    (unsigned char)st_jmj.t_ip[3]);
  Server_Port = (((unsigned char)st_jmj.t_port[0]) << 8 );
  Server_Port += (unsigned char)st_jmj.t_port[1];
                                                                                //- 初始化
  if(WSAStartup(MAKEWORD(2, 2), &wsadata) != 0)
  {
    otda.ass = SCKT_ERROR;
    return;
  }

  memset(&sin, 0, sizeof(sin));

  sin.sin_family    = AF_INET;
  sin.sin_addr.s_addr = inet_addr(Server_IP_Address);
  sin.sin_port    = htons((u_short) Server_Port);
                                                                                //- 创建socket
  ppe = getprotobyname("tcp");
  ssock = socket(PF_INET,SOCK_STREAM, ppe->p_proto);
  if(ssock == INVALID_SOCKET)
  {
    otda.ass = SCKT_ERROR;
    return;
  }
                                                                                //- 建立连接
  if(connect(ssock, (struct sockaddr*)(&sin), sizeof(sin)) !=0)
  {
    otda.ass = SCKT_ERROR;
    return;
  }
  //ioctlsocket(ssock, FIONBIO, &p);

                                                                                //- 获得卡片主控密钥
  rt = get_key(so.DCCK, g_cm_ver * 256 +   1, 2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DACK_ERR;)
                                                                                //- 获得卡片维护密钥
  rt = get_key(so.DCMK, g_cm_ver * 256 +   2, 2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DCMK_ERR;)
                                                                                //- 获得外部认证密钥
  rt = get_key(so.DCEAK,g_cm_ver * 256 +   6, 2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DCEAK_ERR;)


  df = 0;

                                                                                //- 获得应用主控密钥
  rt = get_key(so.DACK,   g_cm_ver * 256 + 8 + df * 24 +  1,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DACK_ERR;)
                                                                                //- 获得应用维护密钥
  rt = get_key(so.DAMK,   g_cm_ver * 256 + 8 + df * 24 +  2,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DAMK_ERR;)
                                                                                //- 获得消费密钥
  rt = get_key(so.DPK,    g_cm_ver * 256 + 8 + df * 24 +  21,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DPK_ERR;)
                                                                                //- 获得圈存密钥
  rt = get_key(so.DLK,    g_cm_ver * 256 + 8 + df * 24 +  15,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DLK_ERR;)
                                                                                //- 获得交易认证TAC 密钥
  rt = get_key(so.DTK,    g_cm_ver * 256 + 8 + df * 24 +  23,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DTK_ERR;)
                                                                                //- 获得应用维护密钥01
  rt = get_key(so.DAMK01, g_cm_ver * 256 + 8 + df * 24 +   8,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass =GET_KEY_DAMK01_ERR;)
                                                                                //- 获得应用维护密钥02
  rt = get_key(so.DAMK02, g_cm_ver * 256 + 8 + df * 24 +   9,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass =GET_KEY_DAMK02_ERR;)
                                                                                //- 获得应用锁定密钥
  rt = get_key(so.DABK,   g_cm_ver * 256 + 8 + df * 24 +   3,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DABK_ERR;)
                                                                                //- 获得应用解锁密钥
  rt = get_key(so.DAUK,   g_cm_ver * 256 + 8 + df * 24 +   4,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DAUK_ERR;)
                                                                                //- 获得PIN 解锁密钥
  rt = get_key(so.DPUK,   g_cm_ver * 256 + 8 + df * 24 +   6,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DPUK_ERR;)
                                                                                //- 获得PIN 重装密钥
  rt = get_key(so.DPRK,   g_cm_ver * 256 + 8 + df * 24 +   5,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DPRK_ERR;)
                                                                                //- 获得修改透支限额密钥
  rt = get_key(so.DUK,    g_cm_ver * 256 + 8 + df * 24 +  19,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DUK_ERR;)
                                                                                //- 获得圈提密钥密钥
  rt = get_key(so.DULK,   g_cm_ver * 256 + 8 + df * 24 +  17,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DULK_ERR;)
                                                                                //- 获得外部认证密钥
  rt = get_key(so.DAEAK,  g_cm_ver * 256 + 8 + df * 24 +  24,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DAEAK_ERR;)

  closesocket(ssock);


  memcpy(&otda.data[0], &so, so.ln);                                            //- 输出数据
  otda.ln += so.ln;

}


//--------+---------+---------+---------+---------+---------+---------+---------
//- 说明: 通用指令_获得单程票密钥
//- 参数: 无
//- 返回:        0  成功
//-            非0  失败
//--------+---------+---------+---------+---------+---------+---------+---------
void cmd_0003(void)
{
  int rt;

  char tmp_d[16] = {0};
  char tmp_e[16] = {0};

  struct ST_CMD_IN_DATA                                                         //- 输入数据结构
  {
    char fix_no[4];                                                             //- 物理卡号
    char logic_no[4];                                                           //- 逻辑卡号

    unsigned long ln;                                                           //- 输入数据长度
  } si;

  struct ST_CMD_OT_DATA                                                         //- 输出数据结构
  {
    char mac[4];                                                                //- 认证mac
    char key[6];                                                                //- 认证密钥

    unsigned long ln;                                                           //- 输入数据长度
  } so;


  SOCKET  ssock;                                                                //- client socket
  WSADATA wsadata;                                                              //- WSA data
  struct  protoent *ppe;                                                        //- protoent data
  struct  sockaddr_in sin;                                                      //- sock address data

  char Server_IP_Address[16];
  unsigned short Server_Port;

  if(dll_pin_ok != 0)
  {
    otda.ass = IN_DATA_ERR;
    return;
  }


  si.ln = sizeof(struct ST_CMD_IN_DATA) - 4;
  so.ln = sizeof(struct ST_CMD_OT_DATA) - 4;

  if(inda.ln != si.ln + 4)                                                      //- 指令长度判断
  {
    otda.ass = IN_DATA_ERR;
    return;
  }

  memcpy(&si, inda.data, si.ln);

                                                                                //- 获得配置信息
  sprintf(Server_IP_Address, "%d.%d.%d.%d", (unsigned char)st_jmj.t_ip[0],
    (unsigned char)st_jmj.t_ip[1], (unsigned char)st_jmj.t_ip[2],
    (unsigned char)st_jmj.t_ip[3]);
  Server_Port = (((unsigned char)st_jmj.t_port[0]) << 8 );
  Server_Port += (unsigned char)st_jmj.t_port[1];
                                                                                //- 初始化
  if(WSAStartup(MAKEWORD(2, 2), &wsadata) != 0)
  {
    otda.ass = SCKT_ERROR;
    return;
  }

  memset(&sin, 0, sizeof(sin));

  sin.sin_family    = AF_INET;
  sin.sin_addr.s_addr = inet_addr(Server_IP_Address);
  sin.sin_port    = htons((u_short) Server_Port);
                                                                                //- 创建socket
  ppe = getprotobyname("tcp");
  ssock = socket(PF_INET,SOCK_STREAM, ppe->p_proto);
  if(ssock == INVALID_SOCKET)
  {
    otda.ass = SCKT_ERROR;
    return;
  }
                                                                                //- 建立连接
  if(connect(ssock, (struct sockaddr*)(&sin), sizeof(sin)) !=0)
  {
    otda.ass = SCKT_ERROR;
    return;
  }

                                                                                //- 获得 mac
  memset(tmp_e, 0, 8);
  rt = get_key(tmp_e,  g_cm_ver * 256 + 200 +   1,
      2, sys_div, &si.fix_no[0], 8, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_SJT_MAC_ERR;)
  memcpy(&so.mac[0], tmp_e, 4);
                                                                                //- 获得 key
  memset(tmp_e, 0, 8);
  memcpy(tmp_d, &si.fix_no[2], 6);
  tmp_d[6] = so.mac[0];
  tmp_d[7] =(char)0xA5;
  rt = get_key(tmp_e,  g_cm_ver * 256 + 200 +   4,
      2, sys_div, tmp_d, 8, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_SJT_MAC_ERR;)
  memcpy(&so.key[0], tmp_e, 6);

  closesocket(ssock);

  memcpy(&otda.data[0], &so, so.ln);                                            //- 输出数据
  otda.ln += so.ln;
}


//---------+---------+---------+---------+---------+---------+---------+--------
//- 说明: 应用设备操作接口
//- 参数: In                    [I ]输入数据
//-       Ot                    [ O]输出数据
//- 返回: 0                     成功
//-       非0                   失败
//---------+---------+---------+---------+---------+---------+---------+--------
DWORD WINAPI get_card_key_op(char* In, char* Ot)
{
  CLR_IN_OUT_DATA                                                               //- 缓冲初始化

  if(strlen(In) % 2 != 0)
  {
    otda.ass = IN_DATA_ERR;
  }
  else
  {
    inda.ln = strlen(In) >> 1;
    if(Convert(1, inda.ln, In, (char*)&inda.ver) != 0)                          //- 数据获取
    {
      otda.ass = IN_DATA_ERR;
    }
  }
  otda.ver = inda.ver;                                                          //- 返回数据初始化
  otda.card = inda.card;
  otda.cmd = inda.cmd;
  otda.ln = 4;

  log_op = (inda.ass & 0x80) >> 7;
  if(log_op == 1)
  {
    debug_op = (inda.ass & 0x40) >> 6;
  }

  if(log_op == 1)
  {
    LOG_INFO("====");
    LOG_INFO(In);
  }

  if(otda.ass == 0)
  {
    if(otda.ver != CURRENT_VER)
    {
      otda.ass = CURRENT_VER_ERR;
    }
  }

  if(otda.ass == 0)
  {
    if(otda.card == 0x00)                                                       //- 通用指令
    {
      switch(inda.cmd)
      {
        case 0x01:                                                              //- 获得授权
          cmd_0001();
          break;

        case 0x02:                                                              //- 获得卡密钥
          cmd_0002();
          break;

        case 0x03:                                                              //- 获得单程票密钥
          cmd_0003();
          break;

        default:
          otda.ass = CMD_ERR;
      }
    }
    else
    {
      otda.ass = CARD_ERR;
    }
  }



  Convert(0, otda.ln, (char*)&otda.ver, Ot);

  if(log_op == 1)
  {
    LOG_INFO(Ot);
  }

  return 0;
}
