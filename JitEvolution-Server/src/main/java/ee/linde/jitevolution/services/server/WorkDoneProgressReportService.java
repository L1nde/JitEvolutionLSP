package ee.linde.jitevolution.services.server;

import ee.linde.jitevolution.core.services.ProgressReportService;
import ee.linde.jitevolution.core.services.ProgressReporter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.UUID;

public class WorkDoneProgressReportService implements ProgressReportService {
    private final LanguageClient client;

    public WorkDoneProgressReportService(LanguageClient client) {
        this.client = client;
    }

    public ProgressReporter create(String title) {
        var reporter = new WorkDoneProgressReporter(client, title);
        reporter.start();

        return reporter;
    }

    private static class WorkDoneProgressReporter implements ProgressReporter {
        private final String token;
        private final LanguageClient client;
        private final String title;

        private WorkDoneProgressReporter(LanguageClient client, String title) {
            this.token = UUID.randomUUID().toString();
            this.client = client;
            this.title = title;
        }

        public void start() {
            client.createProgress(new WorkDoneProgressCreateParams(Either.forLeft(token)));

            var notification = new WorkDoneProgressBegin();
            notification.setTitle(title);
            notification.setMessage("started");
            client.notifyProgress(createParamsFor(notification));
        }

        public void progress(String message) {
            var notification = new WorkDoneProgressReport();
            notification.setMessage(message);
            client.notifyProgress(createParamsFor(notification));
        }

        @Override
        public void close() throws Exception {
            var notification = new WorkDoneProgressEnd();
            notification.setMessage("ended");
            client.notifyProgress(createParamsFor(notification));
        }

        private ProgressParams createParamsFor(WorkDoneProgressNotification notification) {
            return new ProgressParams(Either.forLeft(token), Either.forLeft(notification));
        }
    }
}
