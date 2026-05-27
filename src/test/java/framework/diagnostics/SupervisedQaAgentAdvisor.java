package framework.diagnostics;

/**
 * Agente local supervisado basado en reglas.
 * Es una recomendacion humana reutilizable para cualquier dominio de negocio.
 */
public final class SupervisedQaAgentAdvisor {

    private SupervisedQaAgentAdvisor() {
        // Utility class
    }

    public static String buildAdviceReport(String scenarioName, FailureDiagnosis diagnosis) {
        return switch (diagnosis.category()) {
            case LOCATOR_BROKEN -> report(
                    scenarioName,
                    "Selenium could not find or use an element in time.",
                    impactedFile(diagnosis),
                    "Compare the detected locator with the current DOM and propose a small Page Object update if it changed.",
                    "Medium: the locator can be reused by several scenarios.",
                    "No. A broken locator should not be hidden with retry."
            );
            case TIMEOUT -> report(
                    scenarioName,
                    "The failure looks related to waiting time or slow rendering.",
                    impactedFile(diagnosis),
                    "Review explicit waits around the affected method. Avoid broad wait increases.",
                    "Low/Medium: excessive waits can slow the suite.",
                    "Yes, only once and only if evidence suggests temporary slowness."
            );
            case GRID_SELENIUM -> report(
                    scenarioName,
                    "The browser session could not start correctly through Selenium Grid.",
                    "definitions/Hooks.java or environment configuration",
                    "Validate Grid URL, containers, browser nodes and remote capabilities before changing test code.",
                    "Low: this is usually infrastructure, not application behavior.",
                    "Yes, only once if the Grid/node was temporarily unavailable."
            );
            case ENVIRONMENT -> report(
                    scenarioName,
                    "The target application or network environment appears unavailable.",
                    "Environment configuration",
                    "Validate application URL, DNS/network access and environment health.",
                    "Low: test code changes are usually not the right fix.",
                    "Yes, only once if the environment is known to be unstable."
            );
            case DATA_CONFIGURATION -> report(
                    scenarioName,
                    "The failure points to missing or invalid framework data/configuration.",
                    "Test data/configuration resources",
                    "Validate feature keys, properties files, Maven flags and environment variables.",
                    "Low: credentials and test data must be handled carefully.",
                    "No. Configuration errors should fail fast."
            );
            case FUNCTIONAL_ASSERTION -> report(
                    scenarioName,
                    "The application result did not match the expected assertion.",
                    impactedFile(diagnosis),
                    "Review expected behavior, test data and business rule before changing automation code.",
                    "Medium/High: this can be a real product defect.",
                    "No. Functional assertions should not be retried automatically."
            );
            case FLAKY_CANDIDATE -> report(
                    scenarioName,
                    "The failure has signs of instability, but needs history before being confirmed as flaky.",
                    impactedFile(diagnosis),
                    "Review waits, DOM refreshes and CI timing. Track recurrence before applying broad fixes.",
                    "Medium: treating real defects as flaky can hide quality issues.",
                    "Yes, once in CI, only for technical/transient errors."
            );
            default -> report(
                    scenarioName,
                    "The failure does not match a known pattern yet.",
                    impactedFile(diagnosis),
                    "Review stack trace, screenshot and logs manually before changing code.",
                    "Unknown: more evidence is needed.",
                    "No, unless a human confirms it is transient."
            );
        };
    }

    private static String report(String scenarioName,
                                 String explanation,
                                 String impactedFile,
                                 String suggestedFix,
                                 String risk,
                                 String retryDecision) {
        return """
                AI Agent Suggested Review

                Scenario: %s

                Human explanation:
                %s

                Probable impacted file:
                %s

                Suggested fix:
                %s

                Risk:
                %s

                Retry:
                %s

                Requires human review:
                Yes
                """.formatted(
                scenarioName,
                explanation,
                impactedFile,
                suggestedFix,
                risk,
                retryDecision
        );
    }

    private static String impactedFile(FailureDiagnosis diagnosis) {
        return DiagnosticsSettings.pageObjectFilePath(diagnosis.pageObject());
    }
}
