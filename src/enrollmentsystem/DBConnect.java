/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enrollmentsystem;

import java.sql.*;

/**
 *
 * @author Jesse Benjamin Cerbo
 */
public class DBConnect {

    public static final String DEFAULT_DB_NAME = "enrollmentsystem"; // CONSTANT VARIABLE: Database Name
    public static final String DEFAULT_DB_USER = "dbadmin"; // CONSTANT VARIABLE: Username
    public static final String DEFAULT_DB_PASS = "dbadmin"; // CONSTANT VARIABLE: Password

    public Connection con;
    public Statement st;
    public ResultSet rs;

    /**
     * Default Constructor
     */
    public DBConnect() {
        String dbname = DEFAULT_DB_NAME;
        String user = DEFAULT_DB_NAME;
        String pass = DEFAULT_DB_NAME;
        if (EnrollmentSystem.selected_database != null && !EnrollmentSystem.selected_database.trim().isEmpty()) {
            dbname = EnrollmentSystem.selected_database;
        }
        if (EnrollmentSystem.logged_in_username != null && !EnrollmentSystem.logged_in_username.trim().isEmpty()) {
            user = EnrollmentSystem.logged_in_username;
        }
        if (EnrollmentSystem.logged_in_password != null && !EnrollmentSystem.logged_in_password.trim().isEmpty()) {
            pass = EnrollmentSystem.logged_in_password;
        }
        connect("jdbc:mysql://localhost:3306/" + dbname, user, pass);
    }

    /**
     * Constructor: change Database
     * 
     * @param dbname Name of the database
     */
    public DBConnect(String dbname) {
        String user = DEFAULT_DB_NAME;
        String pass = DEFAULT_DB_NAME;
        if (EnrollmentSystem.logged_in_username != null && !EnrollmentSystem.logged_in_username.trim().isEmpty()) {
            user = EnrollmentSystem.logged_in_username;
        }
        if (EnrollmentSystem.logged_in_password != null && !EnrollmentSystem.logged_in_password.trim().isEmpty()) {
            pass = EnrollmentSystem.logged_in_password;
        }
        connect("jdbc:mysql://localhost:3306/" + dbname, user, pass);
    }

    /**
     * 
     * @param user
     * @param password
     */
    public DBConnect(String user, String password) {
        connect("jdbc:mysql://localhost:3306", user, password);
    }

    /**
     * Constructor: Change Database and User
     * 
     * @param dbname   Name of the database
     * @param user     The user to use when logging into the database
     * @param password The password to use when logging into the database
     */
    public DBConnect(String dbname, String user, String password) {
        connect("jdbc:mysql://localhost:3306/" + dbname, user, password);
    }

    /**
     * 
     * @param url
     * @param user
     * @param password
     */
    private void connect(String url, String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            System.out.println("Connected successfully");
        } catch (Exception ex) {
            System.out.println("Connection failed: " + ex);
        }
    }
}