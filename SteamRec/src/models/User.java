package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
	private long id;
	private String name;
	private Map<Game, Integer> ownedGames;
	private List<Recommendation> recommendations;

	public User(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<Game, Integer> getOwnedGames() {
		return ownedGames;
	}
	public void setOwnedGames(Map<Game, Integer> ownedGames) {
		this.ownedGames = ownedGames;
	}
	
	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}	
	
	public boolean recuperarNome() throws ClassNotFoundException {
		String sql = "SELECT display_name FROM User WHERE id = " + Long.toString(this.id);
		try {
			Connection con = new DataGetter().getConnection();			
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				this.setName(rs.getString("display_name"));
				return true;
			}
			else {
				System.out.println("Não existe um usuário com esse id no banco.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void recuperarJogos() throws ClassNotFoundException {
		String sql = "SELECT Owned_Games.id_game, Owned_Games.playtime_2weeks FROM Owned_Games" + 
						" WHERE Owned_Games.id_user = " + Long.toString(this.id);
		Map<Game, Integer> games = new HashMap<Game, Integer>();		
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			int gameid, playtime;
			while(rs.next()) {
				gameid = rs.getInt("id_game");
				playtime = rs.getInt("playtime_2weeks");
				Game game = new Game(gameid);
				game.recuperarInformacoes();
				games.put(game, playtime);
			}
			this.setOwnedGames(games);
			con.close();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Game> recuperarTodosJogos() throws ClassNotFoundException{
		String sql = "SELECT * FROM Game WHERE ";
		List<Game> games = new ArrayList<Game>();
		List<Game> owned = new ArrayList<Game>(this.ownedGames.keySet());
		for(int i=0; i<owned.size(); i++) {
			if(i == 0) {
				sql += "Game.id != " + Integer.toString(owned.get(i).getGameId());
			}
			else {
				sql += " AND Game.id != " + Integer.toString(owned.get(i).getGameId());
			}
		}
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			int gameid;
			while(rs.next()) {
				gameid = rs.getInt("id");
				Game game = new Game(gameid);
				game.recuperarInformacoes();
				games.add(game);
			}
			con.close();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return games;		
	}
	
	public void registrarRecomendacao(Map<Game, Double> recomendacoes) throws ClassNotFoundException {
		String sql = "INSERT INTO Recommendation VALUES";
		String del = "DELETE FROM Recommendation WHERE id_user = " + Long.toString(this.getId());
		for(Map.Entry<Game, Double> entry: recomendacoes.entrySet()) {
			sql += "(" + Long.toString(this.getId()) + ", " + Integer.toString(entry.getKey().getGameId()) + ", -1), ";
		}
		sql = sql.substring(0, sql.length() - 2);
		sql += ";";
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmtd = con.prepareStatement(del);
			stmtd.executeUpdate();
			stmtd.close();
			PreparedStatement stmt = con.prepareStatement(sql);			
			stmt.execute();
			con.close();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}			
	}
}
