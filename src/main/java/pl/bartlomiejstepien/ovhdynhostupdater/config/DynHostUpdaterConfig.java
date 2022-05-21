package pl.bartlomiejstepien.ovhdynhostupdater.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DynHostUpdaterConfig
{
    private static final Logger LOGGER = LogManager.getLogger(DynHostUpdaterConfig.class);
    private static final String CONFIG_PATH = System.getProperty("user.dir") + File.separator + "config.conf";

    private final Duration updateInterval;
    private final List<DynHost> dynHosts;

    public static DynHostUpdaterConfig load()
    {
        Config config = loadConfiguration();
        return new DynHostUpdaterConfig(config);
    }

    private static Config loadConfiguration()
    {
        Path configFilePath = Paths.get(CONFIG_PATH).toAbsolutePath().normalize();
        LOGGER.info("Looking for config file in: {}", configFilePath.toString());
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
        List<DynHost> dynHosts = new ArrayList<>();

        for (final ConfigObject configObject : config.getObjectList("dynhosts"))
        {
            Config dynHostConfig = configObject.toConfig();
            DynHost dynHost = new DynHost(
                    dynHostConfig.getString("username"),
                    dynHostConfig.getString("password"),
                    dynHostConfig.getString("hostname"));
            dynHosts.add(dynHost);
        }

        this.dynHosts = dynHosts;
        this.updateInterval = config.getDuration("update-interval");
    }

    public List<DynHost> getDynHosts()
    {
        return dynHosts;
    }

    public Duration getUpdateInterval()
    {
        return updateInterval;
    }
}
