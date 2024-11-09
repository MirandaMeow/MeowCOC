package fun.miranda.event;

import fun.miranda.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static fun.miranda.MeowCOC.plugin;

public class PCChatEvent implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().startsWith(".")) {
            return;
        }
        Player player = event.getPlayer();
        String KPName = plugin.config.getString("COC.KP");
        if (player.getName().equals(KPName)) {
            if (plugin.log != null) {
                plugin.log.log(String.format("[KP %s] %s", event.getPlayer().getName(), event.getMessage()));
                event.setFormat(String.format("§c[KP %s] §f", event.getPlayer().getName()) + "%2$s");
                return;
            }
        }
        if (Utils.allPCPlayer().contains(player.getName())) {
            String cardName = plugin.config.getString(String.format("COC.Players.%s", player.getName()));
            if (plugin.log != null) {
                plugin.log.log(String.format("[%s] %s", cardName, event.getMessage()));
                event.setFormat(String.format("§e[%s] §f", cardName) + "%2$s");
            }
        }
    }
}
