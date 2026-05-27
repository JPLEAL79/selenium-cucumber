package framework.diagnostics;

/**
 * Resultado final del analisis asistido.
 * Es deliberadamente simple para mantener el MVP entendible y mantenible.
 */
public class FailureDiagnosis {

    public enum Category {
        LOCATOR_BROKEN,
        TIMEOUT,
        ENVIRONMENT,
        GRID_SELENIUM,
        DATA_CONFIGURATION,
        FLAKY_CANDIDATE,
        FUNCTIONAL_ASSERTION,
        UNKNOWN
    }

    public enum Confidence {
        HIGH,
        MEDIUM,
        LOW
    }

    private final Category category;
    private final Confidence confidence;
    private final String probableCause;
    private final String pageObject;
    private final String method;
    private final String locator;
    private final String rootException;
    private final String evidence;
    private final String suggestedAction;
    private final boolean retryRecommended;
    private final boolean humanReviewRequired;

    public FailureDiagnosis(Category category,
                            Confidence confidence,
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

    public Category category() {
        return category;
    }

    public Confidence confidence() {
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
