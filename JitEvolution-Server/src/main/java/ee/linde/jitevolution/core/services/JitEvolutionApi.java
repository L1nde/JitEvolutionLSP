package ee.linde.jitevolution.core.services;

import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;

import java.io.File;

public interface JitEvolutionApi {
    void notifyFileOpened(TextDocumentIdentifier textDocumentIdentifier);
    void notifyFileOpened(TextDocumentItem item);
    void createProject(String projectId, File project);
}
