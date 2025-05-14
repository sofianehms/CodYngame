import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class PythonExecutor implements LanguageExecutor {

    @Override
    public ExecutionResult execute(String userCode, String input) {
        try {
            // 1. Créer un fichier temporaire pour le code utilisateur
            Path tempFile = Files.createTempFile("user_code_", ".py");
            Files.write(tempFile, userCode.getBytes());

            // 2. Préparer le processus d'exécution avec Python
            ProcessBuilder builder = new ProcessBuilder("python3", tempFile.toAbsolutePath().toString());
            Process process = builder.start();

            // 3. Injecter l'entrée standard (STDIN)
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(input);
                writer.flush();
            }

            // 4. Lire la sortie standard (STDOUT)
            String output = readStream(process.getInputStream());

            // 5. Lire la sortie d'erreur (STDERR)
            String error = readStream(process.getErrorStream());

            // 6. Attendre la fin de l'exécution (timeout sécurité)
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return new ExecutionResult("", "Timeout: le programme est trop lent ou tourne en boucle", false);
            }

            boolean success = process.exitValue() == 0 && error.isEmpty();

            // 7. Nettoyage du fichier temporaire
            Files.deleteIfExists(tempFile);

            return new ExecutionResult(output, error, success);

        } catch (IOException | InterruptedException e) {
            return new ExecutionResult("", "Erreur interne : " + e.getMessage(), false);
        }
    }

    @Override
    public String getBoilerplateCode() {
        return "a, b = map(int, input().split())\nprint(a + b)";
    }

    @Override
    public String getFileExtension() {
        return ".py";
    }

    // Méthode utilitaire pour lire un flux (stdout ou stderr)
    private String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }
}
