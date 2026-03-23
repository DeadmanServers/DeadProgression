package dead.deadProgression.commands;

import dead.deadProgression.DeadProgression;
import dead.deadProgression.ability.AbilityData;
import dead.deadProgression.ability.AbilityRegistry;
import dead.deadProgression.ability.AbilityType;
import dead.deadProgression.itemdata.ItemUpgradeData;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

public class ProgressionDebugCommand implements CommandExecutor, TabCompleter {
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
        AbilityData abilityData = null;
        if (args.length != 1) {
            if (!args[1].isBlank() && !args[0].equalsIgnoreCase("createability") && !args[0].equalsIgnoreCase("listabilities")) {
                abilityData = abilityRegistry.get(args[1]);
                if (abilityData == null) {
                    player.sendRichMessage("<red>ERROR: <yellow>That ability does not exist.");
                    return true;
                }
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
                    abilityRegistry.save();
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
                abilityRegistry.save();
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
            case "settype" -> {
                try {
                    AbilityType abilityType = AbilityType.valueOf(args[2].toUpperCase());
                    abilityData.setType(abilityType);
                    player.sendRichMessage("<green>SUCCESS: <white>You have set the type for <green>" + abilityData.getName());
                    return true;
                } catch (IllegalArgumentException e) {
                    player.sendRichMessage("<red>ERROR: <yellow>That is not a valid ability type.");
                    return true;
                }
            }
            case "removedescription" -> {
                if (args.length < 3) {
                    player.sendRichMessage("<red>ERROR: <yellow>You need to specify the line number to remove.");
                    return true;
                }
                try {
                    int line = Integer.parseInt(args[2]);
                    if (line < 0) {
                        player.sendRichMessage("<red>ERROR: <yellow>The line number must be a positive integer or 0.");
                        return true;
                    }
                    abilityData.removeDescription(line - 1);
                    player.sendRichMessage("<green>SUCCESS: <white>You have removed a line in the description for <green>" + abilityData.getName());
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
            case "testwritepdc" -> {
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                ItemStack clone = itemInMainHand.clone();
                if (clone.getType() == Material.AIR || clone.isEmpty()) {
                    player.sendRichMessage("<red>ERROR: <yellow>You need to hold a valid item.");
                    return true;
                }
                ItemUpgradeData itemUpgradeData = ItemUpgradeData.fromItem(clone);
                if (itemUpgradeData == null) {
                    itemUpgradeData = new ItemUpgradeData(new HashMap<>());
                }
                UUID randomProgressionUUID = UUID.randomUUID();
                UUID randomUpgradeUUID = UUID.randomUUID();
                int tier = 2;

                itemUpgradeData.addOrCreate(randomProgressionUUID, randomUpgradeUUID, tier);
                ItemUpgradeData.applyToItem(itemInMainHand, itemUpgradeData);
                player.sendRichMessage("<green>You have successfully created an upgrade item.");
            }
            case "testreadpdc" -> {
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                ItemStack clone = itemInMainHand.clone();
                if (clone.getType() == Material.AIR || clone.isEmpty()) {
                    player.sendRichMessage("<red>ERROR: <yellow>You need to hold a valid item.");
                    return true;
                }
                ItemUpgradeData itemUpgradeData = ItemUpgradeData.fromItem(clone);
                if (itemUpgradeData == null) {
                    player.sendRichMessage("<red>ERROR: <yellow>Failed to load upgrade data.");
                    return true;
                }
                String serialize = itemUpgradeData.serialize();
                player.sendRichMessage(serialize);
            }
        }

        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();

        AbilityRegistry abilityRegistry = DeadProgression.abilityRegistry;

        if (args.length == 1) {
            inputs.addAll(List.of("createability", "removeability", "listabilities",  "setdescription", "setvalue", "settype", "removedescription", "cleardescription", "adddescription", "testwritepdc", "testreadpdc"));
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "removeability", "setdescription", "setvalue", "settype", "removedescription", "cleardescription",
                     "adddescription" -> inputs.addAll(abilityRegistry.getAllNames());
            }
        }
        if (args.length == 3) {

            switch (args[0].toLowerCase()) {
                case "setvalue" -> {
                    AbilityData abilityData = abilityRegistry.get(args[1]);
                    if (abilityData == null) {
                        break;
                    }
                    inputs.add("" + abilityData.getValue());
                }
                case "settype", "createability" -> {
                    AbilityType[] values = AbilityType.values();
                    List<String> types = new ArrayList<>();
                    for (AbilityType value : values) {
                        types.add(value.name());
                    }
                    inputs.addAll(types);
                }
            }
        }

        for (String input : inputs) {
            if (input.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                outputs.add(input);
            }
        }

        return outputs;


    }

}
