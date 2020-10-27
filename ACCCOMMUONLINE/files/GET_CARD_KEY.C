#define WIN32_LEAN_AND_MEAN                                                     //- ��߱���Ч��
#pragma pack(1)

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <Winsock2.h>
#include "..\\file\\file.h"
#include "..\\cvt\\cvt.h"
#include "GET_CARD_KEY.H"
                                                                                //- ϵͳ��ɢ���ݣ���������һ���õ�����Կ���ڶ����õ����򣨵��У���Կ
                                                                                //- ����ɢ: "UrumqiMT"�� ASCII��
																				//- ����ɢ: ��³ľ������ĳ��д������ҵ����  8810 0001 ������ͨ��ʵʩָ�϶��壩
char sys_div[16] = "\x55\x72\x75\x6D\x71\x69\x4D\x54"\
                   "\x88\x10\x00\x01\xFF\xFF\xFF\xFF";
                                                                                //- ��֤��: ��������͵绰���� 8300 0991
char dll_pin[4] = "\x83\x00\x09\x91";


int g_cm_ver;                                                                   //- ʹ�ð汾��
int dll_pin_ok = 1;                                                             //- ��̬�� PINУ���Ƿ�ͨ��


#define CURRENT_VER                     0x01                                    //- ��ǰЭ��汾
#define MAX_DATA_LEN                    0x400                                   //- ����������󳤶�
struct IN_OUT_DATA
{
  unsigned long ln;
  unsigned char ver;
  unsigned char ass;
  unsigned char card;
  unsigned char cmd;
  unsigned char data[MAX_DATA_LEN];
};
struct IN_OUT_DATA inda, otda;                                                  //- �����������
#define CLR_IN_OUT_DATA\
    memset((void*)&inda, 0, sizeof(struct IN_OUT_DATA));\
    memset((void*)&otda, 0, sizeof(struct IN_OUT_DATA));
                                                                                //- ��������˵��
                                                                                //- ��һλ��1����¼��־��0������¼��־
                                                                                //- �ڶ�λ��1����ϸ��Ϣ��0���ؼ���Ϣ�ʹ�����Ϣ
unsigned char debug_op = 0;
unsigned char log_op = 0;



#define LOG_FILE_NAME                   "get_key.log"                           //- ��־�ļ�����

#define JUDGE_CLOSE_RETURN(JUDGE, OPTION)\
    if(JUDGE)\
    {\
      OPTION\
      return;\
    }




struct ST_JMJ                                                                   //- ���ܻ����ݽṹ
{
  char t_ip[4];                                                                 //- ���ܻ�IP��ַ
  char t_port[2];                                                               //- ���ܻ��˿�
} st_jmj;

//--------+---------+---------+---------+---------+---------+---------+---------
//- ˵��: ��־��Ϣ
//- ����: info                [I ]��־��Ϣ
//- ����: ��
//--------+---------+---------+---------+---------+---------+---------+---------
void LOG_INFO(char* info)
{
//  LogAdd(LOG_FILE_NAME, info);
}


//--------+---------+---------+---------+---------+---------+---------+---------
//- ˵��: TCP/IP���ݶ�ȡ����
//- ����: sckt                [I ]��ȡ���ݵ�SOCKET
//-       len                 [I ]��Ҫ���������ݳ���
//-       tm                  [I ]�涨����ѭ������
//-       itvl                [I ]ÿ�ζ�����ʱ����( ���� )
//-       cnt                 [ O]��������
//- ����:        0  �ɹ�����
//-       XXXXXXXX  �������
//-          10000  ���ӹر�
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
//- ˵��: TCP/IP���ݷ��ͺ���
//- ����: sckt                [I ]�������ݵ�SOCKET
//-       len                 [I ]��Ҫ���͵����ݳ���
//-       tm                  [I ]�涨����ѭ������
//-       itvl                [I ]ÿ�η��͵�ʱ����( ���� )
//-       cnt                 [I ]��������
//- ����:        0  �ɹ�����
//-       XXXXXXXX  �������
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
//- ˵��: ͨ��ָ��_��ü��ܻ���Ȩ
//- ����: ��
//- ����:        0  �ɹ�
//-            ��0  ʧ��
//--------+---------+---------+---------+---------+---------+---------+---------
void cmd_0001(void)
{
  int i ;

  struct ST_CMD_IN_DATA                                                         //- �������ݽṹ
  {
    char t_ip[4];                                                               //- ���ܻ�IP��ַ
    char t_port[2];                                                             //- ���ܻ��˿�
    char pin[4];                                                                //- ��Ȩ pin
    char key_ver[1];                                                            //- ���ܻ���Կ�汾

    unsigned long ln;                                                           //- �������ݳ���
  } si;

  struct ST_CMD_OT_DATA                                                         //- ������ݽṹ
  {
    unsigned long ln;                                                           //- �������ݳ���
  } so;

  si.ln = sizeof(struct ST_CMD_IN_DATA) - 4;
  so.ln = sizeof(struct ST_CMD_OT_DATA) - 4;

  if(inda.ln != si.ln + 4)                                                      //- ָ����ж�
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

  memcpy(&otda.data[0], &so, so.ln);                                            //- �������
  otda.ln += so.ln;
}







//--------+---------+---------+---------+---------+---------+---------+---------
//- ˵��: ��ȡ��Կ
//- ����: ot_d                [IO]����
//-       key_id              [I ]��Կ����
//-       div_lv              [I ]��ɢ����
//-       div                 [I ]��ɢ����
//-       in_d                [I ]����
//-       in_d_ln             [I ]���ĳ���(������ 8�ı���)
//-       sck                 [I ]ͨѶsocket
//- ����: 0:�ɹ�    ��0:ʧ��
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
  memcpy(&in[p], JMJ_HAED, JMJ_HAED_LN);                                        //- 12�ֽ�ͷ
  p += JMJ_HAED_LN;
  memcpy(&in[p], "U1", 2);                                                      //- 2�ֽ��������
  p += 2;
  memcpy(&in[p], "X", 1);                                                       //- 1�ֽ��㷨��ʶ
  p += 1;
  memcpy(&in[p], "0", 1);                                                       //- 1�ֽڼ���ģʽ��ʶ
  p += 1;
  memcpy(&in[p], "01", 2);                                                      //- 2�ֽڷ���ID
  p += 2;
  memcpy(&in[p], "109", 3);                                                     //- 3�ֽڸ���Կ����
  p += 3;
  sprintf(&in[p], "K%03X", key_id);                                             //- 4�ֽ���Կ����
  p += 4;
  sprintf(&in[p], "%01X", div_lv);                                              //- 1�ֽ���ɢ����
  p += 1;
  if(div_lv != 0)
  {
    Convert(0, div_lv * 8, div, &in[p]);
	p += div_lv * 8 * 2;
  }
  memcpy(&in[p], "01", 2);                                                      //- 2�ֽ���������ʶ
  p += 2;
  sprintf(&in[p], "%03d", in_d_ln);                                             //- 3�ֽ����ݳ���
  p += 3;
  Convert(0, in_d_ln, in_d, &in[p]);                                            //- ����
  p += in_d_ln * 2;

  in_ln = p - 2;

  in[0] = (char)((in_ln & 0xFF00) >> 8);
  in[1] = (char)(in_ln & 0xFF);

  LOG_INFO(&in[2 + 12]);

  rt = send_wait(sck, in_ln + 2, 5, 0, in);                                     //- ��������
  if(rt != 0)
  {
    return SEND_ERROR;
  }
  rt = recv_wait(sck, 2, 100, 10, ot_ln_c);                                     //- ������Ӧ����
  if(rt != 0)
  {
    return RECV_ERROR;
  }
  ot_ln = (unsigned char)ot_ln_c[0];
  ot_ln = (ot_ln << 8) + ((unsigned char)ot_ln_c[1]);
  rt = recv_wait(sck, ot_ln, 5, 0, ot);                                         //- ������Ӧ
  if(rt != 0)
  {
    return RECV_ERROR;
  }

  LOG_INFO(&ot[12]);

  if((ot[JMJ_HAED_LN + 2] != '0' ) && (ot[JMJ_HAED_LN + 3] != '0' ))
  {
    return STAT_ERROR;
  }

  Convert(1, in_d_ln,  &ot[JMJ_HAED_LN + 7], ot_d);                             //- ��������ǰ�� 3�ֽڳ���

  return 0;
}











//--------+---------+---------+---------+---------+---------+---------+---------
//- ˵��: ͨ��ָ��_��ÿ���Կ
//- ����: ��
//- ����:        0  �ɹ�
//-            ��0  ʧ��
//--------+---------+---------+---------+---------+---------+---------+---------
void cmd_0002(void)
{
  int i;
  int rt ;

  struct ST_CMD_IN_DATA                                                         //- �������ݽṹ
  {
    char logic_no[8];                                                           //- �û����߼�����

    unsigned long ln;                                                           //- �������ݳ���
  } si;

  struct ST_CMD_OT_DATA                                                         //- ������ݽṹ
  {
    char DCCK[16];                                                              //- ��Ƭ������Կ
    char DCMK[16];                                                              //- ��Ƭά����Կ
    char DCEAK[16];                                                             //- �ⲿ��֤��Կ

    char DACK[16];                                                              //- Ӧ��������Կ��DACK��
    char DAMK[16];                                                              //- Ӧ��ά����Կ��DAMK��
    char DPK[16];                                                               //- ������Կ��DPK��
    char DLK[16];                                                               //- Ȧ����Կ��DLK��
    char DTK[16];                                                               //- ������֤TAC ��Կ��DTK��
    char DAMK01[16];                                                            //- Ӧ��ά����Կ01��DAMK01��
    char DAMK02[16];                                                            //- Ӧ��ά����Կ02��DAMK02��
    char DABK[16];                                                              //- Ӧ��������Կ��DABK��
    char DAUK[16];                                                              //- Ӧ�ý�����Կ��DAUK��
    char DPUK[16];                                                              //- PIN ������Կ��DPUK��
    char DPRK[16];                                                              //- PIN ��װ��Կ��DPRK��
    char DUK[16];                                                               //- �޸�͸֧�޶���Կ��DUK��
    char DULK[16];                                                              //- Ȧ����Կ��Կ��DULK��
    char DAEAK[16];                                                             //- �ⲿ��֤��Կ��DAEAK��


    unsigned long ln;                                                           //- �������ݳ���
  } so;

  char div_d[16];                                                               //- ���ŷ�ɢ


  SOCKET  ssock;                                                                //- client socket
  WSADATA wsadata;                                                              //- WSA data
  struct  protoent *ppe;                                                        //- protoent data
  struct  sockaddr_in sin;                                                      //- sock address data

  int df;                                                                       //- Ӧ�����

  char Server_IP_Address[16];
  unsigned short Server_Port;


  if(dll_pin_ok != 0)
  {
    otda.ass = IN_DATA_ERR;
    return;
  }

  si.ln = sizeof(struct ST_CMD_IN_DATA) - 4;
  so.ln = sizeof(struct ST_CMD_OT_DATA) - 4;

  if(inda.ln != si.ln + 4)                                                      //- ָ����ж�
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

                                                                                //- ���������Ϣ
  sprintf(Server_IP_Address, "%d.%d.%d.%d", (unsigned char)st_jmj.t_ip[0],
    (unsigned char)st_jmj.t_ip[1], (unsigned char)st_jmj.t_ip[2],
    (unsigned char)st_jmj.t_ip[3]);
  Server_Port = (((unsigned char)st_jmj.t_port[0]) << 8 );
  Server_Port += (unsigned char)st_jmj.t_port[1];
                                                                                //- ��ʼ��
  if(WSAStartup(MAKEWORD(2, 2), &wsadata) != 0)
  {
    otda.ass = SCKT_ERROR;
    return;
  }

  memset(&sin, 0, sizeof(sin));

  sin.sin_family    = AF_INET;
  sin.sin_addr.s_addr = inet_addr(Server_IP_Address);
  sin.sin_port    = htons((u_short) Server_Port);
                                                                                //- ����socket
  ppe = getprotobyname("tcp");
  ssock = socket(PF_INET,SOCK_STREAM, ppe->p_proto);
  if(ssock == INVALID_SOCKET)
  {
    otda.ass = SCKT_ERROR;
    return;
  }
                                                                                //- ��������
  if(connect(ssock, (struct sockaddr*)(&sin), sizeof(sin)) !=0)
  {
    otda.ass = SCKT_ERROR;
    return;
  }
  //ioctlsocket(ssock, FIONBIO, &p);

                                                                                //- ��ÿ�Ƭ������Կ
  rt = get_key(so.DCCK, g_cm_ver * 256 +   1, 2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DACK_ERR;)
                                                                                //- ��ÿ�Ƭά����Կ
  rt = get_key(so.DCMK, g_cm_ver * 256 +   2, 2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DCMK_ERR;)
                                                                                //- ����ⲿ��֤��Կ
  rt = get_key(so.DCEAK,g_cm_ver * 256 +   6, 2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DCEAK_ERR;)


  df = 0;

                                                                                //- ���Ӧ��������Կ
  rt = get_key(so.DACK,   g_cm_ver * 256 + 8 + df * 24 +  1,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DACK_ERR;)
                                                                                //- ���Ӧ��ά����Կ
  rt = get_key(so.DAMK,   g_cm_ver * 256 + 8 + df * 24 +  2,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DAMK_ERR;)
                                                                                //- ���������Կ
  rt = get_key(so.DPK,    g_cm_ver * 256 + 8 + df * 24 +  21,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DPK_ERR;)
                                                                                //- ���Ȧ����Կ
  rt = get_key(so.DLK,    g_cm_ver * 256 + 8 + df * 24 +  15,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DLK_ERR;)
                                                                                //- ��ý�����֤TAC ��Կ
  rt = get_key(so.DTK,    g_cm_ver * 256 + 8 + df * 24 +  23,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DTK_ERR;)
                                                                                //- ���Ӧ��ά����Կ01
  rt = get_key(so.DAMK01, g_cm_ver * 256 + 8 + df * 24 +   8,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass =GET_KEY_DAMK01_ERR;)
                                                                                //- ���Ӧ��ά����Կ02
  rt = get_key(so.DAMK02, g_cm_ver * 256 + 8 + df * 24 +   9,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass =GET_KEY_DAMK02_ERR;)
                                                                                //- ���Ӧ��������Կ
  rt = get_key(so.DABK,   g_cm_ver * 256 + 8 + df * 24 +   3,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DABK_ERR;)
                                                                                //- ���Ӧ�ý�����Կ
  rt = get_key(so.DAUK,   g_cm_ver * 256 + 8 + df * 24 +   4,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DAUK_ERR;)
                                                                                //- ���PIN ������Կ
  rt = get_key(so.DPUK,   g_cm_ver * 256 + 8 + df * 24 +   6,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DPUK_ERR;)
                                                                                //- ���PIN ��װ��Կ
  rt = get_key(so.DPRK,   g_cm_ver * 256 + 8 + df * 24 +   5,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DPRK_ERR;)
                                                                                //- ����޸�͸֧�޶���Կ
  rt = get_key(so.DUK,    g_cm_ver * 256 + 8 + df * 24 +  19,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DUK_ERR;)
                                                                                //- ���Ȧ����Կ��Կ
  rt = get_key(so.DULK,   g_cm_ver * 256 + 8 + df * 24 +  17,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DULK_ERR;)
                                                                                //- ����ⲿ��֤��Կ
  rt = get_key(so.DAEAK,  g_cm_ver * 256 + 8 + df * 24 +  24,
      2, sys_div, div_d, 16, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_KEY_DAEAK_ERR;)

  closesocket(ssock);


  memcpy(&otda.data[0], &so, so.ln);                                            //- �������
  otda.ln += so.ln;

}


//--------+---------+---------+---------+---------+---------+---------+---------
//- ˵��: ͨ��ָ��_��õ���Ʊ��Կ
//- ����: ��
//- ����:        0  �ɹ�
//-            ��0  ʧ��
//--------+---------+---------+---------+---------+---------+---------+---------
void cmd_0003(void)
{
  int rt;

  char tmp_d[16] = {0};
  char tmp_e[16] = {0};

  struct ST_CMD_IN_DATA                                                         //- �������ݽṹ
  {
    char fix_no[4];                                                             //- ��������
    char logic_no[4];                                                           //- �߼�����

    unsigned long ln;                                                           //- �������ݳ���
  } si;

  struct ST_CMD_OT_DATA                                                         //- ������ݽṹ
  {
    char mac[4];                                                                //- ��֤mac
    char key[6];                                                                //- ��֤��Կ

    unsigned long ln;                                                           //- �������ݳ���
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

  if(inda.ln != si.ln + 4)                                                      //- ָ����ж�
  {
    otda.ass = IN_DATA_ERR;
    return;
  }

  memcpy(&si, inda.data, si.ln);

                                                                                //- ���������Ϣ
  sprintf(Server_IP_Address, "%d.%d.%d.%d", (unsigned char)st_jmj.t_ip[0],
    (unsigned char)st_jmj.t_ip[1], (unsigned char)st_jmj.t_ip[2],
    (unsigned char)st_jmj.t_ip[3]);
  Server_Port = (((unsigned char)st_jmj.t_port[0]) << 8 );
  Server_Port += (unsigned char)st_jmj.t_port[1];
                                                                                //- ��ʼ��
  if(WSAStartup(MAKEWORD(2, 2), &wsadata) != 0)
  {
    otda.ass = SCKT_ERROR;
    return;
  }

  memset(&sin, 0, sizeof(sin));

  sin.sin_family    = AF_INET;
  sin.sin_addr.s_addr = inet_addr(Server_IP_Address);
  sin.sin_port    = htons((u_short) Server_Port);
                                                                                //- ����socket
  ppe = getprotobyname("tcp");
  ssock = socket(PF_INET,SOCK_STREAM, ppe->p_proto);
  if(ssock == INVALID_SOCKET)
  {
    otda.ass = SCKT_ERROR;
    return;
  }
                                                                                //- ��������
  if(connect(ssock, (struct sockaddr*)(&sin), sizeof(sin)) !=0)
  {
    otda.ass = SCKT_ERROR;
    return;
  }

                                                                                //- ��� mac
  memset(tmp_e, 0, 8);
  rt = get_key(tmp_e,  g_cm_ver * 256 + 200 +   1,
      2, sys_div, &si.fix_no[0], 8, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_SJT_MAC_ERR;)
  memcpy(&so.mac[0], tmp_e, 4);
                                                                                //- ��� key
  memset(tmp_e, 0, 8);
  memcpy(tmp_d, &si.fix_no[2], 6);
  tmp_d[6] = so.mac[0];
  tmp_d[7] =(char)0xA5;
  rt = get_key(tmp_e,  g_cm_ver * 256 + 200 +   4,
      2, sys_div, tmp_d, 8, ssock);
  JUDGE_CLOSE_RETURN(rt != 0, closesocket(ssock); otda.ass = GET_SJT_MAC_ERR;)
  memcpy(&so.key[0], tmp_e, 6);

  closesocket(ssock);

  memcpy(&otda.data[0], &so, so.ln);                                            //- �������
  otda.ln += so.ln;
}


//---------+---------+---------+---------+---------+---------+---------+--------
//- ˵��: Ӧ���豸�����ӿ�
//- ����: In                    [I ]��������
//-       Ot                    [ O]�������
//- ����: 0                     �ɹ�
//-       ��0                   ʧ��
//---------+---------+---------+---------+---------+---------+---------+--------
DWORD WINAPI get_card_key_op(char* In, char* Ot)
{
  CLR_IN_OUT_DATA                                                               //- �����ʼ��

  if(strlen(In) % 2 != 0)
  {
    otda.ass = IN_DATA_ERR;
  }
  else
  {
    inda.ln = strlen(In) >> 1;
    if(Convert(1, inda.ln, In, (char*)&inda.ver) != 0)                          //- ���ݻ�ȡ
    {
      otda.ass = IN_DATA_ERR;
    }
  }
  otda.ver = inda.ver;                                                          //- �������ݳ�ʼ��
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
    if(otda.card == 0x00)                                                       //- ͨ��ָ��
    {
      switch(inda.cmd)
      {
        case 0x01:                                                              //- �����Ȩ
          cmd_0001();
          break;

        case 0x02:                                                              //- ��ÿ���Կ
          cmd_0002();
          break;

        case 0x03:                                                              //- ��õ���Ʊ��Կ
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