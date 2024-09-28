package me.dovide.reporty.minecraft;

import me.dovide.reporty.Reporty;
import me.dovide.reporty.utils.Config;
import me.dovide.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReportAdmin implements TabExecutor {

    private final Config banned;
    private final Config config;
    private final Reporty instance;

    public ReportAdmin(Reporty instance){
        this.banned = instance.getBannedFromReport();
        this.config = instance.getConfig();
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("You cannot use this command here");
            return true;
        }

        Player player = (Player) commandSender;

        if(!player.hasPermission(config.getString("admin.use"))){
            player.sendMessage(Util.cc("&c&lReporty &7You don't have enough permissions to use this command"));
            return true;
        }

        if(args.length != 2){
            player.sendMessage(Util.cc("&c&lReporty &7Usage: /reporty &b<reload/ban> <configName/playerName>"));
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")){

            if(!player.hasPermission(config.getString("admin.reload"))){
                player.sendMessage(Util.cc("&c&lReporty &7You don't have enough permissions to use this command"));
                return true;
            }

            String config = args[1];

            switch (config) {
                case "bannedfromreport":
                    instance.createConfig("bannedfromreport.yml");
                    player.sendMessage(Util.cc("&a&lReporty &7bannedfromreport.yml &bReloaded"));
                    break;
                case "config":
                    instance.createConfig("config.yml");
                    player.sendMessage(Util.cc("&a&lReporty &7config.yml &bReloaded"));
                    break;
                default:
                    player.sendMessage(Util.cc("&c&lReporty &7I couldn't find a valid config with that name"));
                    break;
            }

        }else if (args[0].equalsIgnoreCase("ban")){

            if(!player.hasPermission(instance.getConfig().getString("admin.ban"))){
                player.sendMessage(Util.cc("&c&lReporty &7You don't have enough permissions to use this command"));
                return true;
            }

            Player toBan = Bukkit.getPlayerExact(args[1]);

            if (toBan == null || !toBan.isOnline()){
                player.sendMessage(Util.cc("&c&lReporty &7Player not found (or offline)"));
                return true;
            }

            if (toBan == player){
                player.sendMessage(Util.cc("&c&lReporty &7You cannot ban yourself from reporting"));
                return true;
            }

            String uuidToBan = toBan.getUniqueId().toString();

            if(banned.getStringList("uuids").contains(uuidToBan)){
                player.sendMessage(Util.cc("&c&lReporty &7This player is already banned from reporting. Use &b/reporty unban " + toBan.getName()));
                return true;
            }

            if (banned.get("uuids") == null)
                banned.set("uuids", Arrays.asList(uuidToBan.toString()));
            else {
                List<String> newList = banned.getStringList("uuids");
                newList.add(uuidToBan);
                banned.set("uuids", newList);
            }
            instance.saveBanned();
            player.sendMessage(Util.cc("&a&lReporty &7You have banned &b" + toBan.getName() + "&7 from reporting"));
        }else if (args[0].equalsIgnoreCase("unban")){

            if(!player.hasPermission(instance.getConfig().getString("admin.unban"))){
                player.sendMessage(Util.cc("&c&lReporty &7You don't have enough permissions to use this command"));
                return true;
            }

            Player toUnBan = Bukkit.getPlayerExact(args[1]);

            if (toUnBan == null || !toUnBan.isOnline()){
                player.sendMessage(Util.cc("&c&lReporty &7Player not found (or offline)"));
                return true;
            }

            String uuidToUnban = toUnBan.getUniqueId().toString();

            if(!banned.getStringList("uuids").contains(uuidToUnban)){
                player.sendMessage(Util.cc("&c&lReporty &7This player is not banned from reporting. Use &b/reporty ban " + toUnBan.getName()));
                return true;
            }

            List<String> newList = banned.getStringList("uuids");

            newList.remove(uuidToUnban);

            banned.set("uuids", newList);
            instance.saveBanned();

            player.sendMessage(Util.cc("&a&lReporty &7You have unbanned &b" + toUnBan.getName() + "&7 from reporting"));

        }

        return true;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            if (commandSender.hasPermission(instance.getConfig().getString("admin.use"))) {
                return Arrays.asList("reload", "ban", "unban");
            }
        } else if (args.length == 2){
            if (!commandSender.hasPermission(instance.getConfig().getString("admin.use"))) {
                return Collections.emptyList();
            }
            if(args[0].equals("reload")) {
                return Arrays.asList("bannedfromreport", "config");
            }else{
                return null;
            }
        }

        return Collections.emptyList();
    }
}
