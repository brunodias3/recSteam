package models;

import java.sql.*;
import java.util.ArrayList;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Game {

	private int gameId;
	private String name;
	private ArrayList<String> genres;
	private ArrayList<String> categories;
	private ArrayList<String> developers;
	private ArrayList<Game> ownedGames;
	private String description;
	
	public Game(int gameId) {
		this.gameId = gameId;
	}

	public ArrayList<String> getGenres() {
		return genres;
	}

	public void setGenres(ArrayList<String> genres) {
		this.genres = genres;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}

	public ArrayList<String> getDevelopers() {
		return developers;
	}

	public void setDevelopers(ArrayList<String> developers) {
		this.developers = developers;
	}

	public ArrayList<Game> getOwnedGames() {
		return ownedGames;
	}

	public void setOwnedGames(ArrayList<Game> ownedGames) {
		this.ownedGames = ownedGames;
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
	
	
	public void recuperarNomeEDescricao() throws ClassNotFoundException {
		String sql = "SELECT name, description FROM Game WHERE id = " + Integer.toString(this.gameId);
		try {
			Connection con = new DataGetter().getConnection();			
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				this.setName(rs.getString("name"));
				this.setDescription(rs.getString("description"));
			}
			else {
				System.out.println("Não existe um jogo com esse id no banco.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void recuperarGeneros(String nome) {
		String sql = "SELECT Searched_Game.name, Genres.id_game, Genres.genre FROM Genres, "
			    + "(SELECT id, name FROM Game WHERE name = '" + nome + "') AS Searched_Game "
			    + "WHERE Searched_Game.id = Genres.id_game";
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			ArrayList<String> genres = new ArrayList<String>();
			while(rs.next()) {
				genres.add(rs.getString("genre"));
			}
			this.setGenres(genres);
			con.close();
			stmt.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void recuperarGeneros(int id) {
		String sql = "SELECT Searched_Game.name, Genres.id_game, Genres.genre FROM Genres, "
			    + "(SELECT id, name FROM Game WHERE id = " + Integer.toString(id) + " ) AS Searched_Game "
			    + "WHERE Searched_Game.id = Genres.id_game";
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			ArrayList<String> genres = new ArrayList<String>();
			while(rs.next()) {
				genres.add(rs.getString("genre"));
			}
			this.setGenres(genres);
			con.close();
			stmt.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void recuperarCategorias(String nome) {
		String sql = "SELECT Searched_Game.name, Categories.id_game, Categories.category FROM Categories, "
			    + "(SELECT id, name FROM Game WHERE name = '" + nome + "') AS Searched_Game "
			    + "WHERE Searched_Game.id = Categories.id_game";
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			ArrayList<String> categories = new ArrayList<String>();
			while(rs.next()) {
				categories.add(rs.getString("category"));
			}
			this.setCategories(categories);
			con.close();
			stmt.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void recuperarCategorias(int id) {
		String sql = "SELECT Searched_Game.name, Categories.id_game, Categories.category FROM Categories, "
			    + "(SELECT id, name FROM Game WHERE id = " + Integer.toString(id) + " ) AS Searched_Game "
			    + "WHERE Searched_Game.id = Categories.id_game";
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			ArrayList<String> categories = new ArrayList<String>();
			while(rs.next()) {
				categories.add(rs.getString("category"));
			}
			this.setCategories(categories);
			con.close();
			stmt.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void recuperarDesenvolvedores(String nome) {
		String sql = "SELECT Searched_Game.name, Developers.id_game, Developers.developer FROM Developers, "
			    + "(SELECT id, name FROM Game WHERE name = '" + nome + "') AS Searched_Game "
			    + "WHERE Searched_Game.id = Developers.id_game";
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			ArrayList<String> developers = new ArrayList<String>();
			while(rs.next()) {
				developers.add(rs.getString("developer"));
			}
			this.setDevelopers(developers);
			con.close();
			stmt.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void recuperarDesenvolvedores(int id) {
		String sql = "SELECT Searched_Game.name, Developers.id_game, Developers.developer FROM Developers, "
			    + " (SELECT id, name FROM Game WHERE id = " + Integer.toString(id) + " ) AS Searched_Game "
			    + " WHERE Searched_Game.id = Developers.id_game";
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			ArrayList<String> developers = new ArrayList<String>();
			while(rs.next()) {
				developers.add(rs.getString("developer"));
			}
			this.setDevelopers(developers);
			con.close();
			stmt.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}		
}
