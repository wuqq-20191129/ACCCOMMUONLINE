-- Add comments to the columns 
comment on column W_OL_CHG_PLUS.termina_no
  is '终端编号';
comment on column W_OL_CHG_PLUS.transation_seq
  is '终端交易序号';
comment on column W_OL_CHG_PLUS.status
  is '1:申请；2：确认；';
comment on column W_OL_CHG_SUB.status
  is '1:申请；2：确认；';
comment on column W_OL_LOG_RECV_SEND.result
  is '0:成功 1:失败 2:消息长度0';
