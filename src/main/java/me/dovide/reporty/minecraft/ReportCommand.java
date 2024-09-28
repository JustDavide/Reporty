package me.dovide.reporty.minecraft;

import me.dovide.reporty.Reporty;
import me.dovide.reporty.utils.Config;
import me.dovide.utils.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

public class ReportCommand implements CommandExecutor {

    private final JDA bot;
    private final Config banned;
    private final Config config;

    public ReportCommand(Reporty instance) {
        this.bot = instance.getJDA();
        this.banned = instance.getBannedFromReport();
        this.config = instance.getConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You cannot use this command here");
            return true;
        }

        Player player = (Player) commandSender;

        if (banned.getStringList("uuids").contains(player.getUniqueId().toString())) {
            player.sendMessage(Util.cc("&c&lReporty &7You have been banned from reporting"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Util.cc("&c&lReporty &7To report use /report <Name> <Reason>"));
            return true;
        }

        Player reported = Bukkit.getPlayerExact(args[0]);

        if (reported == null || !reported.isOnline()) {
            player.sendMessage(Util.cc("&c&lReporty &7Couldn't find this player"));
            return true;
        }

        if (reported == player) {
            player.sendMessage(Util.cc("&c&lReporty &7You cannot report yourself"));
            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Bukkit.broadcast(Util.format("&8--- [&9&lReporty&8] ---\n" +
                "&7Reporter: &f%s\n" +
                "&7Reported: &f%s\n" +
                "&7Reason: &f%s\n" +
                "&8----------------", player.getName(), reported.getName(), reason), config.getString("admin.staff"));

        player.sendMessage(Util.cc("&a&lReporty &7Your report was successfully sent"));

        MessageEmbed reportEmbed = new EmbedBuilder()
                .setAuthor("Player Report")
                .addField("Player Reported", String.format("Reported By: %s\nPlayer Reported: %s\nReason: %s", player.getName(), reported.getName(), reason), false)
                .setFooter("reporty", "https://cdn.discordapp.com/attachments/1285644729286918259/1285657264153301067/Reporty_Logo-removebg.png?ex=66eb1122&is=66e9bfa2&hm=6905679f67ab394bba74fb0e6f3ef2de3b8c42b4bc718bfd099aed41fb349466&")
                .setColor(Color.RED)
                .build();

        Button banUser = Button.danger("ban", "Ban " + player.getName());

        bot.getTextChannelById(config.getString("bot.report_channel_id")).sendMessageEmbeds(reportEmbed).setActionRow(banUser).queue();

        return true;
    }
}

