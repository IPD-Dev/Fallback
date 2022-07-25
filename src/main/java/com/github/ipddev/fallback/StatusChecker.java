package com.github.ipddev.fallback;

import com.github.ipddev.fallback.messages.Messages;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.Scheduler;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public class StatusChecker {

	private final Main plugin;

	private void failureMessage(@NonNull Player player, @NonNull Throwable ex) {
		final Messages messages = plugin.getMessages();

		player.sendMessage(messages.getReconnectErrorMessage(ex));
	}

	public void pingServers() {
		final Messages messages = plugin.getMessages();
		final Component serverAvailableMessage = messages.getServerAvailableMessage();
		final Scheduler scheduler = plugin.getScheduler();
		final ArrayList<Player> toRemove = new ArrayList<>();

		for (Entry<Player, RegisteredServer> entry : plugin.getWaiting().entrySet()) {
			final Player player = entry.getKey();
			final RegisteredServer server = entry.getValue();

			try {
				server.ping().orTimeout(5, TimeUnit.SECONDS).get();
			} catch (Exception e) {
				failureMessage(player, e);
				return;
			}

			player.sendMessage(serverAvailableMessage);

			scheduler.buildTask(plugin, () -> {
					try {
						player.createConnectionRequest(server).connect();
					} catch (Exception e) {
						failureMessage(player, e);
					}
				})
				.delay(5, TimeUnit.SECONDS)
				.schedule();

			toRemove.add(player);
		}

		for (Player player : toRemove) {
			plugin.removePlayerFromQueue(player);
		}
	}
}
