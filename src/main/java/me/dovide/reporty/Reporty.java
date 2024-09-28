package me.dovide.reporty;

import me.dovide.reporty.discord.ButtonPressListener;
import me.dovide.reporty.minecraft.ReportAdmin;
import me.dovide.reporty.minecraft.ReportCommand;
import me.dovide.reporty.utils.Config;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import net.dv8tion.jda.api.JDA;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.EnumSet;
import java.util.logging.Logger;

public final class Reporty extends JavaPlugin {

    private final Logger logger = Logger.getLogger("Reporty");
    private JDA jda;
    private Config config;
    private Config banned;

    @Override
    public void onEnable() {

        logger.info("Reporty Starting Up.");
        logger.info("Developed by Dovide <3");

        config = createConfig("config.yml");
        banned = createConfig("bannedfromreport.yml");
        logger.info("[Reporty] Config Created / Loaded");

        jda = JDABuilder.createLight(config.getString("bot.token"),
                        EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(new ButtonPressListener(this))
                .build();
        logger.info("[Reporty] Bot Started");

        getCommand("reporty").setExecutor(new ReportAdmin(this));
        getCommand("report").setExecutor(new ReportCommand(this));
        logger.info("[Reporty] Minecraft Commands Initialized");


        logger.info("[Reporty] Plugin fully Initialized");
    }

    @Override
    public void onDisable(){
        logger.info("Shutting Down the bot");

        if (jda != null) {
            try {
                jda.shutdown();
                jda.awaitShutdown(Duration.ofSeconds(10));
                logger.info("Bot Shut Down");
            } catch (IllegalStateException | InterruptedException err) {
                logger.severe("Failed to shut down JDA. Forcing ShutDown");
            } catch (NoClassDefFoundError ignore) {}
        }

        logger.info("Plugin Disabled");
    }

    public Config createConfig(String name) {
        File fc = new File(getDataFolder(), name);
        if (!fc.exists()) {
            fc.getParentFile().mkdir();
            saveResource(name, false);
        }
        Config config = new Config();
        try {
            config.load(fc);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        return config;
    }

    public void saveBanned(){
        File fc = new File(getDataFolder(), "bannedfromreport.yml");
        try {
            banned.save(fc);
        }catch (IOException err){
            throw new RuntimeException(err);
        }
    }

    public Config getBannedFromReport(){
        return banned;
    }

    public JDA getJDA(){
        return jda;
    }

    @Override
    public Config getConfig(){
        return config;
    }
}
