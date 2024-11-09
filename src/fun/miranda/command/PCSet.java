package fun.miranda.command;

import fun.miranda.controller.PlayerCard;
import fun.miranda.utils.Strings;
import fun.miranda.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class PCSet implements TabExecutor {
    private PlayerCard card = null;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Strings.PCSetUsage);
            return true;
        }
        String cardName = args[0];
        String key = args[1];
        String value = args[2];
        if (!Utils.allCards().contains(cardName)) {
            sender.sendMessage(Strings.CardNotFound);
            return true;
        }
        this.card = new PlayerCard(cardName);
        if (value.startsWith("+") || value.startsWith("-")) {
            Integer adjusted = card.setWithSymbol(key, value);
            if (adjusted == null) {
                sender.sendMessage(String.format(Strings.KeyNotFound, key));
                return true;
            }
            if (value.startsWith("+")) {
                sender.sendMessage(String.format(Strings.PCSetAdd, cardName, key, value.substring(1), adjusted));
            } else {
                sender.sendMessage(String.format(Strings.PCSetSub, cardName, key, value.substring(1), adjusted));
            }
        } else {
            try {
                Integer adjust = Integer.parseInt(value);
                Integer adjusted = card.set(key, adjust);
                sender.sendMessage(String.format(Strings.PCSetAdjust, cardName, key, adjusted));
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return Utils.PCTabCompleter(args);
    }
}
