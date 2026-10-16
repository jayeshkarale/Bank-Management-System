package com.bts.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionConfig {
	
	static String url="jdbc:mysql://localhost:3306/bankaccount";
	static String username = "root";
	static String password = "J4YE5H@sql";
	
	public static Connection getConnection() {
		Connection con = null;
		
		try {
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
}
