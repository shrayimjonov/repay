package uz.nodir.repayotp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.nodir.repayotp.config.TrustedDBConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;



public class StorageComponent {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public void saveOtp(String username, String sessionId, String otp) {
        log.info("Save OTP sessionId: {} otp: ******", sessionId);


        String sql = "INSERT INTO user_session (user_name, " +
                "session_id, otp, created_time) VALUES (?, ?, ?, ?)";

        try (Connection connection = TrustedDBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            log.info("Saving user {} session: {}", username, sessionId);
            statement.setString(1, username);
            statement.setString(2, sessionId);
            statement.setString(3, otp);
            Timestamp initTime = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Tashkent")));
            statement.setTimestamp(4, initTime);

            log.info("Values: {} {} {}", username, sessionId, initTime);

            // Выполняем запрос
            int rowsInserted = statement.executeUpdate();
            log.info("Inserted rows: {}", rowsInserted);

        } catch (SQLException e) {
            log.error("Occurred error while saving user into database with message: {}", e.getMessage());
        }

    }

    public Optional<String> getOtp(String sessionId) {
        log.info("Get OTP sessionId: {}", sessionId);

        String sql = "SELECT id, user_name, session_id, otp FROM user_session WHERE session_id = ?";


        try (Connection connection = TrustedDBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Устанавливаем параметр для запроса
            statement.setString(1, sessionId);

            // Выполняем запрос
            ResultSet resultSet = statement.executeQuery();

            // Обрабатываем результат
            if (resultSet.next()) {
                var id = resultSet.getLong("id");
                var username = resultSet.getString("user_name");
                var session_id = resultSet.getString("session_id");
                var otp = resultSet.getString("otp");

                log.info("OTP sessionId: {} username: {}", sessionId, username);
                return Optional.of(otp);
            }

        } catch (SQLException e) {
            log.error("Occurred error while getting OTP by sessionId: {}", e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void deleteOtpBySessionId(String sessionId) {
        log.info("Delete OTP sessionId by sessionId: {}", sessionId);

        String sql = "DELETE FROM user_session WHERE session_id = ?";

        try (Connection connection = TrustedDBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, sessionId);
            int rowsAffected = statement.executeUpdate();

            log.info("Deleted rows: {}", rowsAffected);

        } catch (SQLException e) {
            log.error("Occurred error while deleting OTP by sessionId: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteAllSessionsByUsername(String username) {
        log.info("Delete all sessions by username: {}", username);

        String sql = "DELETE FROM user_session WHERE user_name = ?";

        try (Connection connection = TrustedDBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            int rowsAffected = statement.executeUpdate();

            log.info("Deleted rows: {}", rowsAffected);

        } catch (SQLException e) {
            log.error("Occurred error while deleting OTP by sessionId: {}", e.getMessage());
            e.printStackTrace();
        }
    }


}
