package com.github.ipddev.fallback.messages;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.ipddev.fallback.Main;
import com.google.common.io.Resources;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class Messages {

	private static final MiniMessage miniMessage = MiniMessage.miniMessage();
	private static final ClassLoader classLoader = Messages.class.getClassLoader();
	private static final URL defaultMessagesUrl = classLoader.getResource("messages.toml");

	private final String fallbackMessage;
	private final String serverAvailableMessage;
	private final String reconnectErrorMessage;

	@SuppressWarnings("UnstableApiUsage")
	@SneakyThrows
	public Messages(Main plugin) {
		assert defaultMessagesUrl != null;

		final Path configurationFolder = plugin.getConfigurationFolder();

		if (!Files.exists(configurationFolder)) {
			Files.createDirectory(configurationFolder);
		}

		final Path configurationPath = configurationFolder.resolve("messages.toml");

		if (!Files.exists(configurationPath)) {
			Files.writeString(configurationPath, Resources.toString(defaultMessagesUrl, StandardCharsets.UTF_8));
		}

		FileConfig config = FileConfig.of(configurationPath);

		config.load();

		fallbackMessage = config.get("fallbackMessage");
		serverAvailableMessage = config.get("serverAvailableMessage");
		reconnectErrorMessage = config.get("reconnectErrorMessage");
	}

	public Component getFallbackMessage(@NonNull String previousServerName) {
		return miniMessage.deserialize(fallbackMessage, Placeholder.parsed("server", previousServerName));
	}

	public Component getServerAvailableMessage() {
		return miniMessage.deserialize(serverAvailableMessage);
	}

	public Component getReconnectErrorMessage(@NonNull Throwable ex) {
		return miniMessage.deserialize(reconnectErrorMessage,
			Placeholder.parsed("error", ex.getMessage()));
	}
}
