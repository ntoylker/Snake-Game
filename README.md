# Java Snakes and Ladders Game

An object-oriented, console-based implementation of the classic Snakes and Ladders board game, developed as a project for a Data Structures course. This version fulfills all the core requirements of the original assignment and expands upon them with additional features, most notably a strategic **Heuristic AI Player**.

The game provides a dynamic and engaging experience directly in your terminal, built on a fully customizable game board.

---

## Features ✨

* **Dynamic Board Generation**: Create a board of any `N x M` size, not just a fixed one.
* **Customizable Game Elements**: Specify the number of snakes, ladders, and presents to be placed randomly on the board at the start of the game.
* **Heuristic AI Player**: An advanced feature beyond the original scope, this AI opponent evaluates all possible moves to choose the most strategic one.
* **Random Player Simulation**: Includes a standard player who moves based on a random dice roll, as required by the project specifications.
* **Unique 'Present' System**: Land on squares with presents to gain (or lose!) points, adding a scoring dimension to the classic game.
* **One-Time-Use Ladders**: A strategic twist where ladders are "broken" and disappear after being used once.
* **Detailed Console UI**: A dynamic text-based board is printed each turn, showing player positions and all game elements (`SH` for Snake Head, `ld` for Ladder Down, etc.).

---

## How to Run 🚀

1.  Ensure you have a Java Development Kit (JDK) installed on your system.
2.  Place all `.java` files (`Game.java`, `Player.java`, `Board.java`, etc.) in the same directory.
3.  Open a terminal or command prompt and navigate to that directory.
4.  Compile the source code by running the following command:
    ```bash
    javac *.java
    ```
5.  Run the game using this command:
    ```bash
    java Game
    ```
6.  Follow the on-screen prompts to set up your board dimensions, add players, and start the game!

---

## Gameplay & Rules 🎲

The game follows classic rules, with specific constraints and features defined by the project requirements.

### Setup & Turn Order
Players start at the first tile and aim for the last one. Before the game begins, each player rolls a die. The player with the **lowest roll** plays first, and the turn order proceeds sequentially.

### Movement & Board Interaction
* A player's turn consists of rolling a six-sided die and moving their piece forward that many squares.
* **Chain Reactions**: A single move can trigger a sequence of events. For example, a player might take a ladder to a new square that contains a snake's head, forcing them to then move down the snake all in the same turn.

### Game Elements
* 🐍 **Snakes**: If you land on a square with a snake's head (**SH**), you will slide down to the square with its tail (**st**). During board creation, a snake's head is always placed on a higher-numbered tile than its tail.
* 🪜 **Ladders**: If you land on a square with the bottom of a ladder (**ld**), you will climb up to the square with its top (**LU**). A ladder's top is always on a higher-numbered tile than its bottom. **Important:** Each ladder is single-use! After a player climbs it, the ladder is considered "broken" and cannot be used again.
* 🎁 **Presents**: If you land on a square with a present (**PR**), your score will change based on the points associated with that present. The present is then "removed" from the board (its points are set to zero) and cannot be collected again.

### Winning the Game
The winner is the first player to reach the final tile on the board. The game ends by printing the total rounds played, the final score for each player, and the name of the winner.

---

## Code Structure 📂

The project is broken down into the following classes as specified in the assignment document.

### `Game.java`
The main driver class that orchestrates the game.
* **Attributes**: `int round` to keep track of the current round.
* **Key Methods**: Contains the `main()` method which serves as the entry point, initializes the board (the spec suggests a default 20x10 board with 6 snakes, 6 ladders, and 6 presents), adds two players, and runs the main game loop until a winner is found.

### `Board.java`
Represents the game board and all its elements.
* **Attributes**: `int N, M` for dimensions; `int[][] squares` for tile IDs; and arrays for `Snake[] snakes`, `Ladder[] ladders`, and `Present[] presents`.
* **Key Methods**:
    * `createBoard()`: Randomly populates the board with a given number of snakes, ladders, and presents, ensuring valid placements (e.g., snake heads above tails).
    * `createElementBoard()`: Generates and prints string-based representations of the board, showing the locations of all game elements.

### `Player.java`
The base class for a player.
* **Attributes**: `int playerId`, `String name`, `int score`, and a `Board` object to interact with the game world.
* **Key Methods**: The `move(int id, int die)` method calculates the result of a player's turn, including all subsequent moves from snakes and ladders, and returns an array containing the final tile ID, and the count of snakes, ladders, and presents encountered.

### `HeuristicPlayer.java`
An extension of the `Player` class that implements a strategic AI.
* This class was not part of the original assignment but was added to enhance the project.
* **Key Methods**: Its `doNextMove()` method evaluates all six possible dice rolls using the function `$Evaluation = (steps\_moved \times 0.7) + (points\_gained \times 0.3)$` and selects the optimal move.

### Data Model Classes
* **`Snake.java`**: Represents a snake with `snakeId`, `headId`, and `tailId`.
* **`Ladder.java`**: Represents a ladder with `ladderId`, `topSquareId`, `bottomSquareId`, and a `boolean broken` status.
* **`Present.java`**: Represents a present with `presentId`, `presentSquareId`, and the `points` it awards.
