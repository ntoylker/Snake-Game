
public class Snake {
	// VARIABLES
	int snakeId;
	int headId;
	int tailId;
	
	// METHODS
	/* CONSTRUCTORS */
	Snake(){} // empty
	Snake(int s, int h, int t){ // all manually
		snakeId = s;
		headId = h;
		tailId = t;
	}

	/* GET-SETTERS  */
	int getSnakeId() {
		return snakeId;
	}
	int getHeadId() {
		return headId;
	}
	int getTailId() {
		return tailId;
	}
	void setSnakeId(int x) { 
		snakeId = x;
	}
	void setHeadId(int x) { 
		headId = x;
	}
	void setTailId(int x) { 
		tailId = x;
	}

}
