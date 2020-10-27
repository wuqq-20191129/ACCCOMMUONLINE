insert into W_OL_PUB_FLAG (TYPE, CODE, CODE_TEXT, DESCRIPTION)
values ('3', '0', '正式票', 'HCE发票测试标志0正常,1测试');

commit;
select t.* from W_OL_PUB_FLAG t;
