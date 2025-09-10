package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    public Optional<Mpa> getById(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id);
            return Optional.ofNullable(mpa);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name"));
        mpa.setDescription(rs.getString("description"));
        return mpa;
    }
}