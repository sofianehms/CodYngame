public interface LanguageExecutor {
    ExecutionResult execute(String userCode, String input);
    String getBoilerplateCode();
    String getFileExtension();
}
