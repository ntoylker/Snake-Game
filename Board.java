//import java.util.Arrays;

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

	// e.
	/* PERIGRAFH: κάνει initialize τις μεταβλητές του board, βάσει κάποιων κανόνων τοποθέτησης
		εχω εναν 2D_int_array που αντιπροσωπευει το ταμπλο NxM
		είναι αρχικά γεμάτος με μηδενικά. Θα κάνω έλεγχο στην τιμή ενός check[][]
		ώστε να δω αν μπορεί εκεί να τοποθετηθεί σκάλα,φίδι,δώρο.
		Πρώτα θα τοποθετήσω τις σκάλες
		σε όποιο κουτί θα υπάρχει ladder_down, θα το κανω check[][]=4
		και για ladder_up, check[][]>=5
		Έπειτα, θα τοποθετήσω τα φίδια
		snake's head, check[][]=2
		και snake's tail, check[][]=3
		Τέλος, θα τοποθετήσω τα δώρα
		present, check[][] = 1.
		Οι κανόνες τοποθέτησης βρίσκονται σε σχόλια ΕΝΤΌΣ της συνάρτησης!
		Η διαδικασία:
		Παράγω 2 τυχαίους αριθμούς, ταξινομώ σε μικρός-μεγάλος
		Ελέγχοντας τον check[][], τοποθετώ αρχικά τις σκάλες και τα φίδια
		Αν δεν είναι valid η τοποθέτηση με αυτούς τους 2 αριθμούς, παράγω 2 νέους τυχαίους
		Όταν οι αριθμοί θα είναι valid, καλώ τους constructoρες των ladders() και snakes()
		του board, και δημιουργώ τα αντικείμενα του παιχνιδιού.
		
		Όταν θα έρθει η σειρά τοποθέτησης των δώρων, θα κατασκευάσω ταυτόχρονα
		και τα elementBoardLadders, elementBoardSnakes (γράφω στις αντίστοιχες θέσεις τα st,SH,ld,LU)
		Τότε, παράγω έναν τυχαίο αριθμό θέσης δώρου, ελέγχω αν είναι valid.
		Αν είναι, τότε τοποθετώ το δώρο σε αυτήν τη θέση του Board
		το present θα μπορούσε να έχει πόντους 10,20,30,40,50 +-
		τύπος παραγωγής: 10*(τυχ_αριθμ)*(-1)^(τυχ_αριθμ%2)
		Τέλος, κάνω initialize τη μεταβλητή squares[][], που μόνο ο Χριστός γνωρίζει τη χρήση τους, καθώς εγώ δεν τα χρειάστηκα πουθενά.
		Αλλα that's fine, I guess -_-
	 */
	void createBoard() {

		int[][] check = new int[N][M];
		int[] ladder_down_check = new int[ladders.length]; // [ith ladder][its up/down position]
		int temp;
		int temp1;
		int temp2;
		
		/* KWDIKOPOIHSH
		 	()exei present: check = 1
			  exei snake's head: check = 2
			  exei snake's tail: check = 3
			  exei ladder's down: check = 4
			  exei ladder's up: check = 5++
		*/ 
		
		// init the ladders randomly
		for(int i=0; i<ladders.length; i++){
			// GENERATE UP-DOWN
			temp1 = ((int)(Math.random()*100000)) % (N*M);
			temp2 = ((int)(Math.random()*100000)) % (N*M);
			// SORT THEM: UP-DOWN:  temp1==UP THEORHSH
			if(temp1<temp2) {
				temp = temp2;
				temp2 = temp1;
				temp1 = temp;
			}
			// ladder_down ()NA MHN:  sumpesei me snake_HEAD (==2)   (paraleipw, den uparxoun snakes akoma)
			//		   		 NA MHN:  sumpeftei me ALLO ladder_DOWN (==4)
			//				 NA MHN:  sumpeftei me ALLO ladder_UP (>=5)   (DIOTI THA BGALEI SFALMA STHN EKTUPWSH, DEN MPOREIS NA APOTHIKEUSEIS ME AUTES TIS METABLHTES PARAPANW APO 2 ladder_UPs or snake_TAILs
			// 				 NA MHN:  sumpeftei me to 0 (thn arxh)
			//
			// ladder_up    NA MHN:  sumpeftei me ALLO ladder_UP (>=5)   (DIOTI THA BGALEI SFALMA STHN EKTUPWSH, DEN MPOREIS NA APOTHIKEUSEIS ME AUTES TIS METABLHTES PARAPANW APO 2 ladder_UPs or snake_TAILs
			//				NA MHN:  sumpeftei me allo ladder_DOWN (==4)
			//
			// Genika 		NA MHN:  einai sthn idia grammh tou pinaka ta UP kai DOWN (periexei kai thn periptwsh ths tautishs up-down)
			
			if( (check[temp2/M][temp2%M] == 4) || (check[temp2/M][temp2%M] >= 5) || (check[temp1/M][temp1%M] == 4) || (check[temp1/M][temp1%M] >= 5) || ((temp1/M) == (temp2/M)) || (temp2==0)) {
				i--;
				continue;
			}
			else{
				check[temp1/M][temp1%M] = 5+i;	// KWDIKOPOIHSH TOU "there is ladder_UP here" -> (XREIAZETAI GIA ON ELEGXO TAUTISHS snake-ladder (loupa anebokatebasmatos)
				check[temp2/M][temp2%M] = 4;	// KWDIKOPOIHSH TOU "these is ladder_DOWN here"	
				
				ladder_down_check[i] = temp2; // DOWN coords for 'i'th ladder
				ladders[i] = new Ladder(i, temp1, temp2);
			}
		}

		// init the snakes randomly
		for(int i=0; i<snakes.length; i++){
			// GENERATE THEM
			temp1 = ((int)(Math.random()*100000)) % (N*M);
			temp2 = ((int)(Math.random()*100000)) % (N*M);
			// SORT them: t1 is the head of the snake
			if(temp1<temp2) {
				temp = temp2;
				temp2 = temp1;
				temp1 = temp;
			}
			
			// snakes_HEAD   NA MHN:  sumpesei me ladder_DOWN (==4)
			//        	 	 NA MHN:  sumpesei me ALLO snake_HEAD (==2)
			//				 NA MHN:  sumpesei me ALLO snake_TAIL(==3)	 (sfalma sthn ektupwsh, den mporw na exw diplh plhroforia enos antikeimenou kapou) 
			//
			// snakes_TAIL  NA MHN:  sumpesei me ALLO snake_TAIL(==3)    (sfalma sthn ektupwsh, den mporw na exw diplh plhroforia enos antikeimenou kapou) 
			//				NA MHN:  sumpesei me ALLO snake_HEAD(==2)
			//
			// Genika		NA MHN:  einai sthn idia grammh tou pinaka ta HEAD kai TAIL (kobei kai thn periptwsh tautishs)
			//		   		NA MHN:  TAUTISTEI SNAKE==LADDER (loupa anebokatebasmatos)
			
			// ELEGXOS TAUTISHS SNAKE==LADDER (SPANIO)
			if( (check[temp1/M][temp1%M]>=5) ) { // sto check tha exei katagrafthei OTI UPARXEI ladder_UP
				// briskeis gia poianou ladder einai to UP me to opoio tautistike to snake_HEAD sou
				// i = check[][] - 5 (kodikopoihsh: "5+i" gia ta ladder_UPs
				int k = check[temp1/M][temp1%M]-5; 
				// elegxos tautishs tou snake_TAIL sou me to ladder_DOWN tou SUGKEKRIMENOU ladder_UP
				// (plhroforia pou metaferetai me to i kai ton ladder_down_check)
				// ston ladder_down_check apothikeuthke parapanw h THESI sto NXM plegma tou ladder_DOWN[i]
				if( temp2 == ladder_down_check[k] ) {
					i--;
					continue;
				}
			}
			
			// MAIN ELEGXOS
			if( (check[temp1/M][temp1%M] == 2) || (check[temp1/M][temp1%M]==3) || (check[temp1/M][temp1%M] == 4) || (check[temp2/M][temp2%M] == 2) || (check[temp2/M][temp2%M] == 3) || ((temp1/M) == (temp2/M)) ){
				i--;
				continue;
			}
			else{
				check[temp1/M][temp1%M] = 2; // KWDIKOPOIHSH TOU "there is a snake_HEAD here"
				check[temp2/M][temp2%M] = 3; // KWDIKOPOIHSH TOU "there is a snake_TAIL here"
				snakes[i] = new Snake(i, temp1, temp2);
			}
		}
		
		
		// ADD THE PRESENTS wherever possible (no snake_HEAD exists and no ladder_DOWN exists)
		
		String[][] elementBoardSnakes   = new String[N][M];
		String[][] elementBoardLadders  = new String[N][M];
		// LOUPA GIA LADDERS
		for(int i=0; i<ladders.length; i++)
		{
			elementBoardLadders[(ladders[i].getTopSquareId())/M][(ladders[i].getTopSquareId())%M] = "LU";
			elementBoardLadders[(ladders[i].getBottomSquareId())/M][(ladders[i].getBottomSquareId())%M] = "ld";
		}
		// LOUPA GIA SNAKES
		for(int i=0; i<snakes.length; i++)
		{
			elementBoardSnakes[(snakes[i].getHeadId())/M][(snakes[i].getHeadId())%M] = "SH";
			elementBoardSnakes[(snakes[i].getTailId())/M][(snakes[i].getTailId())%M] = "st";
		}
		// LOUPA GIA "___"
		for(int i=0;i<N;i++) {
			for(int j=0; j<M; j++) {
				if(elementBoardSnakes[i][j] == null)
					elementBoardSnakes[i][j] = "___";
			
				if(elementBoardLadders[i][j] == null)
					elementBoardLadders[i][j] = "___";
			}
		}
		
		
		// init the presents[] RANDOMLY
		for(int i=0; i<presents.length; i++){
			temp1 = ((int)(Math.random()*100000)) % (N*M);
			// presentId = i
			// presentPositionId = temp (0->N*M)
			// presentPoints = Position/2 * (-1)^i
			// OSO PIO PSHLA ANEBAINEIS, TOSO PERISSOTEROI PONTOI DINONTAI H PAIRNONTAI me ta gifts
			if( elementBoardSnakes[temp1/M][temp1%M] == "SH") {
				i--;
				continue;
			}
			if( elementBoardLadders[temp1/M][temp1%M] == "ld") {
				i--;
				continue;
			}
			if( check[temp1/M][temp1%M] == 1){
				i--;
				continue;
			}
			if( temp1 == 0) {
				i--;
				continue;
			}
			// DEN PERIEXETAI STO KOUTI EKEINO snake_HEAD or ladder_DOWN or present
			// ara mporei na topothetithei PRESENT
			check[temp1/M][temp1%M] = 1;
			temp = ((int)(Math.random()*100)) % 5 + 1; // 1,2,3,4,5 generated
			presents[i] = new Present(i, temp1, 10*temp*((int)Math.pow((double)(-1), (double)((int)(Math.random()*50)%2))));
			// points: 10-20-30-40-50 (+-)
		}
		
		// init the squares
		for(int i=0; i<N; i++)
			for(int j=0; j<M; j++) {
				if(i%2 == 0) // is odd
					squares[i][j] = (i*M +j); 
				else
					squares[i][M-1-j] = (i*M +j);
			}
		/*       COUNTING
		 * 	 0   1  2  3  4  5
		 *   11 10  9  8  7  6
		 *   12 13 14 15 16 17
		 *   23 22 21 20 19 18
		 */
		
		/*
		 for(int i=0; i<N; i++) {
			for(int j=0; j<M; j++)
				System.out.print(squares[i][j]+ " ");
			System.out.println();
		} 
		*/
		
	}
	// f.
	/* PERIGRAFH: κάνει initialize τις μεταβλητές elementBoardSnakes_Ladders_Presents του Board, βάσει των ladders[],snakes[],presents[]
	 * οι μεταβλητές αυτές είναι ΝxM 2D_String_Array οι οποίες αναπαριστούν
	 * το board και τις θέσεις των ladders (ld-LU) + ('___')
	 * το board και τις θέσεις των snakes (st-SH) + ('___')
	 * το board και τις θέσεις των presents (PR) + ('___')
	 * Επίσης, σε σχόλια είναι κρυμμένος και κώδικας που προβάλλει αυτά τα ΝxM boards 
	 */
	void createElementBoard() {
		
		// LOUPA GIA PRESENTS
		for(int i=0; i<presents.length; i++)
			elementBoardPresents[(presents[i].getPresentSquareId())/M][(presents[i].getPresentSquareId())%M] = "PR"+i ;
		
		// LOUPA GIA LADDERS
		for(int i=0; i<ladders.length; i++)
		{
			elementBoardLadders[(ladders[i].getTopSquareId())/M][(ladders[i].getTopSquareId())%M] = "LU"+i;
			elementBoardLadders[(ladders[i].getBottomSquareId())/M][(ladders[i].getBottomSquareId())%M] = "ld"+i;
		}
		
		// LOUPA GIA SNAKES
		for(int i=0; i<snakes.length; i++)
		{
			elementBoardSnakes[(snakes[i].getHeadId())/M][(snakes[i].getHeadId())%M] = "SH"+i;
			elementBoardSnakes[(snakes[i].getTailId())/M][(snakes[i].getTailId())%M] = "st"+i;
		}
		
		// LOUPA GIA "___"
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
		
		/*
		// x3 prints for positions
		for(int i=0; i<snakes.length; i++)
		{
			System.out.println("Snake "+(i)+" at: "+snakes[i].getHeadId()+"-"+snakes[i].getTailId());
		}
		System.out.println();
		for(int i=0; i<ladders.length; i++)
		{
			System.out.println("Ladder "+(i)+" at: "+ladders[i].getTopSquareId()+"-"+ladders[i].getBottomSquareId());
		}
		System.out.println();
		for(int i=0; i<presents.length; i++)
		{
			System.out.println("Present "+(i)+" at: "+presents[i].getPresentSquareId());
		}
		System.out.println();
		System.out.println();
		
		
		// x4 PRINTS. PRESENTS-SNAKES-LADDERS-WHOLE BOARD PRINTS
		System.out.println(" PRESENTS");
		for(int i=0;i<N;i++) {
			for(int j=0; j<M; j++) {
				if((N-1-i)%2==0) // is even
					System.out.print(elementBoardPresents[N-1-i][M-1-j] +" ");
				else
					System.out.print(elementBoardPresents[N-1-i][j] +" ");
			}
			System.out.println();
		}

		System.out.println("\n LADDERS");
		for(int i=0;i<N;i++) {
			for(int j=0; j<M; j++) {
				if((N-1-i)%2==0) // is even
					System.out.print(elementBoardLadders[N-1-i][M-1-j] +" ");
				else
					System.out.print(elementBoardLadders[N-1-i][j] +" ");
			}
			System.out.println();
		}

		System.out.println("\n SNAKES");
		for(int i=0;i<N;i++) {
			for(int j=0; j<M; j++) {
				if((N-1-i)%2==0) // is even
					System.out.print(elementBoardSnakes[N-1-i][M-1-j] +" ");
				else
					System.out.print(elementBoardSnakes[N-1-i][j] +" ");
			}
			System.out.println();
		}
		*/
		
	}
	
	// -_-
}
