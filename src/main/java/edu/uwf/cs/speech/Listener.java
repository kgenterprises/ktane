package edu.uwf.cs.speech;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1beta1.*;
import com.google.cloud.speech.v1beta1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;
import edu.uwf.cs.ktane.game.KtaneGame;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Client that sends streaming audio to Speech.Recognize and returns streaming transcript.
 */
public class Listener implements Runnable {

    private static final Logger LOG = LogManager.getLogger(Listener.class);
    private static final String CREDENTIALS_FILE = "GoogleSpeechCredentials.json";

    private static final String HOST = "speech.googleapis.com";
    private static final Integer PORT = 443;
    private static final Integer SAMPLING_RATE = 16000;

    private final ManagedChannel channel;
    private final SpeechGrpc.SpeechStub speechClient;
    private static final List<String> OAUTH2_SCOPES = Arrays.asList("https://www.googleapis.com/auth/cloud-platform");

    static final int BYTES_PER_SAMPLE = 2; // bytes per sample for LINEAR16

    final int bytesPerBuffer; // buffer size in bytes

    private KtaneGame game;

    // Used for testing
    protected TargetDataLine mockDataLine = null;

    private AtomicBoolean running;

    /**
     * Construct client connecting to Cloud Speech server at {@code HOST:PORT}.
     */
    public Listener() throws IOException {

        this.channel = createChannel(HOST, PORT);
        this.bytesPerBuffer = SAMPLING_RATE * BYTES_PER_SAMPLE / 10; // 100 ms

        speechClient = SpeechGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown()
               .awaitTermination(5, TimeUnit.SECONDS);
    }

    ManagedChannel createChannel(String host, int port) throws IOException {
        getClass().getClassLoader()
            .getResourceAsStream(CREDENTIALS_FILE);
        GoogleCredentials creds = GoogleCredentials.fromStream(getClass().getClassLoader()
                                                                         .getResourceAsStream(CREDENTIALS_FILE));

        creds = creds.createScoped(OAUTH2_SCOPES);
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .intercept(new ClientAuthInterceptor(creds, Executors.newSingleThreadExecutor()))
                .build();

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

        AudioFormat format = new AudioFormat(SAMPLING_RATE, BYTES_PER_SAMPLE * 8, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new RuntimeException(String.format("Device doesn't support LINEAR16 mono raw audio format at {%d}Hz", SAMPLING_RATE));
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
                String transcript = result.getAlternatives(0)
                                          .getTranscript();
                if (result.getIsFinal()) {

                    LOG.info("Raw response: " + transcript);

                    game.processResponse(transcript);
                }
            }

            @Override
            public void onError(Throwable error) {
                // ignore 60 second timeout according to: https://cloud.google.com/speech/limits
                if (!(error instanceof StatusRuntimeException) && !error.getMessage().contains("Client GRPC deadline too short")) {
                    LOG.info("GetRecognition failed: {0}", error);
                    running.getAndSet(false);
                }
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                LOG.info("GetRecognition completed.");
                finishLatch.countDown();
                running.getAndSet(false);
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
                    .setSampleRate(SAMPLING_RATE)
                    .setSpeechContext(SpeechContext
                            .newBuilder()
                            .addPhrases("bomb description battery FRK parallel PORT wires button key one two three four five six")  // TODO space delimited? multiple phrases
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
        finishLatch.await(2, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        LOG.info("Starting listener client");
        running = new AtomicBoolean(true);
        try {
            while(running.get()) {
                getRecognition();
                LOG.info("Resetting connection with GCP");
            }
        } catch (Exception e) {
            LOG.info("An unexpected error occurred: " + e.toString());
        }
    }

    public void setGame(KtaneGame game) {
        this.game = game;
    }
}
