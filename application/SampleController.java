package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class SampleController {
		
	@FXML
    private GridPane GameBoardPane;

    @FXML
    private Button Enter;

    @FXML
    private TextField PlayerOne;

    @FXML
    private TextField PlayerTwo;
    

    @FXML
    private MenuBar menubar;

    @FXML
    private MenuItem reset;

    @FXML
    private MenuItem resume;
    
    @FXML
    private MenuItem exit;
    
    @FXML
    public Label PlayerTextField;
    
    @FXML
    private Button enterButton;
    
    @FXML
    private MenuItem RandomAI;

    @FXML
    private MenuItem ThoughtfulAI;
    
    // GameBoard
    private boolean circlesClickable = false; //flag to check if circles are clickable 
    
    private static boolean PlayerOneTurn = true; //flag to track whose turn it is
    
    private static final String Player1Disc = "Red"; //color of Player 1's disc
    
    private static final String Player2Disc = "Blue"; //color of Player 2's disc
    
    double circle_diameter = 55.0; //diameter of the circles representing discs
    
    static int numRows = 6; //number of rows in the game board
    
    static int numCols = 7; //number of columns in the game board
    
    int margin = 5; // Margin between circles in the game board
    
    private boolean gamePaused = false; //flag to check if the game is paused

    
    
    //Data Object 2D Data Structure ArrayList
    private ArrayList<ArrayList<Boolean>> columnStates; //keeps track of filled slots in each column
    
    private ArrayList<ArrayList<Circle>> insertedDiscArray = new ArrayList<>(); //an array to store references to the inserted discs

    private static ArrayList<ArrayList<Integer>> gameBoardState = new ArrayList<>(); // represents the state of the game board (0: empty, 1: Player 1, 2: Player 2)
     
    
    // AI
    private boolean isRandomAI = true; //flag to check if the AI is set to random
    
    private boolean isThoughtfulAI = true; //flag to check if the AI is set to thoughtful

    // Log File I/O
    private BufferedWriter logWriter; //used to write game moves and winner information to a log file
     
    
    @FXML
    public void initialize() {
        //Generate Player One name when the FXML is loaded
    	PlayerOne.setText(""); 
        PlayerOne.setEditable(false); 
        
        // Initialize ArrayLists for the game state
        insertedDiscArray = new ArrayList<>();
        gameBoardState = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            // Create a list for each row in insertedDiscArray
            ArrayList<Circle> discRow = new ArrayList<>();
            insertedDiscArray.add(discRow);

            // Create a list for each row in gameBoardState
            ArrayList<Integer> stateRow = new ArrayList<>();
            gameBoardState.add(stateRow);

            // Populate the lists with initial values
            for (int j = 0; j < numCols; j++) {
                discRow.add(null); // Initialize with null for Circle references
                stateRow.add(0);   // Initialize with 0 for game board state
            }
        }
        if(getLastLogFile() == null) {
        	
            
            populateGameBoard();
            initializeColumnStates();
            initializeLogFile();
        }
        
        else {
        	GameState savedGameState = loadGameState();
            if (savedGameState != null) {
            	
            	
                
                populateGameBoard();
                initializeColumnStates();
                
                Alert resumeAlert = new Alert(Alert.AlertType.CONFIRMATION);
                resumeAlert.setTitle("Resume Game");
                resumeAlert.setHeaderText("A saved game state is found. Do you want to resume?");
                resumeAlert.setContentText("Choose your option.");

                ButtonType resume = new ButtonType("Resume");
                ButtonType startNewGame = new ButtonType("Start New Game");
                ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

                resumeAlert.getButtonTypes().setAll(resume, startNewGame, cancel);

                Optional<ButtonType> result = resumeAlert.showAndWait();

                if (result.orElse(null) == resume) {
                    //If user clicked Resume, restore the game state
                    restoreGameState(savedGameState);
                } 
                else if (result.orElse(null) == startNewGame) {
                    //if user clicked Start New Game, clear the saved state delete any previously saved log file and create a new one
                    saveGameState(null);
                    deleteLogFile();
                    initializeLogFile();
                } 
                else {  //else do nothing and exit
                	System.exit(0);
                }
                
            }
          
        }    
        
        //when Random AI is selected as an AI opponent 
        RandomAI.setOnAction(event -> {
            isRandomAI = true;
            isThoughtfulAI = false;
            displayMessage("Player One set to Random AI.");
            PlayerOne.setText("Random AI");
            if (PlayerOneTurn && isRandomAI) {
                int column = makeRandomAIMove();
                handleAIMove(column);
            }
        });

        //when Thoughtful AI is selected as an AI opponent 
        ThoughtfulAI.setOnAction(event -> {
            isRandomAI = false;
            isThoughtfulAI = true;
            displayMessage("Player One set to Thoughtful AI.");
            PlayerOne.setText("Thoughtful AI");
           
            if (PlayerOneTurn && isThoughtfulAI) {
                makeThoughtfulAIMove();
            }
        }); 

    }
    
    private void logMove(String playerName, int row, int col) {
        try {
            // Check if log file is null before writing to it
            if (logWriter != null) {
                logWriter.write("Player: " + playerName + ", Move: Row " + row + ", Column " + col);
                System.out.println("Player: " + playerName + ", Move: Row " + row + ", Column " + col);
                logWriter.newLine();
                logWriter.flush();
            } else {
                System.err.println("log file is null. Saved information not logged.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeColumnStates() {
        int numCols = 7;
        int numRows = 6;
        
        columnStates = new ArrayList<>();

        for (int col = 0; col < numCols; col++) {
            ArrayList<Boolean> column = new ArrayList<>();
            for (int row = 0; row < numRows; row++) {
                // initially, all discs are unfilled
                column.add(false);
            }
            columnStates.add(column);
        }
    }

    
   
    @FXML
    public void setNames() {
        //set the name of player two 
    	PlayerTwo.setText(PlayerTwo.getText());
        PlayerTwo.setEditable(true);
        
        //check if both player names are entered before updating player turn label
        if (!PlayerOne.getText().isEmpty() && !PlayerTwo.getText().isEmpty()) {
            updatePlayerTurnLabel();
        }
       
    }

     
    @FXML
    void enterButton(MouseEvent event) {
        PlayerOne.setStyle("-fx-text-fill: red ; -fx-font-weight: bold;");
        PlayerTwo.setStyle("-fx-text-fill: blue ; -fx-font-style: italic;");

        if (!PlayerOne.getText().isEmpty() && !PlayerTwo.getText().isEmpty() && (isRandomAI || isThoughtfulAI)) {
            circlesClickable = true;
            updatePlayerTurnLabel();

            //if its Player One's turn and is a Random AI or Thoughtful AI, initiate the first move
            if (PlayerOneTurn && (isRandomAI || isThoughtfulAI)) {
                if (isRandomAI) {
                    int column = makeRandomAIMove();
                    handleAIMove(column);
                } else if (isThoughtfulAI) {
                    makeThoughtfulAIMove();
                }
            }
        } else {
            displayMessage("Please select the AI opponent and enter both player names first.");
        }
    }

   
    private void updatePlayerTurnLabel() {
    	String playerOneName = PlayerOne.getText();
        String playerTwoName = PlayerTwo.getText();

        if (PlayerOneTurn) {
            if (playerOneName.isEmpty() || playerTwoName.isEmpty()) {
            	if (gameEnded(0, 0)) {
                    displayMessage("Please enter both player names first.");
                }
                return;
            }
        }

        //her update player turn label to display
        String currentPlayerName = PlayerOneTurn ? playerOneName : playerTwoName;
        PlayerTextField.setText(currentPlayerName);
      
    }
    
        
    private void populateGameBoard() {
        for (int row = 0; row < numRows; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(10 * margin);
            GameBoardPane.getRowConstraints().add(rowConstraints);

            ArrayList<Circle> discRow = new ArrayList<>(); // Create a list to represent a row of discs

            for (int col = 0; col < numCols; col++) {
                int finalCol = col;
                ColumnConstraints colConstraints = new ColumnConstraints();
                colConstraints.setMinWidth(2 * margin);
                GameBoardPane.getColumnConstraints().add(colConstraints);

                Circle circle = new Circle();
                circle.setRadius(circle_diameter / 2);
                circle.setCenterX(circle_diameter / 2);
                circle.setCenterY(circle_diameter / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (circle_diameter + margin) + circle_diameter / 4);
                circle.setTranslateY(row * (circle_diameter + margin) + circle_diameter / 4);
                circle.setFill(Color.valueOf("#C0C0C0"));

                discRow.add(circle); // Add the circle to the list for the current row

                circle.setOnMouseClicked(event -> handleColumnButton(event, finalCol));

                GameBoardPane.getChildren().add(circle);
            }

            insertedDiscArray.add(discRow); // Add the list for the current row to insertedDiscArray
        }
    }

  
    
    private void handleColumnButton(MouseEvent event, int columnIndex) {
        if (circlesClickable && !gameEnded(0, 0)) {
        	
        	if (gamePaused) {
                //display a warning if the game is paused and player tries to click on the board
                displayMessage("Game is paused. Resume the game to drop the disc.");
                return;
            }
        	
            int row = findNextAvailableRow(columnIndex);
            if (row != -1) {
                if (!isColumnFull(columnIndex)) {
                    dropDisc(row, columnIndex);
                } 
                else {
                    //display a warning if the column is full
                    displayMessage("Column is full. Choose another column.");
                }
            }
        } 
        else {
            displayMessage("Please click the 'Enter' button first.");
        }
    }

    private void dropDisc(int row, int col) {
        if (isColumnFull(col)) {
            displayMessage("Column is full. Choose another column.");
            return;
        }

        Color discColor = PlayerOneTurn ? Color.valueOf(Player1Disc) : Color.valueOf(Player2Disc);
        Circle disc = createDisc(discColor);

        double translateX = col * (circle_diameter + margin) + circle_diameter / 4;
        double translateY = row * (circle_diameter + margin) + circle_diameter / 4;

        disc.setTranslateX(translateX);
        disc.setTranslateY(-circle_diameter);

        GameBoardPane.getChildren().add(disc);


        System.out.println("Trying to set disc at row: " + row + ", col: " + col);

	     // Check if row and col are within bounds
	     if (row >= 0 && row < insertedDiscArray.size() && col >= 0 && col < insertedDiscArray.get(row).size()) {
	         insertedDiscArray.get(row).set(col, disc);
	         columnStates.get(col).set(row, true);
	         gameBoardState.get(row).set(col, PlayerOneTurn ? 1 : 2);
	     } else {
	         System.out.println("Invalid row or column index");
	     }



        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.4), disc);
        translateTransition.setToY(translateY);

        translateTransition.setOnFinished(event -> {
            if (gameEnded(row, col)) {
                String winnerName = PlayerOneTurn ? PlayerOne.getText() : PlayerTwo.getText();
                isRandomAI = false;
                isThoughtfulAI = false;
                logMove(winnerName, row, col);
                displayWinner(winnerName);

                PlayerOneTurn = !PlayerOneTurn;
                updatePlayerTurnLabel();

                if (PlayerOneTurn && isRandomAI) {
                    int column = makeRandomAIMove();
                    handleAIMove(column);
                } else if (PlayerOneTurn && isThoughtfulAI) {
                    makeThoughtfulAIMove();
                }

            } else {
                PlayerOneTurn = !PlayerOneTurn;
                updatePlayerTurnLabel();
                logMove(!PlayerOneTurn ? PlayerOne.getText() : PlayerTwo.getText(), row, col);

                if (PlayerOneTurn && isRandomAI) {
                    int column = makeRandomAIMove();
                    handleAIMove(column);
                } else if (PlayerOneTurn && isThoughtfulAI) {
                    makeThoughtfulAIMove();
                }
            }
        });

        translateTransition.play();

        if (isBoardFull() && !gameEnded(0, 0)) {
            displayMessage("The game is a draw, No winner :(");
        }
    }

    
    private boolean isBoardFull() {
        for (int col = 0; col < numCols; col++) {
            if (!isColumnFull(col)) {
                return false; 
            }
        }
        //all columns are full meaning a full board
        return true; 
    }
    
    
    public boolean isColumnFull(int col) {
        // Check if the top row of the column is filled
        return columnStates.get(col).get(0);
    }



    private int findNextAvailableRow(int col) {
        int numRows = columnStates.get(0).size();

        for (int row = numRows - 1; row >= 0; row--) {
            if (row >= 0 && row < numRows && col >= 0 && col < columnStates.size() && !columnStates.get(col).get(row)) {
                return row;
            }
        }
        return -1;
    }


    
    private Circle createDisc(Paint fill) {
        double discDiameter = circle_diameter;
        Circle disc = new Circle(discDiameter/2, fill);
        disc.setSmooth(true);
        return disc;
    }
    

      
	private void displayMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
	

	public static boolean gameEnded(int row, int column) {
	    int numRows = 6;
	    int numCols = 7;
	    int targetColor = PlayerOneTurn ? 1 : 2;

	    //check if there is a chain in vertical line
	    boolean verticalWin = checkLine(row, column, 0, 1, numRows, numCols, targetColor);

	    //check if there is a chain in horizontal line
	    boolean horizontalWin = checkLine(row, column, 1, 0, numRows, numCols, targetColor);

	    //check if there is a chain in diagonal line (from top-left to bottom-right)
	    boolean diagonalWin1 = checkLine(row, column, 1, 1, numRows, numCols, targetColor);

	    //check if there is a chain in diagonal line (from top-right to bottom-left)
	    boolean diagonalWin2 = checkLine(row, column, 1, -1, numRows, numCols, targetColor);

	    //boolean match = verticalWin || horizontalWin || diagonalWin1 || diagonalWin2;
	    //System.out.println("Chain found: " + match );
	    
	    //return true if any of the lines is a winning combination
	    return verticalWin || horizontalWin || diagonalWin1 || diagonalWin2;
	}

	
	public static boolean checkLine(int startRow, int startCol, int rowIncrement, int colIncrement, int numRows, int numCols, int targetColor) {
	    int count = 1; // Start the count at 1 for the initial disc

	    for (int i = 1; i < 4; i++) { // Check the next three discs in the line
	        int currentRow = startRow + i * rowIncrement;
	        int currentCol = startCol + i * colIncrement;

	        if (currentRow >= 0 && currentRow < numRows && currentCol >= 0 && currentCol < numCols) {
	            int discColor = gameBoardState.get(currentRow).get(currentCol);

	            if (discColor == targetColor) {
	                count++;

	                if (count == 4) {
	                    return true;
	                }
	            } else {
	                // Reset count if the color changes
	                count = 0;
	            }
	        } else {
	            // Stop checking if outside the board boundaries
	            break;
	        }
	    }

	    return false;
	}




	private void displayWinner(String winnerName) {
		Platform.runLater(() -> {
	        String winnerColor = !PlayerOneTurn ? Player1Disc : Player2Disc;
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Game Over");
	        alert.setHeaderText(null);
	        alert.setContentText("Congratulations, " + winnerName + "! You won with " + winnerColor + " discs!");
	        System.out.println("Congratulations, " + winnerName + "! You won with " + winnerColor + " discs!");
	        alert.showAndWait();

	        try {
	            //log the winner information to the file
	            logWriter.write("\n");
	            logWriter.write("Winner: " + winnerName + ", Color: " + winnerColor);
	            logWriter.newLine();
	            logWriter.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        resetGame(); 
	    });
	}
	
	private void resetGame() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	    alert.setTitle("Reset Game");
	    alert.setHeaderText("Do you want to reset or exit the game?");
	    alert.setContentText("Choose your option.");
	    
	    ButtonType reset = new ButtonType("Reset");
	    ButtonType exit = new ButtonType("Exit");
	    ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

	    alert.getButtonTypes().setAll(reset, exit, cancel);
	    Optional<ButtonType> result = alert.showAndWait();

	    if (result.orElse(null) == reset) {
	    	clearDiscFills();
	    	
	    	initializeColumnStates();
	        initializeGameBoardState();
	        
	        PlayerOneTurn = true;
	        isRandomAI = true;
        	
        	updatePlayerTurnLabel();
	                	
            PlayerOne.clear();
            PlayerTwo.clear();
            PlayerOne.setStyle("");
            PlayerTwo.setStyle("");
         
	        //closing the log file when resetting the game
	        try {
	            if (logWriter != null) {
	                logWriter.close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        //initialize the log file for the new game
	        initializeLogFile();
	    } 
	    else if (result.orElse(null) == exit) {
	    	deleteLogFile();
	        System.exit(0);
	    }
	}
	
	
	private void initializeGameBoardState() {
	    for (int row = 0; row < numRows; row++) {
	        ArrayList<Integer> rowList = new ArrayList<>();
	        for (int col = 0; col < numCols; col++) {
	            rowList.add(0);
	        }
	        // Replace the existing row in the 2D array with the ArrayList
	        gameBoardState.add(rowList);
	    }
	}

	private void clearDiscFills() {
	    for (int row = 0; row < numRows; row++) {
	        for (int col = 0; col < numCols; col++) {
	            Circle disc = insertedDiscArray.get(row).get(col);
	            if (disc != null) {
	                GameBoardPane.getChildren().remove(disc);
	                insertedDiscArray.get(row).set(col, null);
	            }
	        }
	    }
	}

   
	@FXML
	void resumeButton(ActionEvent event) {
	    if (!gamePaused) {
	        System.out.println("Game Paused...");
	        saveCurrentGameState();
	        // When paused, set the menu item text to Resume
	        resume.setText("Resume Game");
	        gamePaused = true;
	    } else {
	        System.out.println("Game Resumes...Yayyy");
	        GameState gameState = loadGameState();
	        if (gameState != null) {
	            // Restoring the entire game state from log file (load and save)
	            columnStates = gameState.getColumnStates();
	            restoreGameState(gameState);
	        } else {
	            System.out.println("Could not load the game state.");
	        }
	        // Set the menu item text to Pause
	        resume.setText("Pause Game");
	        gamePaused = false;
	    }
	}

    
    
    @FXML
    void resetButton(ActionEvent event) {
    	saveCurrentGameState(); 
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Game");
        alert.setHeaderText("Are you sure you want to reset the game?");
        alert.setContentText("Resetting will start a new game.");

        if (alert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
        	clearDiscFills();
        	PlayerOneTurn = true;
            PlayerTwo.clear();
            initializeColumnStates();
	        initializeGameBoardState();
            PlayerOne.setStyle("");
            PlayerTwo.setStyle("");
                        
        }
    }

    
    @FXML
    void exitButton(ActionEvent event) {
        if (gamePaused) {
            //if the game is paused, prompt the user to save the session
            Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
            saveAlert.setTitle("Save Game");
            saveAlert.setHeaderText("Do you want to save the game before exiting?");
            saveAlert.setContentText("Choose your option.");

            ButtonType save = new ButtonType("Save");
            ButtonType exitWithoutSaving = new ButtonType("Exit Without Saving");
            ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

            saveAlert.getButtonTypes().setAll(save, exitWithoutSaving, cancel);
            Optional<ButtonType> result = saveAlert.showAndWait();

            //if the user clicked Save save the current Game State
            if (result.orElse(null) == save) {
                saveCurrentGameState();
                System.exit(0);
            } 
            else if (result.orElse(null) == exitWithoutSaving) {
                //if user clicked Exit Without Saving  -- delete log file and exit
                deleteLogFile();              
                System.exit(0);
            }
        } 
        else {     
            deleteLogFile();
            exitGame();
        }
    }

    private void deleteLogFile() {
        String lastLogFile = getLastLogFile();
        if (lastLogFile != null) {
            File logFile = new File(lastLogFile);
            if (logFile.exists()) {
                if (logFile.delete()) {
                    System.out.println("Log file deleted successfully.");
                }
                else {
                    System.out.println("Failed to delete the log file.");
                }
            }
        }
    }


    private void exitGame() {
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlert.setTitle("Exit Game");
        exitAlert.setHeaderText("Are you sure you want to exit the game?");
        exitAlert.setContentText("Exiting will close the application.");

        if (exitAlert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            try {
                if (logWriter != null) {
                	deleteLogFile();
                    logWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            deleteLogFile();
            System.exit(0);
        }
    }
    
    
  //===================================== AI Part =========================================================//


    private int makeRandomAIMove() {
        System.out.println("RandomAI is called");
        //choose a random move
        return (int) (Math.random() * numCols);
    }


    private void makeThoughtfulAIMove() {
    	System.out.println("Make Thoughtful AI called.");
        int column = SmartAI.makeSmartMove(gameBoardState, numCols);
        //get the moves from Smart AI class
        System.out.println("Smart column move is: " + column);
        handleAIMove(column);
    }

    
    @FXML
    void thoughtfulAIButton(ActionEvent event) {
    	isRandomAI = false;
    	isThoughtfulAI = true;
        displayMessage("Player One set to Thoughtful AI.");
        if (PlayerOneTurn && isThoughtfulAI) {
            makeThoughtfulAIMove();
        }
    }
    
    
    @FXML
    void randomAIButton(ActionEvent event) {
    	isRandomAI = true;
    	isThoughtfulAI = false;
        displayMessage("Player One set to Random AI.");
        if (PlayerOneTurn && isRandomAI) {
        	int column = makeRandomAIMove();
            handleAIMove(column);
        }
    }
    
    private void handleAIMove(int column) {
        System.out.println("Column for next move is: " + column);
        
        if (circlesClickable) {
            int row = findNextAvailableRow(column);
            
            if (row != -1) {
                if (!columnStates.get(column).get(0)) {
                    // Drop disc with a delay before AI move
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(event -> dropDisc(row, column));
                    pause.play();
                } else {
                    displayMessage("Column is full. Choose another column.");
                }
            }
        }
        saveCurrentGameState();
    }

    
    
    //===================================== Load and Save game =========================================================//
    private void saveGameState(GameState gameState) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("game_state.ser"))) {
            objectOutputStream.writeObject(gameState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GameState loadGameState() {
    	try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("game_state.ser"))) {
            return (GameState) objectInputStream.readObject();
    	} catch (IOException | ClassNotFoundException e) {
            // Ignore exceptions, as they may occur if there's no saved state
            return null;
        }
        
    }
    private void saveCurrentGameState() {
    	
        GameState gameState = new GameState(columnStates, PlayerOneTurn, PlayerOne.getText(), PlayerTwo.getText());
        saveGameState(gameState);
    }
    
    private void restoreGameState(GameState gameState) {
        PlayerOneTurn = gameState.isPlayerOneTurn();
        PlayerOne.setText(gameState.getPlayerOneName());
        PlayerTwo.setText(gameState.getPlayerTwoName());
        columnStates = gameState.getColumnStates();
        updatePlayerTurnLabel();

        //restore the disc drop positions
        String lastLogFile = getLastLogFile();
        if (lastLogFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(lastLogFile))) {
            	// Clear the existing logWriter
                if (logWriter != null) {
                    logWriter.close();
                }

                //update the log file for the current session
                logWriter = new BufferedWriter(new FileWriter(lastLogFile, true));
                
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Player:")) {
                        String[] parts = line.split(", ");
                        String playerName = parts[0].substring("Player: ".length());
                        int row = Integer.parseInt(parts[1].substring("Move: Row ".length()));
                        int col = Integer.parseInt(parts[2].substring("Column ".length()));

                        //check if a disc already exists at the specified row and column
                        if (insertedDiscArray.get(row).get(col) == null) {
                            // Restore the disc for the corresponding player at (row, col)
                            Color discColor = playerName.equals(PlayerOne.getText()) ? Color.valueOf(Player1Disc) : Color.valueOf(Player2Disc);
                            Circle disc = createDisc(discColor);
                            double translateX = col * (circle_diameter + margin) + circle_diameter / 4;
                            double translateY = row * (circle_diameter + margin) + circle_diameter / 4;
                            disc.setTranslateX(translateX);
                            disc.setTranslateY(translateY);
                            GameBoardPane.getChildren().add(disc);

                            // Update the ArrayList and other data structures
                            columnStates.get(col).set(row, true);
                            insertedDiscArray.get(row).set(col, disc);
                            
                            logMove(playerName, row, col);
                            gameBoardState.get(row).set(col, PlayerOneTurn ? 1 : 2);
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } else {
            System.out.println("No log files found. Starting a new game.");
        }
        
    }
    
    private void initializeLogFile() {
        try {
            String logFileName = "connect_four_log.txt";

            //check if the log file already exists
            File logFile = new File(logFileName);
            if (logFile.exists()) {
                //if exists, append to it
                logWriter = new BufferedWriter(new FileWriter(logFile, true));
            } else {
                //if doesn't exist, create a new one
                logWriter = new BufferedWriter(new FileWriter(logFile));
                logWriter.write("\n");
                logWriter.write(" --------------------------------- New Game -----------------------------------\n");
                logWriter.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    private String getLastLogFile() {
    	File logDirectory = new File("/Users/master-node/Desktop/CSCE-314-eclipse/SceneTest3");
        
        File[] logFiles = logDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (logFiles != null && logFiles.length > 0) {
            Arrays.sort(logFiles, Comparator.comparingLong(File::lastModified).reversed());
            return logFiles[0].getAbsolutePath();
        } 
        else {
            System.out.println("No log files found in the directory.");
            return null;
        }      
    }   
}
