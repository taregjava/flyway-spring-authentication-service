package com.halfacode.flyway_spring.encryptMigration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
@PropertySource("classpath:application.properties")  // Ensure Spring loads the properties
public class V5__Encrypt_User_Emails extends BaseJavaMigration {

    @Value("${encryption.key}")
    private String secretKey;  // Inject the key directly into this field

    @Override
    public void migrate(Context context) throws Exception {
        if (secretKey == null || secretKey.length() != 16) {
            throw new IllegalStateException("Invalid or missing encryption key. Ensure it's 16 bytes long.");
        }

        try (PreparedStatement selectStmt = context.getConnection().prepareStatement(
                "SELECT id, email FROM users");
             PreparedStatement updateStmt = context.getConnection().prepareStatement(
                     "UPDATE users SET email = ? WHERE id = ?")) {

            ResultSet rs = selectStmt.executeQuery();
            while (rs.next()) {
                String email = rs.getString("email");
                String encryptedEmail = encrypt(email);
                updateStmt.setString(1, encryptedEmail);
                updateStmt.setInt(2, rs.getInt("id"));
                updateStmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt user emails: " + e.getMessage(), e);
        }
    }

    // AES encryption
    private String encrypt(String email) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(email.getBytes());
        return Base64.getEncoder().encodeToString(encrypted); // Base64 encoding for storage
    }

    // AES decryption (optional, for later use)
    private String decrypt(String encryptedEmail) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedEmail);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted);
    }
}