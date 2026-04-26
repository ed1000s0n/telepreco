package org.example;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.example.model.Product;
import org.json.JSONObject;
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

        product.setName(extractProductName(doc));

        product.setPrice(extractProductPrice(doc));

        product.setId(extractProductId(doc, url));

        return product;
    }

    private static String extractProductName(Document doc) {
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

        String title = doc.title();
        if (title != null && !title.trim().isEmpty()) {
            return title.split("\\|")[0].trim();
        }

        return "Nome não encontrado";
    }

    private static String extractProductPrice(Document doc) {
        Element scriptElement = doc.selectFirst("script[name=\"structured-pdp\"]");

        if (scriptElement == null) {
            return "Elemento não encontrado";
        }

        Pattern pattern = Pattern.compile("\"lowPrice\"\\s*:\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(scriptElement.data());
        if (matcher.find()) {
            String preco = matcher.group(1);
            preco = preco.replace(".", ",");

            return "R$ " + preco;
        }

        return "Preço não encontrado";
    }
    private static String extractProductId(Document doc, String url) {
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

        Pattern urlIdPattern = Pattern.compile("(?:id=|/p/|/product/)([0-9]+)");
        Matcher matcher = urlIdPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return String.valueOf(Math.abs(url.hashCode()));
    }
}
