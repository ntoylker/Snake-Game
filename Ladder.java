public class Ladder {
	// VARIABLES
	int ladderId;
	int topSquareId;
	int bottomSquareId;
	
	// METHODS
	/* CONSTRUCTORS */
	Ladder(){} // empty
	Ladder(int l, int t, int b){
		ladderId = l;
		topSquareId = t;
		bottomSquareId = b;
	}

	/* GET-SETTERS */
	int getLadderId() {
		return ladderId;
	}
	int getTopSquareId() {
		return topSquareId;
	}
	int getBottomSquareId() {
		return bottomSquareId;
	}
	void setLadderId(int x) { 
		ladderId = x;
	}
	void setTopSquareId(int x) { 
		topSquareId = x;
	}
	void setBottomSquareId(int x) { 
		bottomSquareId = x;
	}
}
