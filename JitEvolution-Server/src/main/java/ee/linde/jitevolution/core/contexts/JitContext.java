package ee.linde.jitevolution.core.contexts;

import ee.linde.jitevolution.core.models.configurations.Configuration;
import ee.linde.jitevolution.core.services.JitEvolutionApi;
import ee.linde.jitevolution.core.services.Logger;
import org.eclipse.lsp4j.services.LanguageClient;

public class JitContext {
    private final Logger logger;
    private final LanguageClient client;
    private final JitEvolutionApi evolutionApi;
    private final Configuration configuration;

    public JitContext(Logger logger, LanguageClient client, JitEvolutionApi evolutionApi, Configuration configuration) {
        this.logger = logger;
        this.client = client;
        this.evolutionApi = evolutionApi;
        this.configuration = configuration;
    }

    public JitEvolutionApi getEvolutionApi() {
        return evolutionApi;
    }

    public LanguageClient getClient() {
        return client;
    }

    public Logger getLogger() {
        return logger;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
