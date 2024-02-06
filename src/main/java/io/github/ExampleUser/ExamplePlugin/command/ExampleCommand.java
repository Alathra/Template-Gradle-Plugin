package io.github.ExampleUser.ExamplePlugin.command;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

/**
 * Class containing the code for the example command.
 */
public class ExampleCommand {
    /**
     * Instantiates and registers a new command.
     */
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
            ColorParser.of("<white>Read more about CommandAPI &9<click:open_url:https://commandapi.jorel.dev/9.0.3/>here</click><white>.")
                .parseLegacy() // Parse legacy color codes
                .build()
        );
    }
}
