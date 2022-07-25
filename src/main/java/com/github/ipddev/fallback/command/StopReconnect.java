package com.github.ipddev.fallback.command;

import com.github.ipddev.fallback.Main;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@AllArgsConstructor
public class StopReconnect implements SimpleCommand {
	private final Main plugin;

	@Override
	public void execute(Invocation invocation) {
		final CommandSource source = invocation.source();
		final Player player;

		if (!(source instanceof Player)) {
			return;
		}

		player = (Player) source;

		final boolean result = plugin.removePlayerFromQueue(player);

		if (!result) {
			player.sendMessage(Component.text("You are currently not auto-reconnecting to any servers.", NamedTextColor.RED));
			return;
		}

		player.sendMessage(Component.text("Successfully removed you from auto-reconnect queue.", NamedTextColor.GRAY));
	}
}
