package ee.linde.jitevolution.core.services;

public interface ProgressReporter extends AutoCloseable {
    void progress(String message);
}
