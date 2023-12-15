package application;
import java.io.Serializable;
import java.util.ArrayList;


//The purpose is this class is to save the current state of the game, including the column states,
//whose turn it is, and the names of the players.

public class GameState implements Serializable {
	private static final long serialVersionUID = 1L;

    private ArrayList<ArrayList<Boolean>> columnStates;
    private boolean playerOneTurn;
    private String playerOneName;
    private String playerTwoName;

    public GameState(ArrayList<ArrayList<Boolean>> columnStates, boolean playerOneTurn, String playerOneName, String playerTwoName) {
        this.columnStates = columnStates;
        this.playerOneTurn = playerOneTurn;
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
    }


	public ArrayList<ArrayList<Boolean>> getColumnStates() {
        return columnStates;
    }

    public boolean isPlayerOneTurn() {
        return playerOneTurn;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

}
