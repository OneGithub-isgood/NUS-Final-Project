package sg.edu.nus.server.model;

import java.io.ByteArrayInputStream;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class User {
    private String username;
    private String passcode;
    private String email;
    private boolean isVerified;
    private List<String> productStoreUrl_WatchList;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasscode() {
        return passcode;
    }
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public boolean isVerified() {
        return isVerified;
    }
    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
    public List<String> getProductStoreUrl_WatchList() {
        return productStoreUrl_WatchList;
    }
    public void setProductStoreUrl_WatchList(String ProductStoreUrl) {
        this.productStoreUrl_WatchList.add(ProductStoreUrl);
    }

    public User() { }

    public User(String username, String passcode, String email) {
        this.username = username;
        this.passcode = passcode;
        this.email = email;
    }

    public static User createObjUserForLogin(String jsonString) throws Exception {
        JsonReader jR = Json.createReader(new ByteArrayInputStream(jsonString.getBytes()));
        JsonObject jO = jR.readObject();
        final User userCredential = new User();
        userCredential.username = jO.getString("username");
        userCredential.passcode = jO.getString("passcode");

        return userCredential;
    }

    public static User createObjUserForSignup(String jsonString) throws Exception {
        JsonReader jR = Json.createReader(new ByteArrayInputStream(jsonString.getBytes()));
        JsonObject jO = jR.readObject();
        final User userAccountDetailUser = new User();
        userAccountDetailUser.username = jO.getString("username");
        userAccountDetailUser.passcode = jO.getString("passcode");
        userAccountDetailUser.email = jO.getString("email");

        return userAccountDetailUser;
    }

}

