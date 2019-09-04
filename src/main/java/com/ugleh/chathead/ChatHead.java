package com.ugleh.chathead;

import com.ugleh.chathead.command.CommandHead;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatHead extends JavaPlugin {
    private boolean enableMetrics;
    private long usageCooldown;
    private int imageScale;
    private String printCharacter = "⬛";
    private Map<String, String> languageNodes = new HashMap<>();
    private static ChatHead instance;

    @Override
    public void onEnable() {
        setInstance(this);
        CommandExecutor commandHead = new CommandHead();
        Objects.requireNonNull(this.getCommand("chathead"), "Command chathead not found.").setExecutor(commandHead);
        Objects.requireNonNull(this.getCommand("chathelm"), "Command chathelm not found.").setExecutor(commandHead);
        setupConfig();
        if(enableMetrics)
            new Metrics(this);

    }

    private void setupConfig() {
        this.saveDefaultConfig();
        usageCooldown = this.getConfig().getLong("config.usage-cooldown");
        imageScale = this.getConfig().getInt("config.image-scale");
        printCharacter = this.getConfig().getString("config.print-character");
        enableMetrics = this.getConfig().getBoolean("config.enable-metrics");
        for (String language : Objects.requireNonNull(this.getConfig().getConfigurationSection("language"), "language node not found.").getKeys(false)) {
            languageNodes.put("language." + language, this.getConfig().getString("language." + language));
        }
    }

    public static ChatHead getInstance() {
        return instance;
    }

    private static void setInstance(ChatHead instance1) {
        instance = instance1;
    }

    public String getLanguageNode(String s) {
        return ChatColor.translateAlternateColorCodes('&', languageNodes.getOrDefault(s, s));
    }

    public long getUsageCooldown() {
        return usageCooldown;
    }

    public int getImageScale() {
        return imageScale;
    }

    public String getPrintCharacter() {
        return printCharacter;
    }
}
