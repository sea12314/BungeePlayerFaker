package fake.bungeefakerplayers;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeFakerPlayers extends Plugin implements Listener {

    private Random random = new Random();
    private int minPlayers;
    private int maxPlayers;
    private int minInterval;
    private int maxInterval;

    @Override
    public void onEnable() {
        loadConfig();
        updatePlayerCount();
        getProxy().getPluginManager().registerListener(this, this);
    }

    private void loadConfig() {
        try {
            if (!getDataFolder().exists()) getDataFolder().mkdir();
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                try (InputStream in = getResourceAsStream("config.yml")) {
                    Files.copy(in, file.toPath());
                }
            }
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            minPlayers = config.getInt("minPlayers", 100);
            maxPlayers = config.getInt("maxPlayers", 2000);
            minInterval = config.getInt("minInterval", 1);
            maxInterval = config.getInt("maxInterval", 160);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayerCount() {
        minPlayers = random.nextInt(maxPlayers - minPlayers + 1) + minPlayers;
        int delay = random.nextInt(maxInterval - minInterval + 1) + minInterval;
        getProxy().getScheduler().schedule(this, this::updatePlayerCount, delay, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onServerPing(ProxyPingEvent event) {
        ServerPing ping = event.getResponse();
        Players players = ping.getPlayers();
        players.setOnline(minPlayers);
        ping.setPlayers(players);
        event.setResponse(ping);
    }
}
