package ee.linde.jitevolution.services.utils;

import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class TextDocumentExtensions {
    private TextDocumentExtensions() {
    }

    public static String GetFileRelativePath(TextDocumentIdentifier identifier) throws URISyntaxException {
        return GetFileRelativePath(identifier.getUri());
    }

    public static String GetFileRelativePath(TextDocumentItem item) throws URISyntaxException {
        return GetFileRelativePath(item.getUri());
    }

    public static String GetFileRelativePath(String uri) throws URISyntaxException {
        var projectDirectoryUri = new File("").toURI();

        return projectDirectoryUri.relativize(new URI(URLDecoder.decode(uri, StandardCharsets.UTF_8).replaceFirst("file:///", "file:/").replaceAll(" ", "%20"))).toString();
    }
}
