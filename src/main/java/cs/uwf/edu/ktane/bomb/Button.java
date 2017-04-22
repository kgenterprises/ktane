package cs.uwf.edu.ktane.bomb;

import com.google.common.collect.Sets;
import cs.uwf.edu.ktane.game.ListeningConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Created by Daniel on 3/31/2017.
 */
@Getter
@Setter
public class Button extends ModuleBase {

    private String color;
    private String text;
    private boolean hasFrk;
    private boolean hasCar;
    private int batNum;

    public static final String MOD_NAME = "button";

    private static final Set<String> POSSIBLE_COLORS = Sets.newHashSet("blue", "white", "yellow", "red");

    private static final Set<String> POSSIBLE_TEXT = Sets.newHashSet("abort", "detonate", "hold");

    public Button(Bomb bomb) {
        this.bomb = bomb;
    }

    @Override
    public void collectInfo() {
        hasFrk = bomb.isHasFrk();
        hasCar = bomb.isHasCar();
        batNum = bomb.getBatteryNo();

        /* getFromUser info about button */
        ListeningConfig buttonColorListeningConfig = ListeningConfig.builder()
                                                                    .question("What color is the button")
                                                                    .possibleAnswers(POSSIBLE_COLORS)
                                                                    .invalidNotification(String.format("Valid colors are: %s. Please try again", POSSIBLE_COLORS))
                                                                    .build();

        color = bomb.getFromUser(buttonColorListeningConfig);

        ListeningConfig buttonTextListeningConfig = ListeningConfig.builder()
                                                                   .question("What text is on the button")
                                                                   .possibleAnswers(POSSIBLE_TEXT)
                                                                   .invalidNotification(String.format("Valid text can only be: %s. Please try again", POSSIBLE_TEXT))
                                                                   .build();

        text = bomb.getFromUser(buttonTextListeningConfig);
    }

    @Override
    public void solve() {
        if (getColor().equalsIgnoreCase("blue") && getText().equalsIgnoreCase("abort")) {
            displayClickAndHold();
        } else if (getBatNum() > 1 && getText().equalsIgnoreCase("detonate")) {
            System.out.println("Click and immediately release the button");
        } else if (getColor().equalsIgnoreCase("white") && isHasCar()) {
            displayClickAndHold();
        } else if (getBatNum() > 2 && isHasFrk()) {
            System.out.println("Click and immediately release the button");
        } else if (getColor().equalsIgnoreCase("yellow")) {
            displayClickAndHold();
        } else if (getColor().equalsIgnoreCase("red") && getText().equalsIgnoreCase("hold")) {
            System.out.println("Click and immediately release the button");
        } else {
            displayClickAndHold();
        }
    }

    public void displayClickAndHold() {
        System.out.println("Click and hold.\n\n If blue, release if there is a \"4\" anywhere on the counter.");
        System.out.println("If yellow, release if there is a \"5\" anywhere on the counter.");
        System.out.println("If any other color, release if there is a \"1\" anywhere on the counter.");
    }
}
