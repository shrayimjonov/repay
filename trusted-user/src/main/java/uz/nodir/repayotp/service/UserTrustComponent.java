package uz.nodir.repayotp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.nodir.repayotp.config.TrustedDBConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;


public class UserTrustComponent {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void save(String username, String ipAddress) {
        log.info("Save user {} to trusted from ip: {}", username, ipAddress);


        String sql = "INSERT INTO trusted_user (user_name, " +
                "ip, created_time) VALUES (?, ?, ?)";

        try (Connection connection = TrustedDBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, ipAddress);
            Timestamp initTime = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Tashkent")));
            statement.setTimestamp(3, initTime);

            log.info("Values: {} {} {}", username, ipAddress, initTime);

            // Выполняем запрос
            int rowsInserted = statement.executeUpdate();
            log.info("Inserted rows: {}", rowsInserted);

        } catch (SQLException e) {
            log.error("Occurred error while saving user into database with message: {}", e.getMessage());
        }

    }

    public Optional<String> findTrustedUser(String username, String ipAddress) {
        log.info("Find trusted user by: {} {}", username, ipAddress);

        String sql = "SELECT id, user_name, ip FROM trusted_user WHERE user_name = ? and ip = ?";


        try (Connection connection = TrustedDBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Устанавливаем параметр для запроса
            statement.setString(1, username);
            statement.setString(2, ipAddress);

            // Выполняем запрос
            ResultSet resultSet = statement.executeQuery();

            // Обрабатываем результат
            if (resultSet.next()) {
                var id = resultSet.getLong("id");
                var user_name = resultSet.getString("user_name");
                var ip = resultSet.getString("ip");

                log.info("Found trusted user: {}", user_name);
                return Optional.of(user_name);
            }

        } catch (SQLException e) {
            log.error("Occurred error while finding trusted user with message: {}", e.getMessage());
        }

        log.info("Not found trusted user: {}", username);
        return Optional.empty();
    }
}
