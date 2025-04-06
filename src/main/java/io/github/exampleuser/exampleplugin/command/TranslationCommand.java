package io.github.exampleuser.exampleplugin.command;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.exampleuser.exampleplugin.utility.Cfg;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.wordweaver.Translation;
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
                new StringArgument("key").replaceSuggestions(ArgumentSuggestions.stringCollection(unused -> Translation.getKeys()))
            )
            .executes(this::executorTest);
    }

    private void executorHelp(CommandSender sender, CommandArguments args) {
        sender.sendMessage(Translation.as("commands.translation.help"));
    }

    private void executorReload(CommandSender sender, CommandArguments args) {
        Translation.setLanguage(Cfg.get().get("language", "en_US"));
        Translation.reload();
        sender.sendMessage(Translation.as("commands.translation.reloaded"));
    }

    private void executorTest(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        if (!(args.getOrDefault("key", "") instanceof final String key))
            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>A translation key must be a string!").build());

        if (key.isBlank())
            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>A translation key cannot be empty!").build());

        if (key.startsWith(".") || key.endsWith("."))
            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>A translation key cannot begin/end with a period!").build());

        final String translation = Translation.of(key);

        if (translation == null)
            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>That translation entry doesn't exist!").build());

        if (translation.isBlank())
            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>That translation entry is an empty string!").build());

        sender.sendMessage(Translation.as(key));
    }
}
