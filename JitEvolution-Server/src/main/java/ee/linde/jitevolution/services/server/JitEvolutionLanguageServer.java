package ee.linde.jitevolution.services.server;

import ee.linde.jitevolution.core.contexts.JitContext;
import ee.linde.jitevolution.core.models.configurations.Configuration;
import ee.linde.jitevolution.services.logs.LanguageClientLogger;
import ee.linde.jitevolution.services.evolutionapi.JitEvolutionHttpClient;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class JitEvolutionLanguageServer implements LanguageServer, LanguageClientAware {
    private final JitEvolutionTextDocumentService textDocumentService;
    private final JitEvolutionWorkspaceService workspaceService;
    private final Configuration config;
    private int errorCode = 1;

    public JitEvolutionLanguageServer(Configuration config) {
        this.config = config;
        this.workspaceService = new JitEvolutionWorkspaceService();
        this.textDocumentService = new JitEvolutionTextDocumentService(this.workspaceService);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        var capabilities = new ServerCapabilities();

        capabilities.setTextDocumentSync(getSyncOptions());
        capabilities.setExecuteCommandProvider(new ExecuteCommandOptions());
        capabilities.setColorProvider(new ColorProviderOptions());

//        var completionOptions = new CompletionOptions();
//        capabilites.setCompletionProvider(completionOptions);
//        CodeLensOptions len = new CodeLensOptions();
//        len.setResolveProvider(true);
//        capabilites.setCodeLensProvider(len);

        return CompletableFuture.supplyAsync(()->new InitializeResult(capabilities));
    }

    private TextDocumentSyncOptions getSyncOptions() {
        var syncOptions = new TextDocumentSyncOptions();
        syncOptions.setChange(TextDocumentSyncKind.Incremental);
        syncOptions.setSave(new SaveOptions(true));

        return syncOptions;
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        // If shutdown request comes from client, set the error code to 0.
        errorCode = 0;
        return null;
    }

    @Override
    public void exit() {
        // Kill the LS on exit request from client.
        System.exit(errorCode);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        // Return the endpoint for language features.
        return this.textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        // Return the endpoint for workspace functionality.
        return this.workspaceService;
    }

    @Override
    public void connect(LanguageClient languageClient) {
        // Get the client which started this LS.
        var logger = new LanguageClientLogger(languageClient);
        var context = new JitContext(logger, languageClient, new JitEvolutionHttpClient(config, logger), this.config);
        this.textDocumentService.setContext(context);
        this.workspaceService.setContext(context);
    }
}
