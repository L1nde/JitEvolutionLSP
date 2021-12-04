package ee.linde.jitevolution.services.evolutionapi;

import ee.linde.jitevolution.core.services.JitEvolutionApi;
import ee.linde.jitevolution.core.models.configurations.Configuration;
import ee.linde.jitevolution.core.services.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class JitEvolutionHttpClient implements JitEvolutionApi {
    private final Configuration config;
    private final HttpClient httpClient;
    private final Logger logger;

    public JitEvolutionHttpClient(Configuration config, Logger logger) {
        this.config = config;
        this.httpClient = HttpClient.newHttpClient();
        this.logger = logger;
    }

    public void notifyFileOpened(String openedFile) {
        logger.log(openedFile);
        var request = HttpRequest.newBuilder(URI.create(config.getApiUrl())).build();
//
//        try {
////            var response = httpClient.sendAsync(request, (HttpResponse.BodyHandler<String>) responseInfo -> null).get();
//        } catch (InterruptedException | ExecutionException e) {
//            logger.logError(e.getMessage());
//        }
    }
}
