package framework.junit.extension;

import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;

/**
 * Extensión JUnit 5 para reintentar pruebas fallidas en CI.
 *
 * <p>Reglas:
 * <ul>
 *   <li>Máximo 1 retry</li>
 *   <li>Activo solo en CI (-DCI=true)</li>
 *   <li>Retry solo para errores técnicos</li>
 *   <li>Errores funcionales fallan inmediatamente</li>
 * </ul>
 */
public class RetryExtension implements InvocationInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RetryExtension.class);
    private static final int MAX_RETRIES = 1;

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {

        if (!isCiExecution()) {
            invocation.proceed();
            return;
        }

        int attempt = 0;

        while (true) {
            try {
                invocation.proceed();
                return;
            } catch (Throwable throwable) {

                // Error funcional → nunca retry
                if (throwable instanceof AssertionError) {
                    logger.error(
                            "[CI][FAIL] Functional error, no retry: {}",
                            extensionContext.getDisplayName()
                    );
                    throw throwable;
                }

                // Error técnico → retry controlado
                if (isTechnicalError(throwable) && attempt < MAX_RETRIES) {
                    attempt++;
                    logger.warn(
                            "[CI][RETRY] Technical error, retrying test: {} (attempt {}/{})",
                            extensionContext.getDisplayName(),
                            attempt,
                            MAX_RETRIES
                    );
                    continue;
                }

                // Sin retries restantes
                logger.error(
                        "[CI][FAIL] Test failed after retries: {}",
                        extensionContext.getDisplayName()
                );
                throw throwable;
            }
        }
    }

    /**
     * Determina si el error corresponde a una falla técnica/transitoria.
     */
    private boolean isTechnicalError(Throwable throwable) {
        return throwable instanceof WebDriverException;
    }

    /**
     * Verifica si la ejecución corresponde a CI.
     */
    private boolean isCiExecution() {
        return "true".equalsIgnoreCase(System.getProperty("CI"));
    }
}
