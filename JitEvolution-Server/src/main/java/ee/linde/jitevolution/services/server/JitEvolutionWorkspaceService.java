package ee.linde.jitevolution.services.server;

import ee.linde.jitevolution.core.constants.CommandType;
import ee.linde.jitevolution.core.models.configurations.Configuration;
import ee.linde.jitevolution.core.services.JitEvolutionApi;
import ee.linde.jitevolution.core.services.Logger;
import ee.linde.jitevolution.services.utils.ProjectConfigs;
import ee.linde.jitevolution.services.utils.TextDocumentExtensions;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;


public class JitEvolutionWorkspaceService implements WorkspaceService {
    private Logger logger;
    private Configuration config;
    private JitEvolutionApi api;

    @Override
    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
        switch (params.getCommand()) {
            case CommandType.OPEN_VISUALIZATION:
                openVisualization();
                break;
            case CommandType.SEND_TO_ANALYZER:
                sendProjectToApi();
                break;
            default:
                logger.logError(String.format("Command '%s' is not supported", params.getCommand()));
        }
        return null;
    }

    private void openVisualization(){
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(config.getVisualizationUrl()));
        } catch (IOException | URISyntaxException e) {
            logger.logError(String.format("Failed to open url '%s' in browser", config.getVisualizationUrl()));
        }
    }

    private void sendProjectToApi(){
        try {
            var projectId = ProjectConfigs.ensureProjectConfigs();
            var zip = TextDocumentExtensions.zipProject(config.getFileExtension());
            api.createProject(projectId, zip);
            zip.delete();
        } catch (Exception e) {
            logger.logError("Unable to zip project");
        }
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {
        logger.log("Watched");
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public void setApi(JitEvolutionApi api) {
        this.api = api;
    }
}
