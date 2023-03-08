package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUser {
	
	private static Connection con;
	
	public DBUser(Connection con) {
		DBUser.con = con;
	}
	
	public String findName(long userId) throws Exception {
		String sql = "SELECT display_name FROM User WHERE id = " + Long.toString(userId);
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return rs.getString("display_name");
			}
			else {
				throw new Exception("Não existe um usuário com esse id no banco.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}	
}
