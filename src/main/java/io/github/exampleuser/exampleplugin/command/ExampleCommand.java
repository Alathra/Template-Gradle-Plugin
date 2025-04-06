package io.github.exampleuser.exampleplugin.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.ColorParser;
import org.bukkit.command.CommandSender;

/**
 * Class containing the code for the example command.
 */
class ExampleCommand {
    private static final String BASE_PERM = "example.command";

    /**
     * Instantiates and registers a new command.
     */
    protected ExampleCommand() {
        new CommandAPICommand("example")
            .withFullDescription("Example command.")
            .withShortDescription("Example command.")
            .withPermission(BASE_PERM)
            .withSubcommands(
                new TranslationCommand().command()
            )
            .executes(this::executorExample)
            .register();
    }

    private void executorExample(CommandSender sender, CommandArguments args) {
        sender.sendMessage(
            ColorParser.of("<white>Read more about CommandAPI &9<click:open_url:'https://commandapi.jorel.dev/9.0.3/'>here</click><white>.")
                .parseLegacy() // Parse legacy color codes
                .build()
        );
    }
}
