package rec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.ClassicAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import entities.Game;

import org.apache.commons.text.similarity.CosineSimilarity;

public class SimilarityCalculator {
	
	static int weightGen = 4, weightCat = 2, weightDesc = 1;
	
	public static Map<CharSequence, Integer> tokenize (String s1) throws IOException{
		Map<CharSequence, Integer> tokens = new HashMap<CharSequence, Integer>();
		Analyzer analyzer = new ClassicAnalyzer();
		TokenStream stream = analyzer.tokenStream(null, s1);
		stream.reset();		
		while(stream.incrementToken()) {
			String t = stream.getAttribute(CharTermAttribute.class).toString();
			Integer q = tokens.get(t);
			if(q == null) {
				tokens.put(t, 1);
			}
			else {
				tokens.put(t, q+1);
			}
		}
		analyzer.close();
		return tokens;
	}

	// Returns the cosine similarity between all recommendation candidates' descriptions, genres or categories 
	// and the user profile, here defined as a String containing the description, genre or category of user most played games
	public static List <Double> calculateCosineSimilarity(String userProfileInfo, List <String> recommendationCandidatesInfo) throws IOException {
		List <Double> cosine = new ArrayList <Double>();
		Map<CharSequence, Integer> userProfileTokens = tokenize(userProfileInfo);
		for(int i=0; i<recommendationCandidatesInfo.size(); i++) {
			Map<CharSequence, Integer> candidatesTokens = tokenize(recommendationCandidatesInfo.get(i));
			cosine.add(new CosineSimilarity().cosineSimilarity(userProfileTokens, candidatesTokens));
		}
		return cosine;
	}
	
	public static Map <Game, Double> getRecommendedGames(int recommendationAmmount, Map<Game, Double> gamesSimilarities){
		gamesSimilarities = gamesSimilarities
						.entrySet()
						.stream()
						.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		int i = 0;
		Map<Game, Double> recommendation = new LinkedHashMap<Game, Double>();
		for(Map.Entry<Game, Double> entry: gamesSimilarities.entrySet()) {
			if(i == recommendationAmmount) {
				break;
			}
			recommendation.put(entry.getKey(), entry.getValue());
			i++;
		}
		return recommendation;
	}	
	
	public static Map <Game, Double> calculateMostSimilar(
			int recommendationAmount, 
			Map<String, String> userProfile,
			List<Game> recommendationCandidates
	){
		List<String> recCandidatesDescriptions = new ArrayList<String>();
		List<String> recCandidatesGenres = new ArrayList<String>();
		List<String> recCandidatesCategories = new ArrayList<String>();			
		for(int i=0; i<recommendationCandidates.size(); i++) {
			recCandidatesDescriptions.add(recommendationCandidates.get(i).getDescription());
			recCandidatesGenres.add(recommendationCandidates.get(i).getGenres());
			recCandidatesCategories.add(recommendationCandidates.get(i).getCategories());
		}
	
		List<Double> descriptionsCosineSimilarity;
		List<Double> genresCosineSimilarity;
		List<Double> categoriesCosineSimilarity;
		try {
			categoriesCosineSimilarity = calculateCosineSimilarity(userProfile.get("userCategoriesProfile"),recCandidatesCategories);
			descriptionsCosineSimilarity = calculateCosineSimilarity(userProfile.get("userDescriptionsProfile"),recCandidatesDescriptions);
			genresCosineSimilarity = calculateCosineSimilarity(userProfile.get("userGenresProfile"),recCandidatesGenres);			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Map <Game, Double> gamesSimilarities = new HashMap<Game, Double>();
		for(int i=0; i<descriptionsCosineSimilarity.size(); i++) {
			double similaritiesAverage = 
					(weightDesc*descriptionsCosineSimilarity.get(i) + 
					weightGen*(genresCosineSimilarity.get(i)) + 
					weightCat*(categoriesCosineSimilarity.get(i)))
					/(weightDesc+weightGen+weightCat);
			
			gamesSimilarities.put(recommendationCandidates.get(i), similaritiesAverage);
		}
		return getRecommendedGames(recommendationAmount, gamesSimilarities);
	}
}
