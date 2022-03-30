package pl.bartlomiejstepien.ovhdynhostupdater.ovh.client;

import pl.bartlomiejstepien.ovhdynhostupdater.config.DynHostUpdaterConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

public class OvhDynHostClient
{
    private static final String DYN_HOST_UPDATE_URL = "https://www.ovh.com/nic/update?system=dyndns&hostname={hostname}&myip=${ip}";

    private final DynHostUpdaterConfig dynHostUpdaterConfig;
    private final HttpClient httpClient;

    public OvhDynHostClient(DynHostUpdaterConfig dynHostUpdaterConfig)
    {
        this.dynHostUpdaterConfig = dynHostUpdaterConfig;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void dynhostUpdate(String publicIp) throws IOException, InterruptedException
    {
        String usernameAndPassword = dynHostUpdaterConfig.getUsername() + ":" + dynHostUpdaterConfig.getPassword();
        String encodedUsernameAndPassword = Base64.getEncoder().encodeToString(usernameAndPassword.getBytes(StandardCharsets.UTF_8));

        String preparedUrl = DYN_HOST_UPDATE_URL.replace("{hostname}", dynHostUpdaterConfig.getHostName()).replace("{ip}", publicIp);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .header("Authorization", "Basic " + encodedUsernameAndPassword)
                .uri(URI.create(preparedUrl))
                .build();
        this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}
