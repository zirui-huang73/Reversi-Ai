
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
/**
 * The MCTS implements an pMCTS with different Heuristic
 * * @ZIRUI HUANG
 * * @YIXU YE
 */
public class MCTS {
    private char[][] board;
    private char[][] initialboard;
    private HashMap<int[], List<int[]>> valid_tiles;
    private int computerRole;
    private double score;
    private int[] targetTile;
    private final static int SIZE = 8;
    private static int n = 0;
    public MCTS(char[][] board, int player, int[] targetTile) {
        char playerColor = player == 1 ? 'x' : 'o';
        this.initialboard = board;
        this.board = new char[SIZE][SIZE];
        this.valid_tiles = new HashMap<>();
        this.targetTile = targetTile;
        this.computerRole = player;
        this.score = 0;
    }

    public List<int[]> isValidTile(int row, int col, char style) {
        List<int[]> tilesToFlip = new ArrayList<>();
        // check if target tile is on board and has not been occupied
        if (!isOnBoard(row, col) || this.board[row][col] != 32) {
            return tilesToFlip;
        }
        this.board[row][col] = style;
        char opponent = style == 'o' ? 'x' : 'o';
        outer:
        for (int[] direction : Board.allDirections) {
            int xdirect = direction[0], ydirect = direction[1];
            int x = row + xdirect;
            int y = col + ydirect;
            // if the tile in one move with this direction is an opponent tile
            // chance are there that target tile can be valid
            if (isOnBoard(x, y) && this.board[x][y] == opponent) {
                x += xdirect;
                y += ydirect;
                while (isOnBoard(x, y)) {
                    // opponent tile, keep going
                    if (this.board[x][y] == opponent) {
                        x += xdirect;
                        y += ydirect;
                    }
                    // ally tile, stop
                    // we go back and add those opponent tiles to flip list
                    else if (this.board[x][y] == style) {
                        while (!(x == row && y == col)) {
                            x -= xdirect;
                            y -= ydirect;
                            tilesToFlip.add(new int[]{x, y});
                        }
                        continue outer;
                    }
                    // empty tile, no flip can happen this direction
                    else {
                        continue outer;
                    }
                }
            }
        }
        this.board[row][col] = ' ';
        return tilesToFlip;
    }
    public void findValidTile(int player) {
        char style = player == 1 ? 'x' : 'o';
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                List<int[]> flipList = isValidTile(i, j, style);
                if (flipList.size() > 0) {
                    this.valid_tiles.put(new int[]{i, j}, flipList);
                }
            }
        }
    }
    public boolean isOnBoard(int row, int col) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }
    public void addTile(int[] tileToAdd, int player) {
        char style = player == 1 ? 'x' : 'o';
        int row = tileToAdd[0];
        int col = tileToAdd[1];
        this.board[row][col] = style;
        for (int[] key : this.valid_tiles.keySet()) {
            if (Arrays.equals(tileToAdd, key)) {
                List<int[]> tileToFlip = this.valid_tiles.get(key);
                for (int[] tile : tileToFlip) {
                    this.board[tile[0]][tile[1]] = style;
                }
            }
        }
        this.valid_tiles.clear();
    }
    public void randomPlay(int player, int passCount) {
        if (passCount == 2) {
            this.score += this.gameResult();
            return;
        }
        findValidTile(player);
        if (this.valid_tiles.size() == 0) {
            randomPlay(-player, passCount+1);
            return;
        }
        int randomNum = ThreadLocalRandom.current().nextInt(0, this.valid_tiles.size());
        int i = 0;
        for (int[] tileToAdd : this.valid_tiles.keySet()) {
            if (i == randomNum) {
                addTile(tileToAdd, player);
                randomPlay(-player,  0);
                return;
            }
            i++;
        }
    }

    public int gameResult() {
        char computerStyle = this.computerRole == 1 ? 'x' : 'o';
        char humanStyle = this.computerRole == -1 ? 'x' : 'o';
        int computerCount = 0;
        int humanCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j] == computerStyle) {
                    computerCount++;
                } else if (this.board[i][j] == humanStyle) {
                    humanCount++;
                }
            }
        }
        if (computerCount > humanCount){
            return 1;
        }else{
            return 0;
        }
    }

    public double startMCTSEdge(int timeSlot){
        double wincount = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime+ 5000/(timeSlot+ 1);
        while(System.currentTimeMillis() < endTime){
            // every iteration we start with initial board
            this.board = Arrays.stream(this.initialboard).map(char[]::clone).toArray(char[][]::new);
            // add Herustic target tile to board
            wincount += Heuristic.isEdge(targetTile[0], targetTile[1])/1000.0;
            findValidTile(this.computerRole);
            addTile(this.targetTile, this.computerRole);
            // random play start with human step (because target tile by computer have been added)
            if(this.computerRole == -1){
                randomPlay(-this.computerRole, 0);
            }else {
                randomPlay(-this.computerRole, 0);
            }
            wincount += this.score;
            this.score = 0;
            n++;
        }
        System.out.println(targetTile[0]+","+targetTile[1]+" wincount=" +wincount+" n="+n);
        n = 0;
        return wincount;
    }
    public double startMCTSPowerPoint(int timeSlot){
        double wincount = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime+ 5000/(timeSlot+ 1);
        while(System.currentTimeMillis() < endTime){
            // every iteration we start with initial board
            this.board = Arrays.stream(this.initialboard).map(char[]::clone).toArray(char[][]::new);
            // add target tile to board
            wincount += Heuristic.isPowerPoint(targetTile[0], targetTile[1])/1000.0;
            findValidTile(this.computerRole);
            addTile(this.targetTile, this.computerRole);
            // random play start with human step (because target tile by computer have been added)
            if(this.computerRole == -1){
                randomPlay(-this.computerRole, 0);
            }else {
                randomPlay(-this.computerRole, 0);
            }
            wincount += this.score;
            this.score = 0;
            n++;
        }
        System.out.println(targetTile[0]+","+targetTile[1]+" wincount=" +wincount+" n="+n);
        n = 0;
        return wincount;
    }

    public double startMCTSMoveCounts(int timeSlot){
        double wincount = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime+ 5000/(timeSlot+ 1);
        while(System.currentTimeMillis() < endTime){
            // every iteration we start with initial board
            this.board = Arrays.stream(this.initialboard).map(char[]::clone).toArray(char[][]::new);
            // add target tile to board, change the win count to make it better
             /**
              * Heuristic part
              */
//            wincount += (100* valid_tiles.size() + Heuristic.isPowerPoint(targetTile[0], targetTile[1]))/((n+1)*1.0);
            wincount += ( 50 * valid_tiles.size() + Heuristic.isPowerPoint(targetTile[0], targetTile[1]))/1000.0;
            findValidTile(this.computerRole);
            addTile(this.targetTile, this.computerRole);
            // random play start with human step (because target tile by computer have been added)
            if(this.computerRole == -1){
                randomPlay(-this.computerRole, 0);
            }else {
                randomPlay(-this.computerRole, 0);
            }
            wincount += this.score;
            this.score = 0;
            n++;
        }
//        System.out.println(targetTile[0]+","+targetTile[1]+" wincount=" +wincount+" n="+n);
        n = 0;
        return wincount;
    }
    public double startMCTS(int timeSlot){
        int wincount = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime+ 5000/(timeSlot+ 1);

        while(System.currentTimeMillis() < endTime){
            // every iteration we start with initial board
            this.board = Arrays.stream(this.initialboard).map(char[]::clone).toArray(char[][]::new);
            // add target tile to board
            findValidTile(this.computerRole);
            addTile(this.targetTile, this.computerRole);
            // random play start with human step (because target tile by computer have been added)
            if(this.computerRole == -1){
                randomPlay(-this.computerRole, 0);
            }else {
                randomPlay(-this.computerRole, 0);
            }
            wincount += this.score;
            this.score = 0;
            n++;
        }
//        System.out.println(targetTile[0]+","+targetTile[1]+" wincount=" +wincount+" n="+n);
        n = 0;
        return wincount;
    }
}
