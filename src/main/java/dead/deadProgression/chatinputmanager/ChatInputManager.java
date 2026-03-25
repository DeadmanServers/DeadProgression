package dead.deadProgression.chatinputmanager;

import dead.deadProgression.DeadProgression;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ChatInputManager {

    private static final Map<UUID, PendingInput> pendingMap = new HashMap<>();

    public static void awaitInput(UUID uuid, PendingInput pendingInput) {
        pendingMap.put(uuid, pendingInput);
    }

    public static boolean isAwaiting(UUID uuid) {
        return pendingMap.containsKey(uuid);
    }

    public static void complete(UUID uuid, String input) {
        try {
            PendingInput pendingInput = pendingMap.get(uuid);
            Consumer<String> action = pendingInput.getAction();
            pendingMap.remove(uuid);
            action.accept(input);
        } catch (Exception e) {
            DeadProgression.INSTANCE.getLogger().log(Level.WARNING, "Error completing chat input");
        }
    }

    public static void cancel(UUID uuid) {
        pendingMap.remove(uuid);
    }

    public static String getCancelMessage(UUID uuid) {
        PendingInput pendingInput = pendingMap.get(uuid);
        if (pendingInput == null) {
            return "There is no pending input";
        }
        return pendingInput.getCancelMessage();
    }


}
