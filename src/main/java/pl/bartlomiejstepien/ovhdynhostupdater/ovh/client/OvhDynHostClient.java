package pl.bartlomiejstepien.ovhdynhostupdater.ovh.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.bartlomiejstepien.ovhdynhostupdater.config.DynHost;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class OvhDynHostClient
{
    private static final Logger LOGGER = LogManager.getLogger(OvhDynHostClient.class);
    private static final String DYN_HOST_UPDATE_URL = "https://www.ovh.com/nic/update?system=dyndns&hostname={hostname}&myip={ip}";

    private final HttpClient httpClient;

    public OvhDynHostClient()
    {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public List<DynHostUpdateResponse> dynhostUpdate(List<DynHost> dynHosts, String publicIp) throws IOException, InterruptedException
    {
        List<DynHostUpdateResponse> dynHostUpdateResponses = new ArrayList<>();

        for (final DynHost dynHost : dynHosts)
        {
            HttpResponse<String> response = null;
            try
            {
                final String preparedUrl = getOvhUrl(dynHost.getHostName(), publicIp);
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .GET()
                        .header("Authorization", "Basic " + prepareBase64Credentials(dynHost.getUsername(), dynHost.getPassword()))
                        .uri(URI.create(preparedUrl))
                        .build();
                LOGGER.info("Sending request to: {},", preparedUrl);
                response = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                LOGGER.info("Received response: status: {}, body: {}", response.statusCode(), response.body());
                dynHostUpdateResponses.add(new DynHostUpdateResponse(response.statusCode(), response.body()));
            }
            catch (Exception exception)
            {
                dynHostUpdateResponses.add(new DynHostUpdateResponse(401, exception.getMessage()));
            }

        }

        return dynHostUpdateResponses;
    }

    private String getOvhUrl(final String hostName, final String publicIp)
    {
        return DYN_HOST_UPDATE_URL.replace("{hostname}", hostName).replace("{ip}", publicIp);
    }

    private String prepareBase64Credentials(final String username, final String password)
    {
        String usernameAndPassword = username + ":" + password;
        return Base64.getEncoder().encodeToString(usernameAndPassword.getBytes(StandardCharsets.UTF_8));
    }
}
