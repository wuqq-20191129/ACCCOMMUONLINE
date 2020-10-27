-- Create sequence 
create sequence w_acc_ol.W_SEQ_W_OL_AIR_SALE
minvalue 1
maxvalue 2147483648
start with 1
increment by 1
cache 10;
-- Grant/Revoke object privileges 
grant select on w_acc_ol.W_SEQ_W_OL_AIR_SALE to W_ACC_OL_APP;
grant select on w_acc_ol.W_SEQ_W_OL_AIR_SALE to W_ACC_OL_DEV;
grant select on w_acc_ol.W_SEQ_W_OL_AIR_SALE to W_ACC_ST_APP;

-- Create sequence 
create sequence w_acc_ol.W_SEQ_W_OL_AIR_SALE_CFM
minvalue 1
maxvalue 2147483648
start with 1
increment by 1
cache 10;
-- Grant/Revoke object privileges 
grant select on w_acc_ol.W_SEQ_W_OL_AIR_SALE_CFM to W_ACC_OL_APP;
grant select on w_acc_ol.W_SEQ_W_OL_AIR_SALE_CFM to W_ACC_OL_DEV;
grant select on w_acc_ol.W_SEQ_W_OL_AIR_SALE_CFM to W_ACC_ST_APP;

-- Create sequence 
create sequence w_acc_ol.W_SEQ_W_OL_AIR_CHARGE
minvalue 1
maxvalue 2147483648
start with 1
increment by 1
cache 10;
-- Grant/Revoke object privileges 
grant select on w_acc_ol.W_SEQ_W_OL_AIR_CHARGE to W_ACC_OL_APP;
grant select on w_acc_ol.W_SEQ_W_OL_AIR_CHARGE to W_ACC_OL_DEV;
grant select on w_acc_ol.W_SEQ_W_OL_AIR_CHARGE to W_ACC_ST_APP;

-- Create sequence 
create sequence w_acc_ol.W_SEQ_W_OL_AIR_CHARGE_CFM
minvalue 1
maxvalue 2147483648
start with 1
increment by 1
cache 10;
-- Grant/Revoke object privileges 
grant select on w_acc_ol.W_SEQ_W_OL_AIR_CHARGE_CFM to W_ACC_OL_APP;
grant select on w_acc_ol.W_SEQ_W_OL_AIR_CHARGE_CFM to W_ACC_OL_DEV;
grant select on w_acc_ol.W_SEQ_W_OL_AIR_CHARGE_CFM to W_ACC_ST_APP;

-- Create table
create table w_acc_ol.W_OL_AIR_SALE
(
  water_no       NUMBER(18) not null,
  message_id     VARCHAR2(2),
  msg_gen_time   VARCHAR2(14),
  sys_ref_no NUMBER(18),
  termina_no     VARCHAR2(9),
  sam_logical_id VARCHAR2(16),
  termina_seq    NUMBER(18),
  branches_code  VARCHAR2(16),
  card_type      VARCHAR2(4),
  phone_no       varchar2(11),
  imsi           varchar2(15),
  imei           varchar2(15),
  product_code   CHAR(4) default '0991',
  city_code      CHAR(4) default '8300',
  business_code  CHAR(4) default '0003',
  deal_day       char(8),
  deal_dev_code  varchar2(5) default '15802',
  card_ver       char(2) default '10',
  card_day       char(8),
  card_app_day   char(8),
  card_app_ver   char(2) default '10',
  card_logical_id        varchar2(20),
  card_phy_id            varchar2(20),
  expiry_date            char(8),
  face_value             number(18) default 0,
  deposit_fee            number(18) default 0,
  app_expiry_start       char(8)    default '00000000',
  app_expiry_day         varchar2(5)default '000',
  sale_act_flag   char(1),
  is_test_flag           char(1),
  charge_limit           number(18),
  limit_mode            CHAR(3),
  limit_entry_station   CHAR(4),
  limit_exit_station    CHAR(4),
  card_mac              varchar2(544),
  app_code CHAR(2),
  return_code           char(2),
  err_code              char(2),
  insert_date    DATE  
);
-- Add comments to the table 
comment on table w_acc_ol.W_OL_AIR_SALE
  is '空发申请';

comment on column w_acc_ol.W_OL_AIR_SALE.app_code
  is 'app渠道';
comment on column w_acc_ol.W_OL_AIR_SALE.card_phy_id
  is '票卡物理卡号';
comment on column w_acc_ol.W_OL_AIR_SALE.expiry_date
  is '票卡有效期';
comment on column w_acc_ol.W_OL_AIR_SALE.face_value
  is '面值';
comment on column w_acc_ol.W_OL_AIR_SALE.deposit_fee
  is '押金';
comment on column w_acc_ol.W_OL_AIR_SALE.app_expiry_start
  is '乘次票应用有效期开始时间';
comment on column w_acc_ol.W_OL_AIR_SALE.app_expiry_day
  is '乘次票使用有效期';
comment on column w_acc_ol.W_OL_AIR_SALE.sale_act_flag
  is '发售激活标志 0不激活，1激活';
comment on column w_acc_ol.W_OL_AIR_SALE.is_test_flag
  is '测试标记 0正常，1测试';
comment on column w_acc_ol.W_OL_AIR_SALE.charge_limit
  is '充值上限';
comment on column w_acc_ol.W_OL_AIR_SALE.limit_mode
  is '限制模式';
comment on column w_acc_ol.W_OL_AIR_SALE.limit_entry_station
  is '限制进闸车站';
comment on column w_acc_ol.W_OL_AIR_SALE.limit_exit_station
  is '限制出闸车站';
comment on column w_acc_ol.W_OL_AIR_SALE.card_mac
  is '卡密钥';
comment on column w_acc_ol.W_OL_AIR_SALE.return_code
  is '响应码';
comment on column w_acc_ol.W_OL_AIR_SALE.err_code
  is '错误码';
comment on column w_acc_ol.W_OL_AIR_SALE.insert_date
  is '插入时间';

comment on column w_acc_ol.W_OL_AIR_SALE.card_ver
  is '卡版本号';
comment on column w_acc_ol.W_OL_AIR_SALE.card_day
  is '卡启动日期';
comment on column w_acc_ol.W_OL_AIR_SALE.card_app_day
  is '卡应用启动日期';
comment on column w_acc_ol.W_OL_AIR_SALE.card_app_ver
  is '卡应用版本';
comment on column w_acc_ol.W_OL_AIR_SALE.card_logical_id
  is '票卡逻辑卡号';

comment on column w_acc_ol.W_OL_AIR_SALE.product_code
  is '发卡方代码';
comment on column w_acc_ol.W_OL_AIR_SALE.city_code
  is '城市代码';
comment on column w_acc_ol.W_OL_AIR_SALE.business_code
  is '行业代码';
comment on column w_acc_ol.W_OL_AIR_SALE.deal_day
  is '发行时间';
comment on column w_acc_ol.W_OL_AIR_SALE.deal_dev_code
  is '发行设备代码';
  
comment on column w_acc_ol.W_OL_AIR_SALE.message_id
  is '消息类型';
comment on column w_acc_ol.W_OL_AIR_SALE.msg_gen_time
  is '消息生成时间';
comment on column w_acc_ol.W_OL_AIR_SALE.sys_ref_no
  is '系统参照号';
comment on column w_acc_ol.W_OL_AIR_SALE.termina_no
  is '终端编号';
comment on column w_acc_ol.W_OL_AIR_SALE.sam_logical_id
  is 'sam卡逻辑号';
comment on column w_acc_ol.W_OL_AIR_SALE.termina_seq
  is '终端处理流水';
comment on column w_acc_ol.W_OL_AIR_SALE.branches_code
  is '网点编码 默认值：全0';
comment on column w_acc_ol.W_OL_AIR_SALE.card_type
  is '票卡类型';
comment on column w_acc_ol.W_OL_AIR_SALE.phone_no
  is '手机号';
comment on column w_acc_ol.W_OL_AIR_SALE.imsi
  is '手机用户标识';
comment on column w_acc_ol.W_OL_AIR_SALE.imei
  is '手机设备标识';

-- Create/Recreate primary, unique and foreign key constraints 
alter table w_acc_ol.W_OL_AIR_SALE
  add constraint PK_W_OL_AIR_SALE primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on w_acc_ol.W_OL_AIR_SALE to W_ACC_OL_APP;
grant select on w_acc_ol.W_OL_AIR_SALE to W_ACC_OL_DEV;
grant select, insert, update, delete on w_acc_ol.W_OL_AIR_SALE to W_ACC_ST_APP;


-- Create table
create table w_acc_ol.W_OL_AIR_SALE_CFM
(
  water_no       NUMBER(18) not null,
  message_id     VARCHAR2(2),
  msg_gen_time   VARCHAR2(14),
  termina_no     VARCHAR2(9),
  sam_logical_id VARCHAR2(16),
  termina_seq    NUMBER(18),
  branches_code  VARCHAR2(16),
  iss_main_code   CHAR(4),
  iss_sub_code    CHAR(4),
  phone_no       varchar2(11),
  imsi           varchar2(15),
  imei           varchar2(15),
  card_type      VARCHAR2(4),
  card_logical_id        varchar2(20),
  card_phy_id            varchar2(20),
  is_test_flag           char(1),
  result_code            char(1),
  deal_time              char(14),
  sys_ref_no NUMBER(18),
  app_code CHAR(2),
  return_code           char(2),
  err_code              char(2),
  insert_date    DATE
);
-- Add comments to the table 
comment on table w_acc_ol.W_OL_AIR_SALE_CFM
  is '空发确认';
comment on column  w_acc_ol.W_OL_AIR_SALE_CFM.app_code
  is 'app渠道';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.message_id
  is '消息类型';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.msg_gen_time
  is '消息生成时间';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.termina_no
  is '终端编号';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.sam_logical_id
  is 'sam卡逻辑号';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.termina_seq
  is '终端处理流水';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.branches_code
  is '网点编码 默认值：全0';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.iss_main_code
  is '发行者主编码';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.iss_sub_code
  is '发行者子编码';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.phone_no
  is '手机号';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.imsi
  is '手机用户标识';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.imei
  is '手机设备标识';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.card_type
  is '票卡类型';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.card_logical_id
  is '票卡逻辑卡号';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.card_phy_id
  is '票卡物理卡号';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.is_test_flag
  is '测试标记 0正常，1测试';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.result_code
  is '发卡结果 0成功 1失败 2未知状态';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.deal_time
  is '发卡时间';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.sys_ref_no
  is '系统参照号';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.return_code
  is '响应码';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.err_code
  is '错误码';
comment on column w_acc_ol.W_OL_AIR_SALE_CFM.insert_date
  is '插入时间';

-- Create/Recreate primary, unique and foreign key constraints 
alter table w_acc_ol.W_OL_AIR_SALE_CFM
  add constraint PK_W_OL_AIR_SALE_CFM primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on w_acc_ol.W_OL_AIR_SALE_CFM to W_ACC_OL_APP;
grant select on w_acc_ol.W_OL_AIR_SALE_CFM to W_ACC_OL_DEV;
grant select, insert, update, delete on w_acc_ol.W_OL_AIR_SALE_CFM to W_ACC_ST_APP;



-- Create table
create table w_acc_ol.W_OL_AIR_CHARGE
( 
  water_no       NUMBER(18) not null,
  message_id     VARCHAR2(2),
  msg_gen_time   VARCHAR2(14),
  termina_no     VARCHAR2(9),
  sam_logical_id VARCHAR2(16),
  termina_seq    NUMBER(18),
  branches_code  VARCHAR2(16),
  iss_main_code   CHAR(4),
  iss_sub_code    CHAR(4),
  card_type      VARCHAR2(4),
  imsi           varchar2(15),
  imei           varchar2(15),
  card_logical_id        varchar2(20),
  card_phy_id            varchar2(20),
  is_test_flag           char(1),
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
  phone_no       varchar2(11),
  paid_channel_type     VARCHAR2(2),
  paid_channel_code     VARCHAR2(4),
  mac2             VARCHAR2(8),
  deal_time              char(14),
  sys_ref_no NUMBER(18),
  return_code           char(2),
  err_code              char(2),
  insert_date    DATE
);
-- Add comments to the table 
comment on table w_acc_ol.W_OL_AIR_CHARGE
  is '空充申请';
comment on column w_acc_ol.W_OL_AIR_CHARGE.onl_tran_times
  is '票卡联机交易号';
comment on column w_acc_ol.W_OL_AIR_CHARGE.offl_tran_times
  is '票卡脱机交易号';
comment on column w_acc_ol.W_OL_AIR_CHARGE.buss_type
  is '业务类型 14';
comment on column w_acc_ol.W_OL_AIR_CHARGE.value_type
  is '值类型 0无值1金额2次数3天数';
comment on column w_acc_ol.W_OL_AIR_CHARGE.charge_fee
  is '充值金额';
comment on column w_acc_ol.W_OL_AIR_CHARGE.balance
  is '票卡余额';
comment on column w_acc_ol.W_OL_AIR_CHARGE.mac1
  is 'mac1';
comment on column w_acc_ol.W_OL_AIR_CHARGE.tk_chge_seq
  is '卡片充值随机数';
comment on column w_acc_ol.W_OL_AIR_CHARGE.last_tran_termno
  is '上次交易终端编号';
comment on column w_acc_ol.W_OL_AIR_CHARGE.last_tran_time
  is '上次交易时间';
comment on column w_acc_ol.W_OL_AIR_CHARGE.operator_id
  is '操作员';
comment on column w_acc_ol.W_OL_AIR_CHARGE.paid_channel_type
  is '充值渠道类型';
comment on column w_acc_ol.W_OL_AIR_CHARGE.paid_channel_code
  is '充值渠道代码';
comment on column w_acc_ol.W_OL_AIR_CHARGE.message_id
  is '消息类型';
comment on column w_acc_ol.W_OL_AIR_CHARGE.msg_gen_time
  is '消息生成时间';
comment on column w_acc_ol.W_OL_AIR_CHARGE.termina_no
  is '终端编号';
comment on column w_acc_ol.W_OL_AIR_CHARGE.sam_logical_id
  is 'sam卡逻辑号';
comment on column w_acc_ol.W_OL_AIR_CHARGE.termina_seq
  is '终端处理流水';
comment on column w_acc_ol.W_OL_AIR_CHARGE.branches_code
  is '网点编码 默认值：全0';
comment on column w_acc_ol.W_OL_AIR_CHARGE.iss_main_code
  is '发行者主编码';
comment on column w_acc_ol.W_OL_AIR_CHARGE.iss_sub_code
  is '发行者子编码';
comment on column w_acc_ol.W_OL_AIR_CHARGE.imsi
  is '手机用户标识';
comment on column w_acc_ol.W_OL_AIR_CHARGE.imei
  is '手机设备标识';
comment on column w_acc_ol.W_OL_AIR_CHARGE.card_type
  is '票卡类型';
comment on column w_acc_ol.W_OL_AIR_CHARGE.card_logical_id
  is '票卡逻辑卡号';
comment on column w_acc_ol.W_OL_AIR_CHARGE.card_phy_id
  is '票卡物理卡号';
comment on column w_acc_ol.W_OL_AIR_CHARGE.is_test_flag
  is '测试标记 0正常，1测试';
comment on column w_acc_ol.W_OL_AIR_CHARGE.phone_no
  is '手机号';
comment on column w_acc_ol.W_OL_AIR_CHARGE.mac2
  is '不成功时全0';
comment on column w_acc_ol.W_OL_AIR_CHARGE.deal_time
  is '充值时间';
comment on column w_acc_ol.W_OL_AIR_CHARGE.sys_ref_no
  is '系统参照号';
comment on column w_acc_ol.W_OL_AIR_CHARGE.return_code
  is '响应码';
comment on column w_acc_ol.W_OL_AIR_CHARGE.err_code
  is '错误码';
comment on column w_acc_ol.W_OL_AIR_CHARGE.insert_date
  is '插入时间';

-- Create/Recreate primary, unique and foreign key constraints 
alter table w_acc_ol.W_OL_AIR_CHARGE
  add constraint PK_W_OL_AIR_CHARGE primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on w_acc_ol.W_OL_AIR_CHARGE to W_ACC_OL_APP;
grant select on w_acc_ol.W_OL_AIR_CHARGE to W_ACC_OL_DEV;
grant select, insert, update, delete on w_acc_ol.W_OL_AIR_CHARGE to W_ACC_ST_APP;




-- Create table
create table w_acc_ol.W_OL_AIR_CHARGE_CFM
( 
  water_no       NUMBER(18) not null,
  message_id     VARCHAR2(2),
  msg_gen_time   VARCHAR2(14),
  termina_no     VARCHAR2(9),
  sam_logical_id VARCHAR2(16),
  termina_seq    NUMBER(18),
  branches_code  VARCHAR2(16),
  iss_main_code   CHAR(4),
  iss_sub_code    CHAR(4),
  card_type      VARCHAR2(4),
  imsi           varchar2(15),
  imei           varchar2(15),
  card_logical_id        varchar2(20),
  card_phy_id            varchar2(20),
  is_test_flag           char(1),
  onl_tran_times   NUMBER(18),
  offl_tran_times  NUMBER(18),
  buss_type        VARCHAR2(2),
  value_type       VARCHAR2(1),
  charge_fee       NUMBER(18),
  balance          NUMBER(18),
  tac             VARCHAR2(8),
  phone_no       varchar2(11),
  operator_id      VARCHAR2(6),
  result_code      char(1),
  deal_time              char(14),
  sys_ref_no NUMBER(18),
  return_code           char(2),
  err_code              char(2),
  insert_date    DATE
);
-- Add comments to the table 
comment on table w_acc_ol.W_OL_AIR_CHARGE_CFM
  is '空充确认';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.onl_tran_times
  is '票卡联机交易号';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.offl_tran_times
  is '票卡脱机交易号';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.buss_type
  is '业务类型 14';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.value_type
  is '值类型 0无值1金额2次数3天数';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.charge_fee
  is '充值金额';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.balance
  is '票卡余额';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.tac
  is 'tac';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.operator_id
  is '操作员';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.message_id
  is '消息类型';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.msg_gen_time
  is '消息生成时间';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.termina_no
  is '终端编号';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.sam_logical_id
  is 'sam卡逻辑号';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.termina_seq
  is '终端处理流水';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.branches_code
  is '网点编码 默认值：全0';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.iss_main_code
  is '发行者主编码';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.iss_sub_code
  is '发行者子编码';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.imsi
  is '手机用户标识';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.imei
  is '手机设备标识';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.card_type
  is '票卡类型';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.card_logical_id
  is '票卡逻辑卡号';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.card_phy_id
  is '票卡物理卡号';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.is_test_flag
  is '测试标记 0正常，1测试';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.phone_no
  is '手机号';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.deal_time
  is '充值时间';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.result_code
  is '写卡结果 0成功 1失败 2未知状态';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.sys_ref_no
  is '系统参照号';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.return_code
  is '响应码';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.err_code
  is '错误码';
comment on column w_acc_ol.W_OL_AIR_CHARGE_CFM.insert_date
  is '插入时间';

-- Create/Recreate primary, unique and foreign key constraints 
alter table w_acc_ol.W_OL_AIR_CHARGE_CFM
  add constraint PK_W_OL_AIR_CHARGE_CFM primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on w_acc_ol.W_OL_AIR_CHARGE_CFM to W_ACC_OL_APP;
grant select on w_acc_ol.W_OL_AIR_CHARGE_CFM to W_ACC_OL_DEV;
grant select, insert, update, delete on w_acc_ol.W_OL_AIR_CHARGE_CFM to W_ACC_ST_APP;



-- Add comments to the table 
comment on table W_OL_CHG_PLUS
  is '充值';
comment on table W_OL_QRCODE_AFC
  is '二维码';
comment on table W_OL_CHG_ACTIVATION
  is '激活';
comment on table W_OL_CHG_SUB
  is '充值撤销';
comment on table W_OL_QRCODE_ORDER
  is '二维码订单表';
