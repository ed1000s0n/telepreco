package org.example.controller;

import org.example.model.Product;
import org.example.service.TelegramService;
import org.example.Scrap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductController {

    private final TelegramService telegramService;

    public ProductController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    public Map<String, Object> extractAndSendProduct(String url) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extrair informações do produto
            Product product = Scrap.extractProductInfo(url);

            // Enviar para o Telegram
            telegramService.sendProductMessage(product);

            response.put("success", true);
            response.put("message", "Produto extraído e enviado com sucesso!");
            response.put("product", product);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Erro de conexão: " + e.getMessage());
        } catch (InterruptedException e) {
            response.put("success", false);
            response.put("message", "Operação interrompida: " + e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro inesperado: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> extractProductOnly(String url) {
        Map<String, Object> response = new HashMap<>();

        try {
            Product product = Scrap.extractProductInfo(url);

            response.put("success", true);
            response.put("message", "Produto extraído com sucesso!");
            response.put("product", product);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao extrair produto: " + e.getMessage());
        }

        return response;
    }
}
