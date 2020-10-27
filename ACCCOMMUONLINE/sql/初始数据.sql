insert into W_OL_PUB_FLAG (TYPE, CODE, CODE_TEXT, DESCRIPTION)
values ('1', '03', 'BOM', '允许充值设备类型');

insert into W_OL_PUB_FLAG (TYPE, CODE, CODE_TEXT, DESCRIPTION)
values ('1', '02', 'TVM', '允许充值设备类型');

insert into W_OL_PUB_FLAG (TYPE, CODE, CODE_TEXT, DESCRIPTION)
values ('1', '10', 'ISM', '允许充值设备类型');

commit;


insert into w_ol_qrpay_id (qrpay_id) values (0);
commit;

insert into w_ol_sys_version
  (version_no, operator_id, valid_date, del_desc, update_desc, add_desc, note)
values
  (v_version_no, v_operator_id, v_valid_date, v_del_desc, v_update_desc, v_add_desc, v_note);
