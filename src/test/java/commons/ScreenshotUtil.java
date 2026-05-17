package commons;

import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {

    // Carpeta base para evidencias (preparado para futura integración S3)
    private static final String SCREENSHOT_DIR = "target/screenshots/";

    // Formato estándar enterprise para timestamp
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Captura screenshot únicamente cuando el escenario falla.
     * Compatible con ejecución paralela (AWS + Jenkins).
     */
    public static void captureOnFailure(WebDriver driver, Scenario scenario) {

        if (driver == null) {
            return;
        }

        if (!(driver instanceof TakesScreenshot)) {
            return;
        }

        try {
            // Validamos estado dentro del bloque try para asegurar que
            // el escenario ya esté marcado como failed por Cucumber
            if (!scenario.isFailed()) {
                return;
            }

            System.out.println("[ScreenshotUtil] Scenario failed, taking screenshot: " + scenario.getName());

            byte[] screenshot =
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            String safeName = scenario.getName()
                    .replaceAll("[^a-zA-Z0-9-_\\.]", "_");

            String browser = System.getProperty("browser", "unknown");

            String timestamp = LocalDateTime.now().format(FORMATTER);

            String fileName = SCREENSHOT_DIR
                    + safeName + "_"
                    + browser + "_"
                    + timestamp + ".png";

            File destFile = new File(fileName);
            destFile.getParentFile().mkdirs();

            Files.write(destFile.toPath(), screenshot);

            Allure.addAttachment(
                    "Screenshot - " + scenario.getName() + " [" + browser + "]",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    "png"
            );

        } catch (Exception e) {
            System.out.println("[ScreenshotUtil] Could not capture screenshot: " + e.getMessage());
        }
    }
}
