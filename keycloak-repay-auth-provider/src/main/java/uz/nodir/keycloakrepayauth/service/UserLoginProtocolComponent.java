package uz.nodir.keycloakrepayauth.service;

import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.nodir.keycloakrepayauth.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class UserLoginProtocolComponent {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void saveEnteredUser(UserModel user, String ipAddress) {
        logger.info("Save entered user: {}", user.getUsername());

        String sql = "INSERT INTO keycloak.user_protocol (user_id, user_name, " +
                "first_name, last_name, ip_address, created_time) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            logger.info("Saving user: {} from ip address: {}", user, ipAddress);
            statement.setString(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, ipAddress);
            Timestamp initTime = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Tashkent")));
            statement.setTimestamp(6, initTime);

            logger.info("Prepared sql: {}", sql);
            logger.info("Values: {} {} {} {} {} {}", user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), ipAddress, initTime);

            // Выполняем запрос
            int rowsInserted = statement.executeUpdate();
            logger.info("Inserted rows: {}", rowsInserted);
          //  connection.commit();

        } catch (SQLException e) {
            logger.error("Occurred error while saving user into database with message: {}", e.getMessage());
            e.printStackTrace();
        }

    }
}
