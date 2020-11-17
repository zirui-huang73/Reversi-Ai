// https://medium.com/dev-genius/winning-reversi-with-monte-carlo-tree-search-3e1209c160b
// https://bonaludo.com/2016/02/18/reversi-and-othello-two-different-games-do-you-know-their-different-rules/
/**
 * The Reversi trigger the reversi game class
 * * @ZIRUI HUANG
 * * @YIXU YE
 */
import java.util.*;
public class Reversi {
    private int player;
    private int computer;
    private Board board;
    private Input inputer;

    public Reversi(int player) {
        this.player = player;
        this.computer =  -player;
        this.board = new Board();
        this.inputer = new Input();
//        System.out.println("computer is:" + this.computer);
    }
    public boolean isValidInput(int[] input){
        for(int[] tile: this.board.getValidTilesKey()){
            if(Arrays.equals(tile, input)){
                return true;
            }
        }
        return false;
    }

    public void start(){
        boolean onGame = true;
        int turn = 1;
        int passCount = 0;
        while(onGame) {
            // if board is full, end game
            if (this.board.isFullBoard()) {
                return;
            }
            // Find valid tiles and corresponding flip tiles
            this.board.findValidTile(turn);
            // If it is a pass, transfer control to next player
            if (this.board.isPass()) {
                System.out.println("It's a pass");
                passCount++;
                // two consecutive pass, end game
                if (passCount == 2) {
                    return;
                } else {
                    turn *= -1;
                    continue;
                }
            }
            // human step or test heuristic step
            if (turn == this.player) {
                System.out.println("Your turn");
                // if no valid for this player, pass
                this.board.drawBoard();
                int[] input_tile;
                while (true) {
                    input_tile = inputer.tileInput();
                    if (isValidInput(input_tile)) {
                        this.board.addTile(input_tile, turn);
                        break;
                    } else {
                        System.out.println("invalid tile, choose another one");
                    }
                }
                passCount = 0;
                turn *= -1;
                //
//              For AI vs AI testing
//              int[] input_tile = this.board.getComputerTileModified(this.player);
//              this.board.addTile(input_tile, turn);
//              turn *= -1;
//              passCount = 0;
            }
            // MCTS computer step
            else {
                System.out.println("AI turn, please wait for a second...");
                int[] computer_tile = this.board.getComputerTileModified(this.computer);
                this.board.addTile(computer_tile, turn);
                turn *= -1;
                passCount = 0;
            }
        }
    }

    public int end(){
        this.board.drawBoard();
        int blackCount = this.board.countBoard('x');
        int whiteCount = this.board.countBoard('o');
        System.out.println(blackCount+ ":" + whiteCount);
        if (blackCount > whiteCount){
            if(this.player==1){
                System.out.println("**********");
                System.out.println("Human Wins");
                System.out.println("**********");
                return 1;
            } else{
                System.out.println("**********");
                System.out.println("Computer Wins");
                System.out.println("**********");
                return -1;
            }

        } else if(blackCount < whiteCount){
            if (this.player==-1){
                System.out.println("**********");
                System.out.println("Human Wins");
                System.out.println("**********");
                return 1;
            } else{
                System.out.println("*************");
                System.out.println("Computer Wins");
                System.out.println("*************");
                return -1;
            }
        } else {
            System.out.println("****");
            System.out.println("Draw");
            System.out.println("****");
            return 0;
        }
    }
} // Reversi.java

