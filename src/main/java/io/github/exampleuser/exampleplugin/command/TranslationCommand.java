package io.github.exampleuser.exampleplugin.command;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.translation.Translation;
import org.bukkit.command.CommandSender;

/**
 * Class containing the code for the translation commands.
 */
class TranslationCommand {
    private static final String BASE_PERM = "example.command.translation";

    /**
     * Instantiates a new command tree.
     */
    protected CommandAPICommand command() {
        return new CommandAPICommand("translation")
            .withFullDescription("Example command.")
            .withShortDescription("Example command.")
            .withPermission(BASE_PERM)
            .withSubcommands(
                commandReload(),
                commandTest(),
                new CommandAPICommand("help")
                    .executes(this::executorHelp)
            )
            .executes(this::executorHelp);
    }

    private CommandAPICommand commandReload() {
        return new CommandAPICommand("reload")
            .withFullDescription("Reloads the translation files.")
            .withShortDescription("Reload the translation files.")
            .withPermission(BASE_PERM + ".reload")
            .executes(this::executorReload);
    }

    private CommandAPICommand commandTest() {
        return new CommandAPICommand("test")
            .withFullDescription("Test a translation.")
            .withShortDescription("Test a translation.")
            .withPermission(BASE_PERM + ".test")
            .withArguments(
                new StringArgument("key").replaceSuggestions(ArgumentSuggestions.strings(Translation.getAllKeys()))
            )
            .executes(this::executorTest);
    }

    private void executorHelp(CommandSender sender, CommandArguments args) {
        sender.sendMessage(
            ColorParser.of(Translation.of("commands.translation.help"))
                .parseLegacy()
                .build()
        );
    }

    private void executorReload(CommandSender sender, CommandArguments args) {
        ExamplePlugin.getInstance().getTranslationManager().onReload();
        sender.sendMessage(
            ColorParser.of(Translation.of("commands.translation.reloaded"))
                .parseLegacy()
                .build()
        );
    }

    private void executorTest(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        if (!(args.getOrDefault("key", "") instanceof String key))
            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>You need to specify a valid translation string!").build());

        if (key.isEmpty() || key.startsWith(".") || Translation.of(key) == null || Translation.of(key).isEmpty())
            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>This translation entry doesn't exist or is an empty string!").build());

        sender.sendMessage(
            ColorParser.of(Translation.of(key))
                .parseLegacy()
                .build()
        );
    }
}
