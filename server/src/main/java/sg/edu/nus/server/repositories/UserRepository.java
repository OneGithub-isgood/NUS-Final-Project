package sg.edu.nus.server.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.edu.nus.server.model.ConsumerProduct;
import sg.edu.nus.server.model.User;

import static sg.edu.nus.server.repositories.SQLs.*;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate templates;

    public boolean confirmLoginCredential(User user) {

        final SqlRowSet rs = templates.queryForRowSet(
            SQL_GET_USERS_BY_USERNAME_AND_PASSWORD, user.getUsername(), user.getPasscode());

        if (rs.next()) {
            return rs.getInt("user_count") > 0;
        }
        return false;

    }

    public String confirmNewUserRecord_getVerificationCode(User user) {

        String verificationCode = "";
        int numRecord = templates.update(SQL_CREATE_NEW_USERS,
            user.getUsername(), user.getPasscode(), user.getEmail());

        if (numRecord > 0) {
            final SqlRowSet rs = templates.queryForRowSet(
                SQL_GET_USERNAME_HASHED_VALUE_BY_USERNAME, user.getUsername());
            while (rs.next()) {
                verificationCode = rs.getString("hashed_username");
            }
        } else {return null;}

        return verificationCode;

    }

    public boolean confirmAccountVerification(String verificationCode) {

        final SqlRowSet rs = templates.queryForRowSet(
            SQL_GET_USERNAME_BY_HASHED_VALUE, verificationCode);

        if (rs.next()) {
            String userName = rs.getString("username");
            return templates.update(
                SQL_UPDATE_VERIFY_STATUS_BY_USERNAME, userName) > 0;
        }

        return false;

    }

    public boolean confirmNewWatchList(User user, ConsumerProduct product) {

        final SqlRowSet rs = templates.queryForRowSet(
            SQL_GET_PRODUCT_BY_PRODUCTSTOREURL, product.getProductStoreUrl());

        if (rs.next()) {
            if (rs.getInt("product_count") > 0) {
                int numWLRecord = templates.update(SQL_CREATE_NEW_WATCHLIST,
                user.getUsername(), product.getProductStoreUrl());
                return numWLRecord > 0;
            } else {
                int numProductRecord = templates.update(SQL_CREATE_NEW_PRODUCT,
                product.getProductStoreUrl(), product.getProductName(), product.getProductImageUrl(), product.getSupermarketStore());
                if (numProductRecord > 0) {
                    int numWLRecord = templates.update(SQL_CREATE_NEW_WATCHLIST,
                    user.getUsername(), product.getProductStoreUrl());
                    return numWLRecord > 0;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
    
}
