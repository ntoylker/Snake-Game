import java.util.Scanner; // import the Scanner class 
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;

public class Game {
	// VARIABLES
	int round;
	
	// METHODS 
	Game(){}
	Game(int r){round = r;}
	int getRound() {return round;}
	void setRound(int x) {round = x;}
	
	// c.
	static Map<Integer,Integer> setTurns(ArrayList<HeuristicPlayer> players)
	{
	
		int[] playerDice = new int[players.size()];	// array with players' die
		
		System.out.println("\nRolling the die to set turns: (smallest die plays first)");
		for(int playerId=0; playerId<players.size(); playerId++)		// generates random die for each player
		{
			playerDice[playerId] = (int)(1 + (Math.random() * 6));
			
			for(int j = 0; j<playerId; j++)	// if two players roll the same die, the last player will reroll
			{
				while(playerDice[playerId] == playerDice[j])
				{
					playerDice[playerId] = (int)(1 + (Math.random() * 6));
				}
			}
			
			System.out.println(players.get(playerId).getName() + " " + playerDice[playerId]);
		}
		System.out.println();
		
		
		TreeMap<Integer,Integer> PTurnOrdered = new  TreeMap<> (); 	// Ordered(low->high) Map, with dice as keys, playerId as values 
		
		for(int playerId=0; playerId<players.size(); playerId++)
		{
			PTurnOrdered.put(playerDice[playerId], playerId+1);
		}
		
		return PTurnOrdered;
		
	}
	
	
	// MAIN 
	/* PERIGRAFH:
	 * -> ζητείται input από τον χρήστη
	 * -> ανα round, πρέπει να πατηθεί το κουμπί του Enter
	 * Δυνατότητες μπορούν να κατασκευαστούν παρτίδες που περιέχουν:
	 * --> NxM ταμπλό, για NxM<1000
	 * --> unlimited players (για λόγους προβολής του παιχνιδιού, players<=9)
	 * --> unlimited snakes-ladders-presents (για λόγους προβολής του παιχνιδιού, everything<=10)
	 * --> έναν HeuristicPlayer
	 * Ιδιορυθμίες:
	 * } για λόγους προβολής, τα στοιχεία που εισάγετε στο ταμπλό, να είναι σε πλήθος <=10. (αλλιώς θα χαλάσει η ιδανική προβολή του ταμπλό)
	 * } αν το τελικό evaluation αποδεικνύει ισοπαλία, το παιχνίδι δεν εκτυπώσει ισοπαλία. Θα θεωρήσει ως νικητή αυτόν που τερμάτισε πρώτος
	 * } το ταμπλό ξεκινάει απο το 0 και φτάνει μέχρι το NxM-1
	 */
	public static void main(String[] args) {
		
		// VARIABLES
		int r,c,s,l,p;
		String ans;
		Scanner scan = new Scanner(System.in);
		Board board;
		Map<Integer,Integer> turns;
		ArrayList<HeuristicPlayer> players = new ArrayList<>();
		// parameters

		
		// CREATE BOARD:
		System.out.println("Set the dimensions of the board: ");
		r = Integer.parseInt(scan.nextLine());
		c = Integer.parseInt(scan.nextLine());
		System.out.println("Now, set the number of snakes, ladders and presents to exist on the board: ");
		s = Integer.parseInt(scan.nextLine());
		l = Integer.parseInt(scan.nextLine());
		p = Integer.parseInt(scan.nextLine());
		
		board = new Board(r,c,s,l,p);
		board.createBoard(); // will create the board according to the regulations
		board.createElementBoard();
		
		System.out.println("Would you like to involve a Heuristic Player? (y or n): ");
		ans = scan.nextLine();
		if(ans.contains("y") || ans.contains("Y"))
			players.add (new HeuristicPlayer(players.size()+1,"Heuristic Player",0,board,0) );
		
		System.out.println("How many players should the game have?: ");
		r = Integer.parseInt(scan.nextLine());
		for(int i=0; i<r; i++) {
			System.out.println("Name of #"+(i+1)+" player: ");
			ans = scan.nextLine();
			players.add (new HeuristicPlayer(players.size()+1,ans,0,board,0) );
		}
		
		
		// will set the turns for the game
		turns = setTurns(players);
		
		
		Game game = new Game(0);
		boolean game_over = false;
		int dice;
		int player_id_turn;
		int[] move_results = new int[4];
		
		// LET THE GAME BEGIN
		while(!game_over && (game.getRound()<=100))
		{
			System.out.println("Press Enter to Play the next round: ");
			while((ans = scan.nextLine())=="\n") {
				// WAIT
			}
			
			// AUJHSE TON GURO KATA ENA <- se auton briskomaste
			game.setRound(game.getRound()+1);
			if(game.getRound()==101) {
				System.out.println("100 rounds have been played, so the game ends!\n~GAME OVER");
				continue;
			}
			System.out.println("\n\t\t~ ROUND "+game.getRound()+" ~");
			
			// all the players shall roll the dice on their turn
			for(Map.Entry<Integer,Integer> m:turns.entrySet()) {    
				//(m.getKey()+" "+m.getValue());   
				player_id_turn = m.getValue();
				
				// check if is Heuristic or not
				// heuristic players will have a "Heuristic" in their name
				if((players.get(player_id_turn-1)).getName().contains("Heuristic") == true) { // IS heuristic
					// print the board
					(players.get(player_id_turn-1)).print_current_board();
					// save the current_position
					dice = (players.get(player_id_turn-1)).getPosition();
					
					// make the move
					(players.get(player_id_turn-1)).doNextMove();
					
					// check if WON
					if((players.get(player_id_turn-1)).getPosition() == board.getM()*board.getN()-1) {
						// winning die is: N*M-old_position <-saved in 'dice' variable
						System.out.println("-> With the die "+(board.getM()*board.getN()-1-dice)+", "+(players.get(player_id_turn-1)).getName()+" goes over the finishing line!\n~GAME OVER");
						game_over = true;
						break;
					}
					// print the info for the move
					(players.get(player_id_turn-1)).statistics();
					// print the NEW board
					(players.get(player_id_turn-1)).print_current_board();
					// print the line to distinct the player
					for(int i=0; i<board.getM()*9+1; i++) {
						System.out.print("_");
					}
					System.out.println();
				}
				else { // the player is NOT a heuristic
					// print the board
					(players.get(player_id_turn-1)).print_current_board();
					// roll the die
					dice = ((int)(Math.random()*66))%6 + 1;
					// print the die rolled
					System.out.println((players.get(player_id_turn-1)).getName() + " rolled a "+dice+".");
					// do the move
					move_results = (players.get(player_id_turn-1)).move(dice);
					// check if WON
					if(move_results[0] == board.getM()*board.getN()-1) {
						System.out.println("-> With the die "+dice+", "+(players.get(player_id_turn-1)).getName()+" goes over the finishing line!\n~GAME OVER");
						game_over = true;
						break;
					}
					// print the NEW board
					(players.get(player_id_turn-1)).print_current_board();
					// print the line to distinct the player
					for(int i=0; i<board.getM()*9+1; i++) {
						System.out.print("_");
					}
					System.out.println();					
				}
			}
			
			// print line to distinct game ROUNDS
			for(int i=0; i<board.getM()*9+1; i++) {
				System.out.print("_");
			}
			System.out.println();
			
		}

		// close the scan
		scan.close();
		
		HeuristicPlayer winner;
		double[] eval = new double[players.size()];
		double max = 0;
		int index_of_max;
		/* sthn stats tha exw apothikeusei int_arrays[3],
		 * me ta statistika gia kathe paikth. fidia-skales-dwra
		 */
		ArrayList<Integer[]> stats = new ArrayList<>();
		Integer[] temp = new Integer[3];
		temp[0] = 0;
		temp[1] = 0;
		temp[2] = 0;
		
		for(int i=0; i<players.size(); i++) {
			for(int j=0; j<players.get(i).getPath().size(); j++) {
				 temp[0] += (players.get(i).getPath()).get(j)[3];
				 temp[1] += (players.get(i).getPath()).get(j)[4];
				 temp[2] += (players.get(i).getPath()).get(j)[5];
			}
			stats.add(temp);
		}
		
		// eval its player
		for(int i=0; i<players.size(); i++) {
			eval[i] = players.get(i).getPosition()*0.7 + players.get(i).getScore()*0.3;
		}
		// find max eval
		max = eval[0];
		index_of_max = 0;
		for(int i=1; i<players.size(); i++) {
			if(eval[i]>max) {
				max = eval[i];
				index_of_max = i;
			}
		}
		
		// found winner
		winner = players.get(index_of_max);
		System.out.println("\n\n\tWINNER IS "+winner.getName());
		System.out.println("\n\tSTATISTICS:");
		for(int i=0; i<players.size(); i++) {
			System.out.print(players.get(i).getName()+": "+players.get(i).getScore()+" points, finished at position "+players.get(i).getPosition());
			System.out.print(", got bitten by "+stats.get(i)[0]+" snakes"); //  FIDIA
			System.out.print(", climbed "+stats.get(i)[1]+" ladders"); //  SKALES
			System.out.print(", found and opened "+stats.get(i)[2]+" presents."); //  DWRA
					
			if(i == index_of_max)
				System.out.print(" [WINNER]");
			System.out.println();
		}
		
	}

}
