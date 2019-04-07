/*
 * Plugin-Portfolio - A portfolio of some of the Minecraft plugins made by SizableShrimp.
 *
 * Copyright (C) 2019 SizableShrimp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
