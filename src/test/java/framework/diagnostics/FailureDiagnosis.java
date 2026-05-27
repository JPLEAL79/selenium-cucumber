package framework.diagnostics;

/**
 * Resultado final del analisis asistido.
 * Es deliberadamente simple para mantener el MVP entendible y mantenible.
 */
public class FailureDiagnosis {

    private final FailureCategory category;
    private final DiagnosisConfidence confidence;
    private final String probableCause;
    private final String pageObject;
    private final String method;
    private final String locator;
    private final String rootException;
    private final String evidence;
    private final String suggestedAction;
    private final boolean retryRecommended;
    private final boolean humanReviewRequired;

    public FailureDiagnosis(FailureCategory category,
                            DiagnosisConfidence confidence,
                            String probableCause,
                            String pageObject,
                            String method,
                            String locator,
                            String rootException,
                            String evidence,
                            String suggestedAction,
                            boolean retryRecommended,
                            boolean humanReviewRequired) {
        this.category = category;
        this.confidence = confidence;
        this.probableCause = probableCause;
        this.pageObject = pageObject;
        this.method = method;
        this.locator = locator;
        this.rootException = rootException;
        this.evidence = evidence;
        this.suggestedAction = suggestedAction;
        this.retryRecommended = retryRecommended;
        this.humanReviewRequired = humanReviewRequired;
    }

    public FailureCategory category() {
        return category;
    }

    public DiagnosisConfidence confidence() {
        return confidence;
    }

    public String probableCause() {
        return probableCause;
    }

    public String pageObject() {
        return pageObject;
    }

    public String method() {
        return method;
    }

    public String locator() {
        return locator;
    }

    public String rootException() {
        return rootException;
    }

    public String evidence() {
        return evidence;
    }

    public String suggestedAction() {
        return suggestedAction;
    }

    public boolean retryRecommended() {
        return retryRecommended;
    }

    public boolean humanReviewRequired() {
        return humanReviewRequired;
    }
}
