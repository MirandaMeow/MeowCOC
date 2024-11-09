package fun.miranda;

import fun.miranda.command.COC;
import fun.miranda.command.PCGet;
import fun.miranda.command.PCSet;
import fun.miranda.command.PCShow;
import fun.miranda.controller.Log;
import fun.miranda.event.PCChatEvent;
import fun.miranda.event.RollDiceEvent;
import fun.miranda.utils.Strings;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class MeowCOC extends JavaPlugin {
    public static MeowCOC plugin;
    public FileConfiguration config;
    public Logger logger;
    public Log log;

    /**
     * 插件载入
     */
    @Override
    public void onLoad() {
        logger = this.getServer().getLogger();
        this.saveDefaultConfig();
        this.config = this.getConfig();

        createCardFolder();
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger.info(Strings.Enable);
        this.log = null;
        registerCommand("coc", new COC());
        registerCommand("pcget", new PCGet());
        registerCommand("pcset", new PCSet());
        registerCommand("pcshow", new PCShow());
        registerEvent(new RollDiceEvent());
        registerEvent(new PCChatEvent());
    }

    @Override
    public void onDisable() {
        logger.info(Strings.Disable);
        this.saveConfig();
    }

    /**
     * 注册命令
     *
     * @param command  命令字符串
     * @param executor 命令
     */
    private void registerCommand(String command, TabExecutor executor) {
        PluginCommand cmd = this.getCommand(command);
        assert cmd != null;
        cmd.setExecutor(executor);
    }

    /**
     * 注册监听器
     *
     * @param listener 监听器
     */
    private void registerEvent(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    private void createCardFolder() {
        File cardsFolder = new File(this.getDataFolder(), "cards");
        File logsFolder = new File(this.getDataFolder(), "logs");
        if (!logsFolder.exists()) {
            logsFolder.mkdir();
        }
        if (!cardsFolder.exists()) {
            cardsFolder.mkdir();
        }
    }
}
