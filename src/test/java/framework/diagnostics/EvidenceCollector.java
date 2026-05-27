package framework.diagnostics;

import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Recolecta evidencia segura del navegador antes de cerrar el driver.
 * No captura datos sensibles ni page source completo en este MVP.
 */
public final class EvidenceCollector {

    private EvidenceCollector() {
        // Utility class
    }

    public static void attachBrowserEvidence(WebDriver driver, Scenario scenario) {
        if (driver == null || !scenario.isFailed()) {
            return;
        }

        String currentUrl = safeRead(driver::getCurrentUrl);
        String title = safeRead(driver::getTitle);

        String evidence = """
                Browser Evidence

                Scenario: %s
                Browser: %s
                Grid URL: %s
                Current URL: %s
                Page title: %s
                """.formatted(
                scenario.getName(),
                System.getProperty("browser", "unknown"),
                System.getProperty("seleniumGridUrl", System.getenv().getOrDefault("SELENIUM_GRID_URL", "http://localhost:4444/wd/hub")),
                currentUrl,
                title
        );

        Allure.addAttachment(
                "Browser Evidence",
                "text/plain",
                new ByteArrayInputStream(evidence.getBytes(StandardCharsets.UTF_8)),
                "txt"
        );
    }

    private static String safeRead(ValueSupplier supplier) {
        try {
            String value = supplier.get();
            return value == null || value.isBlank() ? "Not available" : value;
        } catch (Exception e) {
            return "Not available: " + e.getClass().getSimpleName();
        }
    }

    @FunctionalInterface
    private interface ValueSupplier {
        String get();
    }
}
