public class Player {
	// x5 VARIABLES
	int playerId;
	String name;
	int score;
	Board board; // MAIN SHI
	int position; // NEW VAR
	/*
	int times_bitten_by_snake;
	int presents_found;
	int ladders_climbed;
	*/
	
	// METHODS
	/* CONSTRUCTORS */
	Player(){}
	Player(int p, String n, int s, Board b, int pos){ // ,    int v1,int v2,int v3
		playerId = p;
		name = n;
		score = s;
		board = b;
		position = pos;
		/*
		times_bitten_by_snake = v1;
		presents_found = v2;
		ladders_climbed = v3; */
		
	}
	
	/* getters */
	int getPlayerId() {
		return playerId;
	}
	String getName() {
		return name;
	}
	int getScore() {
		return score;
	}
	Board getBoard() {
		return board;
	}
	int getPosition() {
		return position;
	}
	/*
	int getTimesBittenBySnake() {
		return times_bitten_by_snake;
	}
	int getPresentsFound() {
		return presents_found;
	}
	int getLaddersClimbed() {
		return ladders_climbed;
	}
	*/
	/* setters */
	void setPlayerId(int x) {
		playerId = x;
	}
	void setName(String x) {
		name = x;
	}
	void setScore(int x) {
		score = x;
	}
	void setBoard(Board x) {
		board = x;
	}
	void setPosition(int p) {
		position = p;
	}
	/*
	void setTimesBittenBySnake(int x) {
		times_bitten_by_snake = x;
	}
	void setPresentsFound(int x) {
		presents_found = x;
	}
	void setLaddersClimbed(int x) {
		ladders_climbed = x;
	}
	*/
	
	// c.
	int[] move(int dice) {

		int[] mov = new int[4]; // new square,snakes,ladders,presents = 4
		String temp;
		position = position + dice;
		
		// WINNING MOVE: if the dice goes over the finish line or on it
		if(position>=(board.getN()*board.getM() - 1)) {
			mov[0] = board.getN()*board.getM() - 1;
			mov[1] = 0;
			mov[2] = 0;
			mov[3] = 0;
			return mov;
		}
		
		while(true) {
			
			// CHECK FOR LADDER
			temp = board.getBoardLadders()[position/board.getM()][position%board.getM()];
			if(temp.contains("ld")) {
				// found a ladder down
				int i = Character.getNumericValue(temp.charAt(2)); // o teleutaios xarakthras -> einai to index tou element (snake or ladder)
				
				// PRINT THE CLIMBING info
				System.out.print("("+position+")~"+name); 
				
				// MOVE UP
				// new position = ladder_UP
				position = board.getLadders()[i].getTopSquareId();
				System.out.println(" found a ladder and climbed it! (->"+position+")");
				
                // LADDERS ARE NOW REUSABLE AND ARE NOT BROKEN OR REMOVED
				
				// SAVE IT IN move[]
				mov[2] ++;
			
				// LOOP AGAIN
				continue;
			}
			
			// CHECK FOR SNAKE
			temp = board.getBoardSnakes()[position/board.getM()][position%board.getM()];
			if(temp.contains("SH")) {
				// found a snake_HEAD
				int i = Character.getNumericValue(temp.charAt(2)); // o teleutaios xarakthras -> einai to index tou element (snake or ladder)
				
				// PRINT THE BITTING info
				System.out.print("("+position+")~"+name); 
				
				// MOVE DOWN
				// new position = snake_TAIL
				position = board.getSnakes()[i].getTailId();
				System.out.println(" got bitten by a Snake! (->"+position+")");
				// SAVE IT IN move[]
				mov[1] ++;
				// LOOP AGAIN
				continue;
			}
			
			// CHECK FOR PRESENT
			temp = board.getBoardPresents()[position/board.getM()][position%board.getM()];
			if(temp.contains("PR")) {
				// found a RPESENT
				int i = Character.getNumericValue(temp.charAt(2)); // o teleutaios xarakthras -> einai to index tou element (snake or ladder)
				
				// add points to player's var SCORE
				score += board.getPresents()[i].getPointsId();
				// PRINT THE FINDING info
				System.out.println("("+position+")~"+name+" found a present and got "+board.getPresents()[i].getPointsId()+" points!");
				
				// 0 the points in that present
				board.getPresents()[i].setPointsId(0);
				board.getBoardPresents()[position/board.getM()][position%board.getM()] = "___";
				
				// SAVE IT IN move[]
				mov[3] ++;
				// LOOP AGAIN
				continue;
			}
			break;
		}
		
		mov[0] = position;
		return mov;
	}
	
	// EXTRA FUNCTION
	void print_current_board() {
		String[][] ladders = board.getBoardLadders();
		String[][] snakes = board.getBoardSnakes();
		String[][] presents = board.getBoardPresents();
		String total = "";
		int flag = 0;
		int rows = board.getN();
		int cols = board.getM();
		
		// UPPER BOUND
		total = total.concat("+");
        for(int k=0; k<board.getM()*9-1; k++)
        	total = total.concat("-");
        total = total.concat("+\n");
        
        // MAIN BODY
	    for(int i=0; i<rows; i++) {
	    	total = total.concat("|"); // start of every line
	    	
	    	// 1ST LINE

	        if((rows-1-i)%2==0) { // is even ->DOESNT REVERSE
		        for(int k=0; k<cols; k++){
		        	
		        	flag = 0;
		        	
		        	if(ladders[rows-1-i][cols-1-k].contains("l") || ladders[rows-1-i][cols-1-k].contains("L") ) {
		        		total = total.concat(ladders[rows-1-i][cols-1-k]);
		        		flag ++;
		        	}
		        	if(snakes[rows-1-i][cols-1-k].contains("s") || snakes[rows-1-i][cols-1-k].contains("S") ) {
		        		if(flag == 1) 
		        			total = total.concat(" " + snakes[rows-1-i][cols-1-k]);
		        		else// flag == 0
		        			total = total.concat(snakes[rows-1-i][cols-1-k]);
		        		
		        		flag ++;
		        	}
		        	if(presents[rows-1-i][cols-1-k].contains("P") ) {
		        		if(flag==1)
		        			total = total.concat(" " + presents[rows-1-i][cols-1-k]);
		        		else // flag == 0
		        			total = total.concat(presents[rows-1-i][cols-1-k]);
		        		
		        		flag ++;
		        	}
		        	
		        	if(flag==2) {
		        		total = total.concat(" |"); 
		        		continue;
		        	}
		        	if(flag==1) {
		        		total = total.concat("     |"); 
		        		continue;
		        	}
		        	if(flag==0) {
		        		total = total.concat("        |");
		        		continue;
		        	}
		        }
	    	}
	        else { // REVERSE
        		for(int k=cols-1; k>=0; k--){
	        	
        			flag = 0;
	        	
		        	if(ladders[rows-1-i][cols-1-k].contains("l") || ladders[rows-1-i][cols-1-k].contains("L") ) {
		        		total = total.concat(ladders[rows-1-i][cols-1-k]);
		        		flag ++;
		        	}
		        	if(snakes[rows-1-i][cols-1-k].contains("s") || snakes[rows-1-i][cols-1-k].contains("S") ) {
		        		if(flag == 1) 
		        			total = total.concat(" " + snakes[rows-1-i][cols-1-k]);
		        		else// flag == 0
		        			total = total.concat(snakes[rows-1-i][cols-1-k]);
		        		
		        		flag ++;
		        	}
		        	if(presents[rows-1-i][cols-1-k].contains("P") ) {
		        		if(flag==1)
		        			total = total.concat(" " + presents[rows-1-i][cols-1-k]);
		        		else // flag == 0
		        			total = total.concat(presents[rows-1-i][cols-1-k]);
	        		
	        		flag ++;
		        	}
		        	
		        	if(flag==2) {
		        		total = total.concat(" |"); 
		        		continue;
		        	}
		        	if(flag==1) {
		        		total = total.concat("     |"); 
		        		continue;
		        	}
		        	if(flag==0) {
		        		total = total.concat("        |"); 
		        		continue;
		        	}
        		}
	        }
	        total = total.concat("\n");
	        
	        
	        // 2ND LINE
	        total = total.concat("|");
	        if((rows-1-i)%2==0) { // is even ->DOESNT REVERSE
		        for(int k=0; k<cols; k++)
		        {
		        	if(position == (rows*cols-1-i*cols-k)){ // print player
		        		if(position<=99 && position>=10)
		        			total = total.concat("pl"+playerId+"   "+(position)+"|");
		        		else if(position>=100 && position<=999)
		        			total = total.concat("pl"+playerId+"  "+(position)+"|");
		        		else // <=9
		        			total = total.concat("pl"+playerId+"    "+(position)+"|");
		        	}
		        	else { // DONT PRINT PLAYER
		        		if((rows*cols-1-i*cols-k)<=99 && (rows*cols-1-i*cols-k)>=10)
		        			total = total.concat("      "+(rows*cols-1-i*cols-k)+"|");
		        		else if((rows*cols-1-i*cols-k)>=100 && (rows*cols-1-i*cols-k)<=999)
		        			total = total.concat("     "+(rows*cols-1-i*cols-k)+"|");
		        		else // <=9
		        			total = total.concat("       "+(rows*cols-1-i*cols-k)+"|");
		        		
		        	}	
		        }
	        }
	        else { // is odd
	        	for(int k=cols-1; k>=0; k--)
		        {
		        	if(position == (rows*cols-1-i*cols-k)){ // print player
		        		if(position<=99 && position>=10)
		        			total = total.concat("pl"+playerId+"   "+(position)+"|");
		        		else if(position>=100 && position<=999)
		        			total = total.concat("pl"+playerId+"  "+(position)+"|");
		        		else // <=9
		        			total = total.concat("pl"+playerId+"    "+(position)+"|");
		        	}
		        	else { // DONT PRINT PLAYER
		        		if((rows*cols-1-i*cols-k)<=99 && (rows*cols-1-i*cols-k)>=10)
		        			total = total.concat("      "+(rows*cols-1-i*cols-k)+"|");
		        		else if((rows*cols-1-i*cols-k)>=100 && (rows*cols-1-i*cols-k)<=999)
		        			total = total.concat("     "+(rows*cols-1-i*cols-k)+"|");
		        		else // <=9
		        			total = total.concat("       "+(rows*cols-1-i*cols-k)+"|");
		        	}	
		        }
	        }
	        total = total.concat("\n");
	        
			// LOWER BOUND
			total = total.concat("+");
	        for(int k=0; k<board.getM()*9-1; k++)
	        	total = total.concat("-");
	        total = total.concat("+\n");
	    }
	    System.out.println("BOARD is: \t("+position+") "+name+"  SCORE: "+score+".\n"+total );
        
	}

}
