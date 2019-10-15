package models;

import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Game {

	private int gameId;
	private String name;
	private String genres;
	private String categories;
	private String developers;
	private String description;
	
	public Game(int gameId) {
		this.gameId = gameId;
	}

	public String getGenres() {
		return genres;
	}

	public void setGenres(String genres) {
		this.genres = genres;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getDevelopers() {
		return developers;
	}

	public void setDevelopers(String developers) {
		this.developers = developers;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}		
	
	public void recuperarInformacoes() throws ClassNotFoundException {
		String sql = "SELECT * FROM Game WHERE id = " + Integer.toString(this.gameId);
		try {
			Connection con = new DataGetter().getConnection();			
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				this.setName(rs.getString("name"));
				this.setDescription(rs.getString("description").replace('’', '\''));
				this.setCategories(rs.getString("categories").replace(", In-App Purchases", ""));
				this.setGenres(rs.getString("genres").replace(", Free to Play", ""));
				this.setDevelopers(rs.getString("developers"));
			}
			else {
				System.out.println("Não existe um jogo com esse id no banco.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
