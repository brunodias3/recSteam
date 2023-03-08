package entities;

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
}
