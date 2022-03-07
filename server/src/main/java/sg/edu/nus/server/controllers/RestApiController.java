package sg.edu.nus.server.controllers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.server.model.ConsumerProduct;
import sg.edu.nus.server.model.User;
import sg.edu.nus.server.services.DataService;

@RestController

@RequestMapping(path="/api") 
public class RestApiController {
    private final Logger logger = Logger.getLogger(RestApiController.class.getName());

    @Autowired
    private DataService dataSrv;

    @GetMapping(path="/search", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getProductData(
            @RequestParam(name="product") String product) { // /api/search?product=milk

        logger.log(Level.INFO, "QueryString : %s".formatted(product)+".");
        List<ConsumerProduct> consumerProducts = dataSrv.getProductDetails(product);
        String respBody = dataSrv.ProductsListToJsonString(consumerProducts);

        return ResponseEntity.ok(respBody);

    }

    @GetMapping(path="/archive/{username}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getArchivedProductData(
            @PathVariable(name="username", required=true) String username) { // /api/archive/username

        logger.log(Level.INFO, "Path Variable : %s".formatted(username)+".");
        List<ConsumerProduct> archivedProducts = dataSrv.getArchiveProducts(username);
        String respBody = dataSrv.ArchivedListToJsonString(archivedProducts);

        return ResponseEntity.ok(respBody);

    }

    @GetMapping(path="/verify", produces=MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> verifyUserAccount(
            @RequestParam(name="username") String verificationCode) { // /api/verify?username=aeiou

        logger.log(Level.INFO, "QueryString : %s".formatted(verificationCode)+".");
        if (dataSrv.checkAccountVerification(verificationCode)) {
            return ResponseEntity.ok("Your account has been verified!");
        } else {
            return ResponseEntity.badRequest().body("");
        }

    }



    @PostMapping(path="/login", produces=MediaType.APPLICATION_JSON_VALUE) // /api/login
    public ResponseEntity<String> checkUserLogin(@RequestBody String payload) {

        User user = null;
        try {
            user = User.createObjUserForLogin(payload);
        } catch (Exception ex) {
            JsonObject err = Json.createObjectBuilder()
                .add("Error", ex.getMessage())
                .build();
            return ResponseEntity.badRequest().body(err.toString());
        }
        if (dataSrv.checkLoginCredential(user)) {
            return ResponseEntity.ok("{}");
        } else {
            JsonObject err = Json.createObjectBuilder()
                .add("Error", "Unable to authorize with the credential provided")
                .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(err.toString());
        }

    }

    @PostMapping(path="/favproduct", produces=MediaType.APPLICATION_JSON_VALUE) // /api/favproduct
    public ResponseEntity<String> processAddWatchlist(@RequestBody String payload) {

        User user = null;
        ConsumerProduct product = null;

        try {
            user = User.createObjUserForInsertWatchlist(payload);
            //logger.log(Level.INFO, "User in payload : %s".formatted(user.getUsername()+"."));
            product = ConsumerProduct.createObjProductForFavouriteProduct(payload);
            //logger.log(Level.INFO, "Current Price in payload : %s".formatted(product.getProductCurrentPrice())+".");
        } catch (Exception ex) {
            JsonObject err = Json.createObjectBuilder()
                .add("Error", ex.getMessage())
                .build();
            return ResponseEntity.badRequest().body(err.toString());
        }
        if (dataSrv.createNewFavouriteProduct(user, product)) {
            return ResponseEntity.ok("{}");
        } else {
            JsonObject err = Json.createObjectBuilder()
                .add("Error", "Fail To Create New Favourite Product")
                .build();
            return ResponseEntity.badRequest().body(err.toString());
        }
        
    }

    @PostMapping(path="/signup", produces=MediaType.APPLICATION_JSON_VALUE) // /api/signup
    public ResponseEntity<String> processUserSignup(@RequestBody String payload) throws AddressException, MessagingException {

        User user = null;
        try {
            user = User.createObjUserForSignup(payload);
            logger.log(Level.INFO, "User Email : %s".formatted(user.getEmail())+".");
        } catch (Exception ex) {
            JsonObject err = Json.createObjectBuilder()
                .add("Error", ex.getMessage())
                .build();
            return ResponseEntity.badRequest().body(err.toString());
        }
        String verificationCode = dataSrv.createUserRecord_sendVerificationEmail(user);
        if (verificationCode != "") {
            dataSrv.sendEmail(user, verificationCode);
            logger.log(Level.INFO, "User Email : %s".formatted(user.getEmail())+".");
            logger.log(Level.INFO, "User Verification : %s".formatted(verificationCode)+".");
            return ResponseEntity.ok("{}");
        } else {
            JsonObject err = Json.createObjectBuilder()
                .add("Error", "Unable to generate verification email")
                .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(err.toString());
        }

    }
}
