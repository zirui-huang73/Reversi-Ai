import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/**
 * The Board class form a board for user to see and get the tile
 * from user and AI
 * * @ZIRUI HUANG
 * * @YIXU YE
 */
public class Board {
    final static int WIDTH = 8;
    final static int LENGTH = 8;
    public final static int[][] allDirections = {{0,1}, {1,1}, {1,0}, {1,-1}, {0,-1}, {-1,-1}, {-1,0}, {-1,1}};
    private final static int SIZE = 8;
    private char[][] reversiBoard;
    private HashMap<int[], List<int[]>> valid_tiles;
    private int tileCount;

    public Board() {
        this.reversiBoard = new char[WIDTH][LENGTH];
        this.valid_tiles = new HashMap<>();
        this.tileCount = 4;
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < LENGTH; j++) {
                this.reversiBoard[i][j] = ' ';
            }
        }
        this.reversiBoard[3][3] = 'o';
        this.reversiBoard[3][4] = 'x';
        this.reversiBoard[4][3] = 'x';
        this.reversiBoard[4][4] = 'o';
    }
    public Set<int[]> getValidTilesKey() {
        return this.valid_tiles.keySet();
    }
    public void drawBoard(){
        for (int[] tile: this.valid_tiles.keySet()){
            int row = tile[0];
            int col = tile[1];
            this.reversiBoard[row][col] = '.';
        }
        String graph = "-------------------------------------\n| $ |";
        for (int i = 0; i < 8; i++) {
            graph += " "+  i+" |";
        }
        graph += "\n-------------------------------------\n";
        int i = 0;
        for (char[] x:this.reversiBoard) {
            graph += "| " + i++;
            for (char y : x) {
                graph += ( " | " + y );
            }
            graph+=  " |\n-------------------------------------\n";
        }
        System.out.println(graph);
        for (int[] tile: this.valid_tiles.keySet()){
            int row = tile[0];
            int col = tile[1];
            this.reversiBoard[row][col] = ' ';
        }
    }
    public boolean isOnBoard(int row, int col){
        return row>=0 && row <=7 && col>=0 && col<=7;
    }
    public boolean isPass(){
        return this.valid_tiles.size() == 0;
    }
    public boolean isFullBoard(){
        return this.tileCount == 64;
    }
    // Return empty set if is not valid
    // Otherwise return list of tiles to be flipped
    // Outer like label
    public List<int[]> isValidTile(int row, int col, char style){
        List<int[]> tilesToFlip = new ArrayList<>();
        // Check if target tile is on board and has not been occupied
        if(!isOnBoard(row,col) || this.reversiBoard[row][col] != 32){
            return tilesToFlip;
        }
        this.reversiBoard[row][col] = style;
        char opponent = style == 'o'? 'x':'o';
        outer: for(int[] direction: allDirections){
                    int xdirect = direction[0], ydirect = direction[1];
                    int x = row + xdirect;
                    int y = col + ydirect;
                    // If the tile in one move with this direction is an opponent tile
                    // chance are there that target tile can be valid
                    if(isOnBoard(x, y) && this.reversiBoard[x][y] == opponent){
                        x += xdirect;
                        y += ydirect;
                        while(isOnBoard(x, y)){
                            // opponent tile, keep going
                            if (this.reversiBoard[x][y] == opponent){
                                x += xdirect;
                                y += ydirect;
                            }
                            // All tile, stop
                            // We go back and add those opponent tiles to flip list
                            else if(this.reversiBoard[x][y] == style){
                                while (!(x == row && y ==col)){
                                    x -= xdirect;
                                    y -= ydirect;
                                    tilesToFlip.add(new int[]{x, y});
                                }
                                continue outer;
                            }
                            // Empty tile, no flip can happen this direction
                            else{
                                continue outer;
                            }
                        }
                    }
        }
        this.reversiBoard[row][col] = ' ';
        return tilesToFlip;
    }
    public void findValidTile(int player){
        char style = player == 1 ? 'x':'o';
        for(int i=0; i<WIDTH; i++){
            for(int j=0; j<LENGTH; j++){
                List<int[]> flipList = isValidTile(i, j, style);
                if (flipList.size() > 0){
                    this.valid_tiles.put(new int[]{i,j}, flipList);
                }
            }
        }
    }
    // Add input tile to board
    // Flip tiles
    public void addTile(int[] tileToAdd, int player){
        this.tileCount++;
        char style = player == 1 ? 'x':'o';
        int row = tileToAdd[0];
        int col = tileToAdd[1];
        this.reversiBoard[row][col] = style;
        for (int[] key: this.valid_tiles.keySet()){
            if (Arrays.equals(tileToAdd, key)){
                List<int[]> tileToFlip = this.valid_tiles.get(key);
                for (int[] tile: tileToFlip){
                    this.reversiBoard[tile[0]][tile[1]] = style;
                }
            }
        }
        this.valid_tiles.clear();
    }
    // For count final how many black and white
    public int countBoard(char style){
        int count = 0;
        for (int i=0; i<WIDTH; i++){
            for(int j=0; j<LENGTH; j++){
                if (this.reversiBoard[i][j] == style){
                    count++;
                }
            }
        }
        return count;
    }
    // Pure Random play
    public int[] getComputerTilePR(int player){
        int randomNum = ThreadLocalRandom.current().nextInt(0, this.valid_tiles.size());
        int i = 0;
        for (int[] tileToAdd: this.valid_tiles.keySet()){
            if (i == randomNum){
                return tileToAdd;
            }
            i++;
        }
        return null;
    }
    // Random play with pure MCTS
    public int[] getComputerTilePMCTS(int player){
        List<int[]> tiles = new ArrayList<>(this.valid_tiles.keySet());

        double[] scoreboard = new double[tiles.size()];
        char[][] boardCopy = new char[SIZE][SIZE];
        for (int x=0; x < SIZE; x++){
            for (int y=0; y < SIZE; y++){
                boardCopy[x][y] = this.reversiBoard[x][y];
            }
        }
        for (int i=0; i < tiles.size(); i++){
            MCTS mcts = new MCTS(boardCopy, player, tiles.get(i));
            double score = mcts.startMCTS(this.valid_tiles.size());
            scoreboard[i] = score;
        }
        int index = 0;
        // Get max score for random
        double maxScore = scoreboard[index];
        for (int i=1; i<scoreboard.length; i++){
            if(scoreboard[i] > maxScore){
                index = i;
                maxScore = scoreboard[index];
            }
        }
        return tiles.get(index);
    }
    // Random player with corner
    public int[] getComputerTileModified(int player){
        List<int[]> tiles = new ArrayList<>(this.valid_tiles.keySet());
        double[] scoreboard = new double[tiles.size()];
        char[][] boardCopy = new char[SIZE][SIZE];
        for (int x=0; x < SIZE; x++){
            for (int y=0; y < SIZE; y++){
                boardCopy[x][y] = this.reversiBoard[x][y];
            }
        }
        for (int i=0; i < tiles.size(); i++){
            if(isCorner(tiles.get(i))){
                return tiles.get(i);
            }
            MCTS mcts = new MCTS(boardCopy, player, tiles.get(i));
            /**
             * For different Heuristic test try different startMCTS
             */
            double score = mcts.startMCTSMoveCounts(this.valid_tiles.size());
            scoreboard[i] = score;
        }
        int index = 0;
        // Get max score for random
        double maxScore = scoreboard[index];
        for (int i=1; i<scoreboard.length; i++){
            if(scoreboard[i] > maxScore){
                index = i;
                maxScore = scoreboard[index];
            }
        }
        return tiles.get(index);
    }
    boolean isCorner(int[] tile){
        boolean westNorthCor = tile[0] == 0 && tile[1] == 0;
        boolean westSouthCor = tile[0] == 7 && tile[1] == 0;
        boolean eastNorthCor = tile[0] == 0 && tile[1] == 7;
        boolean eastSouthCor = tile[0] == 7 && tile[1] == 7;
        return  westNorthCor || westSouthCor || eastNorthCor || eastSouthCor;
    }
} // Board.java

