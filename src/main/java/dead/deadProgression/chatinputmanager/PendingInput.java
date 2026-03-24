package dead.deadProgression.chatinputmanager;

import java.util.function.Consumer;

public class PendingInput {

    private final Consumer<String> action;
    private final String cancelMessage;

    public PendingInput(Consumer<String> action, String cancelMessage) {
        this.action = action;
        this.cancelMessage = cancelMessage;
    }

    public Consumer<String> getAction() {
        return action;
    }
    public String getCancelMessage() {
        return cancelMessage;
    }

}
