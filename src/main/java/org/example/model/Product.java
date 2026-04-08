package org.example.model;

public class Product {
    private String id;
    private String name;
    private String price;
    private String url;

    public Product() {}

    public Product(String id, String name, String price, String url) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return String.format(
            "🛍️ *Produto Encontrado*\n\n" +
            "📦 *Nome:* %s\n" +
            "💰 *Preço:* %s\n" +
            "🔢 *ID:* %s\n" +
            "🔗 *Link:* %s",
            name != null ? name : "Não encontrado",
            price != null ? price : "Não encontrado", 
            id != null ? id : "Não encontrado",
            url != null ? url : "Não informado"
        );
    }
}
