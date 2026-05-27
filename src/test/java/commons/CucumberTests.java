package commons;

import framework.junit.extension.RetryExtension;
import io.cucumber.junit.platform.engine.Constants;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Cucumber test runner using JUnit 5 Platform.
 * Punto de entrada central para ejecutar escenarios BDD.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ExtendWith(RetryExtension.class)
@ConfigurationParameter(
        key = Constants.GLUE_PROPERTY_NAME,
        value = "definitions"
)
@ConfigurationParameter(
        key = Constants.PLUGIN_PROPERTY_NAME,
        value = "pretty,io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm,framework.diagnostics.CucumberFailureDiagnosticsPlugin"
)
public class CucumberTests {
    // Runner central de Cucumber (sin lógica adicional)
}
