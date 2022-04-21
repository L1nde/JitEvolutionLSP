package ee.linde.jitevolution.services.server;

import ee.linde.jitevolution.core.models.configurations.Configuration;
import ee.linde.jitevolution.core.services.JitEvolutionApi;
import ee.linde.jitevolution.core.services.Logger;
import ee.linde.jitevolution.core.services.ProgressReportService;
import ee.linde.jitevolution.services.logs.LanguageClientLogger;
import ee.linde.jitevolution.services.utils.ProjectConfigs;
import ee.linde.jitevolution.services.utils.TextDocumentExtensions;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JitEvolutionTextDocumentService implements TextDocumentService {
    private JitEvolutionApi api;
    private Configuration config;
    private Logger logger;
    private ProgressReportService progressReportService;

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        logger.log("Opened");
        api.notifyFileOpened(didOpenTextDocumentParams.getTextDocument());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        logger.log("Changed");
        api.notifyFileChanged(didChangeTextDocumentParams.getTextDocument());
    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
        logger.log("Closed");
        api.notifyFileChanged(didCloseTextDocumentParams.getTextDocument());
    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
        logger.log("Saved");
        try (var progressReporter = progressReportService.create("Analysis")){
            var projectId = ProjectConfigs.ensureProjectConfigs();
            progressReporter.progress("Zipping project");
            var zip = TextDocumentExtensions.zipProject(config.getFileExtension());
            progressReporter.progress("Sending to API");
            api.notifyFileSaved(didSaveTextDocumentParams.getTextDocument().getUri(), zip);
            progressReporter.progress("Deleting temporary files");
            zip.delete();
        } catch (Exception e) {
            logger.logError("Unable to zip project");
        }
    }

    @Override
    public CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params) {
        api.notifyFileOpened(params.getTextDocument());
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    public void setLogger(LanguageClientLogger logger) {
        this.logger = logger;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public void setApi(JitEvolutionApi api) {
        this.api = api;
    }

    public void setProgressReportService(ProgressReportService progressReportService) {
        this.progressReportService = progressReportService;
    }
}
