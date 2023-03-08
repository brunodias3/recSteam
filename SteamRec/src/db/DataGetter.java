package db;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class DataGetter {
	
	private String dbPassword;
	private String dbUser;
	
	public DataGetter() {
		this.dbPassword = "Br92246694!";
		this.dbUser = "steam_db_user";
	}
	
	public Connection getConnection() throws ClassNotFoundException {
        try {
        	Class.forName("com.mysql.jdbc.Driver");
            return (Connection) DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/steam_db?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false", dbUser, dbPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}