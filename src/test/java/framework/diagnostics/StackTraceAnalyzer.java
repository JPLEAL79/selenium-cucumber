package framework.diagnostics;

/**
 * Extrae la clase y metodo mas probable del framework.
 * Para el MVP usamos el primer frame bajo package pages.* como Page Object afectado.
 */
public final class StackTraceAnalyzer {

    private StackTraceAnalyzer() {
        // Utility class
    }

    public static String findPageObject(Throwable throwable) {
        StackTraceElement frame = findPageObjectFrame(throwable);
        return frame == null ? "Not detected" : simpleClassName(frame.getClassName());
    }

    public static String findPageObjectMethod(Throwable throwable) {
        StackTraceElement frame = findPageObjectFrame(throwable);
        return frame == null ? "Not detected" : frame.getMethodName();
    }

    private static StackTraceElement findPageObjectFrame(Throwable throwable) {
        Throwable root = rootCause(throwable);
        String pageObjectPrefix = DiagnosticsSettings.pageObjectPackage() + ".";
        String stepDefinitionPrefix = DiagnosticsSettings.stepDefinitionPackage() + ".";

        for (StackTraceElement frame : root.getStackTrace()) {
            if (frame.getClassName().startsWith(pageObjectPrefix)) {
                return frame;
            }
        }

        for (StackTraceElement frame : root.getStackTrace()) {
            if (frame.getClassName().startsWith(stepDefinitionPrefix)) {
                return frame;
            }
        }

        return null;
    }

    public static Throwable rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private static String simpleClassName(String className) {
        int index = className.lastIndexOf('.');
        return index >= 0 ? className.substring(index + 1) : className;
    }
}
