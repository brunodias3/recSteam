package rec;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Map;
import java.util.Scanner;
import models.DataGetter;
import models.Game;
import models.User;

public class Main {

	public static List<Double> getPlaytimes() throws ClassNotFoundException{
		String sql = "SELECT Owned_Games.playtime_2weeks FROM Owned_Games";
		List<Double> playtimes = new ArrayList<Double>();
		try {
			Connection con = new DataGetter().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				if(rs.getInt("playtime_2weeks") != 0) {
					playtimes.add((double)rs.getInt("playtime_2weeks"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return playtimes;
	}
	
	public static double mediaPlaytime(List<Double> playtimes) throws ClassNotFoundException {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(int i=0; i<playtimes.size(); i++) {
			stats.addValue(playtimes.get(i));
		}
		return stats.getMean();
	}
	
	public static double[] percentilePlaytime(List<Double> playtimes) throws ClassNotFoundException{
		Percentile stats = new Percentile();
		double[] values = ArrayUtils.toPrimitive(playtimes.toArray(new Double[playtimes.size()]));
		double[] percentiles = {stats.evaluate(values, 20), stats.evaluate(values, 40), stats.evaluate(values, 60), stats.evaluate(values, 80)};
		return percentiles;
	}
	
	public static void main (String[] args) throws ClassNotFoundException, IOException {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Digite um steam id cadastrado");
		Long steamid = keyboard.nextLong();
		keyboard.close();
		User user = new User(steamid);
		user.getUserName();
		user.getUserGames();
		List<Game> gamesNotOwnedByUser = Game.getNotOwnedGames(user);
		Similarity similarity = new Similarity();
		Map<Game, Integer> ownedGames = user.getOwnedGames();
		List<Game> owned = new ArrayList<Game>();
		List<Double> playtimes2w = getPlaytimes();
		double media = mediaPlaytime(playtimes2w);
		percentilePlaytime(playtimes2w);
		System.out.println(media);
		for(Map.Entry<Game, Integer> entry: ownedGames.entrySet()) {
			if((double)entry.getValue() >= media) {
				owned.add(entry.getKey());
			}
		}
		List<String> descCandidates = new ArrayList<String>();
		List<String> genCandidates = new ArrayList<String>();
		List<String> catCandidates = new ArrayList<String>();
		String descOwned = "";
		String genOwned = "";
		String catOwned = "";
		for(int i=0; i<owned.size(); i++) {
			descOwned += owned.get(i).getDescription() + " ";
			genOwned += owned.get(i).getGenres() + " ";
			catOwned += owned.get(i).getCategories() + " ";
		}
		for(int i=0; i<gamesNotOwnedByUser.size(); i++) {
			descCandidates.add(gamesNotOwnedByUser.get(i).getDescription());
			genCandidates.add(gamesNotOwnedByUser.get(i).getGenres());
			catCandidates.add(gamesNotOwnedByUser.get(i).getCategories());
		}
		List<Double> cosineDesc = similarity.cosineSimilarity(descOwned,descCandidates);
		List<Double> cosineGen = similarity.cosineSimilarity(genOwned,genCandidates);
		List<Double> cosineCat = similarity.cosineSimilarity(catOwned,catCandidates);
		Map<Game, Double> recommendations = similarity.allSimilarity(5, gamesNotOwnedByUser, cosineDesc,cosineGen,cosineCat);
		for(Map.Entry<Game, Double> entry: recommendations.entrySet()) {
			System.out.println(entry.getKey().getName() + ": " + entry.getValue());
		}
		user.registerRecommendation(recommendations);
	}
}
