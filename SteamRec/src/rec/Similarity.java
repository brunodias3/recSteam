package rec;

import java.util.ArrayList;
import models.*;
import info.debatty.java.stringsimilarity.*;

public class Similarity {

	public ArrayList < ArrayList<Double> >  descriptionSimilarity(ArrayList<Game> owned, ArrayList<Game> todos){
		ArrayList <ArrayList <Double> > similaridades = new ArrayList <ArrayList <Double> >();
		Cosine similarity = new Cosine(3);		
		for(int i=0; i<owned.size(); i++) {
			String ownedDesc = owned.get(i).getDescription();
			ArrayList<Double> simaux = new ArrayList <Double>();			
			for(int j=0; j<todos.size(); j++) {
				String candidatoDesc = todos.get(j).getDescription();
				simaux.add(similarity.similarity(ownedDesc, candidatoDesc));
			}
			similaridades.add(simaux);
			System.out.println(similaridades.get(0).size());			
		}
		return similaridades;
	}
}
