package com.github.ipddev.fallback.listener;

import com.github.ipddev.fallback.Main;
import com.github.ipddev.fallback.messages.Messages;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.Optional;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public class ConnectListener {

	private final Main plugin;


	@Subscribe
	public void onServerConnect(ServerConnectedEvent event) {
		final Optional<RegisteredServer> previousServerOptional = event.getPreviousServer();

		if (previousServerOptional.isEmpty()) {
			return;
		}

		final RegisteredServer fallbackServer = plugin.getFallbackServer();
		final RegisteredServer previousServer = previousServerOptional.get();
		final Player player = event.getPlayer();

		if (previousServer.equals(fallbackServer)) {
			plugin.removePlayerFromQueue(player);

			return;
		}

		final RegisteredServer currentServer = event.getServer();

		if (!currentServer.equals(fallbackServer)) {
			plugin.removePlayerFromQueue(player);

			return;
		}

		final Messages messages = plugin.getMessages();
		final Component message = messages.getFallbackMessage(previousServer.getServerInfo().getName());

		plugin.removePlayerFromQueue(player);
		plugin.queuePlayer(player, previousServer);
		player.sendMessage(message);
	}

}
