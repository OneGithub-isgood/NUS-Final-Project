package sg.edu.nus.server.repositories;

public class SQLs {
    
    public static final String SQL_GET_USERS_BY_USERNAME_AND_PASSWORD =
        "SELECT count(*) as user_count FROM user WHERE username = ? AND passcode = sha1(?) AND is_verified = TRUE";

    public static final String SQL_CREATE_NEW_USERS =
        "INSERT INTO user (username, passcode, email) VALUES (?, sha1(?), ?)";

    public static final String SQL_GET_USERNAME_HASHED_VALUE_BY_USERNAME =
        "SELECT sha1(username) as hashed_username FROM user WHERE username = ?";

    public static final String SQL_GET_USERNAME_BY_HASHED_VALUE =
        "SELECT username FROM user WHERE sha1(username) = ?";

    public static final String SQL_UPDATE_VERIFY_STATUS_BY_USERNAME =
        "UPDATE user SET is_verified = TRUE WHERE username = ?";

    public static final String SQL_GET_PRODUCT_BY_PRODUCTSTOREURL =
        "SELECT count(*) as product_count FROM product WHERE productStoreUrl = ?";
    
    public static final String SQL_CREATE_NEW_PRODUCT =
        "INSERT INTO product (productStoreUrl, productName, productImageUrl, supermarketStore) VALUES (?, ?, ?, ?)";

    public static final String SQL_CREATE_NEW_WATCHLIST =
        "INSERT INTO watchlist (username, productStoreUrl) VALUES (?, ?)";
}
