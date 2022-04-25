package ee.linde.jitevolution.core.models.configurations;

import ee.linde.jitevolution.services.utils.ProjectConfigs;

import java.io.IOException;

public class Configuration {
    private final String apiUrl;
    private final String apiKey;
    private final String visualizationUrl;
    private final String fileExtension;

    public Configuration(String apiUrl, String apiKey, String jitEvolutionUrl, String fileExtension) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.visualizationUrl = jitEvolutionUrl;
        this.fileExtension = fileExtension;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getProjectId() {

        try {
            return ProjectConfigs.ensureProjectConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Project id not set";
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
