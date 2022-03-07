package sg.edu.nus.server.model;

import java.io.ByteArrayInputStream;
import java.util.Date;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class ConsumerProduct {
    private String productName;
    private float productCurrentPrice; //Every product confirm have this
    private float productPreviousPrice; //But only products with direct discount will have this (Before Slashed price)
    private String productDiscountCondition;
    private int productPercentageDiscount;
    private String productImageUrl;
    private String productStoreUrl;
    private String supermarketStore;
    private Date watchRecord;
    
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getProductCurrentPrice() {
        return productCurrentPrice;
    }

    public void setProductCurrentPrice(float productCurrentPrice) {
        this.productCurrentPrice = productCurrentPrice;
    }

    public float getProductPreviousPrice() {
        return productPreviousPrice;
    }

    public void setProductPreviousPrice(float productPreviousPrice) {
        this.productPreviousPrice = productPreviousPrice;
    }

    public String getProductDiscountCondition() {
        return productDiscountCondition;
    }

    public void setProductDiscountCondition(String productDiscountCondition) {
        this.productDiscountCondition = productDiscountCondition;
    }

    public int getProductPercentageDiscount() {
        return productPercentageDiscount;
    }

    public void setProductPercentageDiscount(int productPercentageDiscount) {
        this.productPercentageDiscount = productPercentageDiscount;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductStoreUrl() {
        return productStoreUrl;
    }

    public void setProductStoreUrl(String productStoreUrl) {
        this.productStoreUrl = productStoreUrl;
    }

    public String getSupermarketStore() {
        return supermarketStore;
    }

    public void setSupermarketStore(String supermarketStore) {
        this.supermarketStore = supermarketStore;
    }

    public Date getWatchRecord() {
        return watchRecord;
    }

    public void setWatchRecord(Date watchRecord) {
        this.watchRecord = watchRecord;
    }

    public ConsumerProduct(String productName, float productCurrentPrice, float productPreviousPrice,
            String productDiscountCondition, int productPercentageDiscount, String productImageUrl,
            String productStoreUrl, String supermarketStore) {
        this.productName = productName;
        this.productCurrentPrice = productCurrentPrice;
        this.productPreviousPrice = productPreviousPrice;
        this.productDiscountCondition = productDiscountCondition;
        this.productPercentageDiscount = productPercentageDiscount;
        this.productImageUrl = productImageUrl;
        this.productStoreUrl = productStoreUrl;
        this.supermarketStore = supermarketStore;
    }

    public ConsumerProduct() {

    }

    public ConsumerProduct(String productName) {
        this.productName = productName;
    }

    public static ConsumerProduct createObjProductForFavouriteProduct(String jsonString) throws Exception {
        JsonReader jR = Json.createReader(new ByteArrayInputStream(jsonString.getBytes()));
        JsonObject jO = jR.readObject();
        final ConsumerProduct product = new ConsumerProduct();
        product.setProductStoreUrl(jO.getJsonObject("product").getString("productStoreUrl"));
        product.setProductName(jO.getJsonObject("product").getString("productName"));
        product.setProductImageUrl(jO.getJsonObject("product").getString("productImageUrl"));
        product.setSupermarketStore(jO.getJsonObject("product").getString("supermarketStore"));
        product.setProductCurrentPrice((float)jO.getJsonObject("product").getJsonNumber("productCurrentPrice").doubleValue());
        product.setProductPreviousPrice((float)jO.getJsonObject("product").getJsonNumber("productPreviousPrice").doubleValue());
        product.setProductDiscountCondition(jO.getJsonObject("product").getString("productDiscountCondition"));
        product.setProductPercentageDiscount(jO.getJsonObject("product").getInt("productPercentageDiscount"));
        return product;
    }
    
}
