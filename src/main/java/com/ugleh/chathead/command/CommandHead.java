package com.ugleh.chathead.command;

import com.ugleh.chathead.ChatHead;
import com.ugleh.chathead.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandHead implements CommandExecutor {
    private Map<UUID, Long> playerUsage = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, final String s, String[] strings) {
        if(!commandSender.hasPermission("chathead."+s.toLowerCase()+".use")) return noPermission(commandSender);

        if(commandSender instanceof Player) {
            //Handle PlayerUsage cooldown
            Player p = ((Player) commandSender);
            long unixTime = System.currentTimeMillis() / 1000L;
            if(playerUsage.containsKey(p.getUniqueId())) {
                long lastTime = playerUsage.get(p.getUniqueId());
                long timeLeft = Math.abs(ChatHead.getInstance().getUsageCooldown() - (unixTime - lastTime));
                if(lastTime <= (unixTime - ChatHead.getInstance().getUsageCooldown())) {
                    playerUsage.put(p.getUniqueId(), unixTime);
                }else {

                    p.sendMessage(String.format(ChatHead.getInstance().getLanguageNode("language.cooldown"), timeLeft));
                    return true;
                }
            }else {
                playerUsage.put(p.getUniqueId(), unixTime);
            }
        }

        Bukkit.getScheduler().runTask(ChatHead.getInstance(), () -> {

            String user;
            if(strings.length == 1) {
                user = strings[0];
            }else {
                if(!(commandSender instanceof Player)) {
                    notPlayer(commandSender);
                    return;
                }
                user = ((Player) commandSender).getUniqueId().toString();
            }


            String urlString = "https://minotar.net/avatar/"+user+"/" + ChatHead.getInstance().getImageScale() + ".png";
            if(s.equalsIgnoreCase("helm")) {
                urlString = "https://minotar.net/helm/"+user+"/"+ ChatHead.getInstance().getImageScale() +".png";
            }

            try {
                URL url = new URL(urlString);
                BufferedImage image = ImageIO.read(url);
                for (int i = 0; i < image.getHeight(); i++) {
                    StringBuilder chatHeadString = new StringBuilder();
                    for (int j = 0; j < image.getWidth(); j++) {
                        Color color = new Color(image.getRGB(j, i));
                        ChatColor chatColor = ColorUtil.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
                        chatHeadString.append(chatColor).append(ChatHead.getInstance().getPrintCharacter());
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(chatHeadString.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    private void notPlayer(CommandSender commandSender) {
        commandSender.sendMessage("You must be a player.");
    }

    private boolean noPermission(CommandSender commandSender) {
        commandSender.sendMessage("You do not have permission.");
        return true;
    }
}
