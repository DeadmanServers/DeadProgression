package dead.deadProgression.chatinputmanager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatInputListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID id = player.getUniqueId();
        if (!ChatInputManager.isAwaiting(id)) {
            return;
        }
        String message = e.getMessage();
        switch (message) {
            case "cancel":
                ChatInputManager.cancel(id);
                player.sendRichMessage("<red>You have cancelled ");
                return;
        }
    }
}
