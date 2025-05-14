public class ExecutionResult {
    public final String output;
    public final String error;
    public final boolean success;

    public ExecutionResult(String output, String error, boolean success) {
        this.output = output;
        this.error = error;
        this.success = success;
    }
}
