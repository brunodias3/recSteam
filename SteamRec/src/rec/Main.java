package rec;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import db.DBGame;
import db.DBRecommendation;
import db.DBUser;
import db.DataGetter;
import entities.Game;
import entities.User;

public class Main {
	
	public static double averagePlaytime(List<Double> playtimes) throws ClassNotFoundException {
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
		try {
			Scanner keyboard = new Scanner(System.in);
			System.out.println("Digite um steam id cadastrado");
			Long steamid = keyboard.nextLong();
			keyboard.close();
			
			Connection con = new DataGetter().getConnection();
			DBGame gamesDBModel = new DBGame(con);
			DBUser userDBModel = new DBUser(con);
			DBRecommendation recommendationDBModel = new DBRecommendation(con);
			
			User user = new User(steamid);
			try {
				user.setName(userDBModel.findName(user.getId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			user.setOwnedGames(gamesDBModel.findWithPlaytimeByUserId(user.getId()));
			
			List<Game> recommendationCandidates = gamesDBModel.findNotOwnedGames(user);
			List<Double> playtimes = DBGame.getAllGamesPlaytimes();
			double averagePlaytime = averagePlaytime(playtimes);

			Map<String, String> userProfile = user.getUserProfile(averagePlaytime);
			
			Map<Game, Double> recommendedGamesWithSimilarity = SimilarityCalculator.calculateMostSimilar(5, userProfile, recommendationCandidates);
			for(Map.Entry<Game, Double> entry: recommendedGamesWithSimilarity.entrySet()) {
				System.out.println(entry.getKey().getName() + ": " + entry.getValue());
			}
			recommendationDBModel.create(user, recommendedGamesWithSimilarity);
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
