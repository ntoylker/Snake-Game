public class Board {
	// VARIABLES
	int N;
	int M;
	int[][] squares;
	Snake[] snakes;
	Ladder[] ladders;
	Present[] presents;
	
	// MINE VARIABLES
	// ULTRA USEFULL for GAME (player, main)
	String[][] elementBoardSnakes;
	String[][] elementBoardLadders;
	String[][] elementBoardPresents;
	
	// METHODS
	/* CONSTRUCTORS */
	Board(){}// empty shit
	Board(int n, int m, int snakeNum, int ladderNum, int presentNum){ // sthn ousia orizei tis diastaseis OLWN twn pinakwn
		N = n;
		M = m;
		squares = new int[N][M];
		snakes = new Snake[snakeNum];
		ladders = new Ladder[ladderNum];
		presents = new Present[presentNum];
		elementBoardSnakes   = new String[N][M];
		elementBoardLadders  = new String[N][M];
		elementBoardPresents = new String[N][M];
	}
	
	/* getters */
	int getN() { return N;}
	int getM() { return M;}
	int[][] getSquares() { return squares;}
	Snake[] getSnakes() { return snakes;}
	Ladder[] getLadders() { return ladders;}
	Present[] getPresents() { return presents;}
	String[][] getBoardSnakes(){return elementBoardSnakes;}
	String[][] getBoardLadders(){return elementBoardLadders;}
	String[][] getBoardPresents(){return elementBoardPresents;}
	/* setters */
	void setN(int n) {N = n;}
	void setM(int m) {M = m;}
	void setSquares(int[][] sq) {squares = sq;}
	void setSnakes(Snake[] sn) {snakes = sn;}
	void setLadders(Ladder[] ld) {ladders = ld;}
	void setPresents(Present[] pr) {presents = pr;}
	void setBoardSnakes( String[][] x){elementBoardSnakes = x;}
	void setBoardLadders(String[][] x){ elementBoardLadders = x;}
	void setBoardPresents(String[][] x){ elementBoardPresents = x;}

    /**
     * Converts a serpentine tile ID into 2D array indices {row, col}.
     * This is crucial for matching the GUI's visual layout with the game's internal logic.
     * @param tileId The ID of the tile.
     * @return An array containing the {row, col} for the 2D arrays.
     */
    public int[] getTileIndices(int tileId) {
        int row = tileId / M;
        int col;
        if (row % 2 == 0) { // Even rows are Left-to-Right
            col = tileId % M;
        } else { // Odd rows are Right-to-Left
            col = M - 1 - (tileId % M);
        }
        return new int[]{row, col};
    }

	void createBoard() {

		int[][] check = new int[N][M];
		int[] ladder_down_check = new int[ladders.length];
		int temp;
		int temp1;
		int temp2;
		
		// init the ladders randomly
		for(int i=0; i<ladders.length; i++){
			temp1 = ((int)(Math.random()*100000)) % (N*M);
			temp2 = ((int)(Math.random()*100000)) % (N*M);
			if(temp1<temp2) {
				temp = temp2;
				temp2 = temp1;
				temp1 = temp;
			}
			
            int[] indicesUp = getTileIndices(temp1);
            int[] indicesDown = getTileIndices(temp2);

			if( (check[indicesDown[0]][indicesDown[1]] == 4) || (check[indicesDown[0]][indicesDown[1]] >= 5) || 
                (check[indicesUp[0]][indicesUp[1]] == 4)   || (check[indicesUp[0]][indicesUp[1]] >= 5)   || 
                ((temp1/M) == (temp2/M)) || (temp2==0)) {
				i--;
				continue;
			}
			else{
				check[indicesUp[0]][indicesUp[1]] = 5+i;	
				check[indicesDown[0]][indicesDown[1]] = 4;	
				
				ladder_down_check[i] = temp2; 
				ladders[i] = new Ladder(i, temp1, temp2);
			}
		}

		// init the snakes randomly
		for(int i=0; i<snakes.length; i++){
			temp1 = ((int)(Math.random()*100000)) % (N*M);
			temp2 = ((int)(Math.random()*100000)) % (N*M);
			if(temp1<temp2) {
				temp = temp2;
				temp2 = temp1;
				temp1 = temp;
			}
			
            int[] indicesHead = getTileIndices(temp1);
            int[] indicesTail = getTileIndices(temp2);
			
			if( (check[indicesHead[0]][indicesHead[1]] >= 5) ) { 
				int k = check[indicesHead[0]][indicesHead[1]] - 5; 
				if( temp2 == ladder_down_check[k] ) {
					i--;
					continue;
				}
			}
			
			if( (check[indicesHead[0]][indicesHead[1]] == 2) || (check[indicesHead[0]][indicesHead[1]] == 3) || (check[indicesHead[0]][indicesHead[1]] == 4) || 
                (check[indicesTail[0]][indicesTail[1]] == 2) || (check[indicesTail[0]][indicesTail[1]] == 3) || ((temp1/M) == (temp2/M)) ){
				i--;
				continue;
			}
			else{
				check[indicesHead[0]][indicesHead[1]] = 2; 
				check[indicesTail[0]][indicesTail[1]] = 3; 
				snakes[i] = new Snake(i, temp1, temp2);
			}
		}
		
		// init the presents[] RANDOMLY
		for(int i=0; i<presents.length; i++){
			temp1 = ((int)(Math.random()*100000)) % (N*M);
            int[] indices = getTileIndices(temp1);

			// A snake head is 2, a ladder bottom is 4, another present is 1.
			if( check[indices[0]][indices[1]] == 2 || check[indices[0]][indices[1]] == 4 || check[indices[0]][indices[1]] == 1 || temp1 == 0){
				i--;
				continue;
			}
			
			check[indices[0]][indices[1]] = 1;
			temp = ((int)(Math.random()*100)) % 5 + 1; // 1,2,3,4,5 generated
			presents[i] = new Present(i, temp1, 10*temp*((int)Math.pow((double)(-1), (double)((int)(Math.random()*50)%2))));
		}
		
		// init the squares (not used in GUI, but keeping for console logic)
		for(int i=0; i<N; i++)
			for(int j=0; j<M; j++) {
				if(i%2 == 0) 
					squares[i][j] = (i*M +j); 
				else
					squares[i][M-1-j] = (i*M +j);
			}
		
	}

	void createElementBoard() {
		
        // Use serpentine logic to populate the element boards correctly
        for (int i = 0; i < presents.length; i++) {
            if (presents[i] != null) {
                int[] indices = getTileIndices(presents[i].getPresentSquareId());
                elementBoardPresents[indices[0]][indices[1]] = "PR" + i;
            }
        }

        for (int i = 0; i < ladders.length; i++) {
            if (ladders[i] != null) {
                int[] topIndices = getTileIndices(ladders[i].getTopSquareId());
                elementBoardLadders[topIndices[0]][topIndices[1]] = "LU" + i;

                int[] bottomIndices = getTileIndices(ladders[i].getBottomSquareId());
                elementBoardLadders[bottomIndices[0]][bottomIndices[1]] = "ld" + i;
            }
        }

        for (int i = 0; i < snakes.length; i++) {
            if (snakes[i] != null) {
                int[] headIndices = getTileIndices(snakes[i].getHeadId());
                elementBoardSnakes[headIndices[0]][headIndices[1]] = "SH" + i;

                int[] tailIndices = getTileIndices(snakes[i].getTailId());
                elementBoardSnakes[tailIndices[0]][tailIndices[1]] = "st" + i;
            }
        }
		
		// Fill empty spots with "___"
		for(int i=0;i<N;i++) {
			for(int j=0; j<M; j++) {
				if(elementBoardSnakes[i][j] == null)
					elementBoardSnakes[i][j] = "___";
			
				if(elementBoardLadders[i][j] == null)
					elementBoardLadders[i][j] = "___";
			
				if(elementBoardPresents[i][j] == null)
					elementBoardPresents[i][j] = "___";	
				
			}
		}
		
	}
	
}

