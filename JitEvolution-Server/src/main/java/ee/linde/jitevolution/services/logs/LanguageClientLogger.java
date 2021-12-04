package ee.linde.jitevolution.services.logs;

import ee.linde.jitevolution.core.services.Logger;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;

public class LanguageClientLogger implements Logger {
    private final LanguageClient languageClient;

    public LanguageClientLogger(LanguageClient languageClient) {
        this.languageClient = languageClient;
    }

    @Override
    public void log(String message) {
        languageClient.logMessage(new MessageParams(MessageType.Info, message));
    }

    @Override
    public void logWarning(String message) {
        languageClient.logMessage(new MessageParams(MessageType.Warning, message));
    }

    @Override
    public void logError(String message) {
        languageClient.logMessage(new MessageParams(MessageType.Error, message));
    }
}
