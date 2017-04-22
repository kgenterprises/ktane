package cs.uwf.edu.ktane;

import cs.uwf.edu.speech.StreamingRecognizeClient;
import cs.uwf.edu.ktane.game.KtaneGame;

/**
 * Manager for Speech Recognition and Speech Processor threads
 */
public class KtaneManager {

    public static void main(String[] args) throws Exception {
        StreamingRecognizeClient listener = new StreamingRecognizeClient();
        KtaneGame game = new KtaneGame();
        listener.setGame(game);
        game.setListener(listener);

        Thread listenerThread = new Thread(listener);
        Thread gameThread = new Thread(game);

        gameThread.start();
        listenerThread.start();
    }
}
