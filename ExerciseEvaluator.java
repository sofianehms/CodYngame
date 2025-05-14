import java.util.Objects;

public class ExerciseEvaluator {

    private final LanguageExecutor referenceExecutor;
    private final LanguageExecutor userExecutor;

    public ExerciseEvaluator(LanguageExecutor referenceExecutor, LanguageExecutor userExecutor) {
        this.referenceExecutor = referenceExecutor;
        this.userExecutor = userExecutor;
    }

    public EvaluationResult evaluate(String referenceCode, String userCode, String input) {
        // 1. Exécuter le code de référence
        ExecutionResult refResult = referenceExecutor.execute(referenceCode, input);

        // Si le code de référence plante, on arrête tout
        if (!refResult.success) {
            return new EvaluationResult(false, "Erreur dans le code de référence :\n" + refResult.error, input, "", "");
        }

        // 2. Exécuter le code utilisateur
        ExecutionResult userResult = userExecutor.execute(userCode, input);

        // 3. Comparer les sorties
        boolean match = userResult.success && Objects.equals(trimmed(userResult.output), trimmed(refResult.output));

        // 4. Retourner le résultat
        return new EvaluationResult(
            match,
            match ? "Succès" : (userResult.success ? "Sortie incorrecte" : "Erreur d'exécution :\n" + userResult.error),
            input,
            refResult.output,
            userResult.output
        );
    }

    // Nettoyage des sauts de ligne et espaces superflus
    private String trimmed(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+$", "");
    }

    // Classe de retour interne
    public static class EvaluationResult {
        public final boolean success;
        public final String message;
        public final String inputUsed;
        public final String expectedOutput;
        public final String actualOutput;

        public EvaluationResult(boolean success, String message, String inputUsed, String expectedOutput, String actualOutput) {
            this.success = success;
            this.message = message;
            this.inputUsed = inputUsed;
            this.expectedOutput = expectedOutput;
            this.actualOutput = actualOutput;
        }

        @Override
        public String toString() {
            return "Success: " + success + "\n"
                + "Message: " + message + "\n"
                + "Input: " + inputUsed + "\n"
                + "Expected: " + expectedOutput + "\n"
                + "Got: " + actualOutput;
        }
    }
}
