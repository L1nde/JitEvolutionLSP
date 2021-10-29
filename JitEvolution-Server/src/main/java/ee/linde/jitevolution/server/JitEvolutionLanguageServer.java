package ee.linde.jitevolution.server;

import ee.linde.jitevolution.models.configurations.Configuration;
import ee.linde.jitevolution.services.HttpJitEvolutionApi;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class JitEvolutionLanguageServer implements LanguageServer, LanguageClientAware {
    private final JitEvolutionTextDocumentService textDocumentService;
    private final WorkspaceService workspaceService;
    private final Configuration config;
    private int errorCode = 1;

    public JitEvolutionLanguageServer(Configuration config) {
        this.config = config;
        this.workspaceService = new JitEvolutionWorkspaceService();
        this.textDocumentService = new JitEvolutionTextDocumentService(this.workspaceService);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        // Initialize the InitializeResult for this LS.
        final InitializeResult initializeResult = new InitializeResult(new ServerCapabilities());
        // Set the capabilities of the LS to inform the client.
        initializeResult.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
        CompletionOptions completionOptions = new CompletionOptions();
        initializeResult.getCapabilities().setCompletionProvider(completionOptions);
        CodeLensOptions len = new CodeLensOptions();
        len.setResolveProvider(true);
        initializeResult.getCapabilities().setCodeLensProvider(len);
        initializeResult.getCapabilities().setExecuteCommandProvider(new ExecuteCommandOptions(Arrays.asList("test", "ass")));
        initializeResult.getCapabilities().setColorProvider(new ColorProviderOptions());
        return CompletableFuture.supplyAsync(()->initializeResult);
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
        this.textDocumentService.setLogger(logger);
        this.textDocumentService.setClient(languageClient);
        this.textDocumentService.setJitEvolutionApi(new HttpJitEvolutionApi(config, logger));
    }
}
