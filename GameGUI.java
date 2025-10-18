import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class GameGUI extends JFrame {

    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel gamePanel;
    private BoardPanel boardPanel;
    private JTextArea messageArea;
    private JButton rollDiceButton;
    private JButton playMoveButton;
    private DicePanel dicePanel;
    private JPanel scorePanel;


    private Board board;
    private ArrayList<HeuristicPlayer> players;
    private Map<Integer, Integer> turns;
    private int currentPlayerIndex = 0;
    private ArrayList<Integer> turnOrder;
    private int lastDiceRoll = 0;

    // Game state variables
    private int N, M, numSnakes, numLadders, numPresents, numPlayers;
    
    private enum GameState {
        WAITING_FOR_ROLL, WAITING_FOR_MOVE, ANIMATING, GAME_OVER
    }
    private GameState gameState;

    public GameGUI() {
        setTitle("Snakes and Ladders");
        setSize(900, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new CardLayout());
        createMenuPanel();
        mainPanel.add(menuPanel, "menu");

        add(mainPanel);
    }

    private void createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        menuPanel.add(new JLabel("Board Rows (N):"), gbc);
        gbc.gridx++;
        JTextField rowsField = new JTextField("10", 5);
        menuPanel.add(rowsField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        menuPanel.add(new JLabel("Board Columns (M):"), gbc);
        gbc.gridx++;
        JTextField colsField = new JTextField("10", 5);
        menuPanel.add(colsField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        menuPanel.add(new JLabel("Number of Snakes:"), gbc);
        gbc.gridx++;
        JTextField snakesField = new JTextField("6", 5);
        menuPanel.add(snakesField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        menuPanel.add(new JLabel("Number of Ladders:"), gbc);
        gbc.gridx++;
        JTextField laddersField = new JTextField("6", 5);
        menuPanel.add(laddersField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        menuPanel.add(new JLabel("Number of Presents:"), gbc);
        gbc.gridx++;
        JTextField presentsField = new JTextField("4", 5);
        menuPanel.add(presentsField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        menuPanel.add(new JLabel("Number of Players:"), gbc);
        gbc.gridx++;
        JTextField playersField = new JTextField("2", 5);
        menuPanel.add(playersField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        menuPanel.add(startButton, gbc);

        startButton.addActionListener(e -> {
            try {
                N = Integer.parseInt(rowsField.getText());
                M = Integer.parseInt(colsField.getText());
                numSnakes = Integer.parseInt(snakesField.getText());
                numLadders = Integer.parseInt(laddersField.getText());
                numPresents = Integer.parseInt(presentsField.getText());
                numPlayers = Integer.parseInt(playersField.getText());

                if (numPlayers < 1 || numPlayers > 4) {
                    JOptionPane.showMessageDialog(this, "Number of players must be between 1 and 4.", "Player Limit", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ArrayList<String> playerNames = getPlayerNames(numPlayers);
                if (playerNames == null) { // User cancelled the name input
                    return;
                }

                startGame(playerNames);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private ArrayList<String> getPlayerNames(int numPlayers) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField[] nameFields = new JTextField[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            panel.add(new JLabel("Player " + (i + 1) + " Name:"));
            nameFields[i] = new JTextField("Player " + (i + 1));
            panel.add(nameFields[i]);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Player Names", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            ArrayList<String> names = new ArrayList<>();
            for (int i = 0; i < numPlayers; i++) {
                String name = nameFields[i].getText().trim();
                if (name.isEmpty()) {
                    names.add("Player " + (i + 1)); // Default name if empty
                } else {
                    names.add(name);
                }
            }
            return names;
        }
        return null; // User clicked Cancel
    }

    private void startGame(ArrayList<String> playerNames) {
        board = new Board(N, M, numSnakes, numLadders, numPresents);
        board.createBoard();
        board.createElementBoard();

        players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            players.add(new HeuristicPlayer(i + 1, playerNames.get(i), 0, board, 0));
        }

        turns = setTurns(players);
        turnOrder = new ArrayList<>(turns.values());

        createGamePanel();
        mainPanel.add(gamePanel, "game");
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, "game");
        
        updateMessage("Game started! It's " + getCurrentPlayer().getName() + "'s turn to roll.");
        setGameState(GameState.WAITING_FOR_ROLL);
        updateScorePanel();
    }
    
    private Map<Integer, Integer> setTurns(ArrayList<HeuristicPlayer> players) {
        int[] playerDice = new int[players.size()];
        StringBuilder turnSetupMessage = new StringBuilder("--- Determining Turn Order ---\n");
        
        for (int playerId = 0; playerId < players.size(); playerId++) {
            playerDice[playerId] = (int) (1 + (Math.random() * 6));
            for (int j = 0; j < playerId; j++) {
                while (playerDice[playerId] == playerDice[j]) {
                    playerDice[playerId] = (int) (1 + (Math.random() * 6));
                }
            }
            turnSetupMessage.append(players.get(playerId).getName()).append(" rolled a ").append(playerDice[playerId]).append("\n");
        }
        
        TreeMap<Integer, Integer> pTurnOrdered = new TreeMap<>();
        for (int playerId = 0; playerId < players.size(); playerId++) {
            pTurnOrdered.put(playerDice[playerId], playerId + 1);
        }
        
        turnSetupMessage.append("\nTurn order (lowest roll first):\n");
        for(int playerId : pTurnOrdered.values()){
            turnSetupMessage.append(players.get(playerId-1).getName()).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, turnSetupMessage.toString(), "Turn Order", JOptionPane.INFORMATION_MESSAGE);
        return pTurnOrdered;
    }

    private void createGamePanel() {
        gamePanel = new JPanel(new BorderLayout(10, 10));
        gamePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        boardPanel = new BoardPanel();
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        
        // Side panel for scores and controls
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(0, 1, 5, 5));
        scorePanel.setBorder(BorderFactory.createTitledBorder("Scores"));
        sidePanel.add(scorePanel);

        dicePanel = new DicePanel();
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidePanel.add(dicePanel);
        
        gamePanel.add(sidePanel, BorderLayout.EAST);


        // Bottom panel for controls and messages
        JPanel controlPanel = new JPanel(new BorderLayout(10,10));
        
        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.setFont(new Font("Arial", Font.BOLD, 20));
        rollDiceButton.addActionListener(new RollDiceListener());
        
        playMoveButton = new JButton("Play Move");
        playMoveButton.setFont(new Font("Arial", Font.BOLD, 20));
        playMoveButton.addActionListener(e -> performMove());

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonContainer.add(rollDiceButton);
        buttonContainer.add(playMoveButton);


        messageArea = new JTextArea(5, 30);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        
        controlPanel.add(buttonContainer, BorderLayout.NORTH);
        controlPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        gamePanel.add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void updateScorePanel() {
        scorePanel.removeAll();
        for (HeuristicPlayer p : players) {
            JLabel scoreLabel = new JLabel(String.format("%s: Tile %d | Score %d", p.getName(), p.getPosition(), p.getScore()));
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
            scoreLabel.setForeground(boardPanel.playerColors[p.getPlayerId() % boardPanel.playerColors.length]);
            scorePanel.add(scoreLabel);
        }
        scorePanel.revalidate();
        scorePanel.repaint();
    }
    
    private HeuristicPlayer getCurrentPlayer() {
        int playerId = turnOrder.get(currentPlayerIndex);
        return players.get(playerId - 1);
    }
    
    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        updateMessage("It's " + getCurrentPlayer().getName() + "'s turn to roll.");
        setGameState(GameState.WAITING_FOR_ROLL);
    }

    private void updateMessage(String message) {
        messageArea.setText(message);
    }
    
    private void appendMessage(String message){
        messageArea.append("\n" + message);
    }
    
    private void setGameState(GameState newState) {
        gameState = newState;
        switch(gameState) {
            case WAITING_FOR_ROLL:
                rollDiceButton.setEnabled(true);
                playMoveButton.setEnabled(false);
                break;
            case WAITING_FOR_MOVE:
                rollDiceButton.setEnabled(false);
                playMoveButton.setEnabled(true);
                break;
            case ANIMATING:
            case GAME_OVER:
                rollDiceButton.setEnabled(false);
                playMoveButton.setEnabled(false);
                break;
        }
    }
    
    private void performMove() {
        setGameState(GameState.ANIMATING);
        HeuristicPlayer currentPlayer = getCurrentPlayer();
        int startPos = currentPlayer.getPosition();
        int intermediatePos = startPos + lastDiceRoll;
        
        // This is a temporary move for the animation. The real logic is in handlePostMoveLogic
        
        boardPanel.animateMove(currentPlayer, startPos, intermediatePos, () -> {
            // This code runs after the first animation is complete
            currentPlayer.setPosition(intermediatePos); // Set logical position after animation
            handlePostMoveLogic(currentPlayer);
        });
    }
    
    private void handlePostMoveLogic(HeuristicPlayer currentPlayer) {
        int originalPosition = currentPlayer.getPosition();
        
        // Use your existing move logic to handle snakes/ladders/presents
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(baos);
        java.io.PrintStream old = System.out;
        System.setOut(ps);

        // This call will update the player's internal position if a snake/ladder is found
        currentPlayer.move(0); 

        System.out.flush();
        System.setOut(old);
        
        String eventMessage = baos.toString().trim();
        if (!eventMessage.isEmpty()) {
            appendMessage(eventMessage);
            JOptionPane.showMessageDialog(this, eventMessage, "Event!", JOptionPane.INFORMATION_MESSAGE);
        }

        int finalPosition = currentPlayer.getPosition();
        updateScorePanel();
        
        // If the player was moved by a snake or ladder, animate that subsequent move
        if (originalPosition != finalPosition) {
             boardPanel.animateMove(currentPlayer, originalPosition, finalPosition, () -> {
                checkWinCondition(currentPlayer);
             });
        } else {
             checkWinCondition(currentPlayer);
        }
    }

    private void checkWinCondition(HeuristicPlayer player) {
        if (player.getPosition() >= N * M - 1) {
            player.setPosition(N * M - 1); // Clamp to last tile
            boardPanel.repaint();
            updateScorePanel();
            appendMessage("\n" + player.getName() + " reached the end and WINS!");
            setGameState(GameState.GAME_OVER);
            JOptionPane.showMessageDialog(GameGUI.this, player.getName() + " wins the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        } else {
            nextTurn();
        }
    }

    private class RollDiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            lastDiceRoll = (int) (Math.random() * 6) + 1;
            dicePanel.setDiceValue(lastDiceRoll);
            updateMessage(getCurrentPlayer().getName() + " rolled a " + lastDiceRoll + ". Now, play the move.");
            setGameState(GameState.WAITING_FOR_MOVE);
        }
    }

    // Inner class for the dice panel
    class DicePanel extends JPanel {
        private int diceValue = 1;

        DicePanel() {
            setPreferredSize(new Dimension(100, 100));
            setBorder(BorderFactory.createTitledBorder("Dice"));
        }

        public void setDiceValue(int value) {
            this.diceValue = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int dim = Math.min(width, height) - 20;
            int x = (width - dim) / 2;
            int y = (height - dim) / 2;

            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(x, y, dim, dim, 15, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(x, y, dim, dim, 15, 15);
            
            drawDots(g2d, x, y, dim);
        }
        
        private void drawDots(Graphics2D g, int x, int y, int size) {
            g.setColor(Color.BLACK);
            int dotSize = size / 7;
            int pad = size / 5;

            if (diceValue % 2 == 1) { // Center dot for 1, 3, 5
                g.fillOval(x + size / 2 - dotSize / 2, y + size / 2 - dotSize / 2, dotSize, dotSize);
            }
            if (diceValue > 1) { // Top-left and bottom-right for 2, 3, 4, 5, 6
                g.fillOval(x + pad - dotSize / 2, y + pad - dotSize / 2, dotSize, dotSize);
                g.fillOval(x + size - pad - dotSize / 2, y + size - pad - dotSize / 2, dotSize, dotSize);
            }
            if (diceValue > 3) { // Top-right and bottom-left for 4, 5, 6
                g.fillOval(x + size - pad - dotSize / 2, y + pad - dotSize / 2, dotSize, dotSize);
                g.fillOval(x + pad - dotSize / 2, y + size - pad - dotSize / 2, dotSize, dotSize);
            }
            if (diceValue == 6) { // Middle-left and middle-right for 6
                g.fillOval(x + pad - dotSize / 2, y + size / 2 - dotSize / 2, dotSize, dotSize);
                g.fillOval(x + size - pad - dotSize / 2, y + size / 2 - dotSize / 2, dotSize, dotSize);
            }
        }
    }


    class BoardPanel extends JPanel {
        private final Color[] playerColors = {Color.decode("#FF6347"), Color.decode("#4682B4"), Color.decode("#32CD32"), Color.decode("#FFD700")};
        private Timer animationTimer;
        private HeuristicPlayer animatingPlayer;
        private Point animatedPosition;
        private Point targetPosition;
        private Runnable onAnimationComplete;

        BoardPanel() {
            setBackground(Color.decode("#F5F5DC"));
        }
        
        public void animateMove(HeuristicPlayer player, int startTile, int endTile, Runnable onComplete) {
            this.animatingPlayer = player;
            this.onAnimationComplete = onComplete;
            
            int cellWidth = getWidth() / M;
            int cellHeight = getHeight() / N;
            
            animatedPosition = getCoordinates(startTile, cellWidth, cellHeight);
            targetPosition = getCoordinates(endTile, cellWidth, cellHeight);
            
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }

            animationTimer = new Timer(15, e -> {
                double dx = targetPosition.x - animatedPosition.x;
                double dy = targetPosition.y - animatedPosition.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < 5) {
                    animatedPosition.x = targetPosition.x;
                    animatedPosition.y = targetPosition.y;
                    ((Timer) e.getSource()).stop();
                    animatingPlayer = null;
                    if(onAnimationComplete != null) {
                        onAnimationComplete.run();
                    }
                } else {
                    animatedPosition.x += (dx / distance) * 5;
                    animatedPosition.y += (dy / distance) * 5;
                }
                repaint();
            });
            animationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            if (M == 0 || N == 0) return; // Avoid division by zero
            int cellWidth = panelWidth / M;
            int cellHeight = panelHeight / N;

            // Draw grid and numbers
            for (int tileId = 0; tileId < N * M; tileId++) {
                Point center = getCoordinates(tileId, cellWidth, cellHeight);
                int x = center.x - cellWidth / 2;
                int y = center.y - cellHeight / 2;
                
                int row = tileId / M;
                int col = (row % 2 == 0) ? (tileId % M) : (M - 1 - (tileId % M));
                
                g.setColor((row + col) % 2 == 0 ? Color.WHITE : Color.decode("#d3d3d3"));
                g.fillRect(x, y, cellWidth, cellHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellWidth, cellHeight);
                g.drawString(String.valueOf(tileId), x + 5, y + 20);
            }
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Draw Snakes
            g2d.setColor(new Color(255, 0, 0, 150));
            for (Snake snake : board.getSnakes()) {
                Point head = getCoordinates(snake.getHeadId(), cellWidth, cellHeight);
                Point tail = getCoordinates(snake.getTailId(), cellWidth, cellHeight);
                g2d.drawLine(head.x, head.y, tail.x, tail.y);
            }

            // Draw Ladders
            g2d.setColor(new Color(0, 180, 0, 150));
            for (Ladder ladder : board.getLadders()) {
                Point bottom = getCoordinates(ladder.getBottomSquareId(), cellWidth, cellHeight);
                Point top = getCoordinates(ladder.getTopSquareId(), cellWidth, cellHeight);
                g2d.drawLine(bottom.x, bottom.y, top.x, top.y);
            }

            // Draw Presents
            for (Present present : board.getPresents()) {
                if (present.getPointsId() != 0) {
                    Point pos = getCoordinates(present.getPresentSquareId(), cellWidth, cellHeight);
                    drawPresent(g, pos, cellWidth, cellHeight);
                }
            }

            // Draw Players
            for (HeuristicPlayer player : players) {
                // If this player is being animated, draw at the animated position
                if (player == animatingPlayer && animatedPosition != null) {
                    drawPlayer(g, player, animatedPosition);
                } else { // Otherwise draw at the static, logical position
                    Point pos = getCoordinates(player.getPosition(), cellWidth, cellHeight);
                    drawPlayer(g, player, pos);
                }
            }
        }
        
        private void drawPresent(Graphics g, Point center, int cellWidth, int cellHeight) {
            int boxSize = Math.min(cellWidth, cellHeight) / 2;
            int x = center.x - boxSize / 2;
            int y = center.y - boxSize / 2;
            
            // Box
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, boxSize, boxSize);
            
            // Ribbon
            g.setColor(Color.RED);
            g.fillRect(x + boxSize / 2 - boxSize / 8, y, boxSize / 4, boxSize);
            g.fillRect(x, y + boxSize / 2 - boxSize / 8, boxSize, boxSize / 4);
        }

        private void drawPlayer(Graphics g, HeuristicPlayer player, Point position) {
            int offset = (player.getPlayerId() - 1) * 6; // Offset players slightly
            g.setColor(playerColors[player.getPlayerId() % playerColors.length]);
            g.fillOval(position.x - 12 + offset, position.y - 12, 24, 24);
            g.setColor(Color.BLACK);
            g.drawOval(position.x - 12 + offset, position.y - 12, 24, 24);
        }

        private Point getCoordinates(int tileId, int cellWidth, int cellHeight) {
            if (tileId >= N * M) {
                tileId = N * M - 1;
            }
            int row = N - 1 - (tileId / M);
            int col;
            
            if ((tileId / M) % 2 == 0) { // Even rows from the bottom (0, 2, 4...)
                 col = tileId % M;
            } else { // Odd rows
                 col = M - 1 - (tileId % M);
            }

            int x = col * cellWidth + cellWidth / 2;
            int y = row * cellHeight + cellHeight / 2;
            return new Point(x,y);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameGUI gameGUI = new GameGUI();
            gameGUI.setVisible(true);
        });
    }
}

