package io.github.exampleuser.exampleplugin.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.command.CommandSender;

import static io.github.exampleuser.exampleplugin.command.CommandHandler.BASE_PERM;

/**
 * Class containing the code for the example command.
 */
class ExampleCommand {
    /**
     * Instantiates and registers a new command.
     */
    protected ExampleCommand() {
        new CommandAPICommand("example")
            .withHelp("Example command.", "Example command.")
            .withPermission(BASE_PERM)
            .withSubcommands(
                new TranslationCommand().command(),
                new DumpCommand().command()
            )
            .executes(this::executorExample)
            .register();
    }

    private void executorExample(CommandSender sender, CommandArguments args) {
        sender.sendMessage(
            ColorParser.of("<white>Read more about CommandAPI &9<click:open_url:'https://commandapi.jorel.dev/9.0.3/'>here</click><white>.")
                .legacy() // Parse legacy color codes
                .build()
        );
    }
}
