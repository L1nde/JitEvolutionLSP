package ee.linde.jitevolution.core.contexts;

import ee.linde.jitevolution.core.services.JitEvolutionApi;
import ee.linde.jitevolution.core.services.Logger;
import org.eclipse.lsp4j.services.LanguageClient;

public class JitContext {
    private final Logger logger;
    private final LanguageClient client;
    private final JitEvolutionApi evolutionApi;

    public JitContext(Logger logger, LanguageClient client, JitEvolutionApi evolutionApi) {
        this.logger = logger;
        this.client = client;
        this.evolutionApi = evolutionApi;
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
}
