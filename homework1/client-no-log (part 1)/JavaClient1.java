import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

public class JavaClient1 {
    public static final String serverUrlGo = "http://127.0.0.1:8080/albums";
    public static final String serverUrlJava = "http://localhost:8081/cs6650hw1daniel/albums";
    public static final String serverUrlGoAws = "http://ec2-18-236-79-92.us-west-2.compute.amazonaws.com:8080/albums";
    public static final String serverUrlJavaAws = "http://ec2-18-236-79-92.us-west-2.compute.amazonaws.com:8080/cs6650hw1daniel_ec2/albums";
    
    private static Thread logWriterThread;
    private static BlockingQueue<String> queue = new LinkedBlockingDeque<>();

    public static void serverExecute(int tasksPerThread, int threadPerGroup, int groupCount, String serverUrl,
            int delay)
            throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(tasksPerThread * threadPerGroup * groupCount);

        Thread[][] threads = new Thread[groupCount][threadPerGroup];

        for (int group = 0; group < groupCount; group++) {
            for (int i = 0; i < threadPerGroup; i++) {
                threads[group][i] = new Thread(new RequestWorker1(serverUrl, latch, tasksPerThread));
                threads[group][i].start();
            }
            System.out.println("Starting thread group " + (group + 1));

            Thread.sleep(delay * 1000);
        }

        latch.await();
    }

    public static void main1(String[] args) throws InterruptedException {

        int tasksPerThread = 1000; // tasks per thread
        int threadGroupSize = 10; // count of threads per group
        int numThreadGroups = 30; // count of group for the execution
        int delay = 2;

        String serverUrl = serverUrlGoAws;

        // warm up the server
        // long startTime = System.currentTimeMillis();
        System.out.println("Warming up server...");
        serverExecute(100, 10, 1, serverUrl, 0); //2000

        // start the timer and actual load test
        System.out.println("Starting load test...");
        long startTime = System.currentTimeMillis();
        serverExecute(tasksPerThread, threadGroupSize, numThreadGroups, serverUrl, delay);

        // All threads have completed
        long endTime = System.currentTimeMillis(); // Get the end time
        long elapsedTime = endTime - startTime;

        // queue.put("STOP_LOG_WRITER");
        System.out.println("Test completed successfully. Total time: " + elapsedTime + "ms");
    }
}
