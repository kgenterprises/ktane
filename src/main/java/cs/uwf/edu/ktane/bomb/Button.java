package cs.uwf.edu.ktane.bomb;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel on 3/31/2017.
 */
@Getter
@Setter
public class Button extends Module {

    private String color;
    private String text;
    private boolean hasFrk;
    private boolean hasCar;
    private int batNum;

    public Button(){
        super("Button");
        setColor("No Color");
        setText("No Text");
        setHasFrk(false);
        setHasCar(false);
        setBatNum(0);
    }

    public Button(String theColor, String theText, boolean fStatus, boolean cStatus, int bNum){
        super("Button");
        setColor(theColor);
        setText(theText);
        setHasFrk(fStatus);
        setHasCar(cStatus);
        setBatNum(bNum);
    }

    public void solve(){
        if(getColor().equalsIgnoreCase("blue") && getText().equalsIgnoreCase("abort")){
            displayClickAndHold();
        }
        else if(getBatNum() > 1 && getText().equalsIgnoreCase("detonate")){
            System.out.println("Click and immediately release the button");
        }
        else if(getColor().equalsIgnoreCase("white") && isHasCar()){
            displayClickAndHold();
        }
        else if(getBatNum() > 2 && isHasFrk()){
            System.out.println("Click and immediately release the button");
        }
        else if(getColor().equalsIgnoreCase("yellow")){
            displayClickAndHold();
        }
        else if(getColor().equalsIgnoreCase("red") && getText().equalsIgnoreCase("hold")){
            System.out.println("Click and immediately release the button");
        }
        else{
            displayClickAndHold();
        }
    }

    public void displayClickAndHold(){
        System.out.println("Click and hold.\n\n If blue, release if there is a \"4\" anywhere on the counter.");
        System.out.println("If yellow, release if there is a \"5\" anywhere on the counter.");
        System.out.println("If any other color, release if there is a \"1\" anywhere on the counter.");
    }
}
