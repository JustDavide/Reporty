package me.dovide.reporty.discord;

import me.dovide.reporty.Reporty;
import me.dovide.reporty.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ButtonPressListener extends ListenerAdapter {

    private final Config bannedFromReporting;
    private final Reporty instance;

    public ButtonPressListener(Reporty instance){
        this.instance = instance;
        this.bannedFromReporting = instance.getBannedFromReport();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e){

        if (e.getButton().getId().equals("ban")){

            if(!hasAnyRoleById(e.getMember(), instance.getConfig().getStringList("bot.staff_roles"))){
                e.reply("You are not part of the staff team, you cannot ban a member from reporting.").setEphemeral(true).queue();
                return;
            }

            String playerToBan = e.getButton().getLabel().replace("Ban ", "");
            UUID uuidToBan = Bukkit.getPlayerExact(playerToBan).getUniqueId();

            if(bannedFromReporting.getStringList("uuids").contains(uuidToBan.toString())){
                e.reply("This user is already banned from reporting").setEphemeral(true).queue();
                return;
            }

            MessageEmbed banned = new EmbedBuilder()
                    .setAuthor(e.getMember().getEffectiveName())
                    .setColor(Color.RED)
                    .addField("New Ban", String.format("%s is now banned from reporting", playerToBan), false)
                    .setFooter("reporty", "https://cdn.discordapp.com/attachments/1285644729286918259/1285657264153301067/Reporty_Logo-removebg.png?ex=66eb1122&is=66e9bfa2&hm=6905679f67ab394bba74fb0e6f3ef2de3b8c42b4bc718bfd099aed41fb349466&")
                    .build();

            e.replyEmbeds(banned).queue();

            if (bannedFromReporting.get("uuids") == null)
                bannedFromReporting.set("uuids", Arrays.asList(uuidToBan.toString()));
            else {
                List<String> newList = bannedFromReporting.getStringList("uuids");
                newList.add(uuidToBan.toString());
                bannedFromReporting.set("uuids", newList);
            }
            instance.saveBanned();
        }

    }

    public static boolean hasAnyRoleById(Member member, List<String> roleIds) {
        List<Role> memberRoles = member.getRoles();

        for (Role role : memberRoles) {
            if (roleIds.contains(role.getId())) {
                return true;
            }
        }
        return false;
    }
}
