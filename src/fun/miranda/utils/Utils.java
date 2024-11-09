package fun.miranda.utils;

import fun.miranda.controller.PlayerCard;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static fun.miranda.MeowCOC.plugin;

public class Utils {
    public static List<String> allCards() {
        File cardsFolder = new File(plugin.getDataFolder(), "cards");
        File[] files = cardsFolder.listFiles();
        List<String> out = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    String fileNameWithoutExtension = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
                    out.add(fileNameWithoutExtension);
                }
            }
        }
        return out;
    }

    public static List<String> allPCPlayer() {
        ConfigurationSection section = plugin.config.getConfigurationSection("COC.Players");
        if (section == null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(section.getKeys(false));
        }
    }

    public static boolean isKP(Player player) {
        String KP = plugin.config.getString("COC.KP");
        if (KP == null) {
            return false;
        }
        return KP.equals(player.getName());
    }

    public static List<String> listFilter(List<String> list, String select) {
        List<String> selected = new ArrayList<>();
        for (String s : list) {
            if (s.startsWith(select)) {
                selected.add(s);
            }
        }
        if (!selected.isEmpty()) {
            return selected;
        }
        return list;
    }

    public static List<String> getAllOnlinePlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }

    public static List<String> PCTabCompleter(String[] args) {
        if (args.length == 1) {
            return Utils.listFilter(Utils.allCards(), args[0]);
        }
        if (args.length == 2) {
            if (Utils.allCards().contains(args[0])) {
                PlayerCard card = new PlayerCard(args[0]);
                List<String> merged = new ArrayList<>(card.getSkills());
                merged.addAll(Lists.attrs);
                merged.addAll(Lists.status);
                return Utils.listFilter(merged, args[1]);
            }
        }
        return List.of();
    }
}
