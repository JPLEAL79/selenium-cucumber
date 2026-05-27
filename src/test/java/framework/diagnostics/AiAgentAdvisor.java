package framework.diagnostics;

/**
 * Contrato del agente QA asistido.
 * Hoy se implementa localmente; luego puede conectarse a OpenAI, Ollama u otro proveedor.
 */
public interface AiAgentAdvisor {

    AiAgentAdvice advise(String scenarioName, FailureDiagnosis diagnosis);
}
