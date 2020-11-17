import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Input {
    Scanner input = new Scanner(System.in);
    public int[] tileInput(){
        int x = -1, y = -1;
        boolean again = true;
        while(again){
            try {
                System.out.print("Enter row: ");
                x= input.nextInt();
                System.out.print("Enter col: ");
                y = input.nextInt();
                again = false;
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input");
                input.next();
            }
        }
        return new int[]{x, y};
    }

    public int playerInput(){
        boolean again = true;
        int result = -1;
        while(again){
            try {
                System.out.print("Enter 1 for x, 0 for o: ");
                result = input.nextInt();
                if (result==0 || result==1){
                    again = false;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input");
                input.next();
            }
        }
        if (result == 1){
            return 1;
        }
        else{
            return -1;
        }
    }
}