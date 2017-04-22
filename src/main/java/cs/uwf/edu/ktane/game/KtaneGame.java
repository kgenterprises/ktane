package cs.uwf.edu.ktane.game;

import cs.uwf.edu.ktane.bomb.Bomb;
import cs.uwf.edu.speech.Listener;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static cs.uwf.edu.ktane.language.Response.*;

@RequiredArgsConstructor
public class KtaneGame implements Runnable {

    private Bomb bomb;

    private boolean listening = false;

    private String currentResponse;

    private final Listener listener;

    @Setter
    private ListeningConfig listeningConfig;

    public void processResponse(String response) {
        if (listeningConfig != null && listeningConfig.isApplicable()) {

            response = response.replaceAll("\\n", "");
            response = response.toLowerCase()
                               .trim();

            String result = null;

            if (listeningConfig.isNumeric()) {
                result = processNumericResponse(response);
            } else if (listeningConfig.isYesNo()) {
                result = processYesNoResponse(response);
            }

            if (listeningConfig.getPossibleAnswers() != null) {
                result = processFiniteSetOfResponses(result == null ? response : result);
            }

            if (result != null) {
//                if (listeningConfig.isValidate()) {
//                    if (listeningConfig.getAnswerToValidate() != null) {
//                        if (!listeningConfig.getAnswerToValidate()
//                                            .equals(result)) {
//                            postToUser(String.format("Your response could not be validated: %s != %s", listeningConfig.getAnswerToValidate(), result));
//                            getFromUser(listeningConfig);
//                        }
//                    } else {
//                        // first loop
//                        postToUser(listeningConfig.getValidationMessage());
//                        return;
//                    }
//                }
                currentResponse = result;
                listening = false;
                listeningConfig.setApplicable(false);
            } else {
                postToUser(listeningConfig.getInvalidNotification());
                postToUser(listeningConfig.getQuestion());
            }
        }
    }

    String processNumericResponse(String response) {
        /* first try the string as is */
        try {
            Integer.parseInt(response);
            return response;
        } catch (NumberFormatException e) {
            // do nothing
        }
        /* okay it might be textual repres., try to map it */
        switch (response) {
            case "one":
                return "1";
            case "two":
                return "2";
            case "three":
                return "3";
            case "four":
                return "4";
            case "five":
                return "5";
            case "six":
                return "6";
            case "seven":
                return "7";
            case "eight":
                return "8";
            case "nine":
                return "9";
            case "ten":
                return "10";
            default:
                return null;
        }
    }

    String processYesNoResponse(String response) {
        switch (response) {
            case "yes":
                return YES.getResponse();
            case "no":
                return NO.getResponse();
            default:
                return null;
        }
    }

    String processFiniteSetOfResponses(String response) {
        if (listeningConfig.getPossibleAnswers()
                           .contains(response)) {
            return response;
        } else {
            return null;
        }
    }

    public void play() {
        //getFromUser module name
        postToUser("Ready to solve modules.");

        while (!bomb.isSolved()) {
            bomb.getModNameFromUser();

            if (bomb.getCurrentModuleName()
                    .equals(DONE.getResponse())) {
                bomb.setSolved(true);
            } else {
                //getFromUser info required to set the module
                bomb.setModule(bomb.getModInfo());

                //solve the module
                bomb.getModule()
                    .solve();
            }
        }

        //Close the application
        postToUser("Goodbye");
    }

    public String getFromUser(ListeningConfig listeningConfig) {
        this.listeningConfig = listeningConfig;
        postToUser(listeningConfig.getQuestion());
        listening = true;
        while (listening) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return currentResponse;
    }

    public void postToUser(String toPost) {
        System.out.println(toPost);
    }

    @Override
    public void run() {

        bomb = new Bomb(this);
        postToUser(String.format("*** Ktane Game %s Started ***", Bomb.VERSION_NUMBER));

        //getFromUser bomb information
        bomb.getBombInfo();

        play();
    }
}
