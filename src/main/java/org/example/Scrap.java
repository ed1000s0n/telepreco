package org.example;

import org.example.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scrap {

    public static Product extractProductInfo(String url) throws IOException, InterruptedException {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL não pode ser vazia");
        }

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Erro ao acessar a URL. Status: " + response.statusCode());
        }

        String html = response.body();
        Document doc = Jsoup.parse(html);

        Product product = new Product();
        product.setUrl(url);

        // Extrair nome do produto
        product.setName(extractProductName(doc));

        // Extrair preço do produto
        product.setPrice(extractProductPrice(doc));

        // Extrair ID do produto
        product.setId(extractProductId(doc, url));

        return product;
    }

    private static String extractProductName(Document doc) {
        // Tentativas comuns para encontrar o nome do produto
        String[] nameSelectors = {
            "h1[data-testid='product-title']",
            "h1.product-title",
            "h1.pdp-product-name",
            ".product-name h1",
            "h1",
            ".title h1",
            "[data-testid='product-name']",
            ".product-title",
            "title"
        };

        for (String selector : nameSelectors) {
            Element element = doc.selectFirst(selector);
            if (element != null && !element.text().trim().isEmpty()) {
                return element.text().trim();
            }
        }

        // Se não encontrar, tenta extrair do título da página
        String title = doc.title();
        if (title != null && !title.trim().isEmpty()) {
            return title.split("\\|")[0].trim();
        }

        return "Nome não encontrado";
    }

    private static String extractProductPrice(Document doc) {
        // Tentativas comuns para encontrar o preço
        String[] priceSelectors = {
            "[data-testid='price-value']",
            ".price-value",
            ".price",
            ".product-price",
            ".current-price",
            ".price-current",
            ".sale-price",
            "[class*='price']",
            "[data-price]"
        };

        for (String selector : priceSelectors) {
            Element element = doc.selectFirst(selector);
            if (element != null && !element.text().trim().isEmpty()) {
                String price = element.text().trim();
                // Limpar e formatar o preço
                price = price.replaceAll("[^0-9,.]", "");
                if (!price.isEmpty()) {
                    return "R$ " + price;
                }
            }
        }

        // Busca por padrões de preço no texto
        Pattern pricePattern = Pattern.compile("R\\$\\s*([0-9]+[.,][0-9]+)");
        Matcher matcher = pricePattern.matcher(doc.text());
        if (matcher.find()) {
            return "R$ " + matcher.group(1);
        }

        return "Preço não encontrado";
    }

    private static String extractProductId(Document doc, String url) {
        // Tentar extrair ID de atributos comuns
        String[] idSelectors = {
            "[data-product-id]",
            "[data-id]",
            "[data-sku]",
            "[product-id]"
        };

        for (String selector : idSelectors) {
            Element element = doc.selectFirst(selector);
            if (element != null) {
                String id = element.attr("data-product-id");
                if (id.isEmpty()) id = element.attr("data-id");
                if (id.isEmpty()) id = element.attr("data-sku");
                if (id.isEmpty()) id = element.attr("product-id");

                if (!id.trim().isEmpty()) {
                    return id.trim();
                }
            }
        }

        // Tentar extrair ID da URL
        Pattern urlIdPattern = Pattern.compile("(?:id=|/p/|/product/)([0-9]+)");
        Matcher matcher = urlIdPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }

        // Gerar ID baseado na URL se não encontrar
        return String.valueOf(Math.abs(url.hashCode()));
    }
}
