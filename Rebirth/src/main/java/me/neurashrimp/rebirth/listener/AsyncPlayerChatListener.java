package me.neurashrimp.rebirth.listener;

import me.neurashrimp.rebirth.placeholderapi.RebirthExpansion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {
    private static final String IDENTIFIER = "{rebirth_format}";

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        //player was not the direct cause so we don't care
        if (!event.isAsynchronous()) return;
        if (!event.getFormat().contains(IDENTIFIER)) return;
        String format = RebirthExpansion.execute(event.getPlayer().getUniqueId());
        event.setFormat(event.getFormat().replace(IDENTIFIER, format));
    }
}
