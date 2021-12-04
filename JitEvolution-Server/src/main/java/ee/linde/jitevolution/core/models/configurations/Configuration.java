package ee.linde.jitevolution.core.models.configurations;

public class Configuration {
    private final String ApiUrl;

    public Configuration(String aPiUrl) {
        ApiUrl = aPiUrl;
    }

    public String getApiUrl() {
        return ApiUrl;
    }
}
