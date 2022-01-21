package ee.linde.jitevolution.core.models.configurations;

public class Configuration {
    private final String apiUrl;
    private final String apiKey;
    private final String projectId;
    private final String visualizationUrl;
    private final String fileExtension;

    public Configuration(String apiUrl, String apiKey, String projectId, String jitEvolutionUrl, String fileExtension) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.projectId = projectId;
        this.visualizationUrl = jitEvolutionUrl;
        this.fileExtension = fileExtension;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getVisualizationUrl() {
        return visualizationUrl;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
