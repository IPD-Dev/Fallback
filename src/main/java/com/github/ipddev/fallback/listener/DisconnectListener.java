package com.github.ipddev.fallback.listener;

import com.github.ipddev.fallback.Main;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.RedirectPlayer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DisconnectListener {
	private final Main plugin;

	@Subscribe
	public void onDisconnect(DisconnectEvent event) {
		plugin.removePlayerFromQueue(event.getPlayer());
	}

	@Subscribe
	public void onKickedFromServer(KickedFromServerEvent event) {
		final RegisteredServer server = event.getServer();
		final RegisteredServer fallbackServer = plugin.getFallbackServer();

		if (server.equals(fallbackServer)) {
			return;
		}

		event.setResult(RedirectPlayer.create(fallbackServer));
	}
}
