package framework.diagnostics;

/**
 * Categorias simples para diagnosticar fallos sin depender aun de IA externa.
 * La idea es entregar una causa probable, no ocultar ni corregir el error.
 */
public enum FailureCategory {
    LOCATOR_BROKEN,
    TIMEOUT,
    ENVIRONMENT,
    GRID_SELENIUM,
    DATA_CONFIGURATION,
    FLAKY_CANDIDATE,
    FUNCTIONAL_ASSERTION,
    UNKNOWN
}
