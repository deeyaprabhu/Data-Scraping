import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scraper {
    private static final String URL = "https://www.scrapingcourse.com/ecommerce/";

    public static void main(String[] args) {
        List<Product> products = scrapeProducts(URL);

        // print all scraped products
        System.out.println(products.toString());
    }

    public static List<Product> scrapeProducts(String url) {
        List<Product> products = new ArrayList<>();
        while (url != null) {

            try {
                // connect to the website and retrieve the HTML document
                Document doc = Jsoup.connect(url).get();

                // select the list of product elements
                Elements productElements = doc.select("li.product");

                // iterate over each product element
                for (Element productElement : productElements) {
                    Product product = new Product();

                    // extracting product details safely
                    Element linkElement = productElement.selectFirst(".woocommerce-LoopProduct-link");
                    Element imgElement = productElement.selectFirst(".product-image");
                    Element nameElement = productElement.selectFirst(".product-name");
                    Element priceElement = productElement.selectFirst(".price");

                    product.setUrl(linkElement != null ? linkElement.attr("href") : "N/A");
                    product.setImage(imgElement != null ? imgElement.attr("src") : "N/A");
                    product.setName(nameElement != null ? nameElement.text() : "N/A");
                    product.setPrice(priceElement != null ? priceElement.text() : "N/A");

                    // add the product to the list
                    products.add(product);
                }
                Element nextButton = doc.selectFirst("a.next");
                if (nextButton != null) {
                    String nextPageUrl = nextButton.attr("href");
                    if (!nextPageUrl.startsWith("http")) {
                        nextPageUrl = url + nextPageUrl.replaceFirst("^/", "");
                    }
                    url = nextPageUrl; // update URL for next iteration
                } else {
                    url = null; // no more pages, exit loop
                }
            } catch (IOException e) {
                System.err.println("Error fetching page: " + e.getMessage());
                break; // stop on error
            }
        }

        return products;
    }
}
