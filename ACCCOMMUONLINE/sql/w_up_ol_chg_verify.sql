create or replace procedure w_up_ol_chg_verify(p_message_id      in varchar2, --消息类型
                                             p_termina_no      in varchar2, --终端编号
                                             p_sam_logical_id  in varchar2, --sam卡逻辑卡号
                                             p_transation_seq  in number, --终端交易序列号
                                             p_branches_code   in varchar2, --网点编码
                                             p_pub_main_code   in varchar2, --发行者主编码
                                             p_pub_sub_code    in varchar2, --发行者子编码
                                             p_card_type       in varchar2, --票卡类型
                                             p_tk_logic_no     in varchar2, --票卡逻辑卡号
                                             p_tk_phy_no       in varchar2, --票卡物理卡号
                                             p_is_test_tk      in varchar2, --是否测试卡
                                             p_onl_tran_times  in number, --票卡联机交易计数
                                             p_offl_tran_times in number, --票卡脱机交易计数
                                             p_buss_type       in varchar2, --业务类型
                                             p_value_type      in varchar2, --值类型
                                             p_charge_fee      in number, --撤销金额
                                             p_balance         in number, --票卡余额
                                             p_effective_min   in number, --激活有效期，单位：分钟
                                             p_result          out varchar2)

  ----------------------------------------------如何保证充值和撤销都有确认消息-----------------------------------
  --功能:在线充值系统中相关校验
  --输出: 23-设备未激活或激活超过有效期
  --      24-冲正设备与充值设备不一致
  --      45-冲正金额与充值金额不一致
  --      61-冲正对应的充值原始记录不存在
  -- 增加ISM设备类型，同时修改设备类型查询表OL_PUB_FLAG
  -------------------------------------------------------------------------------
 as
  v_temp_number integer;
  --v_temp_charge_fee number;
  v_temp_date       date;
  v_temp_deviceType varchar(2); --设备类型
begin
  begin
    select substr(p_termina_no, 5, 2) into v_temp_deviceType from dual;
  end;
  --BOM 或者TVM/ISM设备才能进行充值等操作 (修改设备类型查询表OL_PUB_FLAG)
  select count(1) into v_temp_number from w_acc_ol.w_ol_pub_flag where code=v_temp_deviceType and type='1';
  if v_temp_number=0 then
    p_result := '60';
    return;
  end if;
  /**
  ---------------------设备是否激活或激活超过有效期-----------------------------------------------------------------------

  --只有BOM设备才需要激活
  if v_temp_deviceType = '03' then
    begin
      select count(*)
        into v_temp_number
        from w_acc_ol.w_ol_chg_activation t
       where return_code = '00'
         and t.err_code = '00'
         and termina_no = p_termina_no
         and sam_logical_id = p_sam_logical_id
         and sysdate between update_date and update_date + p_effective_min;
    exception
      when no_data_found then
        p_result := '23';
        return;
    end;
    if v_temp_number = 0 then
      p_result := '23';
      return;
    end if;
  end if;

  --dbms_output.put_line('设备未激活或激活超过有效期');**/
  ---------------------充值撤销申请--------------------------------------------------------------------------------------
  ---1，撤销前，有充值记录；2，撤销有有效期；3、不能连续撤销
  if p_message_id = '53' then
    begin
      select count(*)
        into v_temp_number
        from w_acc_ol.w_ol_chg_plus
       where termina_no = p_termina_no
         and sam_logical_id = p_sam_logical_id
         and transation_seq = p_transation_seq
         and branches_code = p_branches_code
         and pub_main_code = p_pub_main_code
         and pub_sub_code = p_pub_sub_code
         and card_type = p_card_type
         and tk_logic_no = p_tk_logic_no
         and tk_phy_no = p_tk_phy_no
         and is_test_tk = p_is_test_tk
            -- and onl_tran_times = p_onl_tran_times
            --and offl_tran_times = p_offl_tran_times
         and buss_type = '14'
         and value_type = p_value_type
         and charge_fee = p_charge_fee
         and balance = p_balance
         and status = '2';
    exception
      when no_data_found then
        insert into w_acc_ol.w_ol_chg_log_test
          (insert_time, msg)
        values
          (sysdate, '冲正对应的充值原始记录不存在');
        p_result := '61';
        return;
    end;
    if v_temp_number = 0 then
      insert into w_acc_ol.w_ol_chg_log_test
        (insert_time, msg)
      values
        (sysdate, '冲正对应的充值原始记录不存在');
      p_result := '61';
      return;
    end if;
    --有效期判断，先获取充值成功的最大序列号（即最近一次充值成功），再获取充值成功的时间，如果当前时间减去充值时间在有效期内，则可以撤销
    begin
      select max(water_no)
        into v_temp_number
        from w_acc_ol.w_ol_chg_plus
       where termina_no = p_termina_no
         and sam_logical_id = p_sam_logical_id
         and transation_seq = p_transation_seq-1
         and branches_code = p_branches_code
         and pub_main_code = p_pub_main_code
         and pub_sub_code = p_pub_sub_code
         and card_type = p_card_type
         and tk_logic_no = p_tk_logic_no
         and tk_phy_no = p_tk_phy_no
         and is_test_tk = p_is_test_tk
         and buss_type = '14'
         and value_type = p_value_type
         and charge_fee = p_charge_fee
         and balance = p_balance
         and status = '2';
    end;

    begin
      select update_date
        into v_temp_date
        from w_acc_ol.w_ol_chg_plus
       where water_no = v_temp_number;
    end;
    if sysdate - v_temp_date >  p_effective_min / 1440 then
      insert into w_acc_ol.w_ol_chg_log_test
        (insert_time, msg)
      values
        (sysdate, '撤销超过有效期，默认' || p_effective_min || '分钟');
      p_result := '51';
      return;
    end if;

    --不能重复撤销
    begin
      select count(*)
        into v_temp_number
        from w_acc_ol.w_ol_chg_sub
       where termina_no = p_termina_no
         and sam_logical_id = p_sam_logical_id
         and transation_seq = p_transation_seq
         and branches_code = p_branches_code
         and pub_main_code = p_pub_main_code
         and pub_sub_code = p_pub_sub_code
         and card_type = p_card_type
         and tk_logic_no = p_tk_logic_no
         and tk_phy_no = p_tk_phy_no
         and is_test_tk = p_is_test_tk
            -- and onl_tran_times = p_onl_tran_times
            --and offl_tran_times = p_offl_tran_times
         and buss_type = '18'
         and value_type = p_value_type
         and charge_fee = p_charge_fee
         and balance = p_balance
         and status = '2';
      if v_temp_number > 0 then
        insert into w_acc_ol.w_ol_chg_log_test
          (insert_time, msg)
        values
          (sysdate, '不能重复撤销');
        p_result := '61';
        return;
      end if;
    end;
  end if;

  p_result := '00';
  return;
  -----------------------------------------------------------------------------------------------------

end w_up_ol_chg_verify;
/
