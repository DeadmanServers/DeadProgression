package dead.deadProgression.chatinputmanager;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatInputListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Player player = e.getPlayer();
        UUID id = player.getUniqueId();
        if (!ChatInputManager.isAwaiting(id)) {
            return;
        }
        String message = e.getMessage();
        if (message.equalsIgnoreCase("cancel")) {
            String cancelMessage = ChatInputManager.getCancelMessage(id);
            ChatInputManager.cancel(id);
            player.sendMessage(MiniMessage.miniMessage().deserialize(cancelMessage));
        } else {
            ChatInputManager.complete(id, message);
        }
    }
}
