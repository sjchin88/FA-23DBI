package main;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class LogWriter implements Runnable {

    private BlockingQueue<String> queue;
    private final String FILE_NAME = "load_test_logs_aws_go_3.csv";

    public LogWriter(BlockingQueue<String> q) {
        this.queue = q;
    }

    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(
            new FileWriter(FILE_NAME, true))) {
            while (true) {
                String logEntry = queue.take(); // Blocks until log entry is available
                
                // Check for the sentinel value to exit the loop
                if ("STOP_LOG_WRITER".equals(logEntry)) {
                    // Optionally, you can perform cleanup tasks here
                    break; // Exit the loop and stop the thread
                }
                
                writer.println(logEntry);
                writer.flush();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
