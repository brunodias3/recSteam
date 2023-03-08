package steamApiIntegration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DataGetter;

public class UsersCrawler {
	
	public static boolean isUserRegistered(Long steamId) {
		  String userSelect = "SELECT * FROM User WHERE User.id = " + steamId;
		  boolean userRegistered = false;
		  try {
				Connection con = new DataGetter().getConnection();
				PreparedStatement stmt = con.prepareStatement(userSelect);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					userRegistered = true;
				}
				rs.close();
				con.close();
				stmt.close();
			} catch(SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}		  
		  return userRegistered;		
	}
	
	public static boolean userHasPlayed(JSONObject game) {
		if(game.getInt("playtime_forever") > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void saveUserGames(JsonReader apiReader, Long steamId) throws JSONException, IOException {
		JSONArray ownedGames = apiReader.readJsonFromUrl
				("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=" + apiReader.steamApiAccessKey + "&steamid=" + steamId 
				+ "&format=json&include_played_free_games=true")
				.getJSONObject("response")
				.getJSONArray("games");
		try { 
		String deleteOwnedGames = "DELETE FROM Owned_Games WHERE id_user = " + Long.toString(steamId);
		Connection con = new DataGetter().getConnection();
		PreparedStatement stmtDel = con.prepareStatement(deleteOwnedGames);
		stmtDel.executeUpdate();
		stmtDel.close();		
			for(int i = 0; i < ownedGames.length(); i++) {
				JSONObject game = ownedGames.getJSONObject(i);
				if(userHasPlayed(game)) {
					boolean canInsertOwnedGame = true;
					if(!GameCrawler.isGameRegistered(game.getInt("appid"))) {
				    	JSONObject gameDetails = apiReader.readJsonFromUrl("https://store.steampowered.com/api/appdetails?appids=" + game.getInt("appid"));
				    	gameDetails = gameDetails.getJSONObject(String.valueOf(game.getInt("appid")));
				    	canInsertOwnedGame = gameDetails.getBoolean("success");
				    	if(canInsertOwnedGame) {
				    		JSONObject gameData = gameDetails.getJSONObject("data");
				    		GameCrawler.saveGameData(gameData, game.getInt("appid"));
				    	}
					}
					if (canInsertOwnedGame) {
						int gamePlaytime2w = 0;
						if(game.has("playtime_2weeks")) {
							gamePlaytime2w = game.getInt("playtime_2weeks");
						}
						String insertOwnedGame = "INSERT INTO Owned_Games VALUES (" + steamId + ", " 
						+ game.getInt("appid") + ", " 
						+ gamePlaytime2w + ", " 
						+ game.getInt("playtime_forever")
						+ ")";
						PreparedStatement stmt = con.prepareStatement(insertOwnedGame);
						stmt.execute();
						stmt.close();
						System.out.println("Success! " + steamId + " " + game.getInt("appid"));
					}
				}
			}
			con.close();
		}  catch(SQLException | ClassNotFoundException e) {
			  e.printStackTrace();				  
		}
	}
	
	public static void saveUserData(JSONObject userProfile) {
		String insertUser = "INSERT INTO User VALUES (" + userProfile.getLong("steamid") + ", \"" + userProfile.getString("personaname") + "\")";
		  try {
			 Connection con = new DataGetter().getConnection(); 
			 PreparedStatement stmt = con.prepareStatement(insertUser);
			 stmt.execute();
			 con.close();
			 stmt.close();
			 System.out.println("Success! " + userProfile.getString("personaname"));				 
		  } catch(SQLException | ClassNotFoundException e) {
			  System.out.println("SQL Exception with SQL: " + insertUser);
			  e.printStackTrace();				  
		  }		
	}
	
	public static void main(String[] args) throws JSONException, IOException {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Insert an id to register a new user");
		JsonReader apiReader = new JsonReader();		
		Long steamId = keyboard.nextLong();
	    if(!isUserRegistered(steamId)) {
		    JSONObject userProfile = (apiReader.readJsonFromUrl
		    		("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + apiReader.steamApiAccessKey + "&steamids=" + steamId))
		    		.getJSONObject("response")
		    		.getJSONArray("players")
		    		.getJSONObject(0);	    	
	    	saveUserData(userProfile);
    		saveUserGames(apiReader, steamId);	    	
	    } else {
	    	System.out.println("User already registered. Do you wish to update User's owned games?");
	    	System.out.println("Type 1 for Yes");
	    	System.out.println("Type 2 for No");
	    	int option = keyboard.nextInt();
	    	if(option == 1) {
	    		saveUserGames(apiReader, steamId);
	    	}
	    }
		keyboard.close();			    
	}
}
