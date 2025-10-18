
public class Present {
	// VARIABLES
	int presentId;
	int presentSquareId;
	int pointsId;
	
	// METHODS
	Present(){} // empty
	Present(int pid, int s, int points){ // all manually
		presentId = pid;
		presentSquareId = s;
		pointsId = points;
	}
	Present(Present p){ // copies a snake
		presentId = p.getPresentId();
		presentSquareId= p.getPresentSquareId();
		pointsId = p.getPointsId();		
	}

	int getPresentId() {
		return presentId;
	}
	int getPresentSquareId() {
		return presentSquareId;
	}
	int getPointsId() {
		return pointsId;
	}
	void setPresentId(int x) { 
		presentId = x;
	}
	void setPresentSquareId(int x) { 
		presentSquareId = x;
	}
	void setPointsId(int x) { 
		pointsId = x;
	}

}
