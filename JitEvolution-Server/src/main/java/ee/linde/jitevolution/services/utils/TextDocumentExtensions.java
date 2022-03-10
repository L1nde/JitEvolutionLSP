package ee.linde.jitevolution.services.utils;

import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static File zipProject(String extension) throws IOException {
        var projectDirectoryPath = new File("").toPath().toAbsolutePath();
        var tempFile = File.createTempFile("jit", "evolution.zip");
        var zos = new ZipOutputStream(new FileOutputStream(tempFile));
        Files.walkFileTree(projectDirectoryPath, new SimpleFileVisitor<>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(file.toString().endsWith(extension)){
                    zos.putNextEntry(new ZipEntry(projectDirectoryPath.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                }

                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();

        return tempFile;
    }
}
