package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Game;
import entities.User;

public class DBGame {
	
	private static Connection con;
	
	public DBGame(Connection con) {
		DBGame.con = con;
	}
	
	public List<Game> findNotOwnedGames(User user) throws ClassNotFoundException{
		String sql = "SELECT * FROM Game WHERE ";
		List<Game> games = new ArrayList<Game>();
		List<Game> owned = new ArrayList<Game>(user.getOwnedGames().keySet());
		for(int i=0; i<owned.size(); i++) {
			if(i == 0) {
				sql += "Game.id != " + Integer.toString(owned.get(i).getGameId());
			}
			else {
				sql += " AND Game.id != " + Integer.toString(owned.get(i).getGameId());
			}
		}
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			int gameid;
			while(rs.next()) {
				gameid = rs.getInt("id");
				Game game = new Game(gameid);
				game = this.findGameInfo(game);
				games.add(game);
			}
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return games;		
	}
	
	public Game findGameInfo(Game game) throws ClassNotFoundException {
		String sql = "SELECT * FROM Game WHERE id = " + Integer.toString(game.getGameId());
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				game.setName(rs.getString("name"));
				game.setDescription(rs.getString("description").replace('’', '\''));
				game.setCategories(rs.getString("categories").replace(", In-App Purchases", ""));
				game.setGenres(rs.getString("genres").replace(", Free to Play", ""));
				game.setDevelopers(rs.getString("developers"));
			}
			else {
				System.out.println("Não existe um jogo com esse id no banco.");
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return game;
	}	
	
	public Map<Game, Integer> findWithPlaytimeByUserId(long userId) throws ClassNotFoundException {
		String sql = "SELECT Owned_Games.id_game, Owned_Games.playtime_forever FROM Owned_Games" + 
						" WHERE Owned_Games.id_user = " + Long.toString(userId);
		Map<Game, Integer> games = new HashMap<Game, Integer>();		
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			int gameid, playtime;
			while(rs.next()) {
				gameid = rs.getInt("id_game");
				playtime = rs.getInt("playtime_forever");
				Game game = new Game(gameid);
				game = this.findGameInfo(game);
				games.put(game, playtime);
			}
			stmt.close();
			return games;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Double> getAllGamesPlaytimes() throws ClassNotFoundException{
		String sql = "SELECT Owned_Games.playtime_forever FROM Owned_Games";
		List<Double> playtimes = new ArrayList<Double>();
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				if(rs.getInt("playtime_forever") != 0) {
					playtimes.add((double)rs.getInt("playtime_forever"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return playtimes;
	}	
}
