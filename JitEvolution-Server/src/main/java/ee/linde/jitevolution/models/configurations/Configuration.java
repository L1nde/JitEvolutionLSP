package ee.linde.jitevolution.models.configurations;

public class Configuration {
    private final String ApiUrl;

    public Configuration(String aPiUrl) {
        ApiUrl = aPiUrl;
    }

    public String getApiUrl() {
        return ApiUrl;
    }
}
