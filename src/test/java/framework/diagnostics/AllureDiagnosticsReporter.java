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
        String context = AiAgentContextBuilder.buildReviewContext(scenarioName, diagnosis);

        Allure.addAttachment(
                "AI Agent Review Context",
                "text/plain",
                new ByteArrayInputStream(context.getBytes(StandardCharsets.UTF_8)),
                "txt"
        );
    }

    public static void attachAgentAdvice(String scenarioName, AiAgentAdvice advice) {
        String report = """
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
                %s
                """.formatted(
                scenarioName,
                advice.humanExplanation(),
                advice.probableImpactedFile(),
                advice.suggestedFix(),
                advice.risk(),
                advice.retryDecision(),
                advice.humanReviewRequired() ? "Yes" : "No"
        );

        Allure.addAttachment(
                "AI Agent Suggested Review",
                "text/plain",
                new ByteArrayInputStream(report.getBytes(StandardCharsets.UTF_8)),
                "txt"
        );
    }
}
