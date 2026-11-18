package com.streamverse.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Tiny HTTP server (JDK built-in, listens on 127.0.0.1 only) that serves the page hosting the YouTube iframe.
 *
 * Why not engine.loadContent(html)?  YouTube refuses to play embedded videos when the embedding page has no
 * proper origin/Referer ("Error 153"). A page served from http://127.0.0.1:PORT has a real origin, so the
 * embed works, and nothing is ever written to disk. Only the video id (never a file) is passed around.
 */
public final class LocalPlayerServer {
    private static final LocalPlayerServer INSTANCE = new LocalPlayerServer();
    /** YouTube ids are 11 chars of [A-Za-z0-9_-]; validating blocks any HTML injection through the query string. */
    private static final Pattern VIDEO_ID = Pattern.compile("[A-Za-z0-9_-]{11}");

    private HttpServer server;
    private String template;

    private LocalPlayerServer() { }

    public static LocalPlayerServer get() { return INSTANCE; }

    public synchronized String playerUrl(String videoId, int startSeconds) throws IOException {
        ensureStarted();
        return origin() + "/player?v=" + videoId + "&t=" + Math.max(0, startSeconds);
    }

    public synchronized void stop() {
        if (server != null) { server.stop(0); server = null; }
    }

    private void ensureStarted() throws IOException {
        if (server != null) return;
        try (InputStream in = LocalPlayerServer.class.getResourceAsStream("/web/player.html")) {
            if (in == null) throw new IOException("player.html missing from resources");
            template = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/player", this::handle);
        ExecutorService pool = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "player-http");
            t.setDaemon(true); // never keeps the JVM alive
            return t;
        });
        server.setExecutor(pool);
        server.start();
    }

    private String origin() { return "http://127.0.0.1:" + server.getAddress().getPort(); }

    private void handle(HttpExchange ex) throws IOException {
        try {
            String id = queryParam(ex.getRequestURI().getRawQuery(), "v");
            if (id == null || !VIDEO_ID.matcher(id).matches()) {
                ex.sendResponseHeaders(400, -1);
                return;
            }
            String t = queryParam(ex.getRequestURI().getRawQuery(), "t");
            String start = t != null && t.matches("\\d{1,5}") ? t : "0";          // only digits can reach the page
            String html = template
                    .replace("%%VIDEO_ID%%", id)
                    .replace("%%START%%", start)
                    .replace("%%ORIGIN_ENC%%", URLEncoder.encode(origin(), StandardCharsets.UTF_8));
            byte[] body = html.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            ex.getResponseHeaders().set("Cache-Control", "no-store");
            ex.getResponseHeaders().set("Referrer-Policy", "strict-origin-when-cross-origin");
            ex.sendResponseHeaders(200, body.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(body); }
        } finally {
            ex.close();
        }
    }

    private static String queryParam(String query, String name) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && pair.substring(0, eq).equals(name)) return pair.substring(eq + 1);
        }
        return null;
    }
}
