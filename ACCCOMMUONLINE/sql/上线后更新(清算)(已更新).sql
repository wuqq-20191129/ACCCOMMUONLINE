-- Create table
create table w_acc_ol.W_OL_STS_ACC
(
  water_no           NUMBER(18) not null,
  squad_day          VARCHAR2(10) not null,
  line_id            CHAR(2),
  station_id         CHAR(2) default 00,
  lc_charge_fee      NUMBER(18,2) default 0,
  lc_charge_num      NUMBER(18) default 0,
  lc_return_fee      NUMBER(18,2) default 0,
  lc_return_num      NUMBER(18) default 0,
  air_sale_num       NUMBER(18) default 0,
  air_charge_fee     NUMBER(18,2) default 0,
  air_charge_num     NUMBER(18) default 0,
  air_return_fee     NUMBER(18,2) default 0,
  air_return_num     NUMBER(18) default 0,
  qrcode_hce_times   NUMBER(18) default 0,
  qrcode_hce_num     NUMBER(18) default 0,
  qrcode_hce_fee     NUMBER(18,2) default 0,
  qrcode_hce_c_times NUMBER(18) default 0,
  qrcode_hce_c_num   NUMBER(18) default 0,
  qrcode_hce_c_fee   NUMBER(18,2) default 0,
  qrcode_lc_times    NUMBER(18) default 0,
  qrcode_lc_num      NUMBER(18) default 0,
  qrcode_lc_fee      NUMBER(18,2) default 0,
  qrpay_hce_times    NUMBER(18) default 0,
  qrpay_hce_fee      NUMBER(18,2) default 0,
  qrpay_hce_c_times  NUMBER(18) default 0,
  qrpay_hce_c_fee    NUMBER(18,2) default 0,
  qrpay_lc_times     NUMBER(18) default 0,
  qrpay_lc_num       NUMBER(18) default 0,
  qrpay_lc_fee       NUMBER(18,2) default 0
);
comment on table w_acc_ol.W_OL_STS_ACC
  is '多元支付日统计表';
comment on column w_acc_ol.W_OL_STS_ACC.water_no
  is '序列号';
comment on column W_OL_STS_ACC.squad_day
  is '运营日';
comment on column w_acc_ol.W_OL_STS_ACC.line_id
  is '线路';
comment on column w_acc_ol.W_OL_STS_ACC.station_id
  is '车站';
comment on column w_acc_ol.W_OL_STS_ACC.lc_charge_fee
  is '终端充值金额';
comment on column w_acc_ol.W_OL_STS_ACC.lc_charge_num
  is '终端充值次数';
comment on column w_acc_ol.W_OL_STS_ACC.lc_return_fee
  is '终端充值撤销金额';
comment on column w_acc_ol.W_OL_STS_ACC.lc_return_num
  is '终端充值撤销次数';
comment on column w_acc_ol.W_OL_STS_ACC.air_sale_num
  is '空发次数';
comment on column w_acc_ol.W_OL_STS_ACC.air_charge_fee
  is '空充金额';
comment on column w_acc_ol.W_OL_STS_ACC.air_charge_num
  is '空充次数';
comment on column w_acc_ol.W_OL_STS_ACC.air_return_fee
  is '空充撤销金额';
comment on column w_acc_ol.W_OL_STS_ACC.air_return_num
  is '空充撤销次数';
comment on column w_acc_ol.W_OL_STS_ACC.qrcode_hce_times
  is 'HCE二维码订单数';
comment on column w_acc_ol.W_OL_STS_ACC.qrcode_hce_fee
  is 'HCE二维码金额';
comment on column w_acc_ol.W_OL_STS_ACC.qrcode_hce_c_times
  is 'HCE二维码取消订单数';
comment on column w_acc_ol.W_OL_STS_ACC.qrcode_hce_c_fee
  is 'HCE二维码取消金额';
comment on column w_acc_ol.W_OL_STS_ACC.qrcode_lc_times
  is '终端二维码取票订单数';
comment on column w_acc_ol.W_OL_STS_ACC.qrcode_lc_num
  is '终端二维码取票票数';
comment on column w_acc_ol.W_OL_STS_ACC.qrcode_lc_fee
  is '终端二维码取票金额';
comment on column w_acc_ol.W_OL_STS_ACC.qrpay_hce_times
  is 'HCE支付订单数';
comment on column w_acc_ol.W_OL_STS_ACC.qrpay_hce_fee
  is 'HCE支付金额';
comment on column w_acc_ol.W_OL_STS_ACC.qrpay_hce_c_times
  is 'HCE支付取消订单数';
comment on column w_acc_ol.W_OL_STS_ACC.qrpay_hce_c_fee
  is 'HCE支付取消金额';
comment on column w_acc_ol.W_OL_STS_ACC.qrpay_lc_times
  is '支付二维码终端订单数';
comment on column w_acc_ol.W_OL_STS_ACC.qrpay_lc_num
  is '支付二维码终端票数';
comment on column w_acc_ol.W_OL_STS_ACC.qrpay_lc_fee
  is '支付二维码终端金额';
grant select, insert, update, delete on w_acc_ol.W_OL_STS_ACC to W_ACC_OL_APP;
grant select on w_acc_ol.W_OL_STS_ACC to W_ACC_OL_DEV;
grant select on w_acc_ol.W_OL_STS_ACC to W_ACC_ST_RP_APP;


-- Create sequence 
create sequence w_acc_ol.W_SEQ_W_OL_STS_ACC
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
cache 10;
grant select on w_acc_ol.W_SEQ_W_OL_STS_ACC to W_ACC_OL_APP;
