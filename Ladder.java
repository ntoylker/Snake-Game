
public class Ladder {
	// VARIABLES
	int ladderId;
	int topSquareId;
	int bottomSquareId;
	
	boolean broken;
	
	// METHODS
	/* CONSTRUCTORS */
	Ladder(){} // empty
	Ladder(int l, int t, int b){ // broken=false, x3 manually
		ladderId = l;
		topSquareId = t;
		bottomSquareId = b;
		
		broken = false;
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
	boolean getBroken() {
		return broken;
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
	void setBroken(boolean x) {
		broken = x;
	}

}
