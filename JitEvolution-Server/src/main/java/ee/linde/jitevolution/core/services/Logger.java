package ee.linde.jitevolution.core.services;

public interface Logger {
    void log(String message);
    void logWarning(String message);
    void logError(String message);
}
