package commons;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Utils {

    // Timeout para esperas de elementos UI (render / visibilidad / click)
    private static final Duration UI_TIMEOUT = Duration.ofSeconds(30);

    // Timeout para cargas de navegación (page load / redirects)
    private static final Duration NAVIGATION_TIMEOUT = Duration.ofSeconds(60);

    /**
     * Crea un WebDriverWait seguro y desacoplado del Hook.
     * Se utiliza para esperas relacionadas con elementos UI.
     */
    public static WebDriverWait uiWait(WebDriver driver) {
        return new WebDriverWait(driver, UI_TIMEOUT);
    }

    /**
     * Crea un WebDriverWait específico para navegación.
     * Se utilizará cuando validemos cargas completas de página.
     */
    public static WebDriverWait navigationWait(WebDriver driver) {
        return new WebDriverWait(driver, NAVIGATION_TIMEOUT);
    }

    /**
     * Navega a una URL y espera que el documento esté completamente cargado.
     * 1. Ejecuta driver.get(url)
     * 2. Espera hasta que el navegador indique que el DOM terminó de cargarse
     *    usando document.readyState === "complete"
     * Esto reduce flakiness en entornos lentos como AWS.
     */
    public static void openUrl(WebDriver driver, String url) {
        driver.get(url);
        // Espera explícita hasta que el navegador confirme
        // que la página terminó de cargarse completamente.
        navigationWait(driver).until(webDriver ->
                // Cast a JavascriptExecutor para ejecutar JS en el navegador
                ((org.openqa.selenium.JavascriptExecutor) webDriver)
                        // Ejecuta JavaScript dentro del browser
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
    }
}
