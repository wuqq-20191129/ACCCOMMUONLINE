/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @datetime 2018-3-22 9:56:54
 * @author lind
 */
public class TestOracle {

    public static void main(String[] args) {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@localhost:1521:wacc";
        String user = "xx";
        String password = "xx";
        
        Connection cn = null;
        PreparedStatement pstmt = null;
        String sql = "insert into w_op_mb_info(INFO_LEVEL,INFO_FLAG,info_no,remark) values('1','1','1','روستاهای اثر تاریخی')";
        
        try {
            Class.forName(driver);
            cn = DriverManager.getConnection(url, user, password);
            pstmt = cn.prepareStatement(sql);
            pstmt.executeUpdate();
            
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(cn==null){
                    cn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
