package ee.linde.jitevolution.core;

public interface Logger {
    void log(String message);
    void logWarning(String message);
    void logError(String message);
}
