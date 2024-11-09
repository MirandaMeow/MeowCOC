package fun.miranda.event;

import fun.miranda.controller.RollDice;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class RollDiceEvent implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    private void RD(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        if (!message.startsWith(".")) {
            return;
        }
        event.setCancelled(true);
        new RollDice(player, message);
    }
}
