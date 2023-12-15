package application;

import java.util.ArrayList;

public class SmartAI {

    // checks if there is a move which Player One(AI) can play so that it can win
    public static int findWinningMove(ArrayList<ArrayList<Integer>> gameBoardState) {
        int numCols = gameBoardState.get(0).size();

        for (int col = 0; col < numCols; col++) {
            int row = findNextAvailableRow(gameBoardState, col);
            if (row != -1) {
                simulateMove(gameBoardState, row, col, 1);
                if (gameEnded(gameBoardState, row, col, 1)) {
                    undoMove(gameBoardState, row, col);
                    return col;
                }
                undoMove(gameBoardState, row, col);
            }
        }

        return -1;
    }

    // checks if there is a move to block Player Two from winning
 // checks if there is a move to block Player Two from winning
//    public static int findBlockingMove(ArrayList<ArrayList<Integer>> gameBoardState) {
//        int numCols = gameBoardState.get(0).size();
//
//        
//        for (int col = 0; col < numCols; col++) {
//            int row = findNextAvailableRow(gameBoardState, col);
//            if (row != -1) {
//                simulateMove(gameBoardState, row, col, 2); // Simulate Player Two's move
//                if (countConsecutiveDiscs(gameBoardState, row, col, 2) >= 3) { // Check for three consecutive discs
//                    undoMove(gameBoardState, row, col);
//                    return col;
//                }
//                undoMove(gameBoardState, row, col);
//            }
//        }
//
//        
//        for (int col = 0; col < numCols; col++) {
//            int row = findNextAvailableRow(gameBoardState, col);
//            if (row != -1) {
//                simulateMove(gameBoardState, row, col, 2); 
//                if (gameEnded(gameBoardState, row, col, 2)) { 
//                    undoMove(gameBoardState, row, col);
//                    return col;
//                }
//                undoMove(gameBoardState, row, col);
//            }
//        }
//
//      
//        for (int col = 0; col < numCols; col++) {
//            int row = findNextAvailableRow(gameBoardState, col);
//            if (row != -1) {
//                simulateMove(gameBoardState, row, col, 1);
//                if (gameEnded(gameBoardState, row, col, 1)) {
//                    undoMove(gameBoardState, row, col);
//                    return col;
//                }
//                undoMove(gameBoardState, row, col);
//            }
//        }
//
//        // If neither can win, take a strategic move
//        return findStrategicMove(gameBoardState);
//    }
//
//
//
//
    public static int findBlockingMove(ArrayList<ArrayList<Integer>> gameBoardState) {
        int numCols = gameBoardState.get(0).size();

        
        for (int col = 0; col < numCols; col++) {
            int row = findNextAvailableRow(gameBoardState, col);
            if (row != -1) {
                if (canWinInNextMove(gameBoardState, row, col, 2, 3)) {
                    return col;
                }
            }
        }

        
        for (int col = 0; col < numCols; col++) {
            int row = findNextAvailableRow(gameBoardState, col);
            if (row != -1) {
                if (canWinInNextMove(gameBoardState, row, col, 2, 4)) {
                    return col;
                }
            }
        }

       
        for (int col = 0; col < numCols; col++) {
            int row = findNextAvailableRow(gameBoardState, col);
            if (row != -1) {
                if (canWinInNextMove(gameBoardState, row, col, 1, 4)) {
                    return col;
                }
            }
        }

        // If neither can win, take a strategic move
        return findStrategicMove(gameBoardState);
    }

    private static boolean canWinInNextMove(ArrayList<ArrayList<Integer>> gameBoardState, int row, int col,
                                             int player, int consecutiveDiscs) {
        simulateMove(gameBoardState, row, col, player);
        int count = countConsecutiveDiscs(gameBoardState, row, col, player);
        undoMove(gameBoardState, row, col);
        return count >= consecutiveDiscs;
    }

    

    
    // else if check for where Player One(AI) has the most discs in line and place the disc in that line
    public static int findStrategicMove(ArrayList<ArrayList<Integer>> gameBoardState) {
        int numCols = gameBoardState.get(0).size();
        int[] discCounts = new int[numCols];

        // Iterate over each column and count the number of consecutive discs for Player One
        for (int col = 0; col < numCols; col++) {
            int row = findNextAvailableRow(gameBoardState, col);
            if (row != -1) {
                // simulate placing the disc for Player One and count the consecutive discs
                gameBoardState.get(row).set(col, 1);
                discCounts[col] = countConsecutiveDiscs(gameBoardState, row, col, 1);
                gameBoardState.get(row).set(col, 0);
            }
        }

        // find the column with the maximum number of consecutive discs
        int maxCount = 0;
        int strategicMove = -1;
        for (int col = 0; col < numCols; col++) {
            if (discCounts[col] > maxCount) {
                int futureMoveValue = evaluateFutureMove(gameBoardState, col, discCounts);
                if (futureMoveValue > maxCount) {
                    maxCount = futureMoveValue;
                    strategicMove = col;
                }
            }
        }

        return strategicMove;
    }

    // evaluate the future impact of a move
    private static int evaluateFutureMove(ArrayList<ArrayList<Integer>> gameBoardState, int col, int[] discCounts) {
        // simulate the move and evaluate its future impact
        simulateMove(gameBoardState, findNextAvailableRow(gameBoardState, col), col, 1);

        // additional evaluation criteria could be added here based on the game state
        int futureMoveValue = discCounts[col];

        // undo the move to restore the original game state
        undoMove(gameBoardState, findNextAvailableRow(gameBoardState, col), col);

        return futureMoveValue;
    }

    // else: if none of the above is possible, take a random move.
    public static int makeRandomMove(int numCols) {
        return (int) (Math.random() * numCols);
    }

    public static int makeSmartMove(ArrayList<ArrayList<Integer>> gameBoardState, int numCols) {
        // check for a winning move
        int winningMove = findWinningMove(gameBoardState);
        if (winningMove != -1) {
            return winningMove;
        }

        // check for a blocking move
        int blockingMove = findBlockingMove(gameBoardState);
        if (blockingMove != -1) {
            return blockingMove;
        }

        // check for a strategic move
        int strategicMove = findStrategicMove(gameBoardState);
        if (strategicMove != -1) {
            return strategicMove;
        }

        // if no winning, blocking, or strategic move, make a random move
        return makeRandomMove(numCols);
    }

    private static ArrayList<ArrayList<Integer>> simulateMove(ArrayList<ArrayList<Integer>> gameBoardState, int row, int col, int player) {
        ArrayList<ArrayList<Integer>> newGameBoardState = copyGameBoard(gameBoardState);
        newGameBoardState.get(row).set(col, player);
        return newGameBoardState;
    }

    private static ArrayList<ArrayList<Integer>> copyGameBoard(ArrayList<ArrayList<Integer>> gameBoardState) {
        ArrayList<ArrayList<Integer>> copy = new ArrayList<>();
        for (ArrayList<Integer> row : gameBoardState) {
            copy.add(new ArrayList<>(row));
        }
        return copy;
    }


    private static void undoMove(ArrayList<ArrayList<Integer>> gameBoardState, int row, int col) {
        int numRows = gameBoardState.size();
        int numCols = gameBoardState.get(0).size();

        if (row >= 0 && row < numRows && col >= 0 && col < numCols) {
            gameBoardState.get(row).set(col, 0);
        }
    }


    private static int findNextAvailableRow(ArrayList<ArrayList<Integer>> gameBoardState, int col) {
        int numRows = gameBoardState.size();
        for (int row = numRows - 1; row >= 0; row--) {
            if (gameBoardState.get(row).get(col) == 0) {
                return row;
            }
        }
        return -1;
    }

    
    private static boolean gameEnded(ArrayList<ArrayList<Integer>> gameBoardState, int row, int column,
            int targetColor) {
		int numRows = gameBoardState.size();
		int numCols = gameBoardState.get(0).size();
		
		boolean verticalWin = checkLine(gameBoardState, row, column, 0, 1, numRows, numCols, targetColor);
		boolean horizontalWin = checkLine(gameBoardState, row, column, 1, 0, numRows, numCols, targetColor);
		boolean diagonalWin1 = checkLine(gameBoardState, row, column, 1, 1, numRows, numCols, targetColor);
		boolean diagonalWin2 = checkLine(gameBoardState, row, column, 1, -1, numRows, numCols, targetColor);
		
		// Check if Player Two can win with four consecutive discs
		boolean playerTwoWin = countConsecutiveDiscs(gameBoardState, row, column, 2) >= 4;
		
		return verticalWin || horizontalWin || diagonalWin1 || diagonalWin2 || playerTwoWin;
    }


    public static boolean checkLine(ArrayList<ArrayList<Integer>> gameBoardState, int startRow, int startCol, int rowIncrement, int colIncrement, int numRows, int numCols, int targetColor) {
	    int count = 0; 

	    for (int i = 1; i < 4; i++) { 
	        int currentRow = startRow + i * rowIncrement;
	        int currentCol = startCol + i * colIncrement;

	        if (currentRow >= 0 && currentRow < numRows && currentCol >= 0 && currentCol < numCols) {
	            int discColor = gameBoardState.get(currentRow).get(currentCol);

	            if (discColor == targetColor || discColor == 0) {
	                count++;

	                if (count == 4) {
	                    return true;
	                }
	            } else {
	                count = 0;
	            }
	        } else {
	            break;
	        }
	    }

	    return false;
	}

    
    private static int countConsecutiveDiscs(ArrayList<ArrayList<Integer>> gameBoardState, int row, int col, int targetColor) {
        int numRows = gameBoardState.size();
        int numCols = gameBoardState.get(0).size();
        int count = 1;

        count += countLine(gameBoardState, row, col, 0, 1, numRows, numCols, targetColor);
        count += countLine(gameBoardState, row, col, 1, 0, numRows, numCols, targetColor);
        count += countLine(gameBoardState, row, col, 1, 1, numRows, numCols, targetColor);
        count += countLine(gameBoardState, row, col, 1, -1, numRows, numCols, targetColor);

        return count;
    }


    
    private static int countLine(ArrayList<ArrayList<Integer>> gameBoardState, int startRow, int startCol,
            int rowIncrement, int colIncrement, int numRows, int numCols, int targetColor) {
    	int count = 1;
    	
    	for (int i = 1; i < 4; i++) {
            int currentRow = startRow + i * rowIncrement;
            int currentCol = startCol + i * colIncrement;

            if (currentRow >= 0 && currentRow < numRows && currentCol >= 0 && currentCol < numCols) {
                int discColor = gameBoardState.get(currentRow).get(currentCol);
                if (discColor == targetColor) {
                    count++;

                    if (count == 4) {
                        return count; 
                    }
                } else {
                    count = 0;
                }
            }
        }

        return count;
    }
    


}
