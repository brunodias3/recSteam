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

import models.Game;

import org.apache.commons.text.similarity.CosineSimilarity;

public class Similarity {
	
	int wg = 4, wc = 2, wd = 1;
	
	public Map<CharSequence, Integer> tokenize (String s1) throws IOException{
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

	// Retorna cosine similarity de descrição, genero, categoria e desenvolvedor de todos os jogos com o perfil do usuário
	public List <Double> cosineSimilarity(String perfil, List <String> candidatos) throws IOException {
		List <Double> cosine = new ArrayList <Double>();
		Map<CharSequence, Integer> tokensPerfil = tokenize(perfil);
		for(int i=0; i<candidatos.size(); i++) {
			Map<CharSequence, Integer> tokensCandidato = tokenize(candidatos.get(i));
			cosine.add(new CosineSimilarity().cosineSimilarity(tokensPerfil, tokensCandidato));
		}
		return cosine;
	}
	// Calcula similaridade 
	public Map <Game, Double> allSimilarity(int quantidade, List<Game> candidatos, List<Double> description, List<Double> genres, List<Double> categories){
		Map <Game, Double> similaridades = new HashMap<Game, Double>();
		for(int i=0; i<description.size(); i++) {
			double media = (wd*description.get(i) + wg*(genres.get(i)) + wc*(categories.get(i)))/(wd+wg+wc);
			similaridades.put(candidatos.get(i), media);
		}
		return recommendation(quantidade, similaridades);
	}
	
	public Map <Game, Double> recommendation(int quantidade, Map<Game, Double> similaridades){
		similaridades = similaridades
						.entrySet()
						.stream()
						.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		int i = 0;
		Map<Game, Double> recommendation = new LinkedHashMap<Game, Double>();
		for(Map.Entry<Game, Double> entry: similaridades.entrySet()) {
			if(i == quantidade) {
				break;
			}
			recommendation.put(entry.getKey(), entry.getValue());
			i++;
		}
		return recommendation;
	}
}
