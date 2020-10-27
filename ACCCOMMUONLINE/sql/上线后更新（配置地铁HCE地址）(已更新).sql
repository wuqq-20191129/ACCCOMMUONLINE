update w_acc_st.w_st_cod_lcc_line set lcc_line_name='地铁HCE' where lcc_line_id='80';
delete w_acc_st.w_st_cod_lcc_line where lcc_line_id='81';
--insert into w_acc_st.w_st_cod_lcc_line(lcc_line_id, lcc_line_name, lcc_ip)values('81','银行HCE','10.60.1.2');
insert into w_acc_ol.w_ol_cod_lc_line(line_id, line_name, ip) values ('80','地铁HCE','10.99.12.60');
--insert into w_acc_ol.w_ol_cod_lc_line(line_id, line_name, ip) values ('81','银行HCE','10.60.1.2');
commit;
