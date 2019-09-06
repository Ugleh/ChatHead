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
            Player sentPlayer = ((Player) commandSender);
            long unixTime = System.currentTimeMillis() / 1000L;
            if(playerUsage.containsKey(sentPlayer.getUniqueId())) {
                long lastTime = playerUsage.get(sentPlayer.getUniqueId());
                long timeLeft = Math.abs(ChatHead.getInstance().getUsageCooldown() - (unixTime - lastTime));
                if(lastTime <= (unixTime - ChatHead.getInstance().getUsageCooldown())) {
                    playerUsage.put(sentPlayer.getUniqueId(), unixTime);
                }else {

                    String message = ChatHead.getInstance().getMessageNode("language.cooldown").replace("{seconds}", String.valueOf(timeLeft));
                    message = ((int)timeLeft > 1) ? message.replace("!s", "s") : message.replace("!s", "");
                    sentPlayer.sendMessage(message);
                    return true;
                }
            }else {
                playerUsage.put(sentPlayer.getUniqueId(), unixTime);
            }
        }

        Bukkit.getScheduler().runTask(ChatHead.getInstance(), () -> {
            String preNode;
            String postNode;
            String user;
            if(strings.length == 1) {
                user = strings[0];
                preNode = "language.pre-head-other";
                postNode = "language.post-head-other";
            }else {
                if(!(commandSender instanceof Player)) {
                    notPlayer(commandSender);
                    return;
                }
                user = ((Player) commandSender).getUniqueId().toString();
                preNode = "language.pre-head-self";
                postNode = "language.post-head-self";
            }


            String urlString = "https://minotar.net/avatar/"+user+"/" + ChatHead.getInstance().getImageScale() + ".png";
            if(s.equalsIgnoreCase("helm")) {
                urlString = "https://minotar.net/helm/"+user+"/"+ ChatHead.getInstance().getImageScale() +".png";
            }

            try {
                if(!ChatHead.getInstance().getLanguageNode(preNode).equals("")) {
                    String message = ChatHead.getInstance().getMessageNode(preNode);
                    message = message.replace("{sent_player}", getCommandSenderName(commandSender)).replace("{head_player}", user);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(message);
                    }
                }
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
                        String message = ChatHead.getInstance().getMessageNode("language.head").replace("{head_column}", chatHeadString.toString());
                        player.sendMessage(message);
                    }
                }

                if(!ChatHead.getInstance().getLanguageNode(postNode).equals("")) {
                    String message = ChatHead.getInstance().getMessageNode(postNode);
                    message = message.replace("{sent_player}", getCommandSenderName(commandSender)).replace("{head_player}", user);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(message);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    private CharSequence getCommandSenderName(CommandSender commandSender) {
        if(commandSender instanceof Player)
            return ((Player) commandSender).getDisplayName();
        return commandSender.getName();
    }

    private void notPlayer(CommandSender commandSender) {
        commandSender.sendMessage(ChatHead.getInstance().getMessageNode("language.no-permission"));
    }

    private boolean noPermission(CommandSender commandSender) {
        commandSender.sendMessage(ChatHead.getInstance().getMessageNode("language.not-player"));
        commandSender.sendMessage(ChatHead.getInstance().getMessageNode("language.usage"));
        return true;
    }
}
