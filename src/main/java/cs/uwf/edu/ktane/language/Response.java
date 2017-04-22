package cs.uwf.edu.ktane.language;

import lombok.Getter;

public enum Response {

    YES("yes"),
    NO("no"),
    DONE("done");

    @Getter
    private String response;

    Response(String response) {
        this.response = response;
    }
}
