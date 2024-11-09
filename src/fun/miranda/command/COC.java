package fun.miranda.command;

import fun.miranda.controller.Log;
import fun.miranda.utils.Strings;
import fun.miranda.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static fun.miranda.MeowCOC.plugin;

public class COC implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Strings.NotOP);
            return true;
        }
        if (args.length == 0) {
            showUsage(sender);
            return true;
        }
        switch (args[0]) {
            case "setkp":
                if (args.length != 2) {
                    showUsage(sender);
                    return true;
                }
                String KPName = args[1];
                Player KPPlayer = Bukkit.getPlayer(KPName);
                if (KPPlayer == null) {
                    sender.sendMessage(Strings.PlayerNotFound);
                    return true;
                }
                plugin.config.set("COC.KP", KPPlayer.getName());
                plugin.saveConfig();
                sender.sendMessage(String.format(Strings.SetKP, KPPlayer.getName()));
                break;
            case "setplayer":
                if (args.length != 3) {
                    showUsage(sender);
                    return true;
                }
                String PLName = args[1];
                String PCName = args[2];
                Player player = Bukkit.getPlayer(PLName);
                if (player == null) {
                    sender.sendMessage(Strings.PlayerNotFound);
                    return true;
                }
                if (!Utils.allCards().contains(PCName)) {
                    sender.sendMessage(Strings.CardNotFound);
                    return true;
                }
                plugin.config.set(String.format("COC.Players.%s", PLName), PCName);
                plugin.saveConfig();
                sender.sendMessage(String.format(Strings.SetPlayer, PCName, player.getName()));
                break;
            case "reset":
                if (args.length != 1) {
                    showUsage(sender);
                    return true;
                }
                plugin.config.set("COC.Players", new ArrayList<>());
                plugin.config.set("COC.KP", "");
                plugin.saveConfig();
                sender.sendMessage(Strings.Reset);
                break;
            case "game":
                if (args.length == 2) {
                    if (Objects.equals(args[1], "start")) {
                        plugin.getServer().broadcastMessage(Strings.Start);
                        plugin.log = Log.getInstance();
                        plugin.log.log(Strings.Start);
                    } else if (Objects.equals(args[1], "stop")) {
                        if (plugin.log == null) {
                            sender.sendMessage(Strings.NotStart);
                            return true;
                        }
                        plugin.getServer().broadcastMessage(Strings.Stop);
                        plugin.log.log(Strings.Stop);
                        Log.stopLog();
                        plugin.log = null;
                    } else {
                        showUsage(sender);
                    }
                } else {
                    showUsage(sender);
                }
                break;
            default:
                showUsage(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Utils.listFilter(new ArrayList<>(Arrays.asList("setkp", "setplayer", "reset", "game")), args[0]);
        }
        if (args.length == 2) {
            if (Objects.equals(args[0], "game")) {
                return List.of("start", "stop");
            }
            if (Objects.equals(args[0], "setkp")) {
                return Utils.listFilter(Utils.getAllOnlinePlayers(), args[1]);
            }
            if (Objects.equals(args[0], "setplayer")) {
                return Utils.listFilter(Utils.getAllOnlinePlayers(), args[1]);
            }
        }
        if (args.length == 3) {
            if (Objects.equals(args[0], "setplayer")) {
                return Utils.listFilter(Utils.allCards(), args[2]);
            }
        }
        return List.of();
    }

    private void showUsage(CommandSender sender) {
        sender.sendMessage(Strings.COCUsage);
    }
}
