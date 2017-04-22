package cs.uwf.edu.ktane.game;

import com.google.common.collect.Sets;
import cs.uwf.edu.speech.Listener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class KtaneGameTest {

    private KtaneGame game;

    @Mock
    private Listener mockListener;

    @Before
    public void setUp() {
        game = new KtaneGame(mockListener);
        ListeningConfig defaultConfig = ListeningConfig.builder()
                                                       .question("Test question")
                                                       .yesNo(true)
                                                       .build();
        game.setListeningConfig(defaultConfig);
    }

    @Test
    public void testProcessFiniteSetOfResponsesHapiPath() {
        ListeningConfig defaultConfig = ListeningConfig.builder()
                                                       .question("Test finite set of responses question")
                                                       .possibleAnswers(Sets.newHashSet("chicken", "egg"))
                                                       .build();
        game.setListeningConfig(defaultConfig);
        String answer = game.processFiniteSetOfResponses("chicken");
        assertEquals("The response did not match what was expected.", "chicken", answer);
    }

    @Test
    public void testProcessFiniteSetOfResponsesNoMatch() {
        ListeningConfig defaultConfig = ListeningConfig.builder()
                                                       .question("Test finite set of responses question")
                                                       .possibleAnswers(Sets.newHashSet("chicken", "egg"))
                                                       .build();
        game.setListeningConfig(defaultConfig);
        String answer = game.processFiniteSetOfResponses("rooster");
        assertNull("The response was expected to be null.", answer);
    }
}
