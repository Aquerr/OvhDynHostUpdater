package pl.bartlomiejstepien.ovhdynhostupdater.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class DynHostUpdaterConfig
{
    private static final Logger LOGGER = LogManager.getLogger(DynHostUpdaterConfig.class);
    private static final String CONFIG_PATH = "./config.conf";

    private final String username;
    private final String password;
    private final String hostName;
    private final Duration updateInterval;

    public static DynHostUpdaterConfig load()
    {
        Config config = loadConfiguration();
        return new DynHostUpdaterConfig(config);
    }

    private static Config loadConfiguration()
    {
        Path configFilePath = Paths.get(CONFIG_PATH);
        if (Files.notExists(configFilePath))
        {
            try
            {
                Files.createFile(configFilePath);
                Config defaultClasspathConfig = loadDefaultClasspathConfig();
                String configFileString = defaultClasspathConfig.root().render(ConfigRenderOptions.defaults()
                        .setJson(false)
                        .setOriginComments(false)
                        .setFormatted(true));
                Files.writeString(configFilePath, configFileString);
            }
            catch (IOException e)
            {
                LOGGER.error("Could not load configuration file.", e);
            }
        }
        return ConfigFactory.parseFile(configFilePath.toFile());
    }

    private static Config loadDefaultClasspathConfig()
    {
        return ConfigFactory.parseResources("config.conf");
    }

    private DynHostUpdaterConfig(Config config)
    {
        this.username = config.getConfig("dynhost").getString("username");
        this.password = config.getConfig("dynhost").getString("password");
        this.hostName = config.getConfig("dynhost").getString("hostname");
        this.updateInterval = config.getDuration("update-interval");
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getHostName()
    {
        return hostName;
    }

    public Duration getUpdateInterval()
    {
        return updateInterval;
    }
}
