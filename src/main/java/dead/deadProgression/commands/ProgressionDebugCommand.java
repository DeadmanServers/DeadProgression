package dead.deadProgression.commands;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.ability.AbilityData;
import dead.deadProgression.ability.AbilityRegistry;
import dead.deadProgression.ability.AbilityType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/*
    Create ability
    Remove ability
    View list of abilities
    Edit type
    Edit value
    Edit description

    /upgradedebug createability <name> <type> <value>
    /upgradedebug removeability <name>/<UUID>
    /upgradedebug listabilities
    /upgradedebug setvalue <name>/<UUID> <value>
    /upgradedebug setdescription <name>/<UUID> <description>
    /upgradedebug removedescription <name>/<UUID> [<integer>]
    /upgradedebug cleardescription <name>/<UUID>
    /upgradedebug adddescription <name>/<UUID> <description>
 */

public class ProgressionDebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendRichMessage("<red>ERROR: <yellow>/upgradedebug [createability|removeability|listabilities|setvalue|setdescription|removedescription|cleardescription|adddescription]");
            return true;
        }

        AbilityRegistry abilityRegistry = DeadProgression.abilityRegistry;
        AbilityData abilityData = abilityRegistry.get(args[1]);
        if (!args[1].isBlank() && !args[1].equalsIgnoreCase("createability") && !args[1].equalsIgnoreCase("listabilities")) {
            if (abilityData == null) {
                player.sendRichMessage("<red>ERROR: <yellow>That ability does not exist.");
                return true;
            }
        }

        switch (args[0].toLowerCase()) {
            case "createability" -> {
                if (args.length < 4) {
                    player.sendRichMessage("<yellow>USAGE: /upgradedebug createability <name> <type> <value>");
                    return true;
                }
                if (abilityData != null) {
                    player.sendRichMessage("<red>ERROR: <yellow>That ability already exists.");
                    return true;
                }
                UUID newID = UUID.randomUUID();
                try {
                    AbilityType abilityType = AbilityType.valueOf(args[2].toUpperCase());
                    double value = Double.parseDouble(args[3]);
                    abilityRegistry.register(newID,new AbilityData(newID, args[1], abilityType, List.of(), value));
                    player.sendRichMessage("<green>SUCCESS: <white>You have created a new ability named <green>" + args[1]);
                    return true;
                } catch (IllegalArgumentException e) {
                    player.sendRichMessage("<yellow>USAGE: /upgradedebug createability <name> <type> <value>");
                    return true;
                }
            }
            case "removeability" -> {
                String oldName = abilityData.getName();
                abilityRegistry.unregister(abilityData);
                player.sendRichMessage("<green>You have removed the ability named <green>" + oldName);
                return true;
            }
            case "listabilities" -> {
                List<String> allNames = abilityRegistry.getAllNames();
                if (allNames.isEmpty()) {
                    player.sendRichMessage("<red>ERROR: <yellow>There are no loaded abilities.");
                    return true;
                }
                player.sendRichMessage("<yellow>List of Abilities:");
                player.sendRichMessage("");
                for (String name : allNames) {
                    player.sendRichMessage(" <gray>• <white>" + name);
                }
                player.sendRichMessage("");
                return true;
            }
            case "setdescription" -> {
                @NotNull String[] strings = Arrays.copyOfRange(args, 2, args.length);
                String joined = String.join(" ", strings);
                if (!abilityData.setDescription(List.of(joined))) {
                    player.sendRichMessage("<red>ERROR: <yellow>Something went wrong setting the description.");
                    return true;
                }
                player.sendRichMessage("<green>SUCCESS: <white>You have set a new description for <green>" + abilityData.getName());
                return true;
            }
            case "setvalue" -> {
                try {
                    double value = Double.parseDouble(args[2]);
                    abilityData.setValue(value);
                    player.sendRichMessage("<green>SUCCESS: <white>You have set the value for <green>" + abilityData.getName());
                    return true;
                } catch (NumberFormatException e) {
                    player.sendRichMessage("<red>ERROR: <yellow>That is not a valid number.");
                    return true;
                }

            }
            case "removedescription" -> {
                try {
                    int line = Integer.parseInt(args[2]);
                    abilityData.removeDescription(line - 1);
                    player.sendRichMessage("<green>SUCCESS: <white>You have removed the description for <green>" + abilityData.getName());
                    return true;
                } catch (NumberFormatException e) {
                    player.sendRichMessage("<green>ERROR: <white>That is not a valid number.");
                    return true;
                }
            }
            case "cleardescription" -> {
                abilityData.clearDescription();
                player.sendRichMessage("<green>SUCCESS: <white>You have cleared the description for <green>" + abilityData.getName());
            }
            case "adddescription" -> {
                @NotNull String[] strings = Arrays.copyOfRange(args, 2, args.length);
                String joined = String.join(" ", strings);
                abilityData.addDescription(joined);
            }
        }

        return false;
    }
}
