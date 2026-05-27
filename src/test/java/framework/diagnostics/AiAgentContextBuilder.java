package framework.diagnostics;

/**
 * Construye el contexto que un agente IA externo podria revisar.
 * En este MVP no llama ningun servicio: solo deja el input listo y auditable.
 */
public final class AiAgentContextBuilder {

    private AiAgentContextBuilder() {
        // Utility class
    }

    public static String buildReviewContext(String scenarioName, FailureDiagnosis diagnosis) {
        return """
                AI Agent Review Context

                Role:
                You are a supervised QA automation assistant for Selenium, Java and Cucumber.

                Safety rules:
                - Do not modify code automatically.
                - Do not recommend merge, push or deployment.
                - Do not hide real failures with retries.
                - Suggest only small, reviewable fixes.
                - Require human approval for any code change.

                Current failure:
                Scenario: %s
                Category: %s
                Confidence: %s
                Probable cause: %s
                Page Object: %s
                Method/Class: %s
                Locator: %s
                Root exception: %s
                Retry recommended by rules: %s

                Evidence:
                %s

                Expected response:
                - Human explanation:
                - Probable impacted file:
                - Suggested fix:
                - Risk:
                - Should retry be used?: Yes/No and why
                - Requires human review: Yes
                """.formatted(
                scenarioName,
                diagnosis.category(),
                diagnosis.confidence(),
                diagnosis.probableCause(),
                diagnosis.pageObject(),
                diagnosis.method(),
                diagnosis.locator(),
                diagnosis.rootException(),
                diagnosis.retryRecommended() ? "Yes" : "No",
                diagnosis.evidence()
        );
    }
}
