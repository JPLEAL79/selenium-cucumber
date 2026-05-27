package framework.diagnostics;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clasificador por reglas.
 * Esta es la base segura antes de conectar cualquier agente IA.
 */
public class FailureClassifier {

    private static final Pattern LOCATOR_PATTERN =
            Pattern.compile("(?i)(By\\.(?:id|xpath|cssSelector|className|name|tagName|linkText|partialLinkText):\\s*[^\\n\\r]+)");

    public FailureDiagnosis classify(Throwable throwable) {
        Throwable root = StackTraceAnalyzer.rootCause(throwable);
        String message = fullMessage(throwable);
        String normalizedMessage = message.toLowerCase();
        String pageObject = StackTraceAnalyzer.findPageObject(throwable);
        String method = StackTraceAnalyzer.findPageObjectMethod(throwable);
        String locator = extractLocator(message);
        String rootException = root.getClass().getSimpleName();

        if (isGridOrSelenium(throwable, normalizedMessage)) {
            return diagnosis(
                    FailureCategory.GRID_SELENIUM,
                    DiagnosisConfidence.HIGH,
                    "Selenium Grid or browser session failed.",
                    pageObject,
                    method,
                    locator,
                    rootException,
                    message,
                    "Validate Grid URL, browser nodes, Docker containers and browser capabilities.",
                    true
            );
        }

        if (isEnvironment(throwable, normalizedMessage)) {
            return diagnosis(
                    FailureCategory.ENVIRONMENT,
                    DiagnosisConfidence.MEDIUM,
                    "The target application or network environment looks unavailable.",
                    pageObject,
                    method,
                    locator,
                    rootException,
                    message,
                    "Validate the application URL, network, DNS and environment health.",
                    true
            );
        }

        if (isDataOrConfiguration(throwable, normalizedMessage)) {
            return diagnosis(
                    FailureCategory.DATA_CONFIGURATION,
                    DiagnosisConfidence.HIGH,
                    "Missing or invalid test data/configuration.",
                    pageObject,
                    method,
                    locator,
                    rootException,
                    message,
                    "Validate properties, feature keys, Maven flags and environment variables.",
                    false
            );
        }

        if (isLocatorBroken(throwable, locator, pageObject)) {
            return diagnosis(
                    FailureCategory.LOCATOR_BROKEN,
                    locator.equals("Not detected") ? DiagnosisConfidence.MEDIUM : DiagnosisConfidence.HIGH,
                    "Element was not found or was not ready before the explicit wait ended.",
                    pageObject,
                    method,
                    locator,
                    rootException,
                    message,
                    "Review the Page Object locator against the current DOM. Do not auto-change code.",
                    false
            );
        }

        if (hasCause(throwable, TimeoutException.class)) {
            return diagnosis(
                    FailureCategory.TIMEOUT,
                    DiagnosisConfidence.MEDIUM,
                    "The application or browser operation exceeded the expected wait time.",
                    pageObject,
                    method,
                    locator,
                    rootException,
                    message,
                    "Validate application response time and waits. Retry only if the issue is transient.",
                    true
            );
        }

        if (hasCause(throwable, AssertionError.class) || root.getClass().getName().contains("Assertion")) {
            return diagnosis(
                    FailureCategory.FUNCTIONAL_ASSERTION,
                    DiagnosisConfidence.HIGH,
                    "The application behavior did not match the expected assertion.",
                    pageObject,
                    method,
                    locator,
                    rootException,
                    message,
                    "Review expected result, business rule and test data. Do not retry automatically.",
                    false
            );
        }

        if (hasCause(throwable, StaleElementReferenceException.class)) {
            return diagnosis(
                    FailureCategory.FLAKY_CANDIDATE,
                    DiagnosisConfidence.MEDIUM,
                    "The DOM changed while Selenium was interacting with the element.",
                    pageObject,
                    method,
                    locator,
                    rootException,
                    message,
                    "Review waits and page stability. Retry can be considered once, only in CI.",
                    true
            );
        }

        return diagnosis(
                FailureCategory.UNKNOWN,
                DiagnosisConfidence.LOW,
                "The failure does not match a known deterministic pattern yet.",
                pageObject,
                method,
                locator,
                rootException,
                message,
                "Review stack trace and evidence manually before changing code.",
                false
        );
    }

    private boolean isLocatorBroken(Throwable throwable, String locator, String pageObject) {
        return hasCause(throwable, NoSuchElementException.class)
                || (hasCause(throwable, TimeoutException.class) && !"Not detected".equals(pageObject))
                || (!"Not detected".equals(locator) && hasCause(throwable, WebDriverException.class));
    }

    private boolean isGridOrSelenium(Throwable throwable, String message) {
        return hasCause(throwable, SessionNotCreatedException.class)
                || message.contains("unable to create session")
                || message.contains("session not created")
                || message.contains("no such session")
                || message.contains("cannot reach")
                || message.contains("connection refused")
                || message.contains("selenium-hub");
    }

    private boolean isEnvironment(Throwable throwable, String message) {
        return hasCause(throwable, WebDriverException.class)
                && (message.contains("net::")
                || message.contains("dns")
                || message.contains("timeout")
                || message.contains("refused")
                || message.contains("unreachable"));
    }

    private boolean isDataOrConfiguration(Throwable throwable, String message) {
        return hasCause(throwable, IllegalArgumentException.class)
                || message.contains("properties not found")
                || message.contains("not loaded")
                || message.contains("webdriver was not initialized")
                || message.contains("configuration");
    }

    private String extractLocator(String message) {
        Matcher matcher = LOCATOR_PATTERN.matcher(message);
        return matcher.find() ? matcher.group(1).trim() : "Not detected";
    }

    private boolean hasCause(Throwable throwable, Class<? extends Throwable> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private String fullMessage(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        Throwable current = throwable;

        while (current != null) {
            builder.append(current.getClass().getSimpleName());
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                builder.append(": ").append(current.getMessage());
            }
            builder.append(System.lineSeparator());
            current = current.getCause();
        }

        return builder.toString();
    }

    private FailureDiagnosis diagnosis(FailureCategory category,
                                       DiagnosisConfidence confidence,
                                       String probableCause,
                                       String pageObject,
                                       String method,
                                       String locator,
                                       String rootException,
                                       String evidence,
                                       String suggestedAction,
                                       boolean retryRecommended) {
        return new FailureDiagnosis(
                category,
                confidence,
                probableCause,
                pageObject,
                method,
                locator,
                rootException,
                sanitize(evidence),
                suggestedAction,
                retryRecommended,
                true
        );
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "No exception message available.";
        }
        return value.length() > 1500 ? value.substring(0, 1500) + "..." : value;
    }
}
