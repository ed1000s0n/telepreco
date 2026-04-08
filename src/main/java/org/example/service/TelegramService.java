package org.example.service;

import org.example.model.Product;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TelegramService {

    private final String botToken;
    private final String chatId;
    private final HttpClient httpClient;

    public TelegramService(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendProductMessage(Product product) throws IOException, InterruptedException {
        String message = product.toString();
        sendMessage(message);
    }

    public void sendMessage(String message) throws IOException, InterruptedException {
        if (botToken == null || botToken.isBlank()) {
            System.out.println("⚠️  Bot Token não configurado. Mensagem seria enviada:");
            System.out.println(message);
            return;
        }

        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);

        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String requestBody = String.format("chat_id=%s&text=%s&parse_mode=Markdown", 
                                         chatId, encodedMessage);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, 
                                                       HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("✅ Mensagem enviada com sucesso para o Telegram!");
        } else {
            System.err.println("❌ Erro ao enviar mensagem: " + response.body());
            throw new IOException("Falha ao enviar mensagem para o Telegram. Status: " + 
                                response.statusCode());
        }
    }
}
