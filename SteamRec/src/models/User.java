package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {
	private long id;
	private String name;
	private ArrayList<Game> ownedGames;
	
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
	public ArrayList<Game> getOwnedGames() {
		return ownedGames;
	}
	public void setOwnedGames(ArrayList<Game> ownedGames) {
		this.ownedGames = ownedGames;
	}
	
	public boolean recuperarNome() throws ClassNotFoundException {
		String sql = "SELECT display_name FROM User WHERE id = " + Long.toString(this.id);
		try {
			Connection con = new DataGetter().getConnection();			
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				this.setName(rs.getString("display_name"));
				System.out.println(this.getName() + " encontrado.");
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
		String sql = "SELECT Owned_Games.id_game FROM Owned_Games" + 
						" WHERE Owned_Games.id_user = " + Long.toString(this.id);
		ArrayList<Game> games = new ArrayList<Game>();		
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			int gameid;
			while(rs.next()) {
				gameid = rs.getInt("id_game");
				Game game = new Game(gameid);
				game.recuperarNomeEDescricao();
				game.recuperarCategorias(gameid);
				game.recuperarDesenvolvedores(gameid);
				game.recuperarGeneros(gameid);
				games.add(game);
			}
			this.setOwnedGames(games);
			con.close();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Game> recuperarTodosJogos() throws ClassNotFoundException{
		String sql = "SELECT Game.id, Game.name, Game.description FROM Game WHERE ";
		ArrayList<Game> games = new ArrayList<Game>();		
		for(int i=0; i<this.ownedGames.size(); i++) {
			if(i == 0) {
				sql += "Game.id != " + Integer.toString(this.ownedGames.get(i).getGameId());
			}
			else {
				sql += " AND Game.id != " + Integer.toString(this.ownedGames.get(i).getGameId());
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
				game.setName(rs.getString("name"));
				game.setDescription(rs.getString("description"));
				game.recuperarCategorias(gameid);
				game.recuperarDesenvolvedores(gameid);
				game.recuperarGeneros(gameid);
				games.add(game);
			}
			con.close();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return games;		
	}
}
