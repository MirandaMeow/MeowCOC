package fun.miranda.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static fun.miranda.MeowCOC.plugin;

public class Log {
    private final File logFile;
    public static Log log;

    private Log() {
        File logsDir = new File(plugin.getDataFolder(), "logs");
        this.logFile = new File(logsDir, String.format("%s.txt", this.getDateTime()));
    }

    public void log(String message) {
        message = message.replaceAll("§.", "");
        message = message.replaceAll("\n", " ");
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(String.format("<%s> %s", this.getTime(), message)); // 追加到文件
        } catch (IOException ignored) {
        }
    }

    public static Log getInstance() {
        if (log == null) {
            log = new Log();
        }
        return log;
    }

    private String getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        return now.format(formatter);
    }

    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }

    public static void stopLog() {
        log = null;
    }
}
