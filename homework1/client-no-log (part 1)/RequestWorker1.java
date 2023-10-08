import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

class RequestWorker1 implements Runnable {
    private final String serverUrl;
    private final CountDownLatch latch;
    private final int tasksPerThread;
    //private BlockingQueue<String> queue;

    public RequestWorker1(String serverUrl, CountDownLatch latch, int count) {
        this.serverUrl = serverUrl;
        this.latch = latch;
        this.tasksPerThread = count;
    }

    private String getHttpResponse(InputStream inputStream) {

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                content.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    private String getLogEntry(Instant time, long start, long end, String type, int responseCode) {
        long latency = end - start;
        return time + ", " + type + ", " + latency + ", " + responseCode + ";";
    }

    @Override
    public void run() {
        for (int task = 0; task < this.tasksPerThread; task++) {
            final int MAX_RETRIES = 5;

            try {
                HttpURLConnection postConnection;

                // Perform POST request
                for (int i = 0; i < MAX_RETRIES; i++) {

                    Instant postStartTimestamp = Instant.now();
                    long postStartTime = System.currentTimeMillis();

                    URL postUrl = new URL(serverUrl);
                    postConnection = (HttpURLConnection) postUrl.openConnection();
                    postConnection.setRequestMethod("POST");
                    postConnection.setRequestProperty("Content-Type", "application/json");
                    postConnection.setDoOutput(true);

                    String postData = "{\"albumID\": \"123\", \"imageUrl\": \"http://example.com/image.jpg\"}";

                    try (OutputStream os = postConnection.getOutputStream()) {
                        byte[] input = postData.getBytes("utf-8");
                        os.write(input, 0, input.length);

                        int postResponseCode = postConnection.getResponseCode();

                        if (postResponseCode == HttpURLConnection.HTTP_OK) {

                            long postHTTPTime = System.currentTimeMillis();

                            String jsonPostResponse = getHttpResponse(postConnection.getInputStream());
                            i = 100;
                            break;
                        } else {
                            throw new ConnectException("Http POST not OK. Status code: " + postResponseCode);
                        }

                    } catch (ConnectException | BindException e) {
                        if (i == 4) {
                            System.out.println("POST Connection failed after max retry.");
                            throw new ConnectException();
                        } else {
                            System.out.println("POST Connection failed; Retrying.. " + i);
                        }
                    } finally {
                        postConnection.disconnect();
                    }
                }

                // Perform GET request
                for (int i = 0; i < MAX_RETRIES; i++) {
                    Instant getStartTimestamp = Instant.now();
                    long getStartTime = System.currentTimeMillis();

                    URL getUrl = new URL(serverUrl + "/123"); // assuming 123 is the albumID
                    HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
                    getConnection.setRequestMethod("GET");

                    try {
                        int getResponseCode = getConnection.getResponseCode();
                        

                        if (getResponseCode == HttpURLConnection.HTTP_OK) {
                            long getHTTPTime = System.currentTimeMillis();

                            // Read and process the GET response if needed
                            String jsonResponse = getHttpResponse(getConnection.getInputStream());
                            i = 100;
                            break;
                        } else {
                            throw new ConnectException("Http GET not OK. Status code: " + getResponseCode);
                        }

                    } catch (ConnectException e) {
                        if (i == 4) {
                            System.out.println("POST Connection failed after max retry.");
                            throw new ConnectException();
                        } else {
                            System.out.println("POST Connection failed; Retrying.. " + i);
                        }
                    } finally {
                        getConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Signal that this thread has completed
                latch.countDown();
            }
        }
    }
}
