/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;

import de.applejuicenet.client.fassade.exception.NoAccessException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/WebsiteContentLoader.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */

public abstract class WebsiteContentLoader {
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000;
    private static final int DEFAULT_REQUEST_TIMEOUT_MILLIS = 30000;
    private static final HttpClient CLIENT = createClient(DEFAULT_CONNECT_TIMEOUT_MILLIS);

    public static String getWebsiteContent(String website) throws NoAccessException {
        return getWebsiteContent(website, CLIENT, DEFAULT_REQUEST_TIMEOUT_MILLIS);
    }

    static String getWebsiteContent(String website, int connectTimeoutMillis, int requestTimeoutMillis)
            throws NoAccessException {
        if (connectTimeoutMillis <= 0 || requestTimeoutMillis <= 0) {
            throw new IllegalArgumentException("Timeouts must be positive");
        }
        try (HttpClient client = createClient(connectTimeoutMillis)) {
            return getWebsiteContent(website, client, requestTimeoutMillis);
        }
    }

    private static HttpClient createClient(int connectTimeoutMillis) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMillis))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    private static String getWebsiteContent(String website, HttpClient client, int requestTimeoutMillis)
            throws NoAccessException {
        CompletableFuture<HttpResponse<String>> pending = null;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(website))
                    .timeout(Duration.ofMillis(requestTimeoutMillis))
                    .header("User-Agent", String.format("ajcorefassade; Java/%s; (%s/%s)",
                            System.getProperty("java.version"), System.getProperty("os.name"),
                            System.getProperty("os.version")))
                    .GET()
                    .build();
            pending = client.sendAsync(request, HttpResponse.BodyHandlers.ofString(Charset.defaultCharset()));
            HttpResponse<String> response = pending.get(requestTimeoutMillis, TimeUnit.MILLISECONDS);
            if (response.statusCode() >= 400) {
                throw new IOException("HTTP status " + response.statusCode());
            }
            return response.body().lines().collect(Collectors.joining());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NoAccessException("Web request interrupted", e);
        } catch (TimeoutException e) {
            throw new NoAccessException("Web request timed out", new HttpTimeoutException("Request timed out"));
        } catch (ExecutionException e) {
            Exception cause = e.getCause() instanceof Exception exception ? exception : e;
            throw new NoAccessException("wrong proxysettings?", cause);
        } catch (IOException | IllegalArgumentException e) {
            throw new NoAccessException("wrong proxysettings?", e);
        } finally {
            if (pending != null && !pending.isDone()) {
                pending.cancel(true);
            }
        }
    }
}
