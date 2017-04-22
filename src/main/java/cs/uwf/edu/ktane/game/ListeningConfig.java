package cs.uwf.edu.ktane.game;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
public class ListeningConfig {

    private String question;

    @Builder.Default
    private boolean applicable = true;

    @Builder.Default
    private String invalidNotification = "Invalid response";

    private boolean numeric;

    private boolean yesNo;

    private Set<String> possibleAnswers;

}