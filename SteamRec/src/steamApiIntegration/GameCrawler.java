package steamApiIntegration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import models.DataGetter;

public class GameCrawler {
	  
	  public static void saveGameData(JSONObject gameData, int gameId) {
		  try {
			  String insertGame = "INSERT INTO Game VALUES ";
			  String gameDescription = (Jsoup.parse(gameData.getString("detailed_description")).text()).replace('\'', '’').replace('\"', '’');
			  insertGame += "(" + String.valueOf(gameId) + ", \"" + gameData.getString("name").replace('\'', '’').replace('\"', '’') + "\", \"" + gameDescription + "\", ";
			  JSONArray gameCategoriesJson = gameData.getJSONArray("categories");
			  JSONArray gameGenresJson = gameData.getJSONArray("genres");
			  JSONArray gameDevelopersJson = gameData.getJSONArray("developers");
			  String gameCategories = "\"";
			  for(int i = 0; i < gameCategoriesJson.length(); i++) {
				  gameCategories += (gameCategoriesJson.getJSONObject(i)).getString("description").replace('\'', '’').replace('\"', '’');
				  if(i < gameCategoriesJson.length() - 1) {
					  gameCategories += ", ";
				  } else {
					  gameCategories += "\"";
				  }
			  }
			  String gameDevelopers = "\"";
			  for(int i = 0; i < gameDevelopersJson.length(); i++) {
				  gameDevelopers += (gameDevelopersJson.getString(i)).replace('\'', '’').replace('\"', '’');
				  if(i < gameDevelopersJson.length() - 1) {
					  gameDevelopers += ", ";
				  } else {
					  gameDevelopers += "\"";
				  }
			  }
			  String gameGenres = "\"";
			  for(int i = 0; i < gameGenresJson.length(); i++) {
				  gameGenres += (gameGenresJson.getJSONObject(i)).getString("description").replace('\'', '’').replace('\"', '’');
				  if(i < gameGenresJson.length() - 1) {
					  gameGenres += ", ";
				  } else {
					  gameGenres += "\"";
				  }
			  }
			  insertGame += gameCategories + ", " + gameDevelopers + ", " + gameGenres + ")";
			  try {
				 Connection con = new DataGetter().getConnection(); 
				 PreparedStatement stmt = con.prepareStatement(insertGame);
				 stmt.execute();
				 con.close();
				 stmt.close();
				 System.out.println("Success! " + gameId);				 
			  } catch(SQLException | ClassNotFoundException e) {
				  System.out.println("SQL Exception with SQL: " + insertGame);
				  e.printStackTrace();				  
			  }
		  } catch(Exception e) {
			  System.out.println("Another exception with gameId " + gameId);
			  e.printStackTrace();			  
		  }
	  }
	  
	  public static boolean isGameRegistered(int gameId) {
		  String gameSelect = "SELECT * FROM Game WHERE Game.id = " + gameId;
		  boolean gameRegistered = false;
		  try {
				Connection con = new DataGetter().getConnection();
				PreparedStatement stmt = con.prepareStatement(gameSelect);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					gameRegistered = true;
				}
				rs.close();
				con.close();
				stmt.close();
			} catch(SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}		  
		  return gameRegistered;
	  }

	  public static void main(String[] args) throws IOException, JSONException {
		JsonReader apiReader = new JsonReader();
	    JSONObject allGamesJson = apiReader.readJsonFromUrl("http://api.steampowered.com/ISteamApps/GetAppList/v0002/?key=" .concat(apiReader.steamApiAccessKey).concat("&format=json"));
	    JSONObject allGamesJsonList = (allGamesJson.getJSONObject("applist"));
	    JSONArray gamesArray = allGamesJsonList.getJSONArray("apps");
	    int gamesCrawled = 0;
	    for(int i = 0; i < gamesArray.length(); i++) {
	    	gamesCrawled++;
	    	JSONObject game = gamesArray.getJSONObject(i);
	    	int gameId = game.getInt("appid");
	    	if(isGameRegistered(gameId)) {
	    		System.out.println("Game " + gameId + " already registered");
	    	} else { 
		    	JSONObject gameDetails = apiReader.readJsonFromUrl("https://store.steampowered.com/api/appdetails?appids=".concat(String.valueOf(gameId)));
		    	gameDetails = gameDetails.getJSONObject(String.valueOf(gameId));
		    	if(gameDetails.getBoolean("success")) {
		    		JSONObject gameData = gameDetails.getJSONObject("data");
		    		saveGameData(gameData, gameId);
		    	}
	    	}
	    	if(gamesCrawled == 150) {
		    	try {
					Thread.sleep(5 * 60 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    	gamesCrawled = 0;
	    	}	    	
	    }
	  }
}
