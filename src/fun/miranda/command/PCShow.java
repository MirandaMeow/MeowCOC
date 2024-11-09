package fun.miranda.command;

import fun.miranda.controller.PlayerCard;
import fun.miranda.utils.Strings;
import fun.miranda.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class PCShow implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Strings.PCShowUsage);
            return true;
        }
        String cardName = args[0];
        if (!Utils.allCards().contains(cardName)) {
            sender.sendMessage(Strings.CardNotFound);
            return true;
        }
        PlayerCard card = new PlayerCard(cardName);
        sender.sendMessage(card.showCard());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Utils.listFilter(Utils.allCards(), args[0]);
        }
        return List.of();
    }
}
