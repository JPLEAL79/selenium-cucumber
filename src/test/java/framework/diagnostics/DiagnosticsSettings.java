package framework.diagnostics;

/**
 * Configuracion liviana para reutilizar diagnosticos en otros frameworks/negocios.
 * Si otro proyecto usa paquetes distintos, puede sobreescribirlos con -Ddiagnostics.*.
 */
public final class DiagnosticsSettings {

    private static final String DEFAULT_PAGE_OBJECT_PACKAGE = "pages";
    private static final String DEFAULT_STEP_DEFINITION_PACKAGE = "definitions";
    private static final String DEFAULT_TEST_SOURCE_ROOT = "src/test/java";

    private DiagnosticsSettings() {
        // Utility class
    }

    public static String pageObjectPackage() {
        return System.getProperty("diagnostics.pageObjectPackage", DEFAULT_PAGE_OBJECT_PACKAGE);
    }

    public static String stepDefinitionPackage() {
        return System.getProperty("diagnostics.stepDefinitionPackage", DEFAULT_STEP_DEFINITION_PACKAGE);
    }

    public static String testSourceRoot() {
        return System.getProperty("diagnostics.testSourceRoot", DEFAULT_TEST_SOURCE_ROOT);
    }

    public static String pageObjectFilePath(String simpleClassName) {
        if (simpleClassName == null || simpleClassName.isBlank() || "Not detected".equals(simpleClassName)) {
            return "Not detected";
        }

        return testSourceRoot()
                + "/"
                + pageObjectPackage().replace('.', '/')
                + "/"
                + simpleClassName
                + ".java";
    }
}
