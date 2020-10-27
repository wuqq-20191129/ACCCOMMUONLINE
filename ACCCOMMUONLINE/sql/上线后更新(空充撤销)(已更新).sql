create sequence W_SEQ_W_OL_AIR_CHARGE_CANCEL
minvalue 1
maxvalue 2147483648
start with 1
increment by 1
cache 10;
create sequence W_SEQ_W_OL_AIR_CHARGE_CL_CFM
minvalue 1
maxvalue 2147483648
start with 1
increment by 1
cache 10;

grant select on W_SEQ_W_OL_AIR_CHARGE_CL_CFM to W_ACC_OL_APP;
grant select on W_SEQ_W_OL_AIR_CHARGE_CL_CFM to W_ACC_ST_APP;
grant select on W_SEQ_W_OL_AIR_CHARGE_CANCEL to W_ACC_OL_APP;
grant select on W_SEQ_W_OL_AIR_CHARGE_CANCEL to W_ACC_ST_APP;


-- Create table
create table W_OL_AIR_CHARGE_CANCEL
(
  water_no          NUMBER(18) not null,
  message_id        VARCHAR2(2),
  msg_gen_time      VARCHAR2(14),
  termina_no        VARCHAR2(9),
  sam_logical_id    VARCHAR2(16),
  termina_seq       NUMBER(18),
  branches_code     VARCHAR2(16),
  iss_main_code     CHAR(4),
  iss_sub_code      CHAR(4),
  card_type         VARCHAR2(4),
  imsi              VARCHAR2(15),
  imei              VARCHAR2(15),
  card_logical_id   VARCHAR2(20),
  card_phy_id       VARCHAR2(20),
  is_test_flag      CHAR(1),
  onl_tran_times    NUMBER(18),
  offl_tran_times   NUMBER(18),
  buss_type         VARCHAR2(2),
  value_type        VARCHAR2(1),
  charge_fee        NUMBER(18),
  balance           NUMBER(18),
  mac1              VARCHAR2(8),
  tk_chge_seq       VARCHAR2(8),
  last_tran_termno  VARCHAR2(16),
  last_tran_time    VARCHAR2(14),
  operator_id       VARCHAR2(6),
  phone_no          VARCHAR2(11),
  paid_channel_type VARCHAR2(2),
  paid_channel_code VARCHAR2(4),
  mac2              VARCHAR2(8),
  deal_time         CHAR(14),
  sys_ref_no        NUMBER(18),
  return_code       CHAR(2),
  err_code          CHAR(2),
  insert_date       DATE,
  sys_ref_no_chr    NUMBER(18)
);
comment on table W_OL_AIR_CHARGE_CANCEL
  is '空充撤销申请';
comment on column W_OL_AIR_CHARGE_CANCEL.message_id
  is '消息类型';
comment on column W_OL_AIR_CHARGE_CANCEL.msg_gen_time
  is '消息生成时间';
comment on column W_OL_AIR_CHARGE_CANCEL.termina_no
  is '终端编号';
comment on column W_OL_AIR_CHARGE_CANCEL.sam_logical_id
  is 'sam卡逻辑号';
comment on column W_OL_AIR_CHARGE_CANCEL.termina_seq
  is '终端处理流水';
comment on column W_OL_AIR_CHARGE_CANCEL.branches_code
  is '网点编码 默认值：全0';
comment on column W_OL_AIR_CHARGE_CANCEL.iss_main_code
  is '发行者主编码';
comment on column W_OL_AIR_CHARGE_CANCEL.iss_sub_code
  is '发行者子编码';
comment on column W_OL_AIR_CHARGE_CANCEL.card_type
  is '票卡类型';
comment on column W_OL_AIR_CHARGE_CANCEL.imsi
  is '手机用户标识';
comment on column W_OL_AIR_CHARGE_CANCEL.imei
  is '手机设备标识';
comment on column W_OL_AIR_CHARGE_CANCEL.card_logical_id
  is '票卡逻辑卡号';
comment on column W_OL_AIR_CHARGE_CANCEL.card_phy_id
  is '票卡物理卡号';
comment on column W_OL_AIR_CHARGE_CANCEL.is_test_flag
  is '测试标记 0正常，1测试';
comment on column W_OL_AIR_CHARGE_CANCEL.onl_tran_times
  is '票卡联机交易号';
comment on column W_OL_AIR_CHARGE_CANCEL.offl_tran_times
  is '票卡脱机交易号';
comment on column W_OL_AIR_CHARGE_CANCEL.buss_type
  is '业务类型 14';
comment on column W_OL_AIR_CHARGE_CANCEL.value_type
  is '值类型 0无值1金额2次数3天数';
comment on column W_OL_AIR_CHARGE_CANCEL.charge_fee
  is '撤销金额';
comment on column W_OL_AIR_CHARGE_CANCEL.balance
  is '票卡余额';
comment on column W_OL_AIR_CHARGE_CANCEL.mac1
  is 'mac1';
comment on column W_OL_AIR_CHARGE_CANCEL.tk_chge_seq
  is '卡片充值随机数';
comment on column W_OL_AIR_CHARGE_CANCEL.last_tran_termno
  is '上次交易终端编号';
comment on column W_OL_AIR_CHARGE_CANCEL.last_tran_time
  is '上次交易时间';
comment on column W_OL_AIR_CHARGE_CANCEL.operator_id
  is '操作员';
comment on column W_OL_AIR_CHARGE_CANCEL.phone_no
  is '手机号';
comment on column W_OL_AIR_CHARGE_CANCEL.paid_channel_type
  is '撤销渠道类型';
comment on column W_OL_AIR_CHARGE_CANCEL.paid_channel_code
  is '撤销渠道代码';
comment on column W_OL_AIR_CHARGE_CANCEL.mac2
  is '不成功时全0';
comment on column W_OL_AIR_CHARGE_CANCEL.deal_time
  is '充值时间';
comment on column W_OL_AIR_CHARGE_CANCEL.sys_ref_no
  is '系统参照号';
comment on column W_OL_AIR_CHARGE_CANCEL.return_code
  is '响应码';
comment on column W_OL_AIR_CHARGE_CANCEL.err_code
  is '错误码';
comment on column W_OL_AIR_CHARGE_CANCEL.insert_date
  is '插入时间';
comment on column W_OL_AIR_CHARGE_CANCEL.sys_ref_no_chr
  is '空充系统参照号';
alter table W_OL_AIR_CHARGE_CANCEL
  add constraint PK_W_OL_AIR_CHARGE_CANCEL primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on W_OL_AIR_CHARGE_CANCEL to W_ACC_OL_APP;
grant select on W_OL_AIR_CHARGE_CANCEL to W_ACC_OL_DEV;
grant select, insert, update, delete on W_OL_AIR_CHARGE_CANCEL to W_ACC_ST_APP;


-- Create table
create table W_OL_AIR_CHARGE_CANCEL_CFM
(
  water_no        NUMBER(18) not null,
  message_id      VARCHAR2(2),
  msg_gen_time    VARCHAR2(14),
  termina_no      VARCHAR2(9),
  sam_logical_id  VARCHAR2(16),
  termina_seq     NUMBER(18),
  branches_code   VARCHAR2(16),
  iss_main_code   CHAR(4),
  iss_sub_code    CHAR(4),
  card_type       VARCHAR2(4),
  imsi            VARCHAR2(15),
  imei            VARCHAR2(15),
  card_logical_id VARCHAR2(20),
  card_phy_id     VARCHAR2(20),
  is_test_flag    CHAR(1),
  onl_tran_times  NUMBER(18),
  offl_tran_times NUMBER(18),
  buss_type       VARCHAR2(2),
  value_type      VARCHAR2(1),
  charge_fee      NUMBER(18),
  balance         NUMBER(18),
  tac             VARCHAR2(8),
  phone_no        VARCHAR2(11),
  operator_id     VARCHAR2(6),
  result_code     CHAR(1),
  deal_time       CHAR(14),
  sys_ref_no      NUMBER(18),
  return_code     CHAR(2),
  err_code        CHAR(2),
  insert_date     DATE,
  sys_ref_no_chr  NUMBER(18)
);
comment on table W_OL_AIR_CHARGE_CANCEL_CFM
  is '空充撤销申请确认';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.message_id
  is '消息类型';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.msg_gen_time
  is '消息生成时间';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.termina_no
  is '终端编号';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.sam_logical_id
  is 'sam卡逻辑号';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.termina_seq
  is '终端处理流水';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.branches_code
  is '网点编码 默认值：全0';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.iss_main_code
  is '发行者主编码';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.iss_sub_code
  is '发行者子编码';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.card_type
  is '票卡类型';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.imsi
  is '手机用户标识';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.imei
  is '手机设备标识';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.card_logical_id
  is '票卡逻辑卡号';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.card_phy_id
  is '票卡物理卡号';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.is_test_flag
  is '测试标记 0正常，1测试';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.onl_tran_times
  is '票卡联机交易号';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.offl_tran_times
  is '票卡脱机交易号';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.buss_type
  is '业务类型 14';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.value_type
  is '值类型 0无值1金额2次数3天数';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.charge_fee
  is '撤销金额';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.balance
  is '票卡余额';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.tac
  is 'tac';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.phone_no
  is '手机号';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.operator_id
  is '操作员';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.result_code
  is '写卡结果 0成功 1失败 2未知状态';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.deal_time
  is '充值时间';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.sys_ref_no
  is '系统参照号';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.return_code
  is '响应码';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.err_code
  is '错误码';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.insert_date
  is '插入时间';
comment on column W_OL_AIR_CHARGE_CANCEL_CFM.sys_ref_no_chr
  is '空充系统参照号';
alter table W_OL_AIR_CHARGE_CANCEL_CFM
  add constraint PK_W_OL_AIR_CHARGE_CANCEL_CFM primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on W_OL_AIR_CHARGE_CANCEL_CFM to W_ACC_OL_APP;
grant select on W_OL_AIR_CHARGE_CANCEL_CFM to W_ACC_OL_DEV;
grant select, insert, update, delete on W_OL_AIR_CHARGE_CANCEL_CFM to W_ACC_ST_APP;

