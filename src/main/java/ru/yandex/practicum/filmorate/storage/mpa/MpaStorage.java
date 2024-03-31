package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.MpaRating;

import java.util.List;

@Component
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;


    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MpaRating> getRatings() {
        String sql = "select * from Mpa_Rating";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MpaRating(rs.getInt("rating_id"), rs.getString("name")));
    }

    public MpaRating getRatingById(int id) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("select * from MPA_RATING where \"rating_id\" = ?", id);
        if (sql.next()) {
            MpaRating mpaRating = new MpaRating(sql.getInt("rating_id"), sql.getString("name"));
            return mpaRating;
        } else {
            String e = String.format("Rating with id %s not found", id);
            throw new NotFoundException(e);
        }
    }

}
