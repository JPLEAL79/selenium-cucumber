package data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * LoginDataResolver
 * Responsabilidad:
 * - Resolver keys lógicas (definidas en los .feature)
 * - Centraliza el acceso a credenciales de prueba
 * - Evita datos sensibles visibles en los features
 */
public final class LoginDataResolver {


     // Contenedor de propiedades cargadas desde el classpath.
    private static final Properties PROPS = new Properties();

    // Carga estática de las credenciales de login.
    // Se ejecuta una sola vez al cargar la clase.
    static {
        try (InputStream is =
                     LoginDataResolver.class
                             .getClassLoader()
                             .getResourceAsStream("features/properties/auth.properties")) {

            // Validación explícita para evitar NullPointerException silencioso
            if (is == null) {
                throw new RuntimeException(
                        "auth.properties not found in classpath"
                );
            }

            // Carga las propiedades en memoria
            PROPS.load(is);

        } catch (IOException e) {
            // Falla rápida y clara si no se pueden cargar los datos
            throw new RuntimeException("Login credentials not loaded", e);
        }
    }

    /**
     * Constructor privado.
     * Evita instanciación: esta clase es solo utilitaria.
     */
    private LoginDataResolver() {
        // No instanciable
    }

    /**
     * Resuelve una key lógica a su valor real.
     * @param key key lógica definida en el feature (ej: USER_OK, PASS_OK)
     * @return valor real asociado o string vacío si no existe
     */
    public static String resolve(String key) {
        // Devuelve vacío cuando la key representa un campo intencionalmente vacío
        return PROPS.getProperty(key, "");
    }
}

