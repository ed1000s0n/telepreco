package org.example;

import org.example.controller.ProductController;
import org.example.service.TelegramService;

import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Configuração do Telegram (pode ser carregada de arquivo de configuração)
        String botToken = System.getProperty("telegram.bot.token", "");
        String chatId = System.getProperty("telegram.chat.id", "");

        TelegramService telegramService = new TelegramService(botToken, chatId);
        ProductController productController = new ProductController(telegramService);

        System.out.println("🛒 === SCRAPER DE PRODUTOS ===");
        System.out.println();

        if (botToken.isBlank()) {
            System.out.println("⚠️  Telegram não configurado. Configure as variáveis:");
            System.out.println("   -Dtelegram.bot.token=SEU_BOT_TOKEN");
            System.out.println("   -Dtelegram.chat.id=SEU_CHAT_ID");
            System.out.println();
        }

        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1 - Extrair produto e enviar para Telegram");
            System.out.println("2 - Apenas extrair informações do produto");
            System.out.println("3 - Sair");
            System.out.print("Opção: ");

            String option = sc.nextLine();

            switch (option) {
                case "1":
                    extractAndSend(sc, productController);
                    break;
                case "2":
                    extractOnly(sc, productController);
                    break;
                case "3":
                    System.out.println("👋 Encerrando...");
                    return;
                default:
                    System.out.println("❌ Opção inválida!");
            }

            System.out.println();
        }
    }

    private static void extractAndSend(Scanner sc, ProductController controller) {
        System.out.print("Insira o link do produto: ");
        String url = sc.nextLine();

        System.out.println("🔄 Processando...");
        Map<String, Object> result = controller.extractAndSendProduct(url);

        if ((Boolean) result.get("success")) {
            System.out.println("✅ " + result.get("message"));
        } else {
            System.out.println("❌ " + result.get("message"));
        }
    }

    private static void extractOnly(Scanner sc, ProductController controller) {
        System.out.print("Insira o link do produto: ");
        String url = sc.nextLine();

        System.out.println("🔄 Extraindo informações...");
        Map<String, Object> result = controller.extractProductOnly(url);

        if ((Boolean) result.get("success")) {
            System.out.println("✅ " + result.get("message"));
            System.out.println();
            System.out.println(result.get("product"));
        } else {
            System.out.println("❌ " + result.get("message"));
        }
    }
}