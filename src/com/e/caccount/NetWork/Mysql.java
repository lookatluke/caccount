/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount.NetWork;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
// import java.util.logging.Level;
// import java.util.logging.Logger;

/**
 *
 * @author trito
 */
public class Mysql {
    ////////////////////////////////////////////////////////////////////////////
    //////////////////             SINGLE TON             //////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private static Mysql INSTANCE = null;

    public static Mysql getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Mysql();
        }
        return INSTANCE;
    }

    private String mysqlurl = "jdbc:mysql://coucou.myasustor.com:3304/shuylmanhan?serverTimezone=Asia/Seoul&useUnicode=true&characterEncoding=UTF-8&collation=utf8mb4_unicode_ci";
    private final String user_name = "SMH";
    private final String suer_pw = "ieoA*O.YnxyQfdUG";

    private boolean runQuery(PreparedStatement ps, Connection con) {
        try {
            int result = ps.executeUpdate();
            ps.close();
            con.close();

            if (result > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            // Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean insertQuery(String name, String offering, int amount, String date) {
        try {
            Connection con = DriverManager.getConnection(mysqlurl, user_name, suer_pw);
            PreparedStatement ps = con.prepareStatement("INSERT INTO Caaccount(name, offerings, amount, timestamp) VALUES (?, ?, ?, ?)");

            ps.setString(1, name);
            ps.setString(2, offering);
            ps.setInt(3, amount);
            ps.setString(4, date);

            return runQuery(ps, con);
        } catch (SQLException ex) {
            // Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean deleteQuery(String name, String offering, String date) {
        try {
            Connection con = DriverManager.getConnection(mysqlurl, user_name, suer_pw);
            PreparedStatement ps = con.prepareStatement("DELETE FROM Caaccount WHERE name = ? AND offerings = ? AND timestamp = ?");
            ps.setString(1, name);
            ps.setString(2, offering);
            ps.setString(3, date);

            return runQuery(ps, con);
        } catch (SQLException ex) {
            // Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean updateQuery(String name, String offering, int amount, String date) {
        try {
            Connection con = DriverManager.getConnection(mysqlurl, user_name, suer_pw);
            PreparedStatement ps = con.prepareStatement("UPDATE Caaccount SET amount = ? WHERE name = ? AND offerings = ? AND timestamp = ?");
            ps.setInt(1, amount);
            ps.setString(2, name);
            ps.setString(3, offering);
            ps.setString(4, date);

            return runQuery(ps, con);
        } catch (SQLException ex) {
            // Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
