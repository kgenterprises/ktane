package cs.uwf.edu.ktane.bomb;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Wires extends Module {

    private int wireCount;
    private ArrayList<String> wireColors;
    private boolean endsOdd;

    public Wires() {
        super("wires");
        setWireCount(0);
        wireColors = new ArrayList<>();
        setEndsOdd(false);
    }

    public boolean goodColorToAdd(String testcolor) {
        if (testcolor.equalsIgnoreCase("white") || testcolor.equalsIgnoreCase("red")
                || testcolor.equalsIgnoreCase("blue") || testcolor.equalsIgnoreCase("yellow")
                || testcolor.equalsIgnoreCase("black")) {

            return true;
        } else {
            System.out.println("Invalid color, please try again");
            return false;
        }
    }

    public void solve() {

        //Get total count of colors
        int redCount = 0;
        int blackCount = 0;
        int yellowCount = 0;
        int blueCount = 0;
        int whiteCount = 0;

        for (int i = 0; i < getWireCount(); i++) {
            if (getWireColors().get(i)
                               .equalsIgnoreCase("red")) {
                redCount++;
            }
            if (getWireColors().get(i)
                               .equalsIgnoreCase("black")) {
                blackCount++;
            }
            if (getWireColors().get(i)
                               .equalsIgnoreCase("yellow")) {
                yellowCount++;
            }
            if (getWireColors().get(i)
                               .equalsIgnoreCase("blue")) {
                yellowCount++;
            }
            if (getWireColors().get(i)
                               .equalsIgnoreCase("white")) {
                yellowCount++;
            }
        }

        //If there are three wires.
        if (getWireCount() == 3) {
            if (redCount == 0) {
                System.out.println("Cut the second wire.");
            } else if (wireColors.get(2)
                                 .equalsIgnoreCase("white")) {
                System.out.println("Cut the last wire");
            } else if (blueCount > 1) {
                System.out.println("Cut the last blue wire");
            } else {
                System.out.println("Cut the last wire");
            }
        }

        //If there are four wires total
        else if (getWireCount() == 4) {
            if (redCount > 1 && isEndsOdd()) {
                System.out.println("Cut the last red wire");
            } else if (getWireColors().get(3)
                                      .equalsIgnoreCase("yellow") && redCount == 0) {
                System.out.println("Cut the first wire");
            } else if (blueCount == 1) {
                System.out.println("Cut the first wire");
            } else if (yellowCount > 1) {
                System.out.println("Cut the last wire");
            } else {
                System.out.println("Cut the second wire");
            }
        }

        //If there are five wires total
        else if (getWireCount() == 5) {
            if (getWireColors().get(4)
                               .equalsIgnoreCase("black") && isEndsOdd()) {
                System.out.println("Cut the fourth wire");
            } else if (redCount == 1 && yellowCount > 1) {
                System.out.println("Cut the first wire");
            } else if (blackCount == 0) {
                System.out.println("Cut the second wire");
            } else {
                System.out.println("Cut the first wire");
            }
        }

        //If there are six wires total
        else {
            if (yellowCount == 0 && isEndsOdd()) {
                System.out.println("Cut the third wire");
            } else if (yellowCount == 1 && whiteCount > 1) {
                System.out.println("Cut the fourth wire");
            } else if (redCount == 0) {
                System.out.println("Cut the last wire");
            } else {
                System.out.println("Cut the fourth wire");
            }
        }

    }
}
