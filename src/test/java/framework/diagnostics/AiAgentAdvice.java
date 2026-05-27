package framework.diagnostics;

/**
 * Respuesta sugerida por un agente QA supervisado.
 * Es una recomendacion revisable, no una correccion automatica.
 */
public class AiAgentAdvice {

    private final String humanExplanation;
    private final String probableImpactedFile;
    private final String suggestedFix;
    private final String risk;
    private final String retryDecision;
    private final boolean humanReviewRequired;

    public AiAgentAdvice(String humanExplanation,
                         String probableImpactedFile,
                         String suggestedFix,
                         String risk,
                         String retryDecision,
                         boolean humanReviewRequired) {
        this.humanExplanation = humanExplanation;
        this.probableImpactedFile = probableImpactedFile;
        this.suggestedFix = suggestedFix;
        this.risk = risk;
        this.retryDecision = retryDecision;
        this.humanReviewRequired = humanReviewRequired;
    }

    public String humanExplanation() {
        return humanExplanation;
    }

    public String probableImpactedFile() {
        return probableImpactedFile;
    }

    public String suggestedFix() {
        return suggestedFix;
    }

    public String risk() {
        return risk;
    }

    public String retryDecision() {
        return retryDecision;
    }

    public boolean humanReviewRequired() {
        return humanReviewRequired;
    }
}
