package data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Resolves logical purchase keys to real values.
 */
public final class PurchaseDataResolver {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is =
                     Thread.currentThread()
                             .getContextClassLoader()
                             .getResourceAsStream("features/properties/purchase.properties")) {

            if (is == null) {
                throw new RuntimeException(
                        "purchase.properties not found in classpath"
                );
            }

            PROPS.load(is);

        } catch (IOException e) {
            throw new RuntimeException("Purchase data not loaded", e);
        }
    }

    private PurchaseDataResolver() {
        // Utility class
    }

    public static String resolve(String key) {
        return PROPS.getProperty(key, "");
    }
}
