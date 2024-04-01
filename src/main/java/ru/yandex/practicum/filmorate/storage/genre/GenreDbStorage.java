package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;

@Component
public class GenreDbStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Genre> getGenres() {
        String sql = "select * from GENRE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")));
    }

    public Genre getGenreById(int genreId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from GENRE where \"genre_id\"=?", genreId);
        if (rowSet.next()) {
            return new Genre(rowSet.getInt("genre_id"), rowSet.getString("name"));
        } else {
            String e = String.format("Genre with id %s not found", genreId);
            throw new NotFoundException(e);
        }
    }
}
