package com.github.ExampleUser.ExamplePlugin.command;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

public class ExampleCommand {
    public ExampleCommand() {
        new CommandAPICommand("example")
            .withFullDescription("Example command.")
            .withShortDescription("Example command.")
            .withPermission("example.command")
            .executes(this::example)
            .register();
    }

    private void example(CommandSender sender, CommandArguments args) {
        sender.sendMessage(
            new ColorParser("<white>Read more about CommandAPI &9<click:open_url:https://commandapi.jorel.dev/9.0.3/>here</click><white>.")
                .parseLegacy() // Parse legacy color codes
                .build()
        );
    }
}
