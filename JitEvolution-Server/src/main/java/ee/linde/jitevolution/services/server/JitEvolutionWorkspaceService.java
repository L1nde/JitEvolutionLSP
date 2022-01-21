package ee.linde.jitevolution.services.server;

import ee.linde.jitevolution.core.constants.CommandType;
import ee.linde.jitevolution.core.contexts.JitContext;
import ee.linde.jitevolution.services.utils.ProjectConfigs;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JitEvolutionWorkspaceService implements WorkspaceService {
    private JitContext context;

    public void setContext(JitContext context){
        this.context = context;
    }

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
                context.getLogger().logError(String.format("Command '%s' is not supported", params.getCommand()));
        }
        return null;
    }

    private void openVisualization(){
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(context.getConfiguration().getVisualizationUrl()));
        } catch (IOException | URISyntaxException e) {
            context.getLogger().logError(String.format("Failed to open url '%s' in browser", context.getConfiguration().getVisualizationUrl()));
        }
    }

    private void sendProjectToApi(){
        try {
            var projectId = ProjectConfigs.ensureProjectConfigs();
            var projectDirectoryPath = new File("").toPath().toAbsolutePath();
            var tempFile = File.createTempFile("jit", "evolution.zip");
            var zos = new ZipOutputStream(new FileOutputStream(tempFile));
            Files.walkFileTree(projectDirectoryPath, new SimpleFileVisitor<>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if(file.toString().endsWith(context.getConfiguration().getFileExtension())){
                        zos.putNextEntry(new ZipEntry(projectDirectoryPath.relativize(file).toString()));
                        Files.copy(file, zos);
                        zos.closeEntry();
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
            zos.close();

            context.getEvolutionApi().createProject(projectId, tempFile);

            tempFile.delete();
        } catch (Exception e) {
            context.getLogger().logError("Unable to zip project");
        }

    }

    @Override
    public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams workspaceSymbolParams) {
        return null;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {
        context.getLogger().log("Watched");
    }

    @Override
    public void didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams params) {
        context.getLogger().log("Folders");
    }
}
