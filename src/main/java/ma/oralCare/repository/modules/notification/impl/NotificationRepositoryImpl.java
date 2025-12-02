package ma.oralCare.repository.notification.impl;

import ma.oralCare.entities.notification.Notification;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.notification.api.NotificationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final Connection connection;

    public NotificationRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Notification save(Notification entity) {
        final String sql = """
                INSERT INTO notifications
                    (titre, message, date, time, type, priorite)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getTitre() != null) {
                ps.setString(1, entity.getTitre().name());
            } else {
                ps.setNull(1, java.sql.Types.VARCHAR);
            }

            ps.setString(2, entity.getMessage());

            LocalDate date = entity.getDate();
            LocalTime time = entity.getTime();

            if (date != null) {
                ps.setDate(3, java.sql.Date.valueOf(date));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            if (time != null) {
                ps.setTime(4, java.sql.Time.valueOf(time));
            } else {
                ps.setNull(4, java.sql.Types.TIME);
            }

            if (entity.getType() != null) {
                ps.setString(5, entity.getType().name());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }

            if (entity.getPriorite() != null) {
                ps.setString(6, entity.getPriorite().name());
            } else {
                ps.setNull(6, java.sql.Types.VARCHAR);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Notification", e);
        }
    }

    @Override
    public Notification update(Notification entity) {
        final String sql = """
                UPDATE notifications
                SET titre = ?, message = ?, date = ?, time = ?, type = ?, priorite = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (entity.getTitre() != null) {
                ps.setString(1, entity.getTitre().name());
            } else {
                ps.setNull(1, java.sql.Types.VARCHAR);
            }

            ps.setString(2, entity.getMessage());

            LocalDate date = entity.getDate();
            LocalTime time = entity.getTime();

            if (date != null) {
                ps.setDate(3, java.sql.Date.valueOf(date));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            if (time != null) {
                ps.setTime(4, java.sql.Time.valueOf(time));
            } else {
                ps.setNull(4, java.sql.Types.TIME);
            }

            if (entity.getType() != null) {
                ps.setString(5, entity.getType().name());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }

            if (entity.getPriorite() != null) {
                ps.setString(6, entity.getPriorite().name());
            } else {
                ps.setNull(6, java.sql.Types.VARCHAR);
            }

            ps.setLong(7, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Notification with id " + entity.getId(), e);
        }
    }

    @Override
    public Notification findById(Long id) {
        final String sql = "SELECT * FROM notifications WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapNotification(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Notification with id " + id, e);
        }
    }

    @Override
    public List<Notification> findAll() {
        final String sql = "SELECT * FROM notifications";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Notification> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapNotification(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Notifications", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM notifications WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Notification with id " + id, e);
        }
    }
}


