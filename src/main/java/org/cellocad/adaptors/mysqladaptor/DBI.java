package org.cellocad.adaptors.mysqladaptor;
/**
 * Created by Bryan Der on 3/26/14.
 */

import java.sql.*;

/**
 * Not used: MySQL replaced by UCF json format.
 *
 */
public class DBI {


    /**
     *
     * execute MySQL query, get result set _rs
     *
     */
    public DBI(String sql) {
        _con = null;
        _pst = null;
        _rs  = null;
        _sql_cmd = sql;

        /**
         * Not good practice to have plaintext here
         */
        String USER = "";
        String PASSWORD = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            _con = DriverManager.getConnection(URL, USER, PASSWORD);
            _pst = _con.createStatement();
            _rs = _pst.executeQuery(_sql_cmd);

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            _con.close();
            _pst.close();
            _rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    /////////////////////////
    //
    // Getters and Setters
    //
    /////////////////////////
    public ResultSet get_rs() {
        return _rs;
    }



    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////
    private static final String URL="jdbc:mysql://127.0.0.1:3306/cellodb";

    private Connection _con;
    private Statement _pst;
    private ResultSet _rs;
    private String _sql_cmd;

};
