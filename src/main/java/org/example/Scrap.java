package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Scrap {
    public static void scrap ()  throws IOException, InterruptedException {

        String url = "https://www.netshoes.com.br/p/kit-1-tenis-olympikus-orbita-e-1-mochila-olympikus-braze-SE7-07VW-006";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        String html = response.body();

        System.out.println(html);
    }
}
