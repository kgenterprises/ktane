package cs.uwf.edu.ktane.bomb;

import com.google.common.collect.Sets;
import cs.uwf.edu.ktane.game.ListeningConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Wires extends ModuleBase {

    private int wireCount;
    private List<String> wireColors;
    private boolean endsWithOdd;

    public static final String MOD_NAME = "wires";

    private static final Set<String> POSSIBLE_COLORS = Sets.newHashSet("white", "blue", "black", "red", "yellow");

    public Wires(Bomb bomb) {
        this.bomb = bomb;
        setWireCount(0);
        wireColors = new ArrayList<>();
        setEndsWithOdd(false);
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

    @Override
    public void collectInfo() {
        //set the flag if the last digit of the serial number is odd
        endsWithOdd = bomb.isEndsWithOdd();

        //getFromUser the valid number of wires to be user
        ListeningConfig numWiresListeningConfig = ListeningConfig.builder()
                                                                 .question("How many wires are there in total?")
                                                                 .numeric(true)
                                                                 .possibleAnswers(Sets.newHashSet("3", "4", "5", "6"))
                                                                 .invalidNotification("Wires must be 3, 4, 5, or 6. please try again")
                                                                 .build();
        String numWiresResponse = bomb.getFromUser(numWiresListeningConfig);
        wireCount = Integer.parseInt(numWiresResponse);

        //getFromUser the colors until the arraylist size matches total wires
        bomb.postToUser("Get ready to provide colors for each wire from top to bottom..");
        while (wireColors.size() < wireCount) {
            ListeningConfig colorListeningConfig = buildColorListeningConfig();
            String nextColor = bomb.getFromUser(colorListeningConfig);
            wireColors.add(nextColor);
        }
    }

    private ListeningConfig buildColorListeningConfig() {
        return ListeningConfig.builder()
                              .question("Next color?")
                              .possibleAnswers(POSSIBLE_COLORS)
                              .invalidNotification(String.format("Valid colors are: %s. Please try again", POSSIBLE_COLORS))
                              .build();
    }

    @Override
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
            if (redCount > 1 && isEndsWithOdd()) {
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
                               .equalsIgnoreCase("black") && isEndsWithOdd()) {
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
            if (yellowCount == 0 && isEndsWithOdd()) {
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
