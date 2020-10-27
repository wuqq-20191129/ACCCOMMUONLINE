-- Add/modify columns 
alter table w_acc_ol.W_OL_QRCODE_AFC add deal_time VARCHAR2(14);
comment on column w_acc_ol.W_OL_QRCODE_AFC.deal_time is '交易时间';
  
alter table w_acc_ol.w_ol_qrcode_cancel add deal_time VARCHAR2(14);
comment on column w_acc_ol.w_ol_qrcode_cancel.deal_time is '订单取消时间';

alter table w_acc_ol.w_ol_qrcode_tkstatus add deal_time VARCHAR2(14);
comment on column w_acc_ol.w_ol_qrcode_tkstatus.deal_time is '取票时间';

alter table w_acc_ol.W_OL_QRCODE_ORDER add deal_time VARCHAR2(14);
comment on column w_acc_ol.W_OL_QRCODE_ORDER.deal_time is '交易时间';

-- Add comments to the columns 
comment on column w_acc_ol.W_OL_QRPAY_APP.return_code
  is '响应码 00:正常;01:系统处理过程中出现异常02:不允许退款,已出票;03:订单更新失败04:订单未支付05:订单已退款';
