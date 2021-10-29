package ee.linde.jitevolution.server;

import ee.linde.jitevolution.core.Logger;
import ee.linde.jitevolution.core.JitEvolutionApi;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JitEvolutionTextDocumentService implements TextDocumentService {
    private LanguageClient client;
    private WorkspaceService workspaceService;
    private JitEvolutionApi jitEvolutionApi;
    private Logger logger;

    public JitEvolutionTextDocumentService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public void setClient(LanguageClient client){
        this.client = client;
    }

    public void setJitEvolutionApi(JitEvolutionApi api){
        this.jitEvolutionApi = api;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams completionParams) {
        // Provide completion item.
        return CompletableFuture.supplyAsync(() -> {
            List<CompletionItem> completionItems = new ArrayList<>();

                // Sample Completion item for sayHello
                CompletionItem completionItem = new CompletionItem();
                // Define the text to be inserted in to the file if the completion item is selected.
                completionItem.setInsertText("sayHello() {\n    print(\"hello\")\n}");
                // Set the label that shows when the completion drop down appears in the Editor.
                completionItem.setLabel("testsayHello()");
                // Set the completion kind. This is a snippet.
                // That means it replace character which trigger the completion and
                // replace it with what defined in inserted text.
                completionItem.setKind(CompletionItemKind.Snippet);
                // This will set the details for the snippet code which will help user to
                // understand what this completion item is.
                completionItem.setDetail("sayHello()\n this will say hello to the people");

                // Add the sample completion item to the list.
                completionItems.add(completionItem);

                CompletionItem comp = new CompletionItem();

                comp.setInsertText("test test");
                comp.setLabel("labrl");
                comp.setKind(CompletionItemKind.File);
                comp.setDetail("detail");
                completionItems.add(comp);
                //client.telemetryEvent(new Exception("Test"));
            // Return the list of completion items.
            return Either.forLeft(completionItems);
        });
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams codeLensParams) {
        return CompletableFuture.supplyAsync(() -> {
            List<CodeLens> codeLens = new ArrayList<>();
            CodeLens len = new CodeLens();
            len.setData(1);
            Range r = new Range();
            r.setStart(new Position(1, 's'));
            r.setEnd(new Position(1, 'y'));
            len.setRange(r);
            len.setCommand(new Command("Test", "test"));
            codeLens.add(len);
            codeLens.add(new CodeLens(new Range(new Position(5, 's'), new Position(5, 'y')), new Command("Asd", "ass"), 1));
            return codeLens;
        });
    }

    @Override
    public CompletableFuture<CodeLens> resolveCodeLens(CodeLens codeLens) {
        return CompletableFuture.supplyAsync(() -> {
            return codeLens;
        });
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        jitEvolutionApi.notifyFileOpened("Open: " + didOpenTextDocumentParams.getTextDocument().getUri());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        jitEvolutionApi.notifyFileOpened("Change: " + didChangeTextDocumentParams.getTextDocument().getUri());
    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
        jitEvolutionApi.notifyFileOpened("Close: " + didCloseTextDocumentParams.getTextDocument().getUri());
    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
        jitEvolutionApi.notifyFileOpened("Save: " + didSaveTextDocumentParams.getTextDocument().getUri());
    }

    @Override
    public CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params) {
        jitEvolutionApi.notifyFileOpened("Color: " + params.getTextDocument().getUri());
        return CompletableFuture.supplyAsync(() -> {
            var colors = new ArrayList<ColorInformation>();
            colors.add(new ColorInformation(new Range(new Position(1, 's'), new Position(1, 't')), new Color(256, 0, 0, 1)));
            return colors;
        });
    }
}
