package cs.uwf.edu.ktane.language;

import lombok.Getter;

public enum Responses {

    YES("yes"),
    NO("no");

    @Getter
    private String response;

    Responses(String response) {
        this.response = response;
    }
}
