package cs.uwf.edu.ktane;

import cs.uwf.edu.ktane.game.KtaneGame;
import cs.uwf.edu.speech.Listener;

/**
 * Manager for Speech Recognition and Ktane game threads
 */
public class KtaneManager {

    public static void main(String[] args) throws Exception {
        Listener listener = new Listener();
        KtaneGame game = new KtaneGame(listener);
        listener.setGame(game);

        Thread listenerThread = new Thread(listener);
        Thread gameThread = new Thread(game);

        gameThread.start();
        listenerThread.start();
    }
}
