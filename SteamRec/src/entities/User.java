package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

	private long id;
	private String name;
	private Map<Game, Integer> ownedGames;

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
	
	public List<Game> getUserMostPlayedGames(double averagePlaytime) {
		List<Game> userMostPlayedGames = new ArrayList<Game>();		
		for(Map.Entry<Game, Integer> entry: this.getOwnedGames().entrySet()) {
			double userGamePlaytime = (double)entry.getValue();
			if(userGamePlaytime >= averagePlaytime) {
				userMostPlayedGames.add(entry.getKey());
			}
		}
		return userMostPlayedGames;
	}
	
	public Map<String, String> getUserProfile(double averagePlaytime) {
		List<Game> userMostPlayedGames = this.getUserMostPlayedGames(averagePlaytime);
		String userGamesDescriptions = "";
		String userGamesGenres = "";
		String userGamesCategories = "";
		for(int i=0; i<userMostPlayedGames.size(); i++) {
			userGamesDescriptions += userMostPlayedGames.get(i).getDescription() + " ";
			userGamesGenres += userMostPlayedGames.get(i).getGenres() + " ";
			userGamesCategories += userMostPlayedGames.get(i).getCategories() + " ";
		}
		Map<String, String> userProfile = new HashMap<String, String>();
		userProfile.put("userGenresProfile", userGamesGenres);
		userProfile.put("userDescriptionsProfile", userGamesDescriptions);
		userProfile.put("userCategoriesProfile", userGamesCategories);
		return userProfile;	
	}
}
