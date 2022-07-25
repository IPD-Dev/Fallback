package com.github.ipddev.fallback;

import com.github.ipddev.fallback.config.Configuration;
import com.github.ipddev.fallback.listener.ConnectListener;
import com.github.ipddev.fallback.listener.DisconnectListener;
import com.github.ipddev.fallback.messages.Messages;
import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.Scheduler;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;

@Plugin(
	id = "fallback",
	name = "Fallback",
	version = BuildConstants.VERSION,
	authors = {"Allink"}
)
public class Main {

	private static final Map<Player, RegisteredServer> WAITING = new HashMap<>();
	@Inject
	private Logger logger;
	@Inject
	@Getter
	private ProxyServer proxy;
	@Inject
	@DataDirectory
	@Getter
	private Path configurationFolder;
	@Getter
	private RegisteredServer fallbackServer;
	@Getter
	private Scheduler scheduler;
	@Getter
	private Messages messages;
	private Configuration configuration;

	private void setFallbackServer() {
		fallbackServer = proxy.getServer(configuration.getFallbackServerName()).orElseThrow();
	}

	@Subscribe
	public void onInitialize(ProxyInitializeEvent event) {
		messages = new Messages(this);
		configuration = new Configuration(this);

		setFallbackServer();

		final EventManager eventManager = proxy.getEventManager();

		eventManager.register(this, new DisconnectListener(this));
		eventManager.register(this, new ConnectListener(this));

		final StatusChecker statusChecker = new StatusChecker(this);

		scheduler = proxy.getScheduler();

		scheduler.buildTask(this, statusChecker::pingServers)
			.repeat(15, TimeUnit.SECONDS)
			.schedule();
	}

	@Subscribe
	public void onReload(ProxyReloadEvent event) {
		setFallbackServer();
	}

	public boolean removePlayerFromQueue(@NonNull Player player) {
		return WAITING.remove(player) != null;
	}

	public void queuePlayer(@NonNull Player player, @NonNull RegisteredServer server) {
		WAITING.put(player, server);
	}

	public Map<Player, RegisteredServer> getWaiting() {
		return Map.copyOf(WAITING);
	}
}
