package com.streamverse.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/** Records a short microphone sample and transcribes it with the OpenAI Audio API. */
public final class VoiceSearchService implements AutoCloseable {
    public enum State { READY, STARTING, LISTENING, TRANSCRIBING }

    private static final String API_KEY_ENV = "OPENAI_API_KEY";
    private static final String TRANSCRIPTION_MODEL = "gpt-4o-mini-transcribe";
    private static final int SAMPLE_RATE = 16_000;
    private static final int SOURCE_RATE = 44_100;
    private static final int MAX_SECONDS = 10;

    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "voice-search");
        thread.setDaemon(true);
        return thread;
    });
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private volatile State state = State.READY;
    private volatile boolean recording;
    private volatile boolean closed;
    private volatile TargetDataLine activeLine;

    public State state() { return state; }

    public void start(Consumer<State> onState, Consumer<String> onTranscript, Consumer<String> onError) {
        if (state != State.READY) return;
        String apiKey = System.getenv(API_KEY_ENV);
        if (apiKey == null || apiKey.isBlank()) {
            dispatch(() -> onError.accept("Voice search is not configured. Set " + API_KEY_ENV + " and restart."));
            return;
        }
        recording = true;
        updateState(State.STARTING, onState);
        worker.execute(() -> captureAndTranscribe(apiKey, onState, onTranscript, onError));
    }

    public void stop() {
        recording = false;
    }

    @Override
    public void close() {
        closed = true;
        recording = false;
        TargetDataLine line = activeLine;
        if (line != null && line.isOpen()) {
            line.close();
        }
        worker.shutdownNow();
    }

    private void captureAndTranscribe(String apiKey, Consumer<State> onState,
                                      Consumer<String> onTranscript, Consumer<String> onError) {
        TargetDataLine line = null;
        try {
            AudioFormat format = new AudioFormat(SOURCE_RATE, 16, 1, true, false);
            line = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, format));
            activeLine = line;
            line.open(format);
            line.start();
            if (!recording) return;
            updateState(State.LISTENING, onState);

            int maxBytes = SOURCE_RATE * 2 * MAX_SECONDS;
            ByteArrayOutputStream audio = new ByteArrayOutputStream(maxBytes);
            byte[] buffer = new byte[4096];
            while (recording && audio.size() < maxBytes) {
                int count = line.read(buffer, 0, Math.min(buffer.length, maxBytes - audio.size()));
                if (count > 0) audio.write(buffer, 0, count);
            }
            recording = false;
            if (audio.size() < 2) {
                if (!closed) dispatch(() -> onError.accept("No speech was recorded. Check your microphone and try again."));
                return;
            }

            updateState(State.TRANSCRIBING, onState);
            String transcript = transcribe(apiKey, resample(audio.toByteArray()));
            if (transcript.isBlank()) {
                if (!closed) dispatch(() -> onError.accept("No speech was recognized. Try again."));
            } else {
                if (!closed) dispatch(() -> onTranscript.accept(transcript));
            }
        } catch (LineUnavailableException e) {
            if (!closed) dispatch(() -> onError.accept("Microphone unavailable. Check your microphone and Windows privacy settings."));
        } catch (SpeechServiceException e) {
            if (!closed) dispatch(() -> onError.accept("OpenAI transcription: " + e.getMessage()));
        } catch (IOException e) {
            if (!closed) dispatch(() -> onError.accept("Could not contact OpenAI transcription. Check your connection and API key."));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (RuntimeException e) {
            if (!closed) dispatch(() -> onError.accept("Voice search failed. Check your microphone and OpenAI setup."));
        } finally {
            recording = false;
            if (line != null && line.isOpen()) {
                line.stop();
                line.close();
            }
            if (activeLine == line) activeLine = null;
            if (!closed) updateState(State.READY, onState);
        }
    }

    private String transcribe(String apiKey, byte[] audio)
            throws IOException, InterruptedException, SpeechServiceException {
        String boundary = "StreamVerse-" + java.util.UUID.randomUUID();
        byte[] wav = toWav(audio);
        ByteArrayOutputStream multipart = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(multipart)) {
            writePart(out, boundary, "model", TRANSCRIPTION_MODEL);
            writePart(out, boundary, "response_format", "json");
            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"speech.wav\"\r\n");
            out.writeBytes("Content-Type: audio/wav\r\n\r\n");
            out.write(wav);
            out.writeBytes("\r\n--" + boundary + "--\r\n");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/audio/transcriptions"))
                .timeout(Duration.ofSeconds(45))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipart.toByteArray()))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String message = body.has("error") && body.get("error").isJsonObject()
                    && body.getAsJsonObject("error").has("message")
                    ? body.getAsJsonObject("error").get("message").getAsString()
                    : "OpenAI returned HTTP " + response.statusCode();
            throw new SpeechServiceException(message);
        }
        return body.has("text") ? body.get("text").getAsString().trim() : "";
    }

    private static void writePart(DataOutputStream out, String boundary, String name, String value) throws IOException {
        out.writeBytes("--" + boundary + "\r\n");
        out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        out.write(value.getBytes(StandardCharsets.UTF_8));
        out.writeBytes("\r\n");
    }

    private static byte[] toWav(byte[] pcm) throws IOException {
        ByteArrayOutputStream wav = new ByteArrayOutputStream(44 + pcm.length);
        try (DataOutputStream out = new DataOutputStream(wav)) {
            out.writeBytes("RIFF");
            writeLittleEndianInt(out, 36 + pcm.length);
            out.writeBytes("WAVEfmt ");
            writeLittleEndianInt(out, 16);
            writeLittleEndianShort(out, 1);
            writeLittleEndianShort(out, 1);
            writeLittleEndianInt(out, SAMPLE_RATE);
            writeLittleEndianInt(out, SAMPLE_RATE * 2);
            writeLittleEndianShort(out, 2);
            writeLittleEndianShort(out, 16);
            out.writeBytes("data");
            writeLittleEndianInt(out, pcm.length);
            out.write(pcm);
        }
        return wav.toByteArray();
    }

    private static void writeLittleEndianInt(DataOutputStream out, int value) throws IOException {
        out.writeByte(value);
        out.writeByte(value >>> 8);
        out.writeByte(value >>> 16);
        out.writeByte(value >>> 24);
    }

    private static void writeLittleEndianShort(DataOutputStream out, int value) throws IOException {
        out.writeByte(value);
        out.writeByte(value >>> 8);
    }

    private static byte[] resample(byte[] source) {
        int samples = source.length / 2;
        int outputSamples = (int) Math.ceil(samples * (double) SAMPLE_RATE / SOURCE_RATE);
        ByteArrayOutputStream output = new ByteArrayOutputStream(outputSamples * 2);
        for (int i = 0; i < outputSamples; i++) {
            double position = i * (double) SOURCE_RATE / SAMPLE_RATE;
            int lower = Math.min((int) position, samples - 1);
            int upper = Math.min(lower + 1, samples - 1);
            double fraction = position - lower;
            short first = sample(source, lower);
            short second = sample(source, upper);
            int interpolated = (int) Math.round(first + (second - first) * fraction);
            output.write(interpolated & 0xff);
            output.write((interpolated >>> 8) & 0xff);
        }
        return output.toByteArray();
    }

    private static short sample(byte[] audio, int index) {
        int offset = index * 2;
        return (short) ((audio[offset] & 0xff) | (audio[offset + 1] << 8));
    }

    private void updateState(State value, Consumer<State> listener) {
        state = value;
        dispatch(() -> listener.accept(value));
    }

    private static void dispatch(Runnable action) {
        if (Platform.isFxApplicationThread()) action.run();
        else Platform.runLater(action);
    }

    private static final class SpeechServiceException extends Exception {
        private SpeechServiceException(String message) { super(message); }
    }
}
