package framework.diagnostics;

/**
 * Agente local supervisado basado en reglas.
 * Sirve como fallback enterprise antes de conectar una IA externa.
 */
public class RuleBasedQaAgentAdvisor implements AiAgentAdvisor {

    @Override
    public AiAgentAdvice advise(String scenarioName, FailureDiagnosis diagnosis) {
        return switch (diagnosis.category()) {
            case LOCATOR_BROKEN -> locatorAdvice(diagnosis);
            case TIMEOUT -> timeoutAdvice(diagnosis);
            case GRID_SELENIUM -> gridAdvice(diagnosis);
            case ENVIRONMENT -> environmentAdvice(diagnosis);
            case DATA_CONFIGURATION -> dataConfigurationAdvice(diagnosis);
            case FUNCTIONAL_ASSERTION -> functionalAdvice(diagnosis);
            case FLAKY_CANDIDATE -> flakyAdvice(diagnosis);
            default -> unknownAdvice(diagnosis);
        };
    }

    private AiAgentAdvice locatorAdvice(FailureDiagnosis diagnosis) {
        return new AiAgentAdvice(
                "The failure points to an element that Selenium could not find or use in time.",
                probablePageFile(diagnosis),
                "Compare the Page Object locator with the current DOM and propose a small locator update if it changed.",
                "Medium: changing a locator can affect all scenarios that reuse this Page Object.",
                "No. A broken locator should not be hidden with retry.",
                true
        );
    }

    private AiAgentAdvice timeoutAdvice(FailureDiagnosis diagnosis) {
        return new AiAgentAdvice(
                "The failure looks related to waiting time or slow rendering.",
                probablePageFile(diagnosis),
                "Review waits around the affected method and prefer explicit waits over fixed sleeps.",
                "Low/Medium: increasing waits can slow the suite if applied broadly.",
                "Yes, only once and only if evidence suggests temporary slowness.",
                true
        );
    }

    private AiAgentAdvice gridAdvice(FailureDiagnosis diagnosis) {
        return new AiAgentAdvice(
                "The browser session could not start correctly through Selenium Grid.",
                "definitions/Hooks.java",
                "Validate Grid URL, Docker containers, browser nodes and remote capabilities before changing test code.",
                "Low: this is usually infrastructure, not application behavior.",
                "Yes, only once if the Grid/node was temporarily unavailable.",
                true
        );
    }

    private AiAgentAdvice environmentAdvice(FailureDiagnosis diagnosis) {
        return new AiAgentAdvice(
                "The target application or network environment appears unavailable.",
                "Environment/Jenkins configuration",
                "Validate application URL, DNS/network access and environment health.",
                "Low: test code changes are usually not the right fix.",
                "Yes, only once if the environment is known to be unstable.",
                true
        );
    }

    private AiAgentAdvice dataConfigurationAdvice(FailureDiagnosis diagnosis) {
        return new AiAgentAdvice(
                "The failure points to missing or invalid framework data/configuration.",
                "Test data/configuration resources",
                "Validate feature keys, properties files, Maven flags and environment variables.",
                "Low: fixing config is usually isolated, but credentials/test data must be handled carefully.",
                "No. Configuration errors should fail fast.",
                true
        );
    }

    private AiAgentAdvice functionalAdvice(FailureDiagnosis diagnosis) {
        return new AiAgentAdvice(
                "The application result did not match the expected assertion.",
                probablePageFile(diagnosis),
                "Review the expected behavior, test data and business rule before changing automation code.",
                "Medium/High: this can be a real product defect.",
                "No. Functional assertions should not be retried automatically.",
                true
        );
    }

    private AiAgentAdvice flakyAdvice(FailureDiagnosis diagnosis) {
        return new AiAgentAdvice(
                "The failure has signs of instability, but needs history before being confirmed as flaky.",
                probablePageFile(diagnosis),
                "Review waits, DOM refreshes and CI timing. Track recurrence before applying broad fixes.",
                "Medium: treating real defects as flaky can hide quality issues.",
                "Yes, once in CI, only for technical/transient errors.",
                true
        );
    }

    private AiAgentAdvice unknownAdvice(FailureDiagnosis diagnosis) {
        return new AiAgentAdvice(
                "The failure does not match a known pattern yet.",
                probablePageFile(diagnosis),
                "Review stack trace, screenshot and logs manually before changing code.",
                "Unknown: more evidence is needed.",
                "No, unless a human confirms it is transient.",
                true
        );
    }

    private String probablePageFile(FailureDiagnosis diagnosis) {
        if ("Not detected".equals(diagnosis.pageObject())) {
            return "Not detected";
        }

        return DiagnosticsSettings.pageObjectFilePath(diagnosis.pageObject());
    }
}
