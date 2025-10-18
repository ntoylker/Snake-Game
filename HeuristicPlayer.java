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
            // Get correct indices for temp_position using serpentine logic
            int[] indices = board.getTileIndices(temp_position);
            int row = indices[0];
            int col = indices[1];

			// ladder_down check in temp_pos
			check = board.getBoardLadders()[row][col];
			if(check.contains("ld") == true) {
				i = Character.getNumericValue(check.charAt(2));
				temp_position = board.ladders[i].getTopSquareId();
				steps += (board.ladders[i].getTopSquareId() - board.ladders[i].getBottomSquareId());
				continue;
			}
			
			// snakes check in temp_pos
			check = board.getBoardSnakes()[row][col];
			if(check.contains("SH") == true) {
				i = Character.getNumericValue(check.charAt(2));
				temp_position = board.snakes[i].getTailId();
				steps -= (board.snakes[i].getHeadId() - board.snakes[i].getTailId());
				continue;
			}
			
			// presents checks in temp_pos
			check = board.getBoardPresents()[row][col];
			if(check.contains("PR") == true) {
				i = Character.getNumericValue(check.charAt(2));
				points += board.presents[i].getPointsId();
			}
			
			loop = false;
		}
		
		evaluation = steps*0.7 + points*0.3;
		return evaluation;
	}
	
	void doNextMove() {

		double max_eval;
		int dice_for_max_eval;
		int[] move_result;
		int old_score = score;
		int old_position = position;
		Integer[] array_to_add_to_path = new Integer[6];
		double[][] possible_moves_eval = new double[6][2];

		
		if(board.getM()*board.getN()- 1 - position <= 6) { // CAN WIN
			dice_for_max_eval = board.getM()*board.getN()-1 - position;
			move(dice_for_max_eval); 
			position = board.getM()*board.getN()-1;
			array_to_add_to_path[0] = dice_for_max_eval;		
			array_to_add_to_path[1] = score - old_score;	 	
			array_to_add_to_path[2] = position - old_position; 
			array_to_add_to_path[3] = 0; 	
			array_to_add_to_path[4] = 0; 	
			array_to_add_to_path[5] = 0; 	
			path.add(array_to_add_to_path);
			return;
		}
		
		
		for(int i=0; i<6; i++) {
			possible_moves_eval[i][0] = i+1;
			possible_moves_eval[i][1] = evaluate(i+1); 
		}
		max_eval = possible_moves_eval[0][1];
		dice_for_max_eval =(int) possible_moves_eval[0][0]; 
		for(int i=1; i<6; i++) {
			if(max_eval < possible_moves_eval[i][1]) {
				max_eval = possible_moves_eval[i][1];
				dice_for_max_eval = i+1;
			}
		}
		
		move_result = this.move(dice_for_max_eval);
				
		array_to_add_to_path[0] = dice_for_max_eval;		
		array_to_add_to_path[1] = score - old_score;		
		array_to_add_to_path[2] = position - old_position; 
		array_to_add_to_path[3] = move_result[1]; 	
		array_to_add_to_path[4] = move_result[2]; 	
		array_to_add_to_path[5] = move_result[3]; 	
		
		path.add(array_to_add_to_path);
	}
	
	void statistics() {

		int snakes = 0;
		int ladders = 0; 
		int presents = 0; 
		
		for(int i=0; i<path.size(); i++) {
			snakes += (path.get(i))[3];
			ladders += (path.get(i))[4];
			presents += (path.get(i))[5];
		}
		
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
		
		System.out.println(); 	
	}

}
