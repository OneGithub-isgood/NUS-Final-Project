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

    public static final String SQL_CREATE_NEW_FAVPRODUCT =
        "INSERT INTO favProduct (productStoreUrl, productName, productImageUrl, supermarketStore, productCurrentPrice, productPreviousPrice, productDiscountCondition, productPercentageDiscount, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_GET_SAVED_PRODUCTS_BY_USERNAME =
        "SELECT productStoreUrl, productName, productImageUrl, supermarketStore, productCurrentPrice, productPreviousPrice, productDiscountCondition, productPercentageDiscount, log_time FROM favProduct f, user u WHERE f.username = ? AND is_verified = TRUE ORDER by log_time";
}
