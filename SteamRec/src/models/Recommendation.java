package models;

public class Recommendation {
	private int gameId;
	private int eval;
	
	
	public Recommendation(int gameId, int eval) {
		this.gameId = gameId;
		this.eval = eval;
	}
	
	
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public int getEval() {
		return eval;
	}
	public void setEval(int eval) {
		this.eval = eval;
	}
}
