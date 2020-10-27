-- Create table
create table w_acc_tk.W_IC_HCE_CARD_INFO
(
  water_no            NUMBER(18) not null,
  card_type           VARCHAR2(4),
  phone_no            VARCHAR2(11),
  imsi                VARCHAR2(15),
  imei                VARCHAR2(15),
  card_logical_id     VARCHAR2(20),
  card_phy_id         VARCHAR2(20),
  logical_seq         NUMBER(18),
  deal_day            CHAR(8),
  face_value          NUMBER(18) default 0,
  deposit_fee         NUMBER(18) default 0, 
  act_flag            CHAR(1),
  reuse_flag          CHAR(1),
  is_test_flag        CHAR(1),
  insert_date         DATE
);
-- Add comments to the table 
comment on table w_acc_tk.W_IC_HCE_CARD_INFO
  is 'HCE票卡逻辑卡号使用表';
-- Add comments to the columns 
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.card_type
  is '票卡类型';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.phone_no
  is '手机号';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.imsi
  is '手机用户标识';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.imei
  is '手机设备标识';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.deal_day
  is '发行时间';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.card_logical_id
  is '票卡逻辑卡号';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.card_phy_id
  is '票卡物理卡号';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.logical_seq
  is '逻辑卡号序号';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.face_value
  is '面值';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.deposit_fee
  is '押金';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.act_flag
  is '状态 1申请，2确认';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.reuse_flag
  is '待重用标志 1可重用';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.is_test_flag
  is '测试标记 0正常，1测试';
comment on column w_acc_tk.W_IC_HCE_CARD_INFO.insert_date
  is '插入时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table w_acc_tk.W_IC_HCE_CARD_INFO
  add constraint PK_W_IC_HCE_CARD_INFO primary key (WATER_NO);
-- Grant/Revoke object privileges 
grant select, insert, update, delete on w_acc_tk.W_IC_HCE_CARD_INFO to W_ACC_OL_APP;
grant select on w_acc_tk.W_IC_HCE_CARD_INFO to W_ACC_OL_DEV;
grant select, insert, update, delete on w_acc_tk.W_IC_HCE_CARD_INFO to W_ACC_ST_APP;
grant select, insert, update, delete on w_acc_tk.W_IC_HCE_CARD_INFO to W_ACC_TK_APP;


-- Create sequence 
create sequence w_acc_tk.W_SEQ_W_IC_HCE_CARD_INFO
minvalue 1
maxvalue 2147483648
start with 1
increment by 1
cache 10;

-- Grant/Revoke object privileges 
grant select on w_acc_tk.W_SEQ_W_IC_HCE_CARD_INFO to W_ACC_OL_APP;
grant select on w_acc_tk.W_SEQ_W_IC_HCE_CARD_INFO to W_ACC_ST_APP;
grant select on w_acc_tk.W_SEQ_W_IC_HCE_CARD_INFO to W_ACC_TK_APP;



--w_acc_tk:
-- Add/modify columns 
alter table w_acc_tk.W_IC_BC_LOGIC_NO add seq_no number default 0;
-- Add comments to the columns 
comment on column w_acc_tk.W_IC_BC_LOGIC_NO.seq_no
  is '序列号';
-- Grant/Revoke object privileges 
grant select,update on w_acc_tk.W_IC_BC_LOGIC_NO to W_ACC_OL_APP;
grant select on w_acc_tk.W_IC_BC_LOGIC_NO to W_ACC_ST_APP;



comment on column w_acc_tk.W_IC_BC_LOGIC_NO.record_flag
  is '单据状态
0:单据有效
1:单据撤消
2:单据删除
3:单据未审核';

