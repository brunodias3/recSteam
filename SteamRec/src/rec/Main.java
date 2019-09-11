package rec;
import java.util.ArrayList;
import java.util.Scanner;

import models.*;

public class Main {

	public static void main (String[] args) throws ClassNotFoundException {
		Scanner teclado = new Scanner(System.in);
		System.out.println("Digite um steam id cadastrado");
		Long steamid = teclado.nextLong();
		teclado.close();
		User user = new User(steamid);
		user.recuperarNome();
		user.recuperarJogos();
		ArrayList<Game> candidatos = user.recuperarTodosJogos();
		Similarity similarity = new Similarity();
		ArrayList <Game> owned = user.getOwnedGames();
		ArrayList <ArrayList <Double> > similaridades = similarity.descriptionSimilarity(owned, candidatos);
		for(int i=0; i<similaridades.size(); i++) {
			System.out.println("\n"  + owned.get(i).getName());
			for(int j=0; j<similaridades.get(i).size(); j++) {
				System.out.println(candidatos.get(j).getName() + ": " + Double.toString(similaridades.get(i).get(j)));
			}
		}
	}
}
