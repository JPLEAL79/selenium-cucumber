package definitions;

import commons.ScreenshotUtil;
import framework.diagnostics.EvidenceCollector;
import framework.diagnostics.AllureDiagnosticsReporter;
import framework.diagnostics.FailureDiagnosis;
import framework.diagnostics.FailureDiagnosisStore;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Hooks ejecutados ANTES y DESPUÉS de cada escenario.
 * Responsabilidades:
 * - Inicializar un WebDriver por escenario
 * - Resolver browser, grid y headless vía flags
 * - Capturar evidencia solo en fallos
 * - Cerrar correctamente el driver
 * REGLAS ENTERPRISE:
 * - NO maneja paralelismo de manera local
 * - El paralelismo vive SOLO en Jenkins (AWS)
 * - Local y Jenkins local: ejecución secuencial
 */
public class Hooks {

    // Logger del framework (SLF4J)
    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);

    // WebDriver aislado por hilo/escenario
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return Objects.requireNonNull(
                DRIVER.get(),
                "WebDriver was not initialized. Verify that the Cucumber @Before hook ran before using page objects."
        );
    }

    /**
     * Resuelve la URL del Selenium Grid.
     * Prioridad:
     * 1) -DseleniumGridUrl
     * 2) Variable de entorno SELENIUM_GRID_URL
     * 3) Fallback local explícito
     */
    private static String resolveGridUrl() {

        String fromProp = System.getProperty("seleniumGridUrl");
        if (fromProp != null && !fromProp.isBlank()) {
            return fromProp;
        }

        String fromEnv = System.getenv("SELENIUM_GRID_URL");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }

        return "http://localhost:4444/wd/hub";
    }

    /**
     * Determina si la ejecución es headless.
     * Default: true
     */
    private static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("headless", "true"));
    }

    @Before
    public void setUp(Scenario scenario) throws MalformedURLException {

        logger.info("Starting scenario: {}", scenario.getName());
        String gridUrl = resolveGridUrl();
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean headless = isHeadless();
        logger.info("Selenium Grid URL: {}", gridUrl);
        logger.info("Browser: {}", browser);
        logger.info("Headless: {}", headless);

        if ("firefox".equals(browser)) {

            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.setAcceptInsecureCerts(true);

            if (headless) {
                firefoxOptions.addArguments("-headless");
            }

            firefoxOptions.addArguments(
                    "--width=1920",
                    "--height=1080"
            );

            DRIVER.set(new RemoteWebDriver(
                    new URL(gridUrl),
                    firefoxOptions
            ));

        } else {

            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setAcceptInsecureCerts(true);

            Map<String, Object> prefs = new HashMap<>();
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            chromeOptions.setExperimentalOption("prefs", prefs);

            if (headless) {
                chromeOptions.addArguments("--headless=new");
            }

            chromeOptions.addArguments(
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu",
                    "--disable-extensions",
                    "--disable-popup-blocking",
                    "--window-size=1920,1080"
            );

            DRIVER.set(new RemoteWebDriver(
                    new URL(gridUrl),
                    chromeOptions
            ));
        }

    }

    /**
     * Se ejecuta después de cada escenario.
     * Captura screenshot cuando el escenario NO terminó en PASSED.
     */
    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DRIVER.get();

        try {

            if (!scenario.getStatus().toString().equalsIgnoreCase("PASSED")) {

                logger.error("Scenario finished with status {}: {}",
                        scenario.getStatus(),
                        scenario.getName()
                );

                ScreenshotUtil.captureOnFailure(driver, scenario);
                EvidenceCollector.attachBrowserEvidence(driver, scenario);
                attachAiAssistedDiagnosis(scenario);

            } else {

                logger.info("Scenario finished successfully: {}", scenario.getName());
            }

        } finally {

            if (driver != null) {
                driver.quit();
                logger.info("WebDriver closed correctly");
            }

            DRIVER.remove();
        }
    }

    /**
     * Adjunta el diagnostico asistido generado por el plugin de Cucumber.
     * Si no existe diagnostico, no falla el teardown ni oculta el error real.
     */
    private void attachAiAssistedDiagnosis(Scenario scenario) {
        FailureDiagnosis diagnosis = FailureDiagnosisStore.consume(scenario.getName());

        if (diagnosis == null) {
            logger.warn("No AI-assisted diagnosis available for scenario: {}", scenario.getName());
            return;
        }

        AllureDiagnosticsReporter.attachFailureDiagnosis(scenario.getName(), diagnosis);
    }
}
