package sg.edu.nus.server.services;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import sg.edu.nus.server.model.ConsumerProduct;
import sg.edu.nus.server.model.User;
import sg.edu.nus.server.repositories.UserRepository;

@Service
public class DataService {

    @Autowired
    private UserRepository userRepo;

    public Boolean checkLoginCredential(User user) {

        if (userRepo.confirmLoginCredential(user)) {
            return true;
        } else {
            return false;
        }

    }

    public Boolean checkAccountVerification(String verificationCode) {

        if (userRepo.confirmAccountVerification(verificationCode)) {
            return true;
        } else {
            return false;
        }

    }

    public String createUserRecord_sendVerificationEmail(User user) {

        return userRepo.confirmNewUserRecord_getVerificationCode(user);

    }

    public void sendEmail(User user, String verificationCode) throws AddressException, MessagingException {

        String gmailPwd = System.getenv("GMAIL_PASSWORD");
        String linkQueryString = verificationCode; // https://lobang-philosophy.herokuapp.com/api/verify?username=
        String emailHTML = "<!DOCTYPE html>\r\n<html>\r\n\t<body>\r\n\t\t<h3>Verify Lobang Philosophy Account</h3>\r\n\t\t<a href=\"http://localhost:8080/api/verify?username=" +
            linkQueryString + "\">http://localhost:8080/api/verify?username=" + linkQueryString + "</a>\r\n\t</body>\r\n</html>\r\n";

        Properties emailConfigProp = new Properties();
        emailConfigProp.put("mail.smtp.auth", "true");
        emailConfigProp.put("mail.smtp.starttls.enable", "true");
        emailConfigProp.put("mail.smtp.host", "smtp.gmail.com");
        emailConfigProp.put("mail.smtp.port", "587");

        Session emailSession = Session.getInstance(emailConfigProp, new javax.mail.Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("lobangphilosophy@gmail.com", gmailPwd); // Please don't hack my email ;)
            }
        });

        Message emailMsg = new MimeMessage(emailSession);
        emailMsg.setFrom(new InternetAddress("jupiterdolby@gmail.com", false));
        emailMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail().toString()));
        emailMsg.setSubject("Confirm Your Lobang Philosophy Account");
        emailMsg.setContent("Confirm Your Lobang Philosophy Account", "text/html; charset=utf-8");
        emailMsg.setSentDate(new Date());
        MimeBodyPart emailBody = new MimeBodyPart();
        emailBody.setContent(emailHTML, "text/html; charset=utf-8");
        Multipart emailMultiPart = new MimeMultipart();
        emailMultiPart.addBodyPart(emailBody);
        emailMsg.setContent(emailMultiPart);
        Transport.send(emailMsg);
        // Snippet Code from https://www.tutorialspoint.com/spring_boot/spring_boot_sending_email.htm

    }

    public Boolean createNewWatchList(User user, ConsumerProduct product) {

        if (userRepo.confirmNewWatchList(user, product)) {
            return true;
        } else {
            return false;
        }

    }

    public List<ConsumerProduct> getProductDetails(String userInput) {
        final Logger logger = Logger.getLogger(DataService.class.getName());

        List<ConsumerProduct> products = new LinkedList<>();
        int fairpriceNumResult = 0;
        int giantNumResult = 0;

        //Start of Data Scrape for FairPrice
        final String urlAddQueryFairprice = UriComponentsBuilder
            .fromUriString("https://www.fairprice.com.sg/search")
            .queryParam("query", userInput.trim().replace(" ", "+"))
            .toUriString();

        final RequestEntity<Void> reqFairprice = RequestEntity
            .get(urlAddQueryFairprice)
            .accept(MediaType.TEXT_HTML)
            .build();

        final RestTemplate tempFairprice = new RestTemplate();
        final ResponseEntity<String> respFairprice = tempFairprice.exchange(reqFairprice, String.class);

        if (respFairprice.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException(
                "NTUC Fairprice HTTP Error Code %s".formatted(respFairprice.getStatusCode().toString()));
        }
        try {
            Document doc = Jsoup.connect(urlAddQueryFairprice).get();
            Elements allProducts = doc.select("div[class=sc-1plwklf-0 iknXK product-container]");
            if (allProducts.size() > 10) {
                fairpriceNumResult = 10;
            } else {
                fairpriceNumResult = allProducts.size();
            }

            String productPreviousPriceStr;
            float productPreviousPrice;
            int productPercentageDiscount = 0;
            String productImageUrl;
            String productDiscountCondition = "";

            for (int p = 0; p < fairpriceNumResult; p++) {
                String productName = doc
                    .select("div[class=sc-1plwklf-0 iknXK product-container]").get(p)
                    .select("span[class=sc-1bsd7ul-1 gGWxuk]")
                    .text();
                ConsumerProduct product = new ConsumerProduct(productName);
                product.setProductPercentageDiscount(productPercentageDiscount);

                float productCurrentPrice = Float.parseFloat(doc
                    .select("div[class=sc-1plwklf-0 iknXK product-container]").get(p)
                    .select("span[class=sc-1bsd7ul-1 gJhHzP]")
                    .text().replace("$", ""));

                productPreviousPriceStr = doc
                    .select("div[class=sc-1plwklf-0 iknXK product-container]").get(p)
                    .select("span[class=sc-1bsd7ul-1 sc-1svix5t-0 gGWxuk lfBriN]")
                    .text();
                if (!productPreviousPriceStr.isEmpty()) {
                    productPreviousPrice = Float.parseFloat(productPreviousPriceStr.replace("$", ""));
                    product.setProductPreviousPrice(productPreviousPrice);
                    double percentageDiscount = ((productPreviousPrice - productCurrentPrice) / productPreviousPrice) * 100;
                    productPercentageDiscount = (int)percentageDiscount;
                    product.setProductPercentageDiscount(productPercentageDiscount);
                    productPercentageDiscount = 0;
                }

                String productStoreUrl = doc // Store Url:(https://www.fairprice.com.sg)/product/meiji-fresh-milk-2lt-10238055
                    .select("div[class=sc-1plwklf-0 iknXK product-container]").get(p)
                    .getElementsByTag("a")
                    .attr("href");
                Elements imageLinks = doc
                    .select("div[class=sc-1plwklf-0 iknXK product-container]").get(p)
                    .select("img[src]");
                for (Element link : imageLinks) {
                    if (link.attr("src").toLowerCase().contains(".jpg?q=")) {
                        productImageUrl = link.attr("src");
                        product.setProductImageUrl(productImageUrl);
                        break;
                    }
                }

                Elements discountConditions = doc
                    .select("div[class=sc-1plwklf-0 iknXK product-container]").get(p)
                    .select("div[class=sc-1plwklf-16 hRbyxZ]");
                for (Element discountCondition : discountConditions) {
                    if (productDiscountCondition != "") {
                        productDiscountCondition = productDiscountCondition + " & " + discountCondition.text();
                    } else {
                        productDiscountCondition = discountCondition.text();
                    }
                    if (discountCondition.text().contains("for $")) {
                        String[] condInformation = discountCondition.text().split(" ");
                        try {
                            int quantityRequirement = Integer.parseInt(condInformation[1]);
                            float promoPrice = Float.parseFloat(condInformation[3].replaceAll("[$,]", ""));
                            double percentageDiscount = ((productCurrentPrice - (promoPrice / quantityRequirement)) / productCurrentPrice) * 100;
                            productPercentageDiscount = (int)percentageDiscount;
                            if (product.getProductPercentageDiscount() < productPercentageDiscount) {
                                product.setProductPercentageDiscount(productPercentageDiscount);
                                productPercentageDiscount = 0;
                        }
                        } catch (NumberFormatException numFormatEX) {}
                    }
                }

                if (productDiscountCondition != "") {
                    product.setProductDiscountCondition(productDiscountCondition);
                    productDiscountCondition = "";
                } else {
                    product.setProductDiscountCondition("");
                }

                product.setProductCurrentPrice(productCurrentPrice);
                product.setProductStoreUrl("https://www.fairprice.com.sg" + productStoreUrl);
                product.setSupermarketStore("Fairprice");

                //logger.log(Level.INFO, "Fairprice Product Name : %s".formatted(product.getProductName())+".");
                //logger.log(Level.INFO, "Fairprice Product Web Url : %s".formatted(product.getProductStoreUrl())+".");
                //logger.log(Level.INFO, "Fairprice Product Image Url : %s".formatted(product.getProductImageUrl())+".");
                //logger.log(Level.INFO, "Fairprice Product Current Price : $%s".formatted(product.getProductCurrentPrice())+".");
                //logger.log(Level.INFO, "Fairprice Product Previous Price : $%s".formatted(product.getProductPreviousPrice())+".");
                //logger.log(Level.INFO, "Fairprice Product Discount Condition : %s".formatted(product.getProductDiscountCondition())+".");
                //logger.log(Level.INFO, "Fairprice Product Discount Percentage : %s".formatted(product.getProductPercentageDiscount())+"%.");

                products.add(product);

            } //End of product looping

        } catch (IOException ioEX) { }
        //End of Data Scrape for FairPrice

        //Start of Data Scrape for Giant
        final String urlAddQueryGiant = UriComponentsBuilder
            .fromUriString("https://giant.sg/search")
            .queryParam("q", userInput.trim().replace(" ", "+"))
            .toUriString();

        final RequestEntity<Void> reqGiant = RequestEntity
            .get(urlAddQueryGiant)
            .accept(MediaType.TEXT_HTML)
            .build();

        final RestTemplate tempGiant = new RestTemplate();
        final ResponseEntity<String> respGiant = tempGiant.exchange(reqGiant, String.class);

        if (respGiant.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException(
                "Giant HTTP Error Code %s".formatted(respGiant.getStatusCode().toString()));
        }
        
        try {
            Document docGiant = Jsoup.connect(urlAddQueryGiant).get();
            Elements allProductsGiant = docGiant.select("div[class=col-lg-2 col-md-4 col-6 col_product open-product-detail algolia-click open-single-page]");
            if (allProductsGiant.size() > 10) {
                giantNumResult  = 10;
            } else {
                giantNumResult  = allProductsGiant.size();
            }

            float productPreviousPriceGiant;
            int productPercentageDiscountGiant = 0;
            String productDiscountConditionGiant = "";

            for (int pG = 0; pG < giantNumResult ; pG++) {
                String productBrandGiant = docGiant
                    .select("div[class=col-lg-2 col-md-4 col-6 col_product open-product-detail algolia-click open-single-page]").get(pG)
                    .select("div[class=category-name]").get(0)
                    .getElementsByTag("a")
                    .text().trim();
                String productItemNameGiant = docGiant
                    .select("div[class=col-lg-2 col-md-4 col-6 col_product open-product-detail algolia-click open-single-page]").get(pG)
                    .select("div[class=product_name]").get(0)
                    .getElementsByTag("a")
                    .text().trim();
                ConsumerProduct productGiant = new ConsumerProduct(productBrandGiant + " " + productItemNameGiant);

                String productCurrentPriceMix = docGiant
                    .select("div[class=col-lg-2 col-md-4 col-6 col_product open-product-detail algolia-click open-single-page]").get(pG)
                    .select("div[class=content_price]").get(0)
                    .getElementsByTag("div").get(0)
                    .text().replace("$", "");
                if (productCurrentPriceMix.contains("/")) {
                    String [] productCurrentPriceArray = productCurrentPriceMix.split(" ");
                    String productCurrentPriceString = productCurrentPriceArray[0];
                    try {
                        productGiant.setProductCurrentPrice(Float.parseFloat(productCurrentPriceString));
                    } catch (NumberFormatException nfEX) { }
                } else {
                    productGiant.setProductCurrentPrice(Float.parseFloat(productCurrentPriceMix));
                }
                
                Elements productPriceElementListGiant = docGiant
                .select("div[class=col-lg-2 col-md-4 col-6 col_product open-product-detail algolia-click open-single-page]").get(pG)
                .select("div[class=product_price]").get(0)
                .getElementsByTag("div");

                float productCurrentPriceGiant = productGiant.getProductCurrentPrice();

                if (productPriceElementListGiant.size() > 1) {
                    boolean discountWithCondGiant = false;
                    for (Element productPriceElementGiant: productPriceElementListGiant) {
                        try {
                            if (productPriceElementGiant.text().contains("@ $")) {
                                productDiscountConditionGiant = productPriceElementGiant.text().trim();
                                productGiant.setProductDiscountCondition(productDiscountConditionGiant);
                                productDiscountConditionGiant = "";
                                discountWithCondGiant = true;
                            }
                        } finally { // Using finally to try finding product with discount but no condition
                            if (discountWithCondGiant == false) {
                                if (productPriceElementGiant.getElementsByTag("span").size() > 0) {
                                    String productPriceTagMixed = productPriceElementGiant
                                    .getElementsByTag("span").get(0).text().replace("$", "");
                                    if (productPriceTagMixed.contains("/")) {
                                        productGiant.setProductDiscountCondition("Selling with weight " + productPriceTagMixed);
                                        break;
                                    } // Throw element with no price value
                                    productPreviousPriceGiant = Float.parseFloat(productPriceTagMixed);
                                    productGiant.setProductPreviousPrice(productPreviousPriceGiant);

                                    double percentageDiscount = ((productPreviousPriceGiant - productCurrentPriceGiant) / productPreviousPriceGiant) * 100;
                                    productPercentageDiscountGiant = (int)percentageDiscount;
                                    productGiant.setProductPercentageDiscount(productPercentageDiscountGiant);
                                    productPercentageDiscountGiant = 0;
                                }
                            }
                        } 
                    }
                } // Else means this product have no discount

                String productStoreUrlGiant = docGiant // Store Url:(https://giant.sg)/fc-uht-milk-1l-5070696
                    .select("div[class=col-lg-2 col-md-4 col-6 col_product open-product-detail algolia-click open-single-page]").get(pG)
                    .getElementsByClass("col-lg-2 col-md-4 col-6 col_product open-product-detail algolia-click open-single-page").get(0)
                    .attr("data-url");
                productGiant.setProductStoreUrl("https://giant.sg" + productStoreUrlGiant);

                String productImageUrlGiant = docGiant // Store Url:(https://giant.sg)/fc-uht-milk-1l-5070696
                    .select("div[class=col-lg-2 col-md-4 col-6 col_product open-product-detail algolia-click open-single-page]").get(pG)
                    .getElementsByClass("img-fluid").get(0)
                    .attr("src");
                productGiant.setProductImageUrl(productImageUrlGiant);

                if (productGiant.getProductDiscountCondition() != null) {
                    if (productGiant.getProductDiscountCondition().contains("Selling with weight")) {
                        productPercentageDiscountGiant = 0;
                    } else {
                        String[] condInformationGiant = productGiant.getProductDiscountCondition().split(" ");
                        int quantityRequirementGiant = Integer.parseInt(condInformationGiant[1]);
                        float promoPriceGiant = Float.parseFloat(condInformationGiant[3].replaceAll("[$]", ""));
                        double percentageDiscountGiant = ((productCurrentPriceGiant - (promoPriceGiant / quantityRequirementGiant)) / productCurrentPriceGiant) * 100;
                        productPercentageDiscountGiant = (int)percentageDiscountGiant;
                        productGiant.setProductPercentageDiscount(productPercentageDiscountGiant);
                        productPercentageDiscountGiant = 0;
                    }
                } else if (productGiant.getProductDiscountCondition() == null && productGiant.getProductPreviousPrice() == 0) {
                    productGiant.setProductPercentageDiscount(0);
                }
                
                if (productGiant.getProductDiscountCondition() == null) {
                    productGiant.setProductDiscountCondition("");
                }
                productGiant.setSupermarketStore("Giant");

                //logger.log(Level.INFO, "Giant Product Name : %s".formatted(productGiant.getProductName())+".");
                //logger.log(Level.INFO, "Giant Product Web Url : %s".formatted(productGiant.getProductStoreUrl())+".");
                //logger.log(Level.INFO, "Giant Product Image Url : %s".formatted(productGiant.getProductImageUrl())+".");
                //logger.log(Level.INFO, "Giant Product Current Price : $%s".formatted(productGiant.getProductCurrentPrice())+".");
                //logger.log(Level.INFO, "Giant Product Previous Price : $%s".formatted(productGiant.getProductPreviousPrice())+".");
                //logger.log(Level.INFO, "Giant Product Discount Condition : %s".formatted(productGiant.getProductDiscountCondition())+".");
                //logger.log(Level.INFO, "Giant Product Discount Percentage : %s".formatted(productGiant.getProductPercentageDiscount())+"%.");
                
                products.add(productGiant);

            } //End of product looping

        } catch (IOException ioEX) { }
        //End of Data Scrape for Giant

        return products;
    }

    public String ProductsListToJsonString(List<ConsumerProduct> productList) {
        JsonArrayBuilder jsonArrBuilderProducts = Json.createArrayBuilder();
        for (ConsumerProduct product: productList) {
            JsonObjectBuilder jsonObj = Json.createObjectBuilder()
                .add("productName", product.getProductName())
                .add("productCurrentPrice", product.getProductCurrentPrice())
                .add("productPreviousPrice", product.getProductPreviousPrice())
                .add("productDiscountCondition", product.getProductDiscountCondition())
                .add("productPercentageDiscount", product.getProductPercentageDiscount())
                .add("productImageUrl", product.getProductImageUrl())
                .add("productStoreUrl", product.getProductStoreUrl())
                .add("supermarketStore", product.getSupermarketStore());
            jsonArrBuilderProducts.add(jsonObj);
        }
        JsonArray jsonArrayProducts = jsonArrBuilderProducts.build();
        return jsonArrayProducts.toString();
    }

}
