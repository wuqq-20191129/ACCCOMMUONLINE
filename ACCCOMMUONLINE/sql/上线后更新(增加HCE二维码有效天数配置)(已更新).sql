insert into w_acc_ol.W_OL_PUB_FLAG (TYPE, CODE, CODE_TEXT, DESCRIPTION)
values ('2', '3', '天数', 'HCE二维码有有效时间');
commit;

select * from W_OL_PUB_FLAG t;
