import java.util.ArrayList;

public class HeuristicPlayer extends Player {
	// VARIABLES
	ArrayList<Integer[]> path;
	/*
	 * [0] dice rolled
	 * [1] score gained
	 * [2] steps did
	 * [3] snakes bit him
	 * [4] ladders climbed
	 * [5] presents found
	 */
	
	// METHODS
	/* CONSTRUCTORS */
	HeuristicPlayer(){super();}
	HeuristicPlayer(int p, String n, int s, Board b, int pos){
		super(p,n,s,b,pos);
		path = new ArrayList<>(); // EMPTY ARRAYLIST for initialization
	}
	/* getter */
	ArrayList<Integer[]> getPath(){ return path;}
	/* setter */
	void setPath(ArrayList<Integer[]> p) { path = p;}
	
	
	/* SUNARTHSH STOXOU: */
	/* F(steps,points) = steps*0.7 + points*0.3  */
	/* PERIGRAFH: εξετάζει την κίνηση ενός παίκτη με το ζάρι dice.
	 * Υλοποιεί όλα τα βήματα, τα ανεβάσματα και κατεβασμάτα σε σκάλες και φίδια
	 * την εύρεση δώρων. Βρίσκει τη διαφορά steps = new_pos - old_pos
	 * και points = points_gained - old_points
	 * Εκτελεί τότε τη συνάρτηση στόχου
	 * steps*0.7 + points*0.3
	 * και επιστρέφει αυτή την τιμή ως αποτίμηση αυτού του ζαριού - αυτής της κίνησης
	 */
	double evaluate(int dice) {
		int i; // counter
		boolean loop = true; // var to escape the 'check' loop
		String check; // aesthetic reasons
		// update the position with 'dice' rolled
		int temp_position = position + dice;
		
		
		int points = 0;
		int steps = dice;
		double evaluation = 0.0;
		
		
		// main loop, checking for the WHOLE move
		while(loop) {
			// ladder_down check in temp_pos
			check = board.getBoardLadders()[temp_position/board.getM()][temp_position%board.getM()];
			if(check.contains("ld") == true) {
				i = Character.getNumericValue(check.charAt(2));
				temp_position = board.ladders[i].getTopSquareId();
				steps += (board.ladders[i].getTopSquareId() - board.ladders[i].getBottomSquareId());
				continue;
			}
			
			// snakes check in temp_pos
			check = board.getBoardSnakes()[temp_position/board.getM()][temp_position%board.getM()];
			if(check.contains("SH") == true) {
				i = Character.getNumericValue(check.charAt(2));
				temp_position = board.snakes[i].getTailId();
				steps -= (board.snakes[i].getHeadId() - board.snakes[i].getTailId());
				continue;
			}
			
			// presents checks in temp_pos
			check = board.getBoardPresents()[temp_position/board.getM()][temp_position%board.getM()];
			if(check.contains("PR") == true) {
				i = Character.getNumericValue(check.charAt(2));
				points += board.presents[i].getPointsId();
			}
			
			// if it reaches here, it means it didnt get in ANY if-statements
			// thus, no action left to be studied
			loop = false;
		}
		
		evaluation = steps*0.7 + points*0.3;
		return evaluation;
	}
	
	// την έχω κάνει void, καθώς ο Player έχει μία extra μεταβλητή 'position'
	/* PERIGRAFH: εξετάζει και τα 6 πιθανά ζάρια του παίκτη, καλεί την evaluate 6 φορές, και αποφασίζει ποια κίνηση θα παρθεί
	 * βάσει του μεγαλύτερου evaluation. καλεί και την move() της κλάσης Player, ώστε να πραγματοποιηθεί η κίνηση
	 * Δομή δεδομένων: 2D_double_array  possible_moves_eval[6][2]
	 * στην πρώτη στήλη: το ζάρι || στη δεύτερη στήλη: το αντίστοιχο evaluation
	 * Ελέγχει επίσης και αν μπορεί να τερματίσει ο παίκτης με κάποιο απο τα 6 ζάρια.
	 * Θα αποθηκέυσει και στη μεταβλητή path τις πληροφορίες της κίνησης που επιλέχθηκε (points gained, steps, ladders climbed, bitten by snakes, presents)
	 */
	void doNextMove() {
		/* i. exei 6 POSSIBLE MOVES o paikths kathe fora
		 * xreiazomaste thn evaluate 6 fores dhladh, gia kathe dice
		 *
		 * chosen data structure: 6x2 double array (possible_dices[], their_evals[])
		 * {	dice_1.0 - eval(currPos,1)
		 * 		dice_2.0 - eval(currPos,2)
		 * 		dice_3.0 - eval(currPos,3)
		 *	 	dice_4.0 - eval(currPos,4)
		 * 		dice_5.0 - eval(currPos,5)
		 * 		dice_6.0 - eval(currPos,6)	}
		 */

		double max_eval;
		int dice_for_max_eval;
		int[] move_result;
		int old_score = score;
		int old_position = position;
		Integer[] array_to_add_to_path = new Integer[6];
		// ZHTOUMENH DATA STRUCTURE
		double[][] possible_moves_eval = new double[6][2];

		
		if(board.getM()*board.getN()- 1 - position <= 6) { // CAN WIN
			// the die
			dice_for_max_eval = board.getM()*board.getN()-1 - position;
			// make the move
			move(dice_for_max_eval); // will return [N*M, 0, 0, 0]
			// change the position of pl
			position = board.getM()*board.getN()-1;
			// assistant array for pathing
			array_to_add_to_path[0] = dice_for_max_eval;		// dice
			array_to_add_to_path[1] = score - old_score;	 	// score gained (+-)
			array_to_add_to_path[2] = position - old_position; 	// steps did (+-) 
			array_to_add_to_path[3] = 0; 	// snakes ton dagkwsan
			array_to_add_to_path[4] = 0; 	// ladders skarfalwse
			array_to_add_to_path[5] = 0; 	// presents brhke	
			/* NOW I NEED TO REFRESH THE path var */
			path.add(array_to_add_to_path);
			// escape function call
			return;
		}
		
		
		for(int i=0; i<6; i++) {
			// saving the possible dices (+1,2,3,4,5,6)
			possible_moves_eval[i][0] = i+1;
			// saving the evaluation of those dices 
			possible_moves_eval[i][1] = evaluate(i+1); // EDW MPAINEI
		}
		// now we need to find the maximum evaluation of those 6 moves
		// -> 'best move' according to my F(steps,points)
		max_eval = possible_moves_eval[0][1];
		dice_for_max_eval =(int) possible_moves_eval[0][0]; // aisthitikous logous = 1
		for(int i=1; i<6; i++) {
			if(max_eval < possible_moves_eval[i][1]) {
				max_eval = possible_moves_eval[i][1];
				dice_for_max_eval = i+1;
			}
		}
		// max_eval = max_evaluation_out_of_possible_6
		// dice_for_max_eval = its dice
		
		
		/* NOW I NEED TO MOVE THE PLAYER */
		move_result = this.move(dice_for_max_eval);
		// move() will change the score and position var of the Player
				
		/* little temporary variables to help
		   Integer[] array
		   with the path.add()  (ArrayList<Integer[]>) */
		array_to_add_to_path[0] = dice_for_max_eval;		// dice
		array_to_add_to_path[1] = score - old_score;		// score gained (+-)
		array_to_add_to_path[2] = position - old_position; 	// steps did (+-) 
		array_to_add_to_path[3] = move_result[1]; 	// snakes ton dagkwsan
		array_to_add_to_path[4] = move_result[2]; 	// ladders skarfalwse
		array_to_add_to_path[5] = move_result[3]; 	// presents brhke
		
		/* NOW I NEED TO REFRESH THE path var */
		path.add(array_to_add_to_path);
	}
	
	/* PERIGRAFH: υπολογίζει και προβάλλει τα δεδομένα μιας κίνησης του Heuristic Player */
	void statistics() {

		int snakes = 0;
		int ladders = 0; 
		int presents = 0; 
		// mporw na parw auto to info apo thn
		// variable 'path' (ama athroisw oles tis epimerous kinhseis)
		// path.size() = number_of_moves_player_has_made_till_FUNCTION_CALL
		
		/* CALCULATE THE DATA TILL THE MOMENT OF FUNCTION_CALL */
		// for every move, add their data to their vars: snakes,ladders,presents
		for(int i=0; i<path.size(); i++) {
			snakes += (path.get(i))[3];
			ladders += (path.get(i))[4];
			presents += (path.get(i))[5];
		}
		
		/* PRINT THE STRING */
		System.out.print("-> O Heuristic player sto round "+path.size());
		System.out.print(" ethese to zari iso me "+(path.get(path.size()-1))[0]);
		if((path.get(path.size()-1))[3] >= 1)
			System.out.print(" kai ton dagkwsan "+(path.get(path.size()-1))[3]+" fidia");
		if((path.get(path.size()-1))[4] >= 1)
			System.out.print(" kai anebhke "+(path.get(path.size()-1))[4]+" skales");
		if((path.get(path.size()-1))[5] >= 1)
			System.out.print(" kai brhke "+(path.get(path.size()-1))[5]+" dwra");
		System.out.print(".");
		System.out.print("\nSunolika ews twra, o "+name+":");
		System.out.print("\n~exei pathsei "+snakes+" kefalia Fidiwn");
		System.out.print("\n~exei anebei "+ladders+" Skales");
		System.out.print("\n~kai exei anoijei "+presents+" Dwra");
		
		System.out.println(); // ME \n XARAKTHRA	
	}

}
