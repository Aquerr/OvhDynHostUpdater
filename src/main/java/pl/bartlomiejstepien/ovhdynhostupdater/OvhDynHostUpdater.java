package pl.bartlomiejstepien.ovhdynhostupdater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.bartlomiejstepien.ovhdynhostupdater.config.DynHostUpdaterConfig;
import pl.bartlomiejstepien.ovhdynhostupdater.exception.CouldNotGetPublicIpException;
import pl.bartlomiejstepien.ovhdynhostupdater.file.FileHandler;
import pl.bartlomiejstepien.ovhdynhostupdater.ovh.client.OvhDynHostClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OvhDynHostUpdater
{
    private static final Logger LOGGER = LogManager.getLogger(OvhDynHostUpdater.class);
    private static final String IPIFY_API = "https://api.ipify.org";
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private final FileHandler fileHandler;
    private final DynHostUpdaterConfig dynHostUpdaterConfig;
    private final OvhDynHostClient ovhDynHostClient;

    public static void main( String[] args )
    {
        new OvhDynHostUpdater().start();
    }

    public OvhDynHostUpdater()
    {
        this.fileHandler = new FileHandler();
        this.dynHostUpdaterConfig = DynHostUpdaterConfig.load();

        if (this.dynHostUpdaterConfig.getHostName().isBlank() || this.dynHostUpdaterConfig.getUsername().isBlank() || this.dynHostUpdaterConfig.getPassword().isBlank())
            throw new IllegalArgumentException("Wrong configuration! Fill your configuration file with correct values!");

        this.ovhDynHostClient = new OvhDynHostClient(this.dynHostUpdaterConfig);
    }

    void start()
    {
        Duration updateInterval = dynHostUpdaterConfig.getUpdateInterval();

        EXECUTOR_SERVICE.scheduleAtFixedRate(this::updateDynHost, 0, updateInterval.getSeconds(), TimeUnit.SECONDS);
    }

    void updateDynHost()
    {
        String publicIp;
        try
        {
            publicIp = getPublicIp();
        }
        catch (CouldNotGetPublicIpException e)
        {
            throw new IllegalStateException(e);
        }

        String lastPublicIp = this.fileHandler.getLastPublicIp();

        LOGGER.info("Comparing ips.... Last public IP: {}, New public IP: {}", lastPublicIp, publicIp);
        if (!lastPublicIp.equals(publicIp))
        {
            LOGGER.info("IPs differ! Performing dynhost update!");
            performDynHostUpdate(publicIp);
            LOGGER.info("Update completed!");
        }
        else
        {
            LOGGER.info("Skipping dynhost update. IPs are the same.");
        }

    }

    private void performDynHostUpdate(String publicIp)
    {
        try
        {
            this.ovhDynHostClient.dynhostUpdate(publicIp);
            this.fileHandler.setLastPublicIp(publicIp);
        }
        catch (IOException | InterruptedException e)
        {
            LOGGER.error(e);
        }
    }

    private String getPublicIp() throws CouldNotGetPublicIpException
    {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(IPIFY_API)).build();
        try
        {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String publicIp = httpResponse.body();
            if (publicIp == null || publicIp.isBlank())
                throw new CouldNotGetPublicIpException("Received publicId is null or empty.");

            return publicIp;
        }
        catch (IOException | InterruptedException e)
        {
            throw new CouldNotGetPublicIpException(e);
        }
    }
}
