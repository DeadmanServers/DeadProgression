package dead.deadProgression.chatinputmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputManager {

    private static final Map<UUID, Consumer<String>> pendingInput = new HashMap<>();

    public static void awaitInput(UUID uuid, Consumer<String> consumer) {
        pendingInput.put(uuid, consumer);
    }

    public static boolean isAwaiting(UUID uuid) {
        return pendingInput.containsKey(uuid);
    }

    public static boolean complete(UUID uuid, String input) {
        try {
            Consumer<String> stringConsumer = pendingInput.get(uuid);
            pendingInput.remove(uuid);
            stringConsumer.accept(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void cancel(UUID uuid) {
        pendingInput.remove(uuid);
    }


}
