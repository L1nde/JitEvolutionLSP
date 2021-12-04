package ee.linde.jitevolution.services.server;

import ee.linde.jitevolution.core.contexts.JitContext;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JitEvolutionTextDocumentService implements TextDocumentService {
    private JitContext context;
    private WorkspaceService workspaceService;

    public JitEvolutionTextDocumentService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public void setContext(JitContext context){
        this.context = context;
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams completionParams) {
        // Provide completion item.
        context.getLogger().log("Completion");
        return CompletableFuture.supplyAsync(() ->
            Either.forLeft(new ArrayList<>()));
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams codeLensParams) {
        context.getLogger().log("Codelens");
        return CompletableFuture.supplyAsync(ArrayList::new);
    }

    @Override
    public CompletableFuture<CodeLens> resolveCodeLens(CodeLens codeLens) {
        context.getLogger().log("CodelensResolve");
        return CompletableFuture.supplyAsync(() -> codeLens);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        context.getEvolutionApi().notifyFileOpened("Open: " + didOpenTextDocumentParams.getTextDocument().getUri());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        context.getEvolutionApi().notifyFileOpened("Change: " + didChangeTextDocumentParams.getTextDocument().getUri());
    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
        context.getEvolutionApi().notifyFileOpened("Close: " + didCloseTextDocumentParams.getTextDocument().getUri());
    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
        context.getEvolutionApi().notifyFileOpened("Save: " + didSaveTextDocumentParams.getTextDocument().getUri());
    }

    @Override
    public CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params) {
        context.getEvolutionApi().notifyFileOpened("Color: " + params.getTextDocument().getUri());
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}
