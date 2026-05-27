package framework.diagnostics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacen temporal por escenario.
 * Cucumber detecta la excepcion real en eventos, y Hooks la publica luego en Allure.
 */
public final class FailureDiagnosisStore {

    private static final Map<String, FailureDiagnosis> DIAGNOSES = new ConcurrentHashMap<>();

    private FailureDiagnosisStore() {
        // Utility class
    }

    public static void save(String scenarioName, FailureDiagnosis diagnosis) {
        DIAGNOSES.put(scenarioName, diagnosis);
    }

    public static FailureDiagnosis consume(String scenarioName) {
        return DIAGNOSES.remove(scenarioName);
    }
}
