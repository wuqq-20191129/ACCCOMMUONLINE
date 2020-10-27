-- Grant/Revoke object privileges 
grant select, insert, update, delete on w_ol_trd_max_hdltime to w_acc_ol_app;
grant select on w_ol_trd_max_hdltime to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_trd_max_hdltime to w_acc_st_app;

grant select, insert, update, delete on w_ol_trd_monitor to w_acc_ol_app;
grant select on w_ol_trd_monitor to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_trd_monitor to w_acc_st_app;

grant select, insert, update, delete on w_ol_trd_monitor_his to w_acc_ol_app;
grant select on w_ol_trd_monitor_his to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_trd_monitor_his to w_acc_st_app;

grant select, insert, update, delete on w_ol_trd_msg_del to w_acc_ol_app;
grant select on w_ol_trd_msg_del to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_trd_msg_del to w_acc_st_app;

grant select, insert, update, delete on w_ol_trd_msg_handup_num to w_acc_ol_app;
grant select on w_ol_trd_msg_handup_num to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_trd_msg_handup_num to w_acc_st_app;

grant select, insert, update, delete on w_ol_trd_msg_handup_num to w_acc_ol_app;
grant select on w_ol_trd_msg_handup_num to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_trd_msg_handup_num to w_acc_st_app;

grant select, insert, update, delete on w_ol_trd_reset to w_acc_ol_app;
grant select on w_ol_trd_reset to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_trd_reset to w_acc_st_app;

grant select, insert, update, delete on w_ol_chg_activation to w_acc_ol_app;
grant select on w_ol_chg_activation to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_chg_activation to w_acc_st_app;

grant select, insert, update, delete on w_ol_chg_plus to w_acc_ol_app;
grant select on w_ol_chg_plus to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_chg_plus to w_acc_st_app;

grant select, insert, update, delete on w_ol_chg_sub to w_acc_ol_app;
grant select on w_ol_chg_sub to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_chg_sub to w_acc_st_app;

grant select, insert, update, delete on w_ol_sys_version to w_acc_ol_app;
grant select on w_ol_sys_version to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_sys_version to w_acc_st_app;

grant select, insert, update, delete on w_ol_pub_flag to w_acc_ol_app;
grant select on w_ol_pub_flag to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_pub_flag to w_acc_st_app;

grant select, insert, update, delete on w_ol_chg_log_test to w_acc_ol_app;
grant select on w_ol_chg_log_test to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_chg_log_test to w_acc_st_app;

grant select, insert, update, delete on w_ol_log_sys_error to w_acc_ol_app;
grant select on w_ol_log_sys_error to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_log_sys_error to w_acc_st_app;

grant select, insert, update, delete on w_ol_log_recv_send to w_acc_ol_app;
grant select on w_ol_log_recv_send to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_log_recv_send to w_acc_st_app;

grant select, insert, update, delete on w_ol_log_connect to w_acc_ol_app;
grant select on w_ol_log_connect to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_log_connect to w_acc_st_app;

grant select, insert, update, delete on w_ol_log_commu to w_acc_ol_app;
grant select on w_ol_log_commu to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_log_commu to w_acc_st_app;


grant select on w_seq_w_ol_air_charge to w_acc_ol_app;
grant select on w_seq_w_ol_air_charge_cfm to w_acc_ol_app;
grant select on w_seq_w_ol_air_sale to w_acc_ol_app;
grant select on w_seq_w_ol_air_sale_cfm to w_acc_ol_app;
grant select on w_seq_w_ol_chg_activation to w_acc_ol_app;
grant select on w_seq_w_ol_chg_plus to w_acc_ol_app;
grant select on w_seq_w_ol_qrcode_afc to w_acc_ol_app;
grant select on w_seq_w_ol_chg_sub to w_acc_ol_app;
grant select on w_seq_w_ol_log_commu to w_acc_ol_app;
grant select on w_seq_w_ol_log_connect to w_acc_ol_app;
grant select on w_seq_w_ol_log_recv_send to w_acc_ol_app;
grant select on w_seq_w_ol_log_sys_error to w_acc_ol_app;
grant select on w_seq_w_ol_trd_monitor_his to w_acc_ol_app;
grant select on w_seq_w_ol_qrcode_order to w_acc_ol_app;


grant select on w_seq_w_ol_qrcode_cancel to w_acc_ol_app;
grant select on w_seq_w_ol_qrcode_tkcode to w_acc_ol_app;
grant select on w_seq_w_ol_qrcode_tkstatus to w_acc_ol_app;
grant select on w_seq_w_ol_qrpay_app to w_acc_ol_app;
grant select on w_seq_w_ol_qrpay_create to w_acc_ol_app;
grant select on w_seq_w_ol_qrpay_down to w_acc_ol_app;
grant select on w_seq_w_ol_qrpay_order to w_acc_ol_app;
grant select on w_seq_w_ol_qrpay_orstatus to w_acc_ol_app;

grant select on W_SEQ_W_OL_QUE_MESSAGE to w_acc_ol_app;


grant execute on w_up_ol_chg_verify to w_acc_ol_app;
 
grant select, insert, update, delete on w_ol_qrcode_afc to w_acc_ol_app;
grant select on w_ol_qrcode_afc to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_qrcode_afc to w_acc_st_app;

grant select, insert, update, delete on w_ol_qrcode_order to w_acc_ol_app;
grant select on w_ol_qrcode_order to w_acc_ol_dev;
grant select, insert, update, delete on w_ol_qrcode_order to w_acc_st_app;


