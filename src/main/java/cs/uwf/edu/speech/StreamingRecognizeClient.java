package cs.uwf.edu.speech;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1beta1.SpeechContext;
import com.google.cloud.speech.v1beta1.SpeechGrpc;
import com.google.cloud.speech.v1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;
import cs.uwf.edu.ktane.game.KtaneGame;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.apache.log4j.ConsoleAppender.SYSTEM_OUT;


/**
 * Client that sends streaming audio to Speech.Recognize and returns streaming transcript.
 */
public class StreamingRecognizeClient implements Runnable {

    private static final Logger logger = Logger.getLogger(StreamingRecognizeClient.class.getName());
    // TODO bring file into project
    private static final String FILENAME = "/Users/bryansolomon/Downloads/speech/grpc/src/main/resources/GoogleSpeechCredentials.json";

    private final String host = "speech.googleapis.com";
    private final Integer port = 443;
    private final Integer samplingRate = 16000;

    private final ManagedChannel channel;
    private final SpeechGrpc.SpeechStub speechClient;
    private static final List<String> OAUTH2_SCOPES = Arrays.asList("https://www.googleapis.com/auth/cloud-platform");

    static final int BYTES_PER_SAMPLE = 2; // bytes per sample for LINEAR16

    final int bytesPerBuffer; // buffer size in bytes

    private KtaneGame game;

    // Used for testing
    protected TargetDataLine mockDataLine = null;

    /**
     * Construct client connecting to Cloud Speech server at {@code host:port}.
     */
    public StreamingRecognizeClient() throws IOException {

        this.channel = createChannel(host, port);
        this.bytesPerBuffer = samplingRate * BYTES_PER_SAMPLE / 10; // 100 ms

        speechClient = SpeechGrpc.newStub(channel);

        // Send log4j logs to Console
        // If you are going to run this on GCE, you might wish to integrate with
        // google-cloud-java logging. See:
        // https://github.com/GoogleCloudPlatform/google-cloud-java/blob/master/README.md#stackdriver-logging-alpha
        ConsoleAppender appender = new ConsoleAppender(new SimpleLayout(), SYSTEM_OUT);
        logger.addAppender(appender);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    static ManagedChannel createChannel(String host, int port) throws IOException {
        //GoogleCredentials creds = GoogleCredentials.getApplicationDefault();
        File initialFile = new File(FILENAME);

        GoogleCredentials creds = GoogleCredentials.fromStream(new FileInputStream(initialFile));

        creds = creds.createScoped(OAUTH2_SCOPES);
        ManagedChannel channel =
                ManagedChannelBuilder.forAddress(host, port).intercept(new ClientAuthInterceptor(creds, Executors.newSingleThreadExecutor())).build();

        return channel;
    }

    /**
     * Return a Line to the audio input device.
     */
    private TargetDataLine getAudioInputLine() {
        // For testing
        if (null != mockDataLine) {
            return mockDataLine;
        }

        AudioFormat format = new AudioFormat(samplingRate, BYTES_PER_SAMPLE * 8, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new RuntimeException(String.format("Device doesn't support LINEAR16 mono raw audio format at {%d}Hz", samplingRate));
        }
        try {
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            // Make sure the line buffer doesn't overflow while we're filling this thread's buffer.
            line.open(format, bytesPerBuffer * 5);
            return line;
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send streaming recognize requests to server and return the converted text result
     */
    public void getRecognition() throws InterruptedException, IOException {

        final CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<StreamingRecognizeResponse> responseObserver = new StreamObserver<StreamingRecognizeResponse>() {
            private int sentenceLength = 1;

            /**
             * Prints the transcription results. Interim results are overwritten by subsequent
             * results, until a final one is returned, at which point we start a new line.
             *
             * Flags the program to exit when it hears "exit".
             */
            @Override
            public void onNext(StreamingRecognizeResponse response) {
                List<StreamingRecognitionResult> results = response.getResultsList();
                if (results.size() < 1) {
                    return;
                }

                StreamingRecognitionResult result = results.get(0);
                String transcript = result.getAlternatives(0).getTranscript();

                // Print interim results with a line feed, so subsequent transcriptions will overwrite
                // it. Final result will print a newline.
                String format = "%-" + this.sentenceLength + 's';
                if (result.getIsFinal()) {
                    format += '\n';
                    this.sentenceLength = 1;

                    if (transcript.toLowerCase().indexOf("exit") >= 0) {
                        finishLatch.countDown();
                    }
                } else {
                    format += '\r';
                    this.sentenceLength = transcript.length();
                }
                String formattedTranscript = String.format(format, transcript);
                System.out.print("Raw response: " + formattedTranscript);

                game.processResponse(formattedTranscript);

                // we can't return from this method due to superclass implementation
                // save transcript results so they can be returned outside of this method
//            recognitionResult += transcript;
            }

            @Override
            public void onError(Throwable error) {
                logger.log(Level.ERROR, "getRecognition failed: {0}", error);
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("getRecognition completed.");
                finishLatch.countDown();
            }
        };

        StreamObserver<StreamingRecognizeRequest> requestObserver = speechClient.streamingRecognize(responseObserver);

        TargetDataLine in = null;

        try {
            // Build and send a StreamingRecognizeRequest containing the parameters for
            // processing the audio.
            RecognitionConfig config = RecognitionConfig
                    .newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setSampleRate(samplingRate)
                    .setSpeechContext(SpeechContext
                            .newBuilder()
                            .addPhrases(
                                    "bomb description battery FRK parallel port wires button key one two three four five six")  // TODO space delimited? multiple phrases
                    )
                    .build();

            StreamingRecognitionConfig streamingConfig = StreamingRecognitionConfig
                    .newBuilder()
                    .setConfig(config)
                    .setInterimResults(false)
                    .setSingleUtterance(false)
                    .build();

            StreamingRecognizeRequest initial = StreamingRecognizeRequest
                    .newBuilder()
                    .setStreamingConfig(streamingConfig)
                    .build();

            requestObserver.onNext(initial);

            // Get a Line to the audio input device.
            in = getAudioInputLine();
            byte[] buffer = new byte[bytesPerBuffer];
            int bytesRead;

            in.start();
            // Read and send sequential buffers of audio as additional RecognizeRequests.
            while (finishLatch.getCount() > 0 && (bytesRead = in.read(buffer, 0, buffer.length)) != -1) {
                StreamingRecognizeRequest request = StreamingRecognizeRequest
                        .newBuilder()
                        .setAudioContent(ByteString.copyFrom(buffer, 0, bytesRead))
                        .build();

                requestObserver.onNext(request);
            }
        } catch (RuntimeException e) {
            // Cancel RPC.
            requestObserver.onError(e);
            throw e;
        } finally {
            if (in != null) {
                in.stop();
            }
        }
        // Mark the end of requests.
        requestObserver.onCompleted();

        // Receiving happens asynchronously.
        finishLatch.await(1, TimeUnit.MINUTES);

    }

    @Override
    public void run() {
        System.out.println("Starting listener client");
        StreamingRecognizeClient client = null;
        try {
            getRecognition();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.toString());
        } finally {
            try {
                client.shutdown();
            } catch (InterruptedException ie) {
                System.out.println("Failed to shutdown client: " + ie.toString());
            }
        }
    }

    public void setGame(KtaneGame game) {
        this.game = game;
    }
}
