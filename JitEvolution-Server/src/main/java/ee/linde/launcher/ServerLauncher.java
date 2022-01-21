package ee.linde.launcher;

import ee.linde.jitevolution.core.models.configurations.Configuration;
import ee.linde.jitevolution.services.server.JitEvolutionLanguageServer;
import ee.linde.jitevolution.services.utils.ProjectConfigs;
import org.eclipse.lsp4j.launch.LSPLauncher;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServerLauncher {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        String projectId = ProjectConfigs.ensureProjectConfigs();
        // As we are using system std io channels
        // we need to reset and turn off the logging globally
        // So our client->server communication doesn't get interrupted.
        Configuration config = new Configuration(args[0], args[1], projectId, args[2], args[3]);
        LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(Level.OFF);

        // start the language server
        startServer(System.in, System.out, config);
    }

    /**
     * Start the language server.
     * @param in System Standard input stream
     * @param out System standard output stream
     * @throws ExecutionException Unable to start the server
     * @throws InterruptedException Unable to start the server
     */
    private static void startServer(InputStream in, OutputStream out, Configuration config) throws ExecutionException, InterruptedException {
        // Initialize the HelloLanguageServer
        var jitEvolutionLanguageServer = new JitEvolutionLanguageServer(config);
        // Create JSON RPC launcher for HelloLanguageServer instance.
        var launcher = LSPLauncher.createServerLauncher(jitEvolutionLanguageServer, in, out);

        // Get the client that request to launch the LS.
        var client = launcher.getRemoteProxy();

        // Set the client to language server
        jitEvolutionLanguageServer.connect(client);

        // Start the listener for JsonRPC
        Future<?> startListening = launcher.startListening();

        // Get the computed result from LS.
        startListening.get();
    }
}
