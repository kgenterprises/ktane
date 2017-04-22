package edu.uwf.cs.ktane.bomb;

import com.google.common.collect.Sets;
import edu.uwf.cs.ktane.game.KtaneGame;
import edu.uwf.cs.ktane.game.ListeningConfig;
import edu.uwf.cs.ktane.language.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Bomb {

    private boolean solved;
    private String currentModuleName; //instance variable might not be required here
    private Module module;
    private int batteryNo;
    private boolean hasParallelPort;
    private boolean hasVowel;
    private boolean endsWithOdd;
    private boolean hasFrk; //here for button purposes
    private boolean hasCar; //here for button purposes
    private final KtaneGame game;

    public static final String VERSION_NUMBER = "Version One; Revision Three";

    private static final Logger LOG = LogManager.getLogger(Bomb.class);

    public void getBombInfo() {

        //getFromUser the batteries
        ListeningConfig batteryListeningConfig = ListeningConfig.builder()
                                                                .question("How many batteries?")
                                                                .numeric(true)
                                                                .build();
        String batteryResponse = game.getFromUser(batteryListeningConfig);
        int batteries = Integer.parseInt(batteryResponse);
        setBatteryNo(batteries);

        //getFromUser pp status.
        ListeningConfig ppListeningConfig = ListeningConfig.builder()
                                                           .question("Parallel Port?")
                                                           .yesNo(true)
                                                           .build();
        String ppResponse = game.getFromUser(ppListeningConfig);
        if (ppResponse.equalsIgnoreCase(Response.YES.getResponse())) {
            setHasParallelPort(true);
        }

        //find out if serial number contains a vowel
        ListeningConfig vowelListeningConfig = ListeningConfig.builder()
                                                              .question("Has vowel?")
                                                              .yesNo(true)
                                                              .build();
        String vowelResponse = game.getFromUser(vowelListeningConfig);
        if (vowelResponse.equalsIgnoreCase(Response.YES.getResponse())) {
            setHasVowel(true);
        }

        //Find out if the serial number ends in an odd number
        ListeningConfig serialNumberListeningConfig = ListeningConfig.builder()
                                                                     .question("Does the serial number end in an odd number?")
                                                                     .yesNo(true)
                                                                     .build();
        String serialNumberResponse = game.getFromUser(serialNumberListeningConfig);
        if (serialNumberResponse.equalsIgnoreCase(Response.YES.getResponse())) {
            setEndsWithOdd(true);
        }

        //find out if there is a lit frk indicator
        ListeningConfig litFrkListeningConfig = ListeningConfig.builder()
                                                               .question("Lit frk indicator?")
                                                               .yesNo(true)
                                                               .build();
        String litFrkResponse = game.getFromUser(litFrkListeningConfig);
        if (litFrkResponse.equalsIgnoreCase(Response.YES.getResponse())) {
            setHasFrk(true);
        }

        //find out if there is a lit CAR indicator
        ListeningConfig litCarIndicatorListeningConfig = ListeningConfig.builder()
                                                                        .question("Lit CAR indicator?")
                                                                        .yesNo(true)
                                                                        .build();
        String litCarIndicatorResponse = game.getFromUser(litCarIndicatorListeningConfig);
        if (litCarIndicatorResponse.equalsIgnoreCase(Response.YES.getResponse())) {
            setHasCar(true);
        }

        //TODO: delete or change to debug configuration with real logger
        LOG.debug(toString());
    }

    public void getModNameFromUser() {
        ListeningConfig moduleNameListeningConfig = ListeningConfig.builder()
                                                                   .question("Which module?")
                                                                   .possibleAnswers(Sets.newHashSet(Wires.MOD_NAME, Button.MOD_NAME, Keypad.MOD_NAME, "simon", "memory", "first", "done"))
                                                                   .build();
        currentModuleName = game.getFromUser(moduleNameListeningConfig);
    }

    public Module getModInfo() {

        switch (currentModuleName) {
            case Wires.MOD_NAME:
                Wires wires = new Wires(this);
                wires.collectInfo();
                return wires;
            case Button.MOD_NAME:
                Button button = new Button(this);
                button.collectInfo();
                return button;
            case Keypad.MOD_NAME:
                Keypad keypad = new Keypad(this);
                keypad.collectInfo();
                return keypad;
            case Simon.MOD_NAME:
                return null;
            case Memory.MOD_NAME:
                return null;
            default:
                return null;
        }
    }

    String getFromUser(ListeningConfig listeningConfig) {
        return game.getFromUser(listeningConfig);
    }

    void postToUser(String toPost) {
        game.postToUser(toPost);
    }
}
