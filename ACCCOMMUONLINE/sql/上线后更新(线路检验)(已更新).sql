-- Create table
create table W_ACC_OL.W_OL_COD_LC_LINE
(
  line_id   CHAR(2),
  line_name VARCHAR2(50),
  ip        VARCHAR2(15)
);
grant select, insert, update, delete on W_ACC_OL.W_OL_COD_LC_LINE to W_ACC_OL_APP;
grant select on W_ACC_OL.W_OL_COD_LC_LINE to W_ACC_OL_DEV;
grant select, insert, update, delete on W_ACC_OL.W_OL_COD_LC_LINE to W_ACC_ST_APP;

insert into W_ACC_OL.W_OL_COD_LC_LINE(line_id, line_name, ip) values('80', 'HCE' , '10.99.12.108');
insert into W_ACC_OL.W_OL_COD_LC_LINE(line_id, line_name, ip) values('81', 'HCE1' , '10.99.12.200');
insert into W_ACC_OL.W_OL_COD_LC_LINE(line_id, line_name, ip) values('82', 'HCE2' , '10.99.10.111');
commit;
