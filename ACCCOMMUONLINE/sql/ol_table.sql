create table W_OL_TRD_MAX_HDLTIME
(
  msg_id       VARCHAR2(10) not null,
  max_hdl_time INTEGER not null,
  remark       VARCHAR2(50) not null
);
alter table W_OL_TRD_MAX_HDLTIME
  add constraint PK_W_OL_TRD_MAX_HDLTIME primary key (MSG_ID);
create table W_OL_TRD_MONITOR
(
  thread_id      VARCHAR2(50) not null,
  thread_name    VARCHAR2(50),
  thread_status  VARCHAR2(2),
  msg_id         VARCHAR2(10),
  msg_name       VARCHAR2(50),
  hdl_time_start VARCHAR2(20),
  hdl_end_time   VARCHAR2(20),
  remark         VARCHAR2(50)
);
alter table W_OL_TRD_MONITOR
  add constraint PK_W_OL_TRD_MONITOR primary key (THREAD_ID);
create table W_OL_TRD_MONITOR_HIS
(
  water_no       NUMBER(18) not null,
  reason_id      VARCHAR2(1) not null,
  dump_date      DATE not null,
  thread_id      VARCHAR2(50) not null,
  thread_name    VARCHAR2(50),
  thread_status  VARCHAR2(2),
  msg_id         VARCHAR2(10),
  msg_name       VARCHAR2(50),
  hdl_time_start VARCHAR2(20),
  hdl_end_time   VARCHAR2(20),
  remark         VARCHAR2(50)
);
alter table W_OL_TRD_MONITOR_HIS
  add constraint PK_W_OL_TRD_MONITOR_HIS primary key (WATER_NO);
create table W_OL_TRD_MSG_DEL
(
  water_no   NUMBER(18) not null,
  del_date   DATE not null,
  thread_id  VARCHAR2(50) not null,
  queue_type VARCHAR2(1) not null,
  msg_id     VARCHAR2(2) not null,
  message    BLOB not null,
  remark     VARCHAR2(50)
);
alter table W_OL_TRD_MSG_DEL
  add constraint PK_W_OL_TRD_MSG_DEL primary key (WATER_NO);
create table W_OL_TRD_MSG_HANDUP_NUM
(
  water_no     NUMBER(18) not null,
  msg_id       VARCHAR2(2) not null,
  hand_up_num  INTEGER not null,
  hand_up_date DATE not null,
  remark       VARCHAR2(50) not null
);
alter table W_OL_TRD_MSG_HANDUP_NUM
  add constraint PK_W_OL_TRD_MSG_HANDUP_NUM primary key (WATER_NO);
create table W_OL_TRD_RESET
(
  reason_id                VARCHAR2(1),
  dump_date                VARCHAR2(20),
  thread_id                VARCHAR2(50),
  thread_name              VARCHAR2(50),
  thread_status            VARCHAR2(2),
  thread_queue_pri_num     INTEGER,
  thread_queue_ord_num     INTEGER,
  thread_id_new            VARCHAR2(50),
  thread_name_new          VARCHAR2(50),
  thread_status_new        VARCHAR2(2),
  thread_queue_pri_num_new INTEGER,
  thread_queue_ord_num_new INTEGER,
  msg_id                   VARCHAR2(10),
  msg_name                 VARCHAR2(50),
  hdl_time_start           VARCHAR2(20),
  hdl_time_end             VARCHAR2(20),
  remark                   VARCHAR2(50),
  message                  VARCHAR2(50)
);
create table W_OL_CHG_ACTIVATION
(
  water_no       NUMBER(18) not null,
  message_id     VARCHAR2(2),
  termina_no     VARCHAR2(9),
  sam_logical_id VARCHAR2(16),
  random_num     VARCHAR2(18),
  mac            VARCHAR2(16),
  return_code    VARCHAR2(2),
  err_code       VARCHAR2(2),
  insert_date    DATE,
  update_date    DATE,
  sys_ref_no     NUMBER(18),
  msg_gen_time1  VARCHAR2(14),
  msg_gen_time2  VARCHAR2(14)
);
comment on column W_OL_CHG_ACTIVATION.insert_date
  is '激活请求时间';
comment on column W_OL_CHG_ACTIVATION.update_date
  is '激活响应时间';
comment on column W_OL_CHG_ACTIVATION.msg_gen_time1
  is '请求消息中的消息生成时间';
comment on column W_OL_CHG_ACTIVATION.msg_gen_time2
  is '响应消息中消息生成时间';
alter table W_OL_CHG_ACTIVATION
  add constraint PK_W_OL_CHG_ACTIVATION primary key (WATER_NO);
create table W_OL_CHG_PLUS
(
  water_no         NUMBER(18) not null,
  message_id       VARCHAR2(2),
  termina_no       VARCHAR2(9),
  sam_logical_id   VARCHAR2(16),
  transation_seq   NUMBER(18),
  branches_code    VARCHAR2(16),
  pub_main_code    VARCHAR2(4),
  pub_sub_code     VARCHAR2(4),
  card_type        VARCHAR2(4),
  tk_logic_no      VARCHAR2(20),
  tk_phy_no        VARCHAR2(20),
  is_test_tk       VARCHAR2(1),
  onl_tran_times   NUMBER(18),
  offl_tran_times  NUMBER(18),
  buss_type        VARCHAR2(2),
  value_type       VARCHAR2(1),
  charge_fee       NUMBER(18),
  balance          NUMBER(18),
  mac1             VARCHAR2(8),
  tk_chge_seq      VARCHAR2(8),
  last_tran_termno VARCHAR2(16),
  last_tran_time   VARCHAR2(14),
  operator_id      VARCHAR2(6),
  mac2             VARCHAR2(8),
  sys_date         VARCHAR2(14),
  return_code      VARCHAR2(2),
  err_code         VARCHAR2(2),
  write_rslt       VARCHAR2(1),
  status           VARCHAR2(1),
  insert_date      DATE,
  update_date      DATE,
  sys_ref_no       NUMBER(18),
  msg_gen_time1    VARCHAR2(14),
  msg_gen_time2    VARCHAR2(14)
);
comment on column W_OL_CHG_PLUS.card_type
  is '主类型代码＋子类型代码';
comment on column W_OL_CHG_PLUS.is_test_tk
  is '0：正式票卡  1：测试票卡';
comment on column W_OL_CHG_PLUS.buss_type
  is '固定填写‘14’';
comment on column W_OL_CHG_PLUS.value_type
  is '0：无值类型；1：金额；2：次数；3：天数；';
comment on column W_OL_CHG_PLUS.write_rslt
  is '0：写卡成功．1：写卡失败．2：未知状态。';
comment on column W_OL_CHG_PLUS.status
  is '0:申请；1：响应；';
comment on column W_OL_CHG_PLUS.insert_date
  is '充值请求、充值确认的数据入库时间';
comment on column W_OL_CHG_PLUS.update_date
  is '充值请求响应、充值确认响应的数据更新时间';
comment on column W_OL_CHG_PLUS.msg_gen_time1
  is '请求消息中的消息生成时间，YYYYMMDD HH24MISS';
comment on column W_OL_CHG_PLUS.msg_gen_time2
  is '响应消息中消息生成时间，YYYYMMDD HH24MISS';
alter table W_OL_CHG_PLUS
  add constraint PK_W_OL_CHG_PLUS primary key (WATER_NO);
create table W_OL_CHG_SUB
(
  water_no         NUMBER(18) not null,
  message_id       VARCHAR2(2),
  termina_no       VARCHAR2(9),
  sam_logical_id   VARCHAR2(16),
  transation_seq   NUMBER(18),
  branches_code    VARCHAR2(16),
  pub_main_code    VARCHAR2(4),
  pub_sub_code     VARCHAR2(4),
  card_type        VARCHAR2(4),
  tk_logic_no      VARCHAR2(20),
  tk_phy_no        VARCHAR2(20),
  is_test_tk       VARCHAR2(1),
  onl_tran_times   NUMBER(18),
  offl_tran_times  NUMBER(18),
  buss_type        VARCHAR2(2),
  value_type       VARCHAR2(1),
  charge_fee       NUMBER(18),
  balance          NUMBER(18),
  last_tran_termno VARCHAR2(16),
  last_trantime    VARCHAR2(14),
  operator_id      VARCHAR2(6),
  tac              VARCHAR2(8),
  sys_date         VARCHAR2(14),
  return_code      VARCHAR2(2),
  err_code         VARCHAR2(2),
  write_rslt       VARCHAR2(1),
  status           VARCHAR2(1),
  insert_date      DATE,
  update_date      DATE,
  sys_ref_no       NUMBER(18),
  msg_gen_time1    VARCHAR2(14),
  msg_gen_time2    VARCHAR2(14)
);
comment on column W_OL_CHG_SUB.status
  is '0:申请；1：响应；';
comment on column W_OL_CHG_SUB.insert_date
  is '充值撤销请求、充值撤销确认的数据入库时间';
comment on column W_OL_CHG_SUB.update_date
  is '充值撤销响应、充值撤销确认响应的数据更新时间';
comment on column W_OL_CHG_SUB.msg_gen_time1
  is '请求消息中的消息生成时间，YYYYMMDD HH24MISS';
comment on column W_OL_CHG_SUB.msg_gen_time2
  is '响应消息中消息生成时间，YYYYMMDD HH24MISS';
alter table W_OL_CHG_SUB
  add constraint PK_W_OL_CHG_SUB primary key (WATER_NO);
create table W_OL_LOG_COMMU
(
  id           NUMBER(18) not null,
  message_id   CHAR(4),
  message_name VARCHAR2(30),
  message_from VARCHAR2(20),
  start_time   VARCHAR2(20),
  end_time     VARCHAR2(20),
  use_time     VARCHAR2(10),
  result       VARCHAR2(1),
  hdl_thread   VARCHAR2(20),
  sys_level    VARCHAR2(3),
  remark       VARCHAR2(100),
  insert_date  DATE
);
comment on table W_OL_LOG_COMMU
  is '通信日志';
comment on column W_OL_LOG_COMMU.result
  is '0：成功，1：失败，2：警告';
alter table W_OL_LOG_COMMU
  add constraint PK_W_OL_LOG_COMMU primary key (ID);
create table W_OL_LOG_CONNECT
(
  id               NUMBER(18) not null,
  connect_datetime DATE,
  connect_ip       VARCHAR2(15),
  connect_result   CHAR(1),
  remark           VARCHAR2(100)
);
comment on table W_OL_LOG_CONNECT
  is '连接日志表';
comment on column W_OL_LOG_CONNECT.connect_result
  is '0：连接成功，1：连接失败，2:连接关闭';
alter table W_OL_LOG_CONNECT
  add constraint PK_W_OL_LOG_CONNECT primary key (ID);
create table W_OL_LOG_RECV_SEND
(
  id           NUMBER(18) not null,
  datetime_rec DATE,
  ip           VARCHAR2(15),
  type         CHAR(1),
  message_code CHAR(2),
  message_sequ CHAR(22),
  message      BLOB,
  result       CHAR(2)
);
comment on table W_OL_LOG_RECV_SEND
  is '记录接收到终端的数据，以及发送给终端的数据';
comment on column W_OL_LOG_RECV_SEND.ip
  is '消息发送方的IP地址';
comment on column W_OL_LOG_RECV_SEND.type
  is '0:请求消息、1：响应消息';
comment on column W_OL_LOG_RECV_SEND.result
  is '0：成功
1：失败';
alter table W_OL_LOG_RECV_SEND
  add constraint PK_W_OL_LOG_RECV_SEND primary key (ID);
create table W_OL_LOG_SYS_ERROR
(
  id          NUMBER(18) not null,
  ip          VARCHAR2(15),
  excp_id     VARCHAR2(4),
  excp_type   VARCHAR2(4),
  class_name  VARCHAR2(100),
  excp_desc   VARCHAR2(300),
  insert_date DATE
);
comment on table W_OL_LOG_SYS_ERROR
  is '记录系统运行中的异常信息';
comment on column W_OL_LOG_SYS_ERROR.ip
  is '消息发送方的IP地址';
comment on column W_OL_LOG_SYS_ERROR.excp_id
  is '异常信息对应的id标识';
comment on column W_OL_LOG_SYS_ERROR.excp_type
  is '1：解析消息包出错
2：连接异常
3：消息处理异常
4：数据库异常
5：加密机异常';
comment on column W_OL_LOG_SYS_ERROR.excp_desc
  is '详细的异常信息';
alter table W_OL_LOG_SYS_ERROR
  add constraint PK_W_OL_LOG_SYS_ERROR primary key (ID);
create table W_OL_CHG_LOG_TEST
(
  insert_time DATE,
  msg         VARCHAR2(100)
);
-- Add comments to the table 
comment on table W_OL_CHG_LOG_TEST
  is '充值撤销检验记录表';
create table W_OL_PUB_FLAG
(
  type        VARCHAR2(50),
  code        VARCHAR2(50),
  code_text   VARCHAR2(50),
  description VARCHAR2(100)
);
comment on table W_OL_PUB_FLAG
  is '在线充值配置表';
comment on column W_OL_PUB_FLAG.type
  is '类型';
comment on column W_OL_PUB_FLAG.code
  is '代码';
comment on column W_OL_PUB_FLAG.code_text
  is '值';
comment on column W_OL_PUB_FLAG.description
  is '类型描述';
create table W_OL_SYS_VERSION
(
  version_no  VARCHAR2(10) not null,
  operator_id VARCHAR2(10),
  valid_date  CHAR(10),
  del_desc    VARCHAR2(255),
  update_desc VARCHAR2(255),
  add_desc    VARCHAR2(255),
  note        VARCHAR2(255)
);
alter table W_OL_SYS_VERSION
  add constraint PK_W_OL_SYS_VERSION primary key (VERSION_NO);
comment on table W_OL_SYS_VERSION
  is '系统版本记录表'; 
  
-- Create table
create table W_OL_QRCODE_AFC
(
  water_no         NUMBER(18) not null,
  message_id       VARCHAR2(2),--消息类型
  msg_gen_time     VARCHAR2(14),--消息生成时间
  termina_no       VARCHAR2(9),--终端编号
  sam_logical_id   VARCHAR2(16),--sam卡逻辑号
  termina_seq      NUMBER(18),--终端处理流水
  acc_seq          NUMBER(18),--中心处理流水
  qrcode           varchar2(64),--二维码
  return_code      VARCHAR2(2),--响应码
  order_no         varchar2(14),--订单号
  phone_no         varchar2(11),--手机号
  sale_fee         NUMBER(18),--发售单程票单价
  sale_times       NUMBER(18),--发售单程票数量
  deal_fee         NUMBER(18),--发售单程票总价
  result_code      varchar2(2),--订单执行结果
  insert_date      DATE--插入时间
);
-- Add comments to the columns 
comment on column W_OL_QRCODE_AFC.message_id
  is '消息类型';
comment on column W_OL_QRCODE_AFC.msg_gen_time
  is '消息生成时间';
comment on column W_OL_QRCODE_AFC.termina_no
  is '终端编号';
comment on column W_OL_QRCODE_AFC.sam_logical_id
  is 'sam卡逻辑号';
comment on column W_OL_QRCODE_AFC.termina_seq
  is '终端处理流水';
comment on column W_OL_QRCODE_AFC.acc_seq
  is '中心处理流水';
comment on column W_OL_QRCODE_AFC.qrcode
  is '二维码';
comment on column W_OL_QRCODE_AFC.return_code
  is '响应码:00:订单未执行01:订单部分完成80:未购票或不存在81:订单已完成(已取票)82:二维码已过有效期83:购票、取票始发站不一致当响应码非00、01时，订单号、手机号、发售单程票价等字段值填默认(0)返回';
comment on column W_OL_QRCODE_AFC.order_no
  is '订单号:8位日期+6位流水';
comment on column W_OL_QRCODE_AFC.phone_no
  is '手机号';
  comment on column W_OL_QRCODE_AFC.sale_fee
  is '发售单程票单价(分)';
comment on column W_OL_QRCODE_AFC.sale_times
  is '发售单程票数量';
comment on column W_OL_QRCODE_AFC.deal_fee
  is '发售单程票总价(分)';
comment on column W_OL_QRCODE_AFC.result_code
  is '订单执行结果:00:订单完整成功执行01:订单部分成功执行10:无票11:写卡失败12:设备故障99:其他故障';
comment on column W_OL_QRCODE_AFC.insert_date
  is '插入时间';
-- Create/Recreate primary, unique and foreign key constraints 
--alter table W_OL_QRCODE_AFC
--  add constraint PK_W_OL_CHG_QRCODE primary key (WATER_NO);

-- Create table
create table W_OL_QRCODE_ORDER
(
  water_no         NUMBER(18) not null,
  order_no         VARCHAR2(14),
  phone_no         VARCHAR2(11),
  sale_fee         NUMBER(18),
  sale_times       NUMBER(18),
  deal_fee         NUMBER(18),
  status           CHAR(1),
  update_date      DATE,
  insert_date      DATE,
  start_station    CHAR(4),
  end_station      CHAR(4),
  sale_fee_total   NUMBER(18),
  sale_times_total NUMBER(18),
  deal_fee_total   NUMBER(18),
  ticket_status    CHAR(2),
  qrcode           VARCHAR2(64),
  valid_time       DATE,
  tkcode           VARCHAR2(32),
  lock_dev         VARCHAR2(9)
);
-- Add comments to the table 
comment on table W_OL_QRCODE_ORDER
  is '二维码订单表';
-- Add comments to the columns 
comment on column W_OL_QRCODE_ORDER.order_no
  is '订单号:8位日期+6位流水';
comment on column W_OL_QRCODE_ORDER.phone_no
  is '手机号';
comment on column W_OL_QRCODE_ORDER.sale_fee
  is '已发售单程票单价(分)';
comment on column W_OL_QRCODE_ORDER.sale_times
  is '已发售单程票数量';
comment on column W_OL_QRCODE_ORDER.deal_fee
  is '已发售单程票总价(分)';
comment on column W_OL_QRCODE_ORDER.status
  is '0:已支付1:未支付';
comment on column W_OL_QRCODE_ORDER.update_date
  is '更新时间';
comment on column W_OL_QRCODE_ORDER.insert_date
  is '插入时间';
comment on column W_OL_QRCODE_ORDER.start_station
  is '始发站(线路+车站)';
comment on column W_OL_QRCODE_ORDER.end_station
  is '终点站(线路+车站)';
comment on column W_OL_QRCODE_ORDER.sale_fee_total
  is '发售单程票单价(分)';
comment on column W_OL_QRCODE_ORDER.sale_times_total
  is '发售单程票数量';
comment on column W_OL_QRCODE_ORDER.deal_fee_total
  is '发售单程票总价(分)';
comment on column W_OL_QRCODE_ORDER.ticket_status
  is '00:订单未执行;01:订单部分完成;02:订单取消;80:未购票或不存在;81:订单已完成(已取票);82:二维码已过有效期;83:购票、取票始发站不一致;84:订单锁定';
comment on column W_OL_QRCODE_ORDER.qrcode
  is '二维码';
comment on column W_OL_QRCODE_ORDER.valid_time
  is '二维码有效时间';
comment on column W_OL_QRCODE_ORDER.tkcode
  is '取票码(二维码加密前)';
comment on column W_OL_QRCODE_ORDER.lock_dev
  is '锁定终端编号';
-- Create/Recreate primary, unique and foreign key constraints 
alter table W_OL_QRCODE_ORDER
  add constraint PK_W_OL_QRCODE_ORDER primary key (WATER_NO);
  
  
-- Create table
create table W_OL_QRPAY_ORDER
(
  water_no         NUMBER(18) not null,
  order_no         VARCHAR2(14),
  sale_fee         NUMBER(18),
  sale_times       NUMBER(18),
  deal_fee         NUMBER(18),
  status           CHAR(1),
  order_date       DATE,
  insert_date      DATE,
  card_type        CHAR(4),
  qrpay_id         CHAR(10),
  qrpay_data       CHAR(34),
  phone_no         VARCHAR2(11),
  pay_date         DATE,
  pay_status       CHAR(2),
  pay_channel_type CHAR(2),
  pay_channel_code CHAR(4),
  sale_fee_total   NUMBER(18),
  sale_times_total NUMBER(18),
  deal_fee_total   NUMBER(18),
  acc_seq          NUMBER(18),
  order_ip         VARCHAR2(15),
  card_type_total  CHAR(4)
);
-- Add comments to the table 
comment on table W_OL_QRPAY_ORDER
  is '支付二维码订单表';
-- Add comments to the columns 
comment on column W_OL_QRPAY_ORDER.order_no
  is '订单号:8位日期+6位流水';
comment on column W_OL_QRPAY_ORDER.sale_fee
  is '出票单程票单价(分)';
comment on column W_OL_QRPAY_ORDER.sale_times
  is '出票售单程票数量';
comment on column W_OL_QRPAY_ORDER.deal_fee
  is '出票售单程票总价(分)';
comment on column W_OL_QRPAY_ORDER.status
  is '0:未支付1:已支付2:订单取消';
comment on column W_OL_QRPAY_ORDER.order_date
  is '订单生成时间';
comment on column W_OL_QRPAY_ORDER.insert_date
  is '插入时间';
comment on column W_OL_QRPAY_ORDER.card_type
  is '出票票卡类型(主类型+子类型)';
comment on column W_OL_QRPAY_ORDER.qrpay_id
  is '支付标识';
comment on column W_OL_QRPAY_ORDER.qrpay_data
  is '支付二维码信息';
comment on column W_OL_QRPAY_ORDER.phone_no
  is '支付手机号';
comment on column W_OL_QRPAY_ORDER.pay_date
  is '支付时间';
comment on column W_OL_QRPAY_ORDER.pay_status
  is '支付结果 00:支付成功;01:余额不足;02:黑名单账户;10:支付通道通讯异常;99:其他异常';
comment on column W_OL_QRPAY_ORDER.pay_channel_type
  is '支付渠道类型 01:银行;02:银联;03:微信支付;04:支付宝支付;09:其他第三方支付99:其他';
comment on column W_OL_QRPAY_ORDER.pay_channel_code
  is '支付渠道代码';
comment on column W_OL_QRPAY_ORDER.sale_fee_total
  is '发售单程票单价(分)';
comment on column W_OL_QRPAY_ORDER.sale_times_total
  is '发售单程票数量';
comment on column W_OL_QRPAY_ORDER.deal_fee_total
  is '发售单程票总价(分)';
comment on column W_OL_QRPAY_ORDER.acc_seq
  is '中心处理流水';
comment on column W_OL_QRPAY_ORDER.order_ip
  is '生成订单终端IP';
comment on column W_OL_QRPAY_ORDER.card_type_total
  is '发售票卡类型';
-- Create/Recreate primary, unique and foreign key constraints 
alter table W_OL_QRPAY_ORDER
  add constraint PK_W_OL_QRPAY_ORDER primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on W_OL_QRPAY_ORDER to W_ACC_OL_APP;
grant select on W_OL_QRPAY_ORDER to W_ACC_OL_DEV;
grant select, insert, update, delete on W_OL_QRPAY_ORDER to W_ACC_ST_APP;


-- Create table
create table W_OL_QRPAY_CREATE
(
  water_no       NUMBER(18) not null,
  message_id     VARCHAR2(2),
  msg_gen_time   VARCHAR2(14),
  termina_no     VARCHAR2(9),
  sam_logical_id VARCHAR2(16),
  termina_seq    NUMBER(18),
  acc_seq        NUMBER(18),
  order_no       VARCHAR2(14),
  order_date     DATE,
  card_type      CHAR(4),
  sale_fee       NUMBER(18),
  sale_times     NUMBER(18),
  deal_fee       NUMBER(18),
  qrpay_id       char(10),
  qrpay_data     char(34),
  return_code    VARCHAR2(2),
  insert_date    DATE
);
-- Add comments to the table 
comment on table W_OL_QRPAY_CREATE
  is '支付二维码订单上传表';
-- Add comments to the columns 
comment on column W_OL_QRPAY_CREATE.message_id
  is '消息类型';
comment on column W_OL_QRPAY_CREATE.msg_gen_time
  is '消息生成时间';
comment on column W_OL_QRPAY_CREATE.termina_no
  is '终端编号';
comment on column W_OL_QRPAY_CREATE.sam_logical_id
  is 'sam卡逻辑号';
comment on column W_OL_QRPAY_CREATE.termina_seq
  is '终端处理流水';
comment on column W_OL_QRPAY_CREATE.acc_seq
  is '中心处理流水';
comment on column W_OL_QRPAY_CREATE.order_no
  is '订单号';
comment on column W_OL_QRPAY_CREATE.order_date
  is '订单生成时间';  
comment on column W_OL_QRPAY_CREATE.card_type
  is '发售票卡类型(主类型+子类型)';
comment on column W_OL_QRPAY_CREATE.sale_fee
  is '发售单程票单价(分)';
comment on column W_OL_QRPAY_CREATE.sale_times
  is '发售单程票数量';
comment on column W_OL_QRPAY_CREATE.deal_fee
  is '发售单程票总价(分)';
comment on column W_OL_QRPAY_CREATE.qrpay_id
  is '支付标识';
comment on column W_OL_QRPAY_CREATE.qrpay_data
  is '支付二维码信息';
comment on column W_OL_QRPAY_CREATE.return_code
  is '响应码: 00:订单处理正常01:SAM卡不存在02:终端编号不合法03:订单号编码不合法04:设备订单号重复10:中心系统异常99:其他';
comment on column W_OL_QRPAY_CREATE.insert_date
  is '插入时间';
-- Grant/Revoke object privileges 
grant select, insert, update, delete on W_OL_QRPAY_CREATE to W_ACC_OL_APP;
grant select on W_OL_QRPAY_CREATE to W_ACC_OL_DEV;
grant select, insert, update, delete on W_OL_QRPAY_CREATE to W_ACC_ST_APP;


-- Create table
create table W_OL_QRPAY_APP
(
  water_no        NUMBER(18) not null,
  message_id      VARCHAR2(2),
  msg_gen_time    VARCHAR2(14),
  termina_no      VARCHAR2(9),
  hce_seq         NUMBER(18),
  acc_seq         NUMBER(18),
  phone_no        VARCHAR2(11),
  imsi            VARCHAR2(15),
  imei            VARCHAR2(15),
  app_code        CHAR(2),
  qrpay_id         char(10),
  qrpay_data       char(34),
  pay_date         DATE,
  pay_status       CHAR(2),
  pay_channel_type    CHAR(2),
  pay_channel_code    char(4),
  return_code    VARCHAR2(2),
  insert_date    DATE
);
-- Add comments to the table 
comment on table W_OL_QRPAY_APP
  is '支付二维码app支付结果';
-- Add comments to the columns 
comment on column W_OL_QRPAY_APP.message_id
  is '消息类型';
comment on column W_OL_QRPAY_APP.msg_gen_time
  is '消息生成时间';
comment on column W_OL_QRPAY_APP.termina_no
  is '终端编号';
comment on column W_OL_QRPAY_APP.hce_seq
  is 'HCE处理流水';
comment on column W_OL_QRPAY_APP.acc_seq
  is '中心处理流水';
comment on column W_OL_QRPAY_APP.phone_no
  is '手机号';
comment on column W_OL_QRPAY_APP.imsi
  is '手机用户标识';
comment on column W_OL_QRPAY_APP.imei
  is '手机设备标识';
comment on column W_OL_QRPAY_APP.app_code
  is 'app渠道';
comment on column W_OL_QRPAY_APP.qrpay_id
  is '支付标识';
comment on column W_OL_QRPAY_APP.qrpay_data
  is '支付二维码信息';
comment on column W_OL_QRPAY_APP.pay_date
  is '支付时间';
comment on column W_OL_QRPAY_APP.pay_status
  is '支付结果 00:支付成功;01:余额不足;02:黑名单账户;10:支付通道通讯异常;99:其他异常';
comment on column W_OL_QRPAY_APP.pay_channel_type
  is '支付渠道类型 01:银行;02:银联;03:微信支付;04:支付宝支付;09:其他第三方支付99:其他';
comment on column W_OL_QRPAY_APP.pay_channel_code
  is '支付渠道代码';
comment on column W_OL_QRPAY_APP.return_code
  is '响应码 00:正常;01:系统处理过程中出现异常02:不允许退款,已出票;03:订单更新失败';
comment on column W_OL_QRPAY_APP.insert_date
  is '插入时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table W_OL_QRPAY_APP
  add constraint PK_W_OL_QRPAY_APP primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on W_OL_QRPAY_APP to W_ACC_OL_APP;
grant select on W_OL_QRPAY_APP to W_ACC_OL_DEV;
grant select, insert, update, delete on W_OL_QRPAY_APP to W_ACC_ST_APP;


-- Create table
create table W_OL_QRPAY_DOWN
(
  water_no        NUMBER(18) not null,
  message_id      VARCHAR2(2),
  msg_gen_time    VARCHAR2(14),
  termina_no      VARCHAR2(9),
  termina_seq         NUMBER(18),
  order_no       VARCHAR2(14),
  acc_seq         NUMBER(18),
  qrpay_id         char(10),
  qrpay_data       char(34),
  pay_date         DATE,
  pay_status       CHAR(2),
  pay_channel_type    CHAR(2),
  pay_channel_code    char(4),
  deal_time         DATE,
  card_type      CHAR(4),
  sale_fee       NUMBER(18),
  sale_times     NUMBER(18),
  deal_fee       NUMBER(18),
  return_code    VARCHAR2(2),
  insert_date    DATE
);
-- Add comments to the table 
comment on table W_OL_QRPAY_DOWN
  is '支付二维码支付结果下发';
-- Add comments to the columns 
comment on column W_OL_QRPAY_DOWN.message_id
  is '消息类型';
comment on column W_OL_QRPAY_DOWN.msg_gen_time
  is '消息生成时间';
comment on column W_OL_QRPAY_DOWN.termina_no
  is '终端编号';
comment on column W_OL_QRPAY_DOWN.termina_seq
  is '终端处理流水';
comment on column W_OL_QRPAY_DOWN.acc_seq
  is '中心处理流水';
comment on column W_OL_QRPAY_DOWN.order_no
  is '订单号';
comment on column W_OL_QRPAY_DOWN.qrpay_id
  is '支付标识';
comment on column W_OL_QRPAY_DOWN.qrpay_data
  is '支付二维码信息';
comment on column W_OL_QRPAY_DOWN.pay_date
  is '支付时间';
comment on column W_OL_QRPAY_DOWN.pay_status
  is '支付结果 00:支付成功;01:余额不足;02:黑名单账户;10:支付通道通讯异常;99:其他异常';
comment on column W_OL_QRPAY_DOWN.pay_channel_type
  is '支付渠道类型 01:银行;02:银联;03:微信支付;04:支付宝支付;09:其他第三方支付99:其他';
comment on column W_OL_QRPAY_DOWN.pay_channel_code
  is '支付渠道代码';
comment on column W_OL_QRPAY_DOWN.deal_time
  is 'acc处理时间';
comment on column W_OL_QRPAY_DOWN.card_type
  is '发售票卡类型(主类型+子类型)';
comment on column W_OL_QRPAY_DOWN.sale_fee
  is '发售单程票单价(分)';
comment on column W_OL_QRPAY_DOWN.sale_times
  is '发售单程票数量';
comment on column W_OL_QRPAY_DOWN.deal_fee
  is '发售单程票总价(分)';
comment on column W_OL_QRPAY_DOWN.return_code
  is '响应码 00:正常01:车票数量不足02:没有正常完成支付10:系统处理过程中出现异常';
comment on column W_OL_QRPAY_DOWN.insert_date
  is '插入时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table W_OL_QRPAY_DOWN
  add constraint PK_W_OL_QRPAY_DOWN primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on W_OL_QRPAY_DOWN to W_ACC_OL_APP;
grant select on W_OL_QRPAY_DOWN to W_ACC_OL_DEV;
grant select, insert, update, delete on W_OL_QRPAY_DOWN to W_ACC_ST_APP;


create table W_OL_QRPAY_ORSTATUS
(
  water_no         NUMBER(18) not null,
  message_id       VARCHAR2(2),
  msg_gen_time     VARCHAR2(14),
  termina_no       VARCHAR2(9),
  hce_seq          NUMBER(18),
  acc_seq          NUMBER(18),
  phone_no         VARCHAR2(11),
  imsi             VARCHAR2(15),
  imei             VARCHAR2(15),
  app_code         CHAR(2),
  qrpay_id         CHAR(10),
  qrpay_data       CHAR(34),
  order_no         VARCHAR2(14),
  order_date       DATE,
  card_type        CHAR(4),
  sale_fee         NUMBER(18),
  sale_times       NUMBER(18),
  deal_fee         NUMBER(18),
  status           CHAR(2),
  insert_date      DATE,
  card_type_total  CHAR(4),
  sale_fee_total   NUMBER(18),
  sale_times_total NUMBER(18),
  deal_fee_total   NUMBER(18)
);
-- Add comments to the table 
comment on table W_OL_QRPAY_ORSTATUS
  is '支付二维码订单查询';
-- Add comments to the columns 
comment on column W_OL_QRPAY_ORSTATUS.message_id
  is '消息类型';
comment on column W_OL_QRPAY_ORSTATUS.msg_gen_time
  is '消息生成时间';
comment on column W_OL_QRPAY_ORSTATUS.termina_no
  is '终端编号';
comment on column W_OL_QRPAY_ORSTATUS.hce_seq
  is 'HCE处理流水';
comment on column W_OL_QRPAY_ORSTATUS.acc_seq
  is '中心处理流水';
comment on column W_OL_QRPAY_ORSTATUS.phone_no
  is '手机号';
comment on column W_OL_QRPAY_ORSTATUS.imsi
  is '手机用户标识';
comment on column W_OL_QRPAY_ORSTATUS.imei
  is '手机设备标识';
comment on column W_OL_QRPAY_ORSTATUS.app_code
  is 'app渠道';
comment on column W_OL_QRPAY_ORSTATUS.qrpay_id
  is '支付标识';
comment on column W_OL_QRPAY_ORSTATUS.qrpay_data
  is '支付二维码信息';
comment on column W_OL_QRPAY_ORSTATUS.order_no
  is '订单号:8位日期+6位流水';
comment on column W_OL_QRPAY_ORSTATUS.order_date
  is '订单生成时间';
comment on column W_OL_QRPAY_ORSTATUS.card_type
  is '出票票卡类型(主类型+子类型)';
comment on column W_OL_QRPAY_ORSTATUS.sale_fee
  is '出票单程票单价(分)';
comment on column W_OL_QRPAY_ORSTATUS.sale_times
  is '出票单程票数量';
comment on column W_OL_QRPAY_ORSTATUS.deal_fee
  is '出票单程票总价(分)';
comment on column W_OL_QRPAY_ORSTATUS.status
  is '00:订单未支付01:订单已支付02:订单支付金额与发售总价不一致03:订单不存在04:订单已取消';
comment on column W_OL_QRPAY_ORSTATUS.insert_date
  is '插入时间';
comment on column W_OL_QRPAY_ORSTATUS.card_type_total
  is '发售票卡类型(主类型+子类型)';
comment on column W_OL_QRPAY_ORSTATUS.sale_fee_total
  is '发售单程票单价(分)';
comment on column W_OL_QRPAY_ORSTATUS.sale_times_total
  is '发售单程票数量';
comment on column W_OL_QRPAY_ORSTATUS.deal_fee_total
  is '发售单程票总价(分)';
-- Create/Recreate primary, unique and foreign key constraints 
alter table W_OL_QRPAY_ORSTATUS
  add constraint PK_W_OL_QRPAY_ORSTATUS primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on W_OL_QRPAY_ORSTATUS to W_ACC_OL_APP;
grant select on W_OL_QRPAY_ORSTATUS to W_ACC_OL_DEV;
grant select, insert, update, delete on W_OL_QRPAY_ORSTATUS to W_ACC_ST_APP;


-- Create table
create table W_OL_QRPAY_ID
(
  qrpay_id NUMBER(10) default 0 not null
);
-- Add comments to the table 
comment on table W_OL_QRPAY_ID
  is '支付二维码支付标识';
-- Add comments to the columns 
comment on column W_OL_QRPAY_ID.qrpay_id
  is '支付标识';
grant select, insert, update, delete on W_OL_QRPAY_ID to W_ACC_OL_APP;
grant select on W_OL_QRPAY_ID to W_ACC_OL_DEV;



-- Create table
create table W_OL_QUE_MESSAGE
(
  message_id           NUMBER(18) not null,
  message_time         DATE,
  line_id              CHAR(2),
  station_id           CHAR(2),
  ip_address           VARCHAR2(15),
  message              BLOB,
  process_flag         CHAR(1),
  is_para_inform_msg   CHAR(1),
  para_inform_water_no INTEGER,
  message_type         VARCHAR2(2),
  message_type_sub     VARCHAR2(2),
  remark               VARCHAR2(50)
);
-- Add comments to the columns 
comment on column W_OL_QUE_MESSAGE.message_id
  is '消息id';
comment on column W_OL_QUE_MESSAGE.message_time
  is '消息时间';
comment on column W_OL_QUE_MESSAGE.line_id
  is '线路代码';
comment on column W_OL_QUE_MESSAGE.station_id
  is '车站代码';
comment on column W_OL_QUE_MESSAGE.ip_address
  is 'ip地址';
comment on column W_OL_QUE_MESSAGE.message
  is '消息';
comment on column W_OL_QUE_MESSAGE.process_flag
  is '处理标志0:未处理1:处理';
comment on column W_OL_QUE_MESSAGE.is_para_inform_msg
  is '是否参数通知消息';
comment on column W_OL_QUE_MESSAGE.para_inform_water_no
  is '参数通知流水号';
comment on column W_OL_QUE_MESSAGE.message_type
  is '消息类型';
comment on column W_OL_QUE_MESSAGE.message_type_sub
  is '消息子类型：01:审计文件；02：错误文件；03：配票数据文件；';
comment on column W_OL_QUE_MESSAGE.remark
  is '备注';
-- Create/Recreate primary, unique and foreign key constraints 
alter table W_OL_QUE_MESSAGE
  add constraint W_OL_QUE_MESSAGE_PK primary key (MESSAGE_ID);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on W_OL_QUE_MESSAGE to W_ACC_ST_APP;
grant select, insert, update, delete on W_OL_QUE_MESSAGE to W_ACC_OL_APP;
grant select on W_OL_QUE_MESSAGE to W_ACC_OL_dev;
