package cs.uwf.edu.ktane.bomb;

import cs.uwf.edu.ktane.game.KtaneGame;
import cs.uwf.edu.ktane.game.ListeningConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Scanner;

import static cs.uwf.edu.ktane.language.Responses.YES;

@Getter
@Setter
@ToString
public class Bomb {

    private Boolean isSolved;
    private String currentModuleName; //instance variable might not be required here
    private Module theModule;
    private int batteryNo;
    private Boolean hasParallelPort;
    private Boolean hasVowel;
    private Boolean endsWithOdd;
    private Boolean hasFrk; //here for button purposes
    private Boolean hasCar; //here for button purposes
    private KtaneGame game;

    //might not need to be static
    public static Scanner consoleInput;

    public Bomb(KtaneGame game) {
        this.game = game;
        setIsSolved(false);
        setCurrentModuleName("none");
        setTheModule(null);
        setBatteryNo(0);
        setHasParallelPort(false);
        setHasVowel(false);
        setEndsWithOdd(false);
        setHasFrk(false);
        setHasCar(false);
        consoleInput = new Scanner(System.in);
    }

    //working
    public void getBombInfo() {

        //get the batteries
        ListeningConfig batteryListeningConfig = ListeningConfig.builder()
                                                                .question("How many batteries?")
                                                                .numeric(true)
                                                                .build();
        String batteryResponse = game.get(batteryListeningConfig);
        int batteries = Integer.parseInt(batteryResponse);
        setBatteryNo(batteries);

        //get pp status.
        ListeningConfig ppListeningConfig = ListeningConfig.builder()
                                                           .question("Parallel Port?")
                                                           .yesNo(true)
                                                           .build();
        String ppResponse = game.get(ppListeningConfig);
        if (ppResponse.equalsIgnoreCase(YES.getResponse())) {
            setHasParallelPort(true);
        }

        //find out if serial number contains a vowel
        ListeningConfig vowelListeningConfig = ListeningConfig.builder()
                                                              .question("Has vowel?")
                                                              .yesNo(true)
                                                              .build();
        String vowelResponse = game.get(vowelListeningConfig);
        if (vowelResponse.equalsIgnoreCase(YES.getResponse())) {
            setHasVowel(true);
        }

        //Find out if the serial number ends in an odd number
        ListeningConfig serialNumberListeningConfig = ListeningConfig.builder()
                                                                     .question("Does the serial number end in an odd number?")
                                                                     .yesNo(true)
                                                                     .build();
        String serialNumberResponse = game.get(serialNumberListeningConfig);
        if (serialNumberResponse.equalsIgnoreCase(YES.getResponse())) {
            setEndsWithOdd(true);
        }

        //find out if there is a lit frk indicator
        ListeningConfig litFrkListeningConfig = ListeningConfig.builder()
                                                               .question("Lit frk indicator?")
                                                               .yesNo(true)
                                                               .build();
        String litFrkResponse = game.get(litFrkListeningConfig);
        if (litFrkResponse.equalsIgnoreCase(YES.getResponse())) {
            setHasFrk(true);
        }

        //find out if there is a lit CAR indicator
        ListeningConfig litCarIndicatorListeningConfig = ListeningConfig.builder()
                                                                        .question("Lit CAR indicator?")
                                                                        .yesNo(true)
                                                                        .build();
        String litCarIndicatorResponse = game.get(litCarIndicatorListeningConfig);
        if (litCarIndicatorResponse.equalsIgnoreCase(YES.getResponse())) {
            setHasCar(true);
        }

        System.out.println(toString());
    }

    //working
    public void getModNameFromUser() {
        Boolean goodName = false;
        ListeningConfig moduleNameListeningConfig = ListeningConfig.builder()
                                                                   .question("Which module?")
                                                                   .build();
        String possibleName = game.get(moduleNameListeningConfig);

//		while(!goodName){
//			//If the name provided by the user is one of the module names
//			if(	possibleName.equalsIgnoreCase("wires") || possibleName.equalsIgnoreCase("button")
//				|| possibleName.equalsIgnoreCase("key") || possibleName.equalsIgnoreCase("simon")
//				|| possibleName.equalsIgnoreCase("first") || possibleName.equalsIgnoreCase("memory")
//				|| possibleName.equalsIgnoreCase("done")){
//
//				//a good module name was received
//				goodName = true;
//
//				//Set the name of the current module being solved. this instance variable might be removed later
//				setCurrentModuleName(possibleName);
//			}
//
//			else{
//				possibleName = game.get("Invalid module name, try again.");
//			}
//		}
    }

    //testing
    public Module getModInfo() {

        //if the wires module needs to be solved...
        if (getCurrentModuleName().equalsIgnoreCase("wires")) {
            //create wire object
            Wires theWires = new Wires();

            //set the flag if the last digit of the serial number is odd
            theWires.setEndsOdd(getEndsWithOdd());

            //get the valid number of wires to be user
            System.out.println("input number of wires\n");
            int wireNo = Integer.parseInt(consoleInput.nextLine());
            while (wireNo < 3 || wireNo > 6) {
                System.out.println("Wires must be 3, 4, 5, or 6. please try again");
                wireNo = Integer.parseInt(consoleInput.nextLine());
            }
            theWires.setWireCount(wireNo);


            //get the colors until the arraylist size matches total wires
            System.out.println("provide colors for each wire from top to bottom");
            String colorToTest = "no color selected";
            while (theWires.getWireColors()
                           .size() < theWires.getWireCount()) {
                System.out.print("input color: ");
                colorToTest = consoleInput.next();
                if (theWires.goodColorToAdd(colorToTest)) {
                    theWires.getWireColors()
                            .add(colorToTest);
                    consoleInput.nextLine();
                } else {
                    consoleInput.nextLine();
                    System.out.println("Bad Color, try again");
                }
            }

            //set wires as the module to be solved
            return theWires;

        }

        //*********************Button start *********************************
        else if (getCurrentModuleName().equalsIgnoreCase("button")) {
            //get info about button
            System.out.println("What color is the button?");
            String butColor = consoleInput.nextLine();
            System.out.println("What text is on the button?");
            String butText = consoleInput.nextLine();

            //create and return button object
            Button aButton = new Button(butColor, butText, getHasFrk(), getHasCar(), getBatteryNo());
            return aButton;


        }
        //*********************Button end *********************************


        //*********************Key start *********************************
        else if (getCurrentModuleName().equalsIgnoreCase("key")) {


            System.out.println("Enter the first symbol");
            String firstSym = consoleInput.nextLine();
            System.out.println("Enter the second symbol");
            String secondSym = consoleInput.nextLine();
            System.out.println("Enter the third symbol");
            String thirdSym = consoleInput.nextLine();
            System.out.println("Enter the fourth symbol");
            String fourthSym = consoleInput.nextLine();


            //create and return keypad object
            Keypad aKeypad = new Keypad(firstSym, secondSym, thirdSym, fourthSym);
            return aKeypad;
        }
        //*********************Key end *********************************


        else if (getCurrentModuleName().equalsIgnoreCase("simon")) {
            //get info about simon module

            //create and return simon object

            //***HERE TO KEEP COPILER HAPPY WHILE TESTING***
            Wires notAWire = new Wires();
            return notAWire;
        } else if (getCurrentModuleName().equalsIgnoreCase("first")) {
            //get info about first module

            //create and return first object

            //***HERE TO KEEP COPILER HAPPY WHILE TESTING***
            Wires notAWire = new Wires();
            return notAWire;
        } else if (getCurrentModuleName().equalsIgnoreCase("memory")) {
            //get info about memory module

            //create and return memory object

            //***HERE TO KEEP COPILER HAPPY WHILE TESTING***
            Wires notAWire = new Wires();
            return notAWire;
        }
        //bomb has been solved or has exploded
        else if (getCurrentModuleName().equalsIgnoreCase("done")) {
            //set flag to stop solving modules

            //***HERE TO KEEP COPILER HAPPY WHILE TESTING***
            Wires notAWire = new Wires();
            return notAWire;
        } else {
            //incorrect,


            //***HERE TO KEEP COPILER HAPPY WHILE TESTING***
            Wires notAWire = new Wires();
            return notAWire;
        }

    }
}
