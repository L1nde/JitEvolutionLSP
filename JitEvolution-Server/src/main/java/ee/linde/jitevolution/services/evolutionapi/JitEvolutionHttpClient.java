package ee.linde.jitevolution.services.evolutionapi;

import com.google.gson.JsonObject;
import ee.linde.jitevolution.core.services.JitEvolutionApi;
import ee.linde.jitevolution.core.models.configurations.Configuration;
import ee.linde.jitevolution.core.services.Logger;
import ee.linde.jitevolution.services.utils.TextDocumentExtensions;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class JitEvolutionHttpClient implements JitEvolutionApi {
    private final Configuration config;
    private final HttpClient httpClient;
    private final Logger logger;

    public JitEvolutionHttpClient(Configuration config, Logger logger) {
        this.config = config;
        this.httpClient = HttpClient
                .newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.logger = logger;
    }

    public void notifyFileOpened(TextDocumentIdentifier textDocumentIdentifier) {
        try {
            notifyFileOpened(TextDocumentExtensions.GetFileRelativePath(textDocumentIdentifier));
        } catch (URISyntaxException e) {
            logger.logError(e.getMessage());
        }
    }

    public void notifyFileOpened(TextDocumentItem item) {
        try {
            notifyFileOpened(TextDocumentExtensions.GetFileRelativePath(item));
        } catch (URISyntaxException e) {
            logger.logError(e.getMessage());
        }
    }

    public void notifyFileChanged(TextDocumentIdentifier textDocumentIdentifier) {
        logger.log("Change");
    }

    public void notifyFileSaved(String fileUri, File project) {
        try {
            var data = new HashMap<>();
            data.put("projectId", config.getProjectId());
            data.put("projectZip", project.toPath());
            data.put("uri", TextDocumentExtensions.GetFileRelativePath(fileUri));
            var request = createPostFormRequest("/ide/file-saved", data);

            var response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            logger.log(response.body());
        } catch (URISyntaxException | IOException | InterruptedException | ExecutionException e) {
            logger.logError(e.getMessage());
        }
    }

    public void createProject(String projectId, File project) {
        var data = new HashMap<>();
        data.put("projectId", projectId);
        data.put("projectZip", project.toPath());

        try {
            var request = createPostFormRequest("/ide/project", data);

            var response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            logger.log(response.body());
        } catch (IOException | InterruptedException | ExecutionException e) {
            logger.logError(e.getMessage());
        }
    }

    private void notifyFileOpened(String fileUri) {
        var requestJson = new JsonObject();
        requestJson.addProperty("projectId", config.getProjectId());
        requestJson.addProperty("fileUri", fileUri);
        var request = createPostJsonRequest("/ide/file-opened", requestJson);

        try {
            var response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.logError(e.getMessage());
        }
    }

    private HttpRequest createPostJsonRequest(String url, JsonObject jsonObject) {
        return HttpRequest
                .newBuilder(URI.create(config.getApiUrl() + url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                .setHeader("X-Api-Key", config.getApiKey())
                .header("Content-Type", "application/json")
                .build();
    }

    private HttpRequest createPostFormRequest(String url, Map<Object, Object> data) throws IOException {
        String boundary = new BigInteger(256, new Random()).toString();

        return HttpRequest
                .newBuilder(URI.create(config.getApiUrl() + url))
                .POST(ofMimeMultipartData(data, boundary))
                .setHeader("X-Api-Key", config.getApiKey())
                .header("Content-Type", "multipart/form-data;boundary=" + boundary)
                .build();
    }

    public static HttpRequest.BodyPublisher ofMimeMultipartData(Map<Object, Object> data,
                                                                String boundary) throws IOException {
        var byteArrays = new ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
                .getBytes(StandardCharsets.UTF_8);
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            byteArrays.add(separator);

            if (entry.getValue() instanceof Path) {
                var path = (Path) entry.getValue();
                String mimeType = Files.probeContentType(path);
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                        + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n")
                        .getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
                        .getBytes(StandardCharsets.UTF_8));
            }
        }
        byteArrays.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
