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
			// TODO Auto-generated catch block
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
		Scanner teclado = new Scanner(System.in);
		System.out.println("Digite um steam id cadastrado");
		Long steamid = teclado.nextLong();
		teclado.close();
		User user = new User(steamid);
		user.recuperarNome();
		user.recuperarJogos();
		List<Game> candidatos = user.recuperarTodosJogos();
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
		List<String> descCand = new ArrayList<String>();
		List<String> genCand = new ArrayList<String>();
		List<String> catCand = new ArrayList<String>();
		String descOwned = "";
		String genOwned = "";
		String catOwned = "";
		for(int i=0; i<owned.size(); i++) {
			descOwned += owned.get(i).getDescription() + " ";
			genOwned += owned.get(i).getGenres() + " ";
			catOwned += owned.get(i).getCategories() + " ";
		}
		for(int i=0; i<candidatos.size(); i++) {
			descCand.add(candidatos.get(i).getDescription());
			genCand.add(candidatos.get(i).getGenres());
			catCand.add(candidatos.get(i).getCategories());
		}
		List<Double> cosineDesc = similarity.cosineSimilarity(descOwned,descCand);
		List<Double> cosineGen = similarity.cosineSimilarity(genOwned,genCand);
		List<Double> cosineCat = similarity.cosineSimilarity(catOwned,catCand);
		Map<Game, Double> recomendacoes = similarity.allSimilarity(5, candidatos, cosineDesc,cosineGen,cosineCat);
		for(Map.Entry<Game, Double> entry: recomendacoes.entrySet()) {
			System.out.println(entry.getKey().getName() + ": " + entry.getValue());
		}
		user.registrarRecomendacao(recomendacoes);
	}
}
