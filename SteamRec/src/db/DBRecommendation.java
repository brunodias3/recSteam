package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import entities.Game;
import entities.User;

public class DBRecommendation {
	
	private static Connection con;
	
	public DBRecommendation(Connection con) {
		DBRecommendation.con = con;
	}
	
	public void create(User user, Map<Game, Double> recommendedGamesWithSimilarities) {
		String insert = "INSERT INTO Recommendation VALUES";
		String del = "DELETE FROM Recommendation WHERE id_user = " + Long.toString(user.getId());
		for(Map.Entry<Game, Double> entry: recommendedGamesWithSimilarities.entrySet()) {
			insert += "(" + Long.toString(user.getId()) + ", " + Integer.toString(entry.getKey().getGameId()) + ", -1), ";
		}
		insert = insert.substring(0, insert.length() - 2);
		insert += ";";
		try {
			PreparedStatement stmtDel = con.prepareStatement(del);
			stmtDel.executeUpdate();
			stmtDel.close();
			PreparedStatement stmt = con.prepareStatement(insert);
			stmt.execute();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}			
	}	
}
