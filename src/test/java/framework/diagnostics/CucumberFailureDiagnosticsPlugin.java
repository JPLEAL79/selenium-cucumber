package framework.diagnostics;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestStepFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plugin Cucumber que escucha el fallo real del escenario.
 * Esta es la pieza que acerca el framework a un agente observador supervisado.
 */
public class CucumberFailureDiagnosticsPlugin implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(CucumberFailureDiagnosticsPlugin.class);
    private final FailureClassifier classifier = new FailureClassifier();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
    }

    private void handleTestStepFinished(TestStepFinished event) {
        if (!DiagnosticsSettings.aiDiagnosticsEnabled()) {
            return;
        }

        Throwable error = event.getResult().getError();

        if (error == null) {
            return;
        }

        String scenarioName = event.getTestCase().getName();
        FailureDiagnosis diagnosis = classifier.classify(error);

        logger.error(
                "[AI-Assisted Diagnosis] scenario='{}', category={}, confidence={}, action='{}'",
                scenarioName,
                diagnosis.category(),
                diagnosis.confidence(),
                diagnosis.suggestedAction()
        );

        FailureDiagnosisStore.save(scenarioName, diagnosis);
    }
}
