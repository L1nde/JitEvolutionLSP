package ee.linde.jitevolution.services;

import ee.linde.jitevolution.core.JitEvolutionApi;
import ee.linde.jitevolution.models.configurations.Configuration;
import ee.linde.jitevolution.core.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class HttpJitEvolutionApi implements JitEvolutionApi {
    private final Configuration config;
    private final HttpClient httpClient;
    private final Logger logger;

    public HttpJitEvolutionApi(Configuration config, Logger logger) {
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
