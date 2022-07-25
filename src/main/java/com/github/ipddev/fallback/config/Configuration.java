package com.github.ipddev.fallback.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.ipddev.fallback.Main;
import com.google.common.io.Resources;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Configuration {

	private static final MiniMessage miniMessage = MiniMessage.miniMessage();
	private static final ClassLoader classLoader = Configuration.class.getClassLoader();
	private static final URL defaultConfigUrl = classLoader.getResource("config.toml");

	@Getter
	private final String fallbackServerName;

	@SuppressWarnings("UnstableApiUsage")
	@SneakyThrows
	public Configuration(Main plugin) {
		assert defaultConfigUrl != null;

		final Path configurationFolder = plugin.getConfigurationFolder();

		if (!Files.exists(configurationFolder)) {
			Files.createDirectory(configurationFolder);
		}

		final Path configurationPath = configurationFolder.resolve("config.toml");

		if (!Files.exists(configurationPath)) {
			Files.writeString(configurationPath, Resources.toString(defaultConfigUrl, StandardCharsets.UTF_8));
		}

		FileConfig config = FileConfig.of(configurationPath);

		config.load();

		fallbackServerName = config.get("fallbackServerName");
	}
}
