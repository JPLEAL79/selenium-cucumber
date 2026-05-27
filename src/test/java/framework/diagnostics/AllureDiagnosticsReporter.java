package framework.diagnostics;

import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Publica diagnosticos humanos en Allure.
 * No modifica codigo ni toma decisiones automaticas.
 */
public final class AllureDiagnosticsReporter {

    private AllureDiagnosticsReporter() {
        // Utility class
    }

    public static void attachFailureDiagnosis(String scenarioName, FailureDiagnosis diagnosis) {
        String report = """
                AI-Assisted Failure Diagnosis

                Scenario: %s
                Category: %s
                Probable cause: %s
                Confidence: %s
                Page Object: %s
                Method/Class: %s
                Locator: %s
                Root exception: %s
                Retry recommended: %s
                Requires human review: %s

                Evidence:
                %s

                Suggested action:
                %s
                """.formatted(
                scenarioName,
                diagnosis.category(),
                diagnosis.probableCause(),
                diagnosis.confidence(),
                diagnosis.pageObject(),
                diagnosis.method(),
                diagnosis.locator(),
                diagnosis.rootException(),
                diagnosis.retryRecommended() ? "Yes" : "No",
                diagnosis.humanReviewRequired() ? "Yes" : "No",
                diagnosis.evidence(),
                diagnosis.suggestedAction()
        );

        Allure.addAttachment(
                "AI-Assisted Failure Diagnosis",
                "text/plain",
                new ByteArrayInputStream(report.getBytes(StandardCharsets.UTF_8)),
                "txt"
        );
    }

    public static void attachAgentReviewContext(String scenarioName, FailureDiagnosis diagnosis) {
        String context = buildAgentReviewContext(scenarioName, diagnosis);

        Allure.addAttachment(
                "AI Agent Review Context",
                "text/plain",
                new ByteArrayInputStream(context.getBytes(StandardCharsets.UTF_8)),
                "txt"
        );
    }

    public static void attachAgentAdvice(String scenarioName, FailureDiagnosis diagnosis) {
        String report = SupervisedQaAgentAdvisor.buildAdviceReport(scenarioName, diagnosis);

        Allure.addAttachment(
                "AI Agent Suggested Review",
                "text/plain",
                new ByteArrayInputStream(report.getBytes(StandardCharsets.UTF_8)),
                "txt"
        );
    }

    private static String buildAgentReviewContext(String scenarioName, FailureDiagnosis diagnosis) {
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
                - Adapt Page Object/package names to the target business framework.

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
