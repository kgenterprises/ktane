package cs.uwf.edu.ktane.game;

import cs.uwf.edu.speech.StreamingRecognizeClient;
import cs.uwf.edu.ktane.bomb.Bomb;

import static cs.uwf.edu.ktane.language.Responses.NO;
import static cs.uwf.edu.ktane.language.Responses.YES;

public class KtaneGame implements Runnable {

    private Bomb bomb;

    private boolean listening = false;

    private String currentResponse;

    private StreamingRecognizeClient listener;

    private ListeningConfig listeningConfig;

    public void processResponse(String response) {
        if (listeningConfig != null && listeningConfig.isApplicable()) {
            response = response.replaceAll("\\n", "");
            String result = null;
            if (listeningConfig.isNumeric()) {
                result = processNumericResponse(response);
            } else if (listeningConfig.isYesNo()) {
                result = processYesNoResponse(response);
            }


            if (result != null) {
                currentResponse = result;
                listening = false;
                listeningConfig.setApplicable(false);
            } else {
                post(listeningConfig.getInvalidNotification());
                post(listeningConfig.getQuestion());
            }
        }
    }

    private String processNumericResponse(String response) {
        /* first try the string as is */
        try {
            Integer.parseInt(response);
            return response;
        } catch (NumberFormatException e) {
            // do nothing
        }
        /* okay it might be textual repres., try to map it */
        switch (response.toLowerCase().trim()) {
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

    private String processYesNoResponse(String response) {
        switch(response.toLowerCase().trim()) {
            case "yes":
                return YES.getResponse();
            case "no":
                return NO.getResponse();
            default:
                return null;
        }
    }

    public void play() {

        //get module name
        post("Ready to solve modules.");

        while (!bomb.getIsSolved()) {
            bomb.getModNameFromUser();

            if (bomb.getCurrentModuleName().equalsIgnoreCase("done")) {
                bomb.setIsSolved(true);
            } else {
                //get info required to set the module
                bomb.setTheModule(bomb.getModInfo());

                //solve the module
                bomb.getTheModule().solve();
            }
        }


        //user input module name
        //String moduleName = sc.nextLine();
        //Gets the module name and makes sure its valid
        //solve the module


        // bomb.getTheModule().solve();

        //bot confirm that the module name was heard correctly

        //Bot asks for first user input

        //user provides input


        //Close the application
        post("Goodbye");
    }

    public String get(ListeningConfig listeningConfig) {
        this.listeningConfig = listeningConfig;
        post(listeningConfig.getQuestion());
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

    public void post(String toPost) {
        System.out.println(toPost);
    }

    public void setListener(StreamingRecognizeClient listener) {
        this.listener = listener;
    }

    @Override
    public void run() {

        System.out.println("***Bot Started***\n.");
        bomb = new Bomb(this);

        //get bomb information
        bomb.getBombInfo();

//        play();
    }
}
