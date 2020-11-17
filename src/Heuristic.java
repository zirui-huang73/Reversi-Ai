/**
 * The Heristic helps MCTS to work better
 * * @ZIRUI HUANG
 * * @YIXU YE
 */

public class Heuristic {
    public static int isEdge(int x, int y){
        if(isCorner(x,y)){
            return 10000;
        }
        if(x >= 0 && x <= 7 && (y==0 || y == 7)){
            return 2;
        }
        if(y >= 0 && y <= 7 && (x==0 || x == 7)){
            return 2;
        }
        return 1;
    }
    public static boolean isCorner(int x, int y) {
        boolean westNorthCor = x == 0 && y == 0;
        boolean westSouthCor = x == 7 && y == 0;
        boolean eastNorthCor = x == 0 && y == 7;
        boolean eastSouthCor = x == 7 && y == 7;
        return  westNorthCor || westSouthCor || eastNorthCor || eastSouthCor;
    }
    public static int isPowerPoint(int x, int y) {
        if(x == 5 && y == 2 || x ==  2 && y == 5){
            return 2;
        }
        if(x == 2 && y == 2 || x ==  5 && y == 5){
            return 2;
        }
        if(x == 0 &&  y == 2 || x == 2 &&  y == 0){
            return 3;
        }
        if(x == 0 &&  y == 6 || x == 6 &&  y == 0){
            return 3;
        }
        if(x == 7 && y == 2 || x == 2 &&  y == 7){
            return 3;
        }
        if(x == 7 && y == 5 || x == 5 &&  y == 7){
            return 3;
        }
        return isEdge(x,y);
    }
}
