import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("-------------------");
        System.out.println("Welcome to Reversi!");
        System.out.println("-------------------");
        Input input = new Input();
        int player = input.playerInput();
        Reversi game = new Reversi(player);
        game.start();
        game.end();
        System.out.println("Game End, See you next time!");
    }
} //Main.java
/*
 * AI vs AI
 * */


//public class Main {
//    public static void main(String[] args) {
//        int i = 0;
//        int win = 0;
//        int draw = 0;
//        int option = 0;
//
//        while(i < 50){
//            System.out.println("-------------------");
//            System.out.println("Welcome to Reversi!");
//            System.out.println("-------------------");
////            Input input = new Input();
//            int player = 1;
//            Reversi game = new Reversi(player);
//            game.start();
//            game.end();
//            if(game.end() == 1){
//                win++;
//            }else if(game.end() == 0){
//                draw++;
//            }
//            System.out.println("Game " + i + " End, See you next time!");
//            i++;
//        }
//        System.out.println("win" + win);
//        System.out.println("draw" +draw);
//    }
//} //Main.java