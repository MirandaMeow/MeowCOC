package fun.miranda.command;

import fun.miranda.controller.PlayerCard;
import fun.miranda.utils.Strings;
import fun.miranda.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class PCGet implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Strings.PCGetUsage);
            return true;
        }
        String cardName = args[0];
        String key = args[1];
        if (!Utils.allCards().contains(cardName)) {
            sender.sendMessage(Strings.CardNotFound);
            return true;
        }
        PlayerCard card = new PlayerCard(cardName);
        Integer result = card.getResult(key);
        if (result == null) {
            sender.sendMessage(String.format(Strings.KeyNotFound, key));
            return true;
        }
        sender.sendMessage(String.format(Strings.CharResult, cardName, key, result));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return Utils.PCTabCompleter(args);
    }
}
